package io.bomberman;

import io.bomberman.messaging.Channels;
import io.bomberman.messaging.codec.BombPayloadCodec;
import io.bomberman.messaging.codec.PlayerPayloadCodec;
import io.bomberman.messaging.codec.SessionPayloadCodec;
import io.bomberman.messaging.consumption.ArtificialMovementEventConsumer;
import io.bomberman.messaging.consumption.ClientInitEventConsumer;
import io.bomberman.messaging.consumption.GameCreationEventConsumer;
import io.bomberman.messaging.consumption.MaxSimultaneousBombsIncrementEventConsumer;
import io.bomberman.messaging.notification.MaxSimultaneousBombsIncrementNotifier;
import io.bomberman.service.GameCreationService;
import io.bomberman.messaging.notification.BombActivationNotifier;
import io.bomberman.messaging.notification.BombAllocationNotifier;
import io.bomberman.messaging.notification.ClientInitNotifier;
import io.bomberman.messaging.notification.GameCreationNotifier;
import io.bomberman.messaging.notification.PlayerPositionModificationNotifier;
import io.bomberman.messaging.payload.BombPayload;
import io.bomberman.messaging.payload.PlayerPayload;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.web.handler.WebRequestHandler;
import io.bomberman.web.handler.WebSocketHandler;
import io.bomberman.web.session.SessionManager;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.eventbus.MessageProducer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.ErrorHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import rx.Observable;

import java.util.concurrent.TimeUnit;

import static io.bomberman.messaging.Channels.ARTIFICIAL_MOVEMENT_CHANNEL;
import static io.bomberman.messaging.Channels.MAX_SIMULTANEOUS_BOMBS_INCREMENT_CHANNEL;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);


  @Value("${session_cleanup_periodicity}")
  private int sessionCleanupPeriodicity;
  @Value("${server_port}")
  private int serverPort;
  @Value("${bot_movement_periodicity}")
  private int botMovementPeriodicity;
  @Value("${bomb_activation_periodicity}")
  private int bombActivationPeriodicity;
  @Value("${max_concurrent_bombs_increase_periodicity}")
  private int maxConcurrentBombsIncreasePeriodicity;

  @Autowired
  private SessionManager sessionManager;

  @Autowired
  private PlayerPositionModificationNotifier playerPositionModificationNotifier;
  @Autowired
  private ArtificialMovementEventConsumer artificialMovementEventConsumer;

  @Autowired
  private BombAllocationNotifier bombAllocationNotifier;
  @Autowired
  private BombActivationNotifier bombActivationNotifier;

  @Autowired
  private MaxSimultaneousBombsIncrementNotifier maxSimultaneousBombsIncrementNotifier;
  @Autowired
  private MaxSimultaneousBombsIncrementEventConsumer maxSimultaneousBombsIncrementEventConsumer;

  @Autowired
  private ClientInitNotifier clientInitNotifier;
  @Autowired
  private ClientInitEventConsumer clientInitEventConsumer;

  @Autowired
  private GameCreationService gameCreationService;
  @Autowired
  private GameCreationNotifier gameCreationNotifier;
  @Autowired
  private GameCreationEventConsumer gameCreationEventConsumer;

  @Autowired
  private PlayerPayloadCodec playerPayloadCodec;
  @Autowired
  private SessionPayloadCodec sessionPayloadCodec;
  @Autowired
  private BombPayloadCodec bombPayloadCodec;

  @Autowired
  private WebSocketHandler webSocketHandler;
  @Autowired
  private WebRequestHandler webRequestHandler;


  @Override
  public void start() throws Exception {
    initApplicationContext();

    Router router = Router.router(vertx);
    router.mountSubRouter("/event", eventRouter());
    router.route("/*").handler(staticHandler());
    router.route().failureHandler(errorHandler());

    initEventWorkflow();
    scheduleSessionCleanUpDaemon();

    HttpServer httpServer = vertx
        .createHttpServer()
        .requestHandler(router::accept);

    httpServer
        .websocketStream()
        .handler(webSocketHandler);

    httpServer.listen(serverPort);
  }

  private void initApplicationContext() {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.getBeanFactory().registerSingleton("eventBus", vertx.eventBus());
    applicationContext.scan(this.getClass().getPackage().getName());
    PropertySourcesPlaceholderConfigurer pph = new PropertySourcesPlaceholderConfigurer();
    pph.setLocation(new ClassPathResource("/application.properties"));
    applicationContext.addBeanFactoryPostProcessor(pph);
    applicationContext.refresh();
    applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
  }

  private void initEventWorkflow() {
    EventBus eventBus = vertx.eventBus();
    io.vertx.core.eventbus.EventBus delegate = (io.vertx.core.eventbus.EventBus) eventBus.getDelegate();
    delegate.registerDefaultCodec(PlayerPayload.class, playerPayloadCodec);
    delegate.registerDefaultCodec(SessionPayload.class, sessionPayloadCodec);
    delegate.registerDefaultCodec(BombPayload.class, bombPayloadCodec);

    eventBus.<PlayerPayload>consumer(Channels.PLAYER_POSITION_MODIFICATION_CHANNEL).toObservable()
        .doOnError(this::logError)
        .subscribe(playerPositionModificationNotifier);

    eventBus.<SessionPayload>consumer(Channels.CLIENT_INIT_CHANNEL).toObservable()
        .doOnError(this::logError)
        .doOnNext(clientInitEventConsumer)
        .subscribe(clientInitNotifier);

    eventBus.<SessionPayload>consumer(MAX_SIMULTANEOUS_BOMBS_INCREMENT_CHANNEL).toObservable()
        .delay(maxConcurrentBombsIncreasePeriodicity, TimeUnit.MILLISECONDS)
        .doOnError(this::logError)
        .filter(this::isSessionOpen)
        .doOnNext(maxSimultaneousBombsIncrementEventConsumer)
        .doOnNext(maxSimultaneousBombsIncrementNotifier)
        .subscribe(message -> eventBus.publish(MAX_SIMULTANEOUS_BOMBS_INCREMENT_CHANNEL, message.body()));

    eventBus.<SessionPayload>consumer(Channels.GAME_CREATION_CHANNEL).toObservable()
        .doOnError(this::logError)
        .doOnNext(gameCreationService)
        .doOnNext(gameCreationEventConsumer)
        .doOnNext(gameCreationNotifier)
        .subscribe(message -> eventBus.publish(MAX_SIMULTANEOUS_BOMBS_INCREMENT_CHANNEL, message.body()));

    eventBus.<BombPayload>consumer(Channels.BOMB_ALLOCATION_CHANNEL).toObservable()
        .doOnError(this::logError)
        .doOnNext(bombAllocationNotifier)
        .delay(bombActivationPeriodicity, TimeUnit.MILLISECONDS)
        .subscribe(bombActivationNotifier);

    eventBus.<PlayerPayload>consumer(ARTIFICIAL_MOVEMENT_CHANNEL).toObservable()
        .doOnError(this::logError)
        .filter(this::isSessionOpen)
        .delay(botMovementPeriodicity, TimeUnit.MILLISECONDS)
        .doOnNext(artificialMovementEventConsumer)
        .subscribe(message -> eventBus.publish(ARTIFICIAL_MOVEMENT_CHANNEL, message.body()));
  }

  private boolean isSessionOpen(Message<? extends SessionPayload> message) {
    return sessionManager.storage(message.body().getSessionId()).isPresent();
  }

  private void logError(Throwable throwable) {
    LOGGER.error("Got error: ", throwable);
  }

  private Router eventRouter() {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route().consumes("application/json");
    router.route().produces("application/json");

    router.post("/move").handler(webRequestHandler::handleMovement);
    router.post("/allocate_bomb").handler(webRequestHandler::handleBombAllocation);

    return router;
  }

  private void scheduleSessionCleanUpDaemon() {
    vertx.setPeriodic(sessionCleanupPeriodicity, timerId -> sessionManager.cleanup());
  }

  private ErrorHandler errorHandler() {
    return ErrorHandler.create();
  }

  private StaticHandler staticHandler() {
    return StaticHandler.create().setCachingEnabled(false);
  }
}
