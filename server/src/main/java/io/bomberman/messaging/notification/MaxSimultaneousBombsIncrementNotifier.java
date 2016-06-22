package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.GameState;
import io.bomberman.web.event.support.MaxSimultaneousBombsIncrementEvent;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaxSimultaneousBombsIncrementNotifier extends AbstractNotifier<SessionPayload> {

  @Autowired
  public MaxSimultaneousBombsIncrementNotifier(SessionManager sessionManager) {
    super(sessionManager);
  }

  @Override
  public void call(Message<SessionPayload> message) {
    String sessionId = message.body().getSessionId();
    sessionManager.storage(sessionId).map(SessionStorage::getGameState)
        .ifPresent(state -> notifyMaxSimultaneousBombsIncrease(state, sessionId));
  }

  private void notifyMaxSimultaneousBombsIncrease(GameState state, String sessionId) {
    state.getUserPlayer()
        .map(state::getMaxSimultaneousBombs)
        .ifPresent(count -> notify(sessionId, new MaxSimultaneousBombsIncrementEvent(count)));
  }
}
