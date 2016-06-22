package io.bomberman.web.event.support;

import io.bomberman.model.GameState;
import io.bomberman.web.event.Event;
import io.bomberman.web.event.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import static com.google.common.collect.ImmutableMap.of;

@Value
@EqualsAndHashCode(callSuper = true)
public class GameCreationEvent extends Event {
  public GameCreationEvent(GameState gameState) {
    super(Type.GAME_CREATION, of("state", gameState));
  }
}
