package io.bomberman.service;

import io.bomberman.model.Position;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.bomberman.messaging.payload.BombPayload;
import io.bomberman.model.Bomb;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.service.aspect.BombAllocationFilter;
import io.bomberman.validation.AttemptValidator;
import io.vertx.rxjava.core.eventbus.EventBus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.bomberman.messaging.Channels.BOMB_ALLOCATION_CHANNEL;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BombAllocationService {
  private final AttemptValidator attemptValidator;
  private final SessionManager sessionManager;
  private final EventBus eventBus;
  private final BombAllocationFilter bombAllocationFilter;

  public void allocate(String sessionId) {
    sessionManager.storage(sessionId)
        .map(SessionStorage::getGameState)
        .ifPresent(state -> allocate(state, state.getUserPlayer().get(), sessionId));
  }

  public void allocate(GameState gameState, Player player, String sessionId) {
    if (bombAllocationFilter.filter(gameState, player) && attemptToAllocateBomb(gameState, player)) {
      Position position = gameState.getPlayerPosition(player);
      eventBus.publish(BOMB_ALLOCATION_CHANNEL, new BombPayload(sessionId, position.getX(), position.getY()));
    }
  }

  private boolean attemptToAllocateBomb(GameState gameState, Player player) {
    Position position = gameState.getPlayerPosition(player);
    if (attemptValidator.canPlaceBombAt(gameState, player, position)) {
      Bomb bomb = new Bomb(player, position);
      gameState.getBombs().add(bomb);
      return true;
    }
    return false;
  }
}
