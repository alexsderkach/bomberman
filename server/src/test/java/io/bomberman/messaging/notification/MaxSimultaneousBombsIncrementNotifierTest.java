package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.web.event.support.MaxSimultaneousBombsIncrementEvent;
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
public class MaxSimultaneousBombsIncrementNotifierTest {
  private static final String SESSION_ID = "1";
  private static final String USER_PLAYER_ID = "1";
  private static final int MAX_CONCURRENT_BOMBS = 1;
  private static final Player USER_PLAYER = new Player(USER_PLAYER_ID);
  private static final MaxSimultaneousBombsIncrementEvent MAX_SIMULTANEOUS_BOMBS_INCREMENT_EVENT
      = new MaxSimultaneousBombsIncrementEvent(MAX_CONCURRENT_BOMBS);

  @Mock
  private SessionManager sessionManager;
  @Mock
  private Message<SessionPayload> message;
  @Mock
  private SessionStorage sessionStorage;
  @Mock
  private ServerWebSocket serverWebSocket;
  @Mock
  private GameState gameState;

  private MaxSimultaneousBombsIncrementNotifier maxSimultaneousBombsIncrementNotifier;

  @Before
  public void setUp() {
    maxSimultaneousBombsIncrementNotifier = new MaxSimultaneousBombsIncrementNotifier(sessionManager);
  }

  @Test
  public void shouldNotifyClientWithMaxSimultaneousBombsIncrementEventWhenMessageIsPassed() {
    // given
    when(message.body()).thenReturn(new SessionPayload(SESSION_ID));
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(sessionStorage.getGameState()).thenReturn(gameState);
    when(sessionStorage.getServerWebSocket()).thenReturn(serverWebSocket);
    when(gameState.getUserPlayer()).thenReturn(Optional.of(USER_PLAYER));
    when(gameState.getMaxSimultaneousBombs(eq(USER_PLAYER))).thenReturn(MAX_CONCURRENT_BOMBS);

    // when
    maxSimultaneousBombsIncrementNotifier.call(message);

    // then
    verify(serverWebSocket).writeFinalTextFrame(eq(encode(MAX_SIMULTANEOUS_BOMBS_INCREMENT_EVENT)));
  }
}