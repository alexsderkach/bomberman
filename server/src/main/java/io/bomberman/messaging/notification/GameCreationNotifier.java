package io.bomberman.messaging.notification;

import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.web.event.support.GameCreationEvent;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameCreationNotifier extends AbstractNotifier<SessionPayload> {

  @Autowired
  public GameCreationNotifier(SessionManager sessionManager) {
    super(sessionManager);
  }

  @Override
  public void call(Message<SessionPayload> message) {
    String sessionId = message.body().getSessionId();
    sessionManager.storage(sessionId)
        .map(SessionStorage::getGameState)
        .map(GameCreationEvent::new)
        .ifPresent(event -> notify(sessionId, event));
  }
}
