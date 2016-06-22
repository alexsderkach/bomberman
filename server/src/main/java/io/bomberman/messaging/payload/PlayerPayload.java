package io.bomberman.messaging.payload;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@Getter
@EqualsAndHashCode(callSuper = true)
public class PlayerPayload extends SessionPayload {
  private String playerId;

  public PlayerPayload(String sessionId, String playerId) {
    super(sessionId);
    this.playerId = playerId;
  }
}
