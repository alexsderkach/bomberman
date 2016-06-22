package io.bomberman.validation;

import io.bomberman.model.Bomb;
import io.bomberman.model.Cell;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.model.Position;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AttemptValidator {
  public boolean canMoveTo(GameState gameState, Position position) {
    if (position.getX() < 0 || position.getY() < 0
        || position.getX() >= gameState.getWidth() || position.getY() >= gameState.getHeight()) {
      return false;
    }

    Optional<Bomb> someBomb = gameState.getBombs().stream()
        .filter(bomb -> bomb.getPosition().equals(position))
        .findFirst();
    if (someBomb.isPresent()) {
      return false;
    }

    Cell cell = gameState.getSite()[position.getX()][position.getY()];
    return !(cell.equals(Cell.PERMANENT_BLOCK) || cell.equals(Cell.VANISHING_BLOCK));
  }

  public boolean canPlaceBombAt(GameState gameState, Player player, Position position) {
    return canMoveTo(gameState, position) &&
        !gameState.getPlayerPositionMap().entrySet().stream()
            .filter(entry -> !entry.getKey().equals(player))
            .filter(entry -> entry.getValue().equals(position))
            .findAny()
            .isPresent();
  }
}
