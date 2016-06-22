package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.PlayerPayload;
import io.bomberman.model.GameState;
import io.bomberman.web.event.support.PositionModificationEvent;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerPositionModificationNotifier extends AbstractNotifier<PlayerPayload> {

  @Autowired
  public PlayerPositionModificationNotifier(SessionManager sessionManager) {
    super(sessionManager);
  }

  @Override
  public void call(Message<PlayerPayload> message) {
    PlayerPayload payload = message.body();
    String sessionId = payload.getSessionId();
    String playerId = payload.getPlayerId();

    sessionManager.storage(sessionId)
        .map(SessionStorage::getGameState)
        .ifPresent(gameState -> notifyForPlayer(gameState, playerId, sessionId));
  }

  private void notifyForPlayer(GameState gameState, String playerId, String sessionId) {
    gameState.getPlayer(playerId)
        .map(player -> new PositionModificationEvent(player, gameState.getPlayerPosition(player)))
        .ifPresent(event -> notify(sessionId, event));
  }
}
