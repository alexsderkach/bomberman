package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.PlayerPayload;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.model.Position;
import io.bomberman.web.event.support.MaxSimultaneousBombsIncrementEvent;
import io.bomberman.web.event.support.PositionModificationEvent;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.ServerWebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static io.vertx.core.json.Json.encode;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlayerPositionModificationNotifierTest {
  private static final String SESSION_ID = "1";
  private static final String PLAYER_ID = "1";
  private static final Player PLAYER = new Player(PLAYER_ID);
  private static final int PLAYER_X_POSITION = 0;
  private static final int PLAYER_Y_POSITION = 0;
  private static final Position NEW_PLAYER_POSITION = new Position(PLAYER_X_POSITION, PLAYER_Y_POSITION);
  private static final PositionModificationEvent POSITION_MODIFICATION_EVENT
      = new PositionModificationEvent(PLAYER, NEW_PLAYER_POSITION);

  @Mock
  private SessionManager sessionManager;
  @Mock
  private Message<PlayerPayload> message;
  @Mock
  private SessionStorage sessionStorage;
  @Mock
  private ServerWebSocket serverWebSocket;
  @Mock
  private GameState gameState;

  private PlayerPositionModificationNotifier playerPositionModificationNotifier;

  @Before
  public void setUp() {
    playerPositionModificationNotifier = new PlayerPositionModificationNotifier(sessionManager);
  }

  @Test
  public void shouldNotifyClientWithPlayerPositionModificationEventWhenMessageIsPassed() {
    // given
    when(message.body()).thenReturn(new PlayerPayload(SESSION_ID, PLAYER_ID));
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(sessionStorage.getGameState()).thenReturn(gameState);
    when(sessionStorage.getServerWebSocket()).thenReturn(serverWebSocket);
    when(gameState.getPlayer(eq(PLAYER_ID))).thenReturn(Optional.of(PLAYER));
    when(gameState.getPlayerPosition(eq(PLAYER))).thenReturn(NEW_PLAYER_POSITION);

    // when
    playerPositionModificationNotifier.call(message);

    // then
    verify(serverWebSocket).writeFinalTextFrame(eq(encode(POSITION_MODIFICATION_EVENT)));
  }
}