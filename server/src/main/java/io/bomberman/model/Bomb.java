package io.bomberman.model;

import lombok.Value;

@Value
public class Bomb {
  private final Player owner;
  private final Position position;
}