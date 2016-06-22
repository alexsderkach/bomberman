package io.bomberman.web.event;

public enum Type {
  CLIENT_INIT,
  GAME_CREATION,
  PLAYER_POSITION_MODIFICATION,
  BOMB_ALLOCATION,
  BOMB_ACTIVATION,
  PLAYER_DEATH,
  CELL_DISAPPEARANCE,
  MAX_SIMULTANEOUS_BOMBS_INCREMENT
}
