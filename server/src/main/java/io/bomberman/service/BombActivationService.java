package io.bomberman.service;

import io.bomberman.model.Cell;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.model.Position;
import io.bomberman.web.event.Event;
import io.bomberman.web.event.support.BombActivationEvent;
import io.bomberman.web.event.support.CellDisappearanceEvent;
import io.bomberman.web.event.support.PlayerDeathEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BombActivationService {

  public List<Event> activateBombAndCollectEvents(GameState gameState, int x, int y) {
    int fullRadius = gameState.getBombRadius();
    List<Event> events = getAffectedPositions(gameState, x, y, fullRadius).stream()
        .flatMap(p -> getEventsAtPosition(gameState, p).stream())
        .collect(Collectors.toList());

    Position position = new Position(x, y);
    gameState.getBombs().removeIf(bomb -> bomb.getPosition().equals(position));
    events.add(new BombActivationEvent(position));
    return events;
  }

  private List<Position> getAffectedPositions(GameState gameState, int x, int y, int fullRadius) {
    List<Position> positions = new ArrayList<>();
    positions.add(new Position(x, y));
    for (int radius = 1; radius < fullRadius; radius++) {
      if (isPositionAffected(gameState, x, y, x, y - radius, fullRadius)) {
        positions.add(new Position(x, y - radius));
      }
      if (isPositionAffected(gameState, x, y, x, y + radius, fullRadius)) {
        positions.add(new Position(x, y + radius));
      }
      if (isPositionAffected(gameState, x, y, x - radius, y, fullRadius)) {
        positions.add(new Position(x - radius, y));
      }
      if (isPositionAffected(gameState, x, y, x + radius, y, fullRadius)) {
        positions.add(new Position(x + radius, y));
      }
    }
    return positions;
  }

  boolean isPositionAffected(GameState gameState, int bombX, int bombY, int x, int y, int fullRadius) {
    if (x < 0 || y < 0 || x >= gameState.getWidth() || y >= gameState.getHeight()) {
      return false;
    }
    Cell[][] site = gameState.getSite();

    return !((bombX > 0 && site[bombX - 1][bombY].equals(Cell.PERMANENT_BLOCK) && x < bombX)
        || (bombX + 1 < gameState.getWidth() && site[bombX + 1][bombY].equals(Cell.PERMANENT_BLOCK) && x > bombX)
        || (bombY > 0 && site[bombX][bombY - 1].equals(Cell.PERMANENT_BLOCK) && y < bombY)
        || (bombY + 1 < gameState.getHeight() && site[bombX][bombY + 1].equals(Cell.PERMANENT_BLOCK) && y > bombY))
        && !Cell.PERMANENT_BLOCK.equals(site[x][y])
        && Math.abs(bombX - x) < fullRadius
        && Math.abs(bombY - y) < fullRadius;

  }

  private List<Event> getEventsAtPosition(GameState gameState, Position position) {
    List<Event> events = new ArrayList<>();
    Cell[][] site = gameState.getSite();
    Cell cell = site[position.getX()][position.getY()];
    if (cell.equals(Cell.VANISHING_BLOCK)) {
      site[position.getX()][position.getY()] = Cell.EMPTY;
      events.add(new CellDisappearanceEvent(position));
    }

    List<Player> players = gameState.getPlayerPositionMap().entrySet().stream()
        .filter(entry -> entry.getValue().equals(position))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    for (Player player : players) {
      gameState.removePlayer(player);
      events.add(new PlayerDeathEvent(player));
    }
    return events;
  }
}
