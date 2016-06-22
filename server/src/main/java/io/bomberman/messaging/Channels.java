package io.bomberman.messaging;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Channels {
  public static final String CLIENT_INIT_CHANNEL = "client.init";
  public static final String GAME_CREATION_CHANNEL = "game.creation";
  public static final String ARTIFICIAL_MOVEMENT_CHANNEL = "artificial.movement";
  public static final String PLAYER_POSITION_MODIFICATION_CHANNEL = "player.position.modification";
  public static final String BOMB_ALLOCATION_CHANNEL = "bomb.allocation";
  public static final String MAX_SIMULTANEOUS_BOMBS_INCREMENT_CHANNEL = "max.simultaneous.bombs.increment";
}
