package io.bomberman.service.aspect;

import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import org.springframework.stereotype.Component;

@Component
public class BombAllocationFilter {

  public boolean filter(GameState gameState, Player player) {
    return gameState.getBombs().stream()
        .filter(bomb -> bomb.getOwner().equals(player))
        .count() < gameState.getMaxSimultaneousBombs(player);
  }
}
