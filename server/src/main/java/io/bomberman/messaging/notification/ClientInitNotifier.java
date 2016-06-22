package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.web.event.support.ClientInitEvent;
import io.bomberman.web.session.SessionManager;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientInitNotifier extends AbstractNotifier<SessionPayload> {

  @Autowired
  public ClientInitNotifier(SessionManager sessionManager) {
    super(sessionManager);
  }

  @Override
  public void call(Message<SessionPayload> message) {
    String sessionId = message.body().getSessionId();
    notify(sessionId, new ClientInitEvent(sessionId));
  }
}
