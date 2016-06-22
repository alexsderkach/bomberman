package io.bomberman.service.aspect;

import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class PlayerMovementFilter {
  @Value("#{T(java.time.Duration).ofMillis(${min_movement_request_interval})}")
  Duration minRequestInterval;
  Clock clock = Clock.systemDefaultZone();

  public boolean filter(GameState gameState, Player player) {
    LocalDateTime now = LocalDateTime.now(clock);
    LocalDateTime lastMoveAt = gameState.getLastMoveTime(player);
    if (lastMoveAt == null ||
        lastMoveAt.plus(minRequestInterval).isBefore(now)) {
      gameState.setLastMoveTime(player, now);
      return true;
    }
    return false;
  }
}
