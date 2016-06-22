package io.bomberman.web.event.support;

import io.bomberman.model.Position;
import io.bomberman.web.event.Event;
import io.bomberman.web.event.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import static com.google.common.collect.ImmutableMap.of;

@Value
@EqualsAndHashCode(callSuper = true)
public class BombActivationEvent extends Event {
  public BombActivationEvent(Position position) {
    super(Type.BOMB_ACTIVATION, of("position", position));
  }
}
