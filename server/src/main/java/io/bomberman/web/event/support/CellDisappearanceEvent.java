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
public class CellDisappearanceEvent extends Event {
  public CellDisappearanceEvent(Position position) {
    super(Type.CELL_DISAPPEARANCE, of("position", position));
  }
}
