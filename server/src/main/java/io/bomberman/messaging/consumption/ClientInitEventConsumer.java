package io.bomberman.messaging.consumption;

import io.bomberman.messaging.payload.SessionPayload;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.bomberman.messaging.Channels.GAME_CREATION_CHANNEL;

@Component
public class ClientInitEventConsumer extends AbstractEventConsumer<SessionPayload> {

  @Autowired
  public ClientInitEventConsumer(EventBus eventBus) {
    super(eventBus);
  }

  @Override
  public void call(Message<SessionPayload> message) {
    String sessionId = message.body().getSessionId();
    publish(GAME_CREATION_CHANNEL, new SessionPayload(sessionId));
  }
}
