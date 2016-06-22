package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.BombPayload;
import io.bomberman.model.Position;
import io.bomberman.web.event.support.BombAllocationEvent;
import io.bomberman.web.session.SessionManager;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BombAllocationNotifier extends AbstractNotifier<BombPayload> {

  @Autowired
  public BombAllocationNotifier(SessionManager sessionManager) {
    super(sessionManager);
  }

  @Override
  public void call(Message<BombPayload> message) {
    BombPayload payload = message.body();
    notify(payload.getSessionId(), new BombAllocationEvent(new Position(payload.getX(), payload.getY())));
  }
}
