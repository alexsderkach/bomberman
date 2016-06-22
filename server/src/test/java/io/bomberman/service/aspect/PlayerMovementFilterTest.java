package io.bomberman.service.aspect;

import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlayerMovementFilterTest {
  private static final String PLAYER_ID = "1";
  private static final Player PLAYER = new Player(PLAYER_ID);
  private static final Duration MIN_REQUEST_INTERVAL = Duration.ofSeconds(5);
  private static final Clock FIXED_CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());
  private PlayerMovementFilter playerMovementFilter;

  @Mock
  private GameState gameState;

  @Before
  public void setUp() {
    playerMovementFilter = new PlayerMovementFilter();
    playerMovementFilter.minRequestInterval = MIN_REQUEST_INTERVAL;
    playerMovementFilter.clock = FIXED_CLOCK;
  }

  @Test
  public void shouldReturnTrueAndSetPlayerLastMoveAtCurrentTimeWhenPlayerLastMoveTimeIsNull() {
    // given
    when(gameState.getLastMoveTime(eq(PLAYER))).thenReturn(null);

    // when
    boolean result = playerMovementFilter.filter(gameState, PLAYER);

    // then
    assertThat(result).isTrue();
    verify(gameState).setLastMoveTime(eq(PLAYER), eq(LocalDateTime.now(FIXED_CLOCK)));
  }

  @Test
  public void shouldReturnTrueAndSetPlayerLastMoveAtCurrentTimeWhenPlayerLastMoveTimeIntervalHasOverPassed() {
    // given
    LocalDateTime playerLastMoveTime = LocalDateTime.now(FIXED_CLOCK).minus(MIN_REQUEST_INTERVAL).minusNanos(1);
    when(gameState.getLastMoveTime(eq(PLAYER))).thenReturn(playerLastMoveTime);

    // when
    boolean result = playerMovementFilter.filter(gameState, PLAYER);

    // then
    assertThat(result).isTrue();
    verify(gameState).setLastMoveTime(eq(PLAYER), eq(LocalDateTime.now(FIXED_CLOCK)));
  }
}