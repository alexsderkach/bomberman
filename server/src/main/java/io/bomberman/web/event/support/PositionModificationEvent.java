package io.bomberman.web.event.support;

import io.bomberman.model.Player;
import io.bomberman.model.Position;
import io.bomberman.web.event.Event;
import io.bomberman.web.event.Type;
import io.vertx.core.json.JsonObject;
import javafx.geometry.Pos;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import static com.google.common.collect.ImmutableMap.of;

@Value
@EqualsAndHashCode(callSuper = true)
public class PositionModificationEvent extends Event {
  public PositionModificationEvent(Player player, Position position) {
    super(Type.PLAYER_POSITION_MODIFICATION, of("player", player, "position", position));
  }
}
