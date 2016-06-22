package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.BombPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.Position;
import io.bomberman.web.event.support.BombActivationEvent;
import io.bomberman.web.event.support.BombAllocationEvent;
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
public class BombAllocationNotifierTest {
  private static final String SESSION_ID = "1";
  private static final int BOMB_X_POSITION = 0;
  private static final int BOMB_Y_POSITION = 0;
  private static final Position BOMB_POSITION = new Position(BOMB_X_POSITION, BOMB_Y_POSITION);
  private static final BombAllocationEvent BOMB_ALLOCATION_EVENT = new BombAllocationEvent(BOMB_POSITION);

  @Mock
  private SessionManager sessionManager;
  @Mock
  private Message<BombPayload> message;
  @Mock
  private GameState gameState;
  @Mock
  private SessionStorage sessionStorage;
  @Mock
  private ServerWebSocket serverWebSocket;

  private BombAllocationNotifier bombAllocationNotifier;

  @Before
  public void setUp() {
    bombAllocationNotifier = new BombAllocationNotifier(sessionManager);
  }

  @Test
  public void shouldNotifyClientWithBombActivationEventWhenMessageIsPassed() {
    // given
    when(message.body()).thenReturn(new BombPayload(SESSION_ID, BOMB_X_POSITION, BOMB_Y_POSITION));
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(sessionStorage.getServerWebSocket()).thenReturn(serverWebSocket);

    // when
    bombAllocationNotifier.call(message);

    // then
    verify(serverWebSocket).writeFinalTextFrame(eq(encode(BOMB_ALLOCATION_EVENT)));
  }
}