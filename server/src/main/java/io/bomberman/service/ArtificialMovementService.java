package io.bomberman.service;

import io.bomberman.model.Bomb;
import io.bomberman.model.Direction;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.model.Position;
import io.bomberman.validation.AttemptValidator;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import static java.lang.Math.*;
import static java.util.Comparator.comparing;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArtificialMovementService {
  private static final Random RANDOM = new Random();

  private final PlayerMovementService playerMovementService;
  private final BombAllocationService bombAllocationService;
  private final AttemptValidator attemptValidator;
  private final BombActivationService bombActivationService;
  private static final int BOMB_COST = 10;

  public void attemptMove(GameState gameState, Player player, String sessionId) {
    Map<Position, Position> cameFrom = new HashMap<>();
    HashMap<Position, Integer> costSoFar = new HashMap<>();
    PriorityQueue<EstimatedPosition> frontier = new PriorityQueue<>();

    Position playerPosition = gameState.getPlayerPosition(player);
    int currentPositionEstimate = estimate(gameState, player, playerPosition);

    cameFrom.put(playerPosition, null);
    costSoFar.put(playerPosition, currentPositionEstimate);
    frontier.add(new EstimatedPosition(playerPosition, currentPositionEstimate));

    while (!frontier.isEmpty()) {
      EstimatedPosition estimatedPosition = frontier.poll();
      Position currentPosition = estimatedPosition.getPosition();
      Set<Position> nextPositions = getPossibleMoves(gameState, currentPosition);
      for (Position nextPosition : nextPositions) {
        int newEstimate = estimate(gameState, player, nextPosition);
        if (!costSoFar.containsKey(nextPosition) || newEstimate < costSoFar.get(nextPosition)) {
          costSoFar.put(nextPosition, newEstimate);
          frontier.removeIf(position -> position.getPosition().equals(nextPosition));
          frontier.add(new EstimatedPosition(nextPosition, newEstimate));
          cameFrom.put(nextPosition, currentPosition);
        }
      }
    }

    boolean affectedByAnyBombCurrently = isAffectedByAnyBomb(gameState, playerPosition);
    costSoFar.entrySet().stream()
        .sorted((a, b) -> a.getValue().compareTo(b.getValue()))
        .filter(entry -> isPerfect(gameState, sessionId, cameFrom, player, playerPosition, entry.getKey(), affectedByAnyBombCurrently))
        .findFirst();
  }

  private Position getTargetPosition(Map<Position, Position> cameFrom, Position playerPosition, Position bestPosition) {
    Position targetPosition = bestPosition;
    Position temp;
    while (!targetPosition.equals(playerPosition) && !(temp = cameFrom.get(targetPosition)).equals(playerPosition)) {
      targetPosition = temp;
    }
    return targetPosition;
  }

  private boolean isPerfect(GameState gameState,
                            String sessionId,
                            Map<Position, Position> cameFrom,
                            Player player,
                            Position playerPosition,
                            Position bestPosition,
                            boolean affectedByAnyBombCurrently) {
    Position targetPosition = getTargetPosition(cameFrom, playerPosition, bestPosition);
    boolean affectedByAnyBombTarget = isAffectedByAnyBomb(gameState, targetPosition);
    if (!affectedByAnyBombCurrently && affectedByAnyBombTarget) return false;

    getDirection(playerPosition, targetPosition)
        .ifPresent(direction -> playerMovementService.move(gameState, player, direction, sessionId));
    if ((targetPosition.equals(bestPosition) && cameFrom.size() > 2)
        || getClosestEnemyDistance(gameState, player, playerPosition) < 3) {
      bombAllocationService.allocate(gameState, player, sessionId);
    }
    return true;
  }

  private int estimate(GameState gameState, Player player, Position position) {
    double estimate = 0;
    if (isAffectedByAnyBomb(gameState, position)) {
      estimate += BOMB_COST;
    }
    double closestEnemyDistance = getClosestEnemyDistance(gameState, player, position);
    if (closestEnemyDistance == 0) {
      estimate += 100;
    } else {
      estimate += closestEnemyDistance;
    }
    estimate += RANDOM.nextDouble() % 3 - 1;
    return (int) estimate;
  }

  private boolean isAffectedByAnyBomb(GameState gameState, Position position) {
    return gameState.getBombs().stream()
        .filter(bomb -> isAffectedByBomb(gameState, position, bomb))
        .findAny().isPresent();
  }

  private Set<Position> getPossibleMoves(GameState gameState, Position position) {
    Set<Position> possibleMoves = new HashSet<>();
    Position left = new Position(position.getX() - 1, position.getY());
    Position right = new Position(position.getX() + 1, position.getY());
    Position up = new Position(position.getX(), position.getY() - 1);
    Position down = new Position(position.getX(), position.getY() + 1);
    if (attemptValidator.canMoveTo(gameState, left)) {
      possibleMoves.add(left);
    }
    if (attemptValidator.canMoveTo(gameState, right)) {
      possibleMoves.add(right);
    }
    if (attemptValidator.canMoveTo(gameState, up)) {
      possibleMoves.add(up);
    }
    if (attemptValidator.canMoveTo(gameState, down)) {
      possibleMoves.add(down);
    }
    return possibleMoves;
  }


  private Optional<Direction> getDirection(Position playerPosition, Position nextPosition) {
    Direction direction = null;
    if (nextPosition.getX() == playerPosition.getX() - 1) {
      direction = Direction.LEFT;
    } else if (nextPosition.getX() == playerPosition.getX() + 1) {
      direction = Direction.RIGHT;
    } else if (nextPosition.getY() == playerPosition.getY() - 1) {
      direction = Direction.UP;
    } else if (nextPosition.getY() == playerPosition.getY() + 1) {
      direction = Direction.DOWN;
    }
    return Optional.ofNullable(direction);
  }

  private double getClosestEnemyDistance(GameState state, Player player, Position position) {
    return state.getPlayerPositionMap().entrySet().stream()
        .filter(entry -> !entry.getKey().equals(player))
        .map(Map.Entry::getValue)
        .map(p -> calculateDistance(p, position))
        .min(Double::compare)
        .orElse(Double.MIN_VALUE);
  }

  private double calculateDistance(Position a, Position b) {
    return abs(a.getX() - b.getX()) + abs(a.getY() - b.getY());
  }

  private boolean isAffectedByBomb(GameState state, Position position, Bomb bomb) {
    Position bombPosition = bomb.getPosition();
    int bombX = bombPosition.getX();
    int bombY = bombPosition.getY();
    int x = position.getX();
    int y = position.getY();
    int bombRadius = state.getBombRadius();

    return bombActivationService.isPositionAffected(state, bombX, bombY, x, y, bombRadius);
  }

  @Value
  private static class EstimatedPosition implements Comparable<EstimatedPosition> {
    private final Position position;
    private final int estimate;

    @Override
    public int compareTo(EstimatedPosition other) {
      return Integer.compare(estimate, other.estimate);
    }
  }
}
