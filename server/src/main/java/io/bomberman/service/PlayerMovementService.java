package io.bomberman.service;

import io.bomberman.model.Position;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.bomberman.messaging.payload.PlayerPayload;
import io.bomberman.model.Direction;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.service.aspect.PlayerMovementFilter;
import io.bomberman.validation.AttemptValidator;
import io.vertx.rxjava.core.eventbus.EventBus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.bomberman.messaging.Channels.PLAYER_POSITION_MODIFICATION_CHANNEL;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlayerMovementService {
  private final AttemptValidator attemptValidator;
  private final SessionManager sessionManager;
  private final EventBus eventBus;
  private final PlayerMovementFilter playerMovementFilter;

  public void move(String sessionId, Direction direction) {
    sessionManager.storage(sessionId)
        .map(SessionStorage::getGameState)
        .ifPresent(state -> move(state, state.getUserPlayer().get(), direction, sessionId));
  }

  void move(GameState gameState, Player player, Direction direction, String sessionId) {
    if (playerMovementFilter.filter(gameState, player) && attemptMove(gameState, player, direction)) {
      eventBus.publish(PLAYER_POSITION_MODIFICATION_CHANNEL, new PlayerPayload(sessionId, player.getId()));
    }
  }

  private boolean attemptMove(GameState gameState, Player player, Direction direction) {
    Position position = gameState.getPlayerPosition(player);
    if (position == null) {
      return false;
    }
    Position newPosition = null;
    switch (direction) {
      case LEFT:
        newPosition = new Position(position.getX() - 1, position.getY());
        break;
      case RIGHT:
        newPosition = new Position(position.getX() + 1, position.getY());
        break;
      case UP:
        newPosition = new Position(position.getX(), position.getY() - 1);
        break;
      case DOWN:
        newPosition = new Position(position.getX(), position.getY() + 1);
        break;
    }
    if (attemptValidator.canMoveTo(gameState, newPosition)) {
      gameState.setPlayerPosition(player, newPosition);
      return true;
    }
    return false;
  }
}
