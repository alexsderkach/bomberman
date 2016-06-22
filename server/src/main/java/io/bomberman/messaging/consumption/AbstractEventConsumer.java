package io.bomberman.messaging.consumption;

import io.bomberman.messaging.payload.SessionPayload;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import rx.functions.Action1;

abstract class AbstractEventConsumer<T extends SessionPayload> implements Action1<Message<T>> {

  private final EventBus eventBus;

  AbstractEventConsumer(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  void publish(String channel, T payload) {
    eventBus.publish(channel, payload);
  }
}
