package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.BombPayload;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.factory.GameStateFactory;
import io.bomberman.service.BombActivationService;
import io.bomberman.web.event.support.GameCreationEvent;
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
public class GameCreationNotifierTest {
  private static final String SESSION_ID = "1";
  private static final GameState DUMMY_GAME_STATE = new GameStateFactory().create();
  private static final GameCreationEvent GAME_CREATION_EVENT = new GameCreationEvent(DUMMY_GAME_STATE);

  @Mock
  private SessionManager sessionManager;
  @Mock
  private Message<SessionPayload> message;
  @Mock
  private SessionStorage sessionStorage;
  @Mock
  private ServerWebSocket serverWebSocket;

  private GameCreationNotifier gameCreationNotifier;

  @Before
  public void setUp() {
    gameCreationNotifier = new GameCreationNotifier(sessionManager);
  }

  @Test
  public void shouldNotifyClientWithGameCreationEventWhenMessageIsPassed() {
    // given
    when(message.body()).thenReturn(new SessionPayload(SESSION_ID));
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(sessionStorage.getGameState()).thenReturn(DUMMY_GAME_STATE);
    when(sessionStorage.getServerWebSocket()).thenReturn(serverWebSocket);

    // when
    gameCreationNotifier.call(message);

    // then
    verify(serverWebSocket).writeFinalTextFrame(eq(encode(GAME_CREATION_EVENT)));

  }
}