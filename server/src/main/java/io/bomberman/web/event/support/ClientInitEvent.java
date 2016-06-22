package io.bomberman.web.event.support;

import io.bomberman.web.event.Event;
import io.bomberman.web.event.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import static com.google.common.collect.ImmutableMap.of;

@Value
@EqualsAndHashCode(callSuper = true)
public class ClientInitEvent extends Event {
  public ClientInitEvent(String sessionId) {
    super(Type.CLIENT_INIT, of("sessionId", sessionId));
  }
}
