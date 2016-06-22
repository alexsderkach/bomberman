package io.bomberman.messaging.consumption;

import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.GameState;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaxSimultaneousBombsIncrementEventConsumer extends AbstractEventConsumer<SessionPayload> {

  private final SessionManager sessionManager;

  @Autowired
  public MaxSimultaneousBombsIncrementEventConsumer(EventBus eventBus, SessionManager sessionManager) {
    super(eventBus);
    this.sessionManager = sessionManager;
  }

  @Override
  public void call(Message<SessionPayload> message) {
    SessionPayload sessionPayload = message.body();
    String sessionId = sessionPayload.getSessionId();
    sessionManager.storage(sessionId)
        .map(SessionStorage::getGameState)
        .ifPresent(this::increaseMaxConcurrentBombCount);
  }

  private void increaseMaxConcurrentBombCount(GameState gameState) {
    gameState.getPlayers().stream()
        .forEach(player -> gameState.setMaxSimultaneousBombs(player, gameState.getMaxSimultaneousBombs(player) + 1));
  }
}
