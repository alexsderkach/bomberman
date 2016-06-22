package io.bomberman.messaging.payload;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@Getter
@EqualsAndHashCode(callSuper = true)
public class BombPayload extends SessionPayload {
  private final int x;
  private final int y;

  public BombPayload(String sessionId, int x, int y) {
    super(sessionId);
    this.x = x;
    this.y = y;
  }
}
