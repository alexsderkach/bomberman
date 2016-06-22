package io.bomberman.messaging.consumption;

import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
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

import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MaxSimultaneousBombsIncrementEventConsumerTest {
  private static final String SESSION_ID = "1";
  private static final String FIRST_PLAYER_ID = "1";
  private static final String SECOND_PLAYER_ID = "2";
  private static final String THIRD_PLAYER_ID = "3";
  private static final Player FIRST_PLAYER = new Player(FIRST_PLAYER_ID);
  private static final Player SECOND_PLAYER = new Player(SECOND_PLAYER_ID);
  private static final Player THIRD_PLAYER = new Player(THIRD_PLAYER_ID);
  private static final int INITIAL_MAX_SIMULTANEOUS_BOMBS_COUNT = 1;
  private static final int EXPECTED_MAX_SIMULTANEOUS_BOMBS_COUNT = 2;

  @Mock
  private EventBus eventBus;
  @Mock
  private SessionManager sessionManager;
  @Mock
  private Message<SessionPayload> message;
  @Mock
  private GameState gameState;
  @Mock
  private SessionStorage sessionStorage;

  private MaxSimultaneousBombsIncrementEventConsumer maxSimultaneousBombsIncrementEventConsumer;

  @Before
  public void setUp() {
    maxSimultaneousBombsIncrementEventConsumer = new MaxSimultaneousBombsIncrementEventConsumer(eventBus, sessionManager);
  }

  @Test
  public void shouldIncreaseMaxSimultaneousBombsFor3PlayersBy1WhenMessageIsConsumedAndGameHas3Players() {
    // given
    when(message.body()).thenReturn(new SessionPayload(SESSION_ID));
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(sessionStorage.getGameState()).thenReturn(gameState);
    when(gameState.getPlayers()).thenReturn(asList(FIRST_PLAYER, SECOND_PLAYER, THIRD_PLAYER));
    when(gameState.getMaxSimultaneousBombs(eq(FIRST_PLAYER))).thenReturn(INITIAL_MAX_SIMULTANEOUS_BOMBS_COUNT);
    when(gameState.getMaxSimultaneousBombs(eq(SECOND_PLAYER))).thenReturn(INITIAL_MAX_SIMULTANEOUS_BOMBS_COUNT);
    when(gameState.getMaxSimultaneousBombs(eq(THIRD_PLAYER))).thenReturn(INITIAL_MAX_SIMULTANEOUS_BOMBS_COUNT);

    // when
    maxSimultaneousBombsIncrementEventConsumer.call(message);

    //then
    verify(gameState).setMaxSimultaneousBombs(FIRST_PLAYER, EXPECTED_MAX_SIMULTANEOUS_BOMBS_COUNT);
    verify(gameState).setMaxSimultaneousBombs(SECOND_PLAYER, EXPECTED_MAX_SIMULTANEOUS_BOMBS_COUNT);
    verify(gameState).setMaxSimultaneousBombs(THIRD_PLAYER, EXPECTED_MAX_SIMULTANEOUS_BOMBS_COUNT);
  }
}