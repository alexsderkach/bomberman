package io.bomberman.web.event.support;

import io.bomberman.model.Position;
import io.bomberman.web.event.Event;
import io.bomberman.web.event.Type;
import io.vertx.core.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import static com.google.common.collect.ImmutableMap.of;

@Value
@EqualsAndHashCode(callSuper = true)
public class BombAllocationEvent extends Event {
  public BombAllocationEvent(Position position) {
    super(Type.BOMB_ALLOCATION, of("position", position));
  }
}
