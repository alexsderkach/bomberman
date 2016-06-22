package io.bomberman.model;

public enum Direction {
  LEFT, RIGHT, UP, DOWN;

  public static Direction of(String direction) {
    switch (direction) {
      case "left":
        return LEFT;
      case "right":
        return RIGHT;
      case "up":
        return UP;
      case "down":
        return DOWN;
      default:
        throw new RuntimeException("Invalid direction: " + direction);
    }
  }
}
