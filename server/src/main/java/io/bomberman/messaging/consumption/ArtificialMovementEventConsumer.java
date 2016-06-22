package io.bomberman.messaging.consumption;

import io.bomberman.MainVerticle;
import io.bomberman.service.ArtificialMovementService;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.bomberman.messaging.payload.PlayerPayload;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observer;
import rx.functions.Action1;

@Component
public class ArtificialMovementEventConsumer implements Action1<Message<PlayerPayload>> {

  private final SessionManager sessionManager;
  private final ArtificialMovementService artificialMovementService;

  @Autowired
  public ArtificialMovementEventConsumer(SessionManager sessionManager,
                                         ArtificialMovementService artificialMovementService) {
    this.sessionManager = sessionManager;
    this.artificialMovementService = artificialMovementService;
  }

  @Override
  public void call(Message<PlayerPayload> message) {
    PlayerPayload playerPayload = message.body();
    String sessionId = playerPayload.getSessionId();
    String playerId = playerPayload.getPlayerId();

      sessionManager.storage(sessionId).map(SessionStorage::getGameState)
          .ifPresent(state -> state.getPlayer(playerId)
              .ifPresent(player -> artificialMovementService.attemptMove(state, player, sessionId)));
  }
}
