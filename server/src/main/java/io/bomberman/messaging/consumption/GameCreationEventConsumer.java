package io.bomberman.messaging.consumption;

import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.bomberman.messaging.payload.PlayerPayload;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.bomberman.messaging.Channels.ARTIFICIAL_MOVEMENT_CHANNEL;

@Component
public class GameCreationEventConsumer extends AbstractEventConsumer<SessionPayload> {

  private final SessionManager sessionManager;

  @Autowired
  public GameCreationEventConsumer(EventBus eventBus, SessionManager sessionManager) {
    super(eventBus);
    this.sessionManager = sessionManager;
  }

  @Override
  public void call(Message<SessionPayload> message) {
    SessionPayload sessionPayload = message.body();
    String sessionId = sessionPayload.getSessionId();
    sessionManager.storage(sessionId)
        .map(SessionStorage::getGameState)
        .map(GameState::getBots)
        .ifPresent(bots -> launchBots(bots, sessionId));
  }

  private void launchBots(List<Player> bots, String sessionId) {
    bots.stream()
        .map(player -> new PlayerPayload(sessionId, player.getId()))
        .forEach(payload -> publish(ARTIFICIAL_MOVEMENT_CHANNEL, payload));
  }
}
