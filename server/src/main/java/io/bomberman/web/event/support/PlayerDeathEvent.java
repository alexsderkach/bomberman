package io.bomberman.web.event.support;

import io.bomberman.model.Player;
import io.bomberman.web.event.Event;
import io.bomberman.web.event.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import static com.google.common.collect.ImmutableMap.of;

@Value
@EqualsAndHashCode(callSuper = true)
public class PlayerDeathEvent extends Event {
  public PlayerDeathEvent(Player player) {
    super(Type.PLAYER_DEATH, of("player", player));
  }
}
