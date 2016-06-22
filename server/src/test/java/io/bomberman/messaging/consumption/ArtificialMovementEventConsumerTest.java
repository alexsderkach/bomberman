package io.bomberman.messaging.consumption;

import io.bomberman.messaging.payload.PlayerPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.service.ArtificialMovementService;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArtificialMovementEventConsumerTest {

  private static final String SESSION_ID = "1";
  private static final String PLAYER_ID = "2";
  private static final String DUMMY_PLAYER_ID = "1";
  private static final Player DUMMY_PLAYER = new Player(DUMMY_PLAYER_ID);

  @Mock
  private SessionManager sessionManager;
  @Mock
  private ArtificialMovementService artificialMovementService;
  @Mock
  private Message<PlayerPayload> message;
  @Mock
  private GameState gameState;
  @Mock
  private SessionStorage sessionStorage;

  private ArtificialMovementEventConsumer artificialMovementEventConsumer;

  @Before
  public void setUp() {
    artificialMovementEventConsumer = new ArtificialMovementEventConsumer(sessionManager, artificialMovementService);
  }

  @Test
  public void shouldAttemptToMovePlayerWhenMessageIsConsumed() {
    // given
    when(message.body()).thenReturn(new PlayerPayload(SESSION_ID, PLAYER_ID));
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(sessionStorage.getGameState()).thenReturn(gameState);
    when(gameState.getPlayer(eq(PLAYER_ID))).thenReturn(Optional.of(DUMMY_PLAYER));

    // when
    artificialMovementEventConsumer.call(message);

    // then
    verify(artificialMovementService).attemptMove(eq(gameState), eq(DUMMY_PLAYER), eq(SESSION_ID));
  }
}