package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.BombPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.Position;
import io.bomberman.service.BombActivationService;
import io.bomberman.web.event.Event;
import io.bomberman.web.event.support.BombActivationEvent;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.ServerWebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static io.vertx.core.json.Json.encode;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BombActivationNotifierTest {

  private static final String SESSION_ID = "1";
  private static final int BOMB_X_POSITION = 0;
  private static final int BOMB_Y_POSITION = 0;
  private static final Position BOMB_POSITION = new Position(BOMB_X_POSITION, BOMB_Y_POSITION);
  private static final BombActivationEvent BOMB_ACTIVATION_EVENT = new BombActivationEvent(BOMB_POSITION);
  private static final List<Event> BOMB_ACTIVATION_EVENTS = singletonList(BOMB_ACTIVATION_EVENT);

  @Mock
  private SessionManager sessionManager;
  @Mock
  private Message<BombPayload> message;
  @Mock
  private GameState gameState;
  @Mock
  private BombActivationService bombActivationService;
  @Mock
  private SessionStorage sessionStorage;
  @Mock
  private ServerWebSocket serverWebSocket;

  private BombActivationNotifier bombActivationNotifier;

  @Before
  public void setUp() {
    bombActivationNotifier = new BombActivationNotifier(sessionManager, bombActivationService);

    // given
    when(message.body()).thenReturn(new BombPayload(SESSION_ID, BOMB_X_POSITION, BOMB_Y_POSITION));
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(sessionStorage.getGameState()).thenReturn(gameState);
    when(sessionStorage.getServerWebSocket()).thenReturn(serverWebSocket);
    when(bombActivationService.activateBombAndCollectEvents(eq(gameState), eq(BOMB_X_POSITION), eq(BOMB_X_POSITION)))
        .thenReturn(BOMB_ACTIVATION_EVENTS);

    // when
    bombActivationNotifier.call(message);
  }

  @Test
  public void shouldActivateBombAndCollectEventsWhenMessageIsPassed() {
    // then
    verify(bombActivationService).activateBombAndCollectEvents(eq(gameState), eq(BOMB_X_POSITION), eq(BOMB_Y_POSITION));
  }

  @Test
  public void shouldNotifyClientWithEventsWhenEventsAreCollected() {
    // then
    verify(serverWebSocket).writeFinalTextFrame(eq(encode(BOMB_ACTIVATION_EVENT)));
  }
}