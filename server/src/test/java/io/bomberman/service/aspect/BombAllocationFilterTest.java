package io.bomberman.service.aspect;

import com.google.common.collect.Sets;
import io.bomberman.model.Bomb;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.model.Position;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BombAllocationFilterTest {
  private static final String PLAYER_ID = "1";
  private static final Player PLAYER = new Player(PLAYER_ID);
  private static final Position FIRST_BOMB_POSITION = new Position(0, 1);
  private static final Position SECOND_BOMB_POSITION = new Position(1, 1);
  private static final Bomb FIRST_BOMB = new Bomb(PLAYER, FIRST_BOMB_POSITION);
  private static final Bomb SECOND_BOMB = new Bomb(PLAYER, SECOND_BOMB_POSITION);

  @Mock
  private GameState gameState;

  private BombAllocationFilter bombAllocationFilter;

  @Before
  public void setUp() {
    bombAllocationFilter = new BombAllocationFilter();
  }

  @Test
  public void shouldReturnFalseWhenPlayerHasPlacedAllowedAmountOfBombs() {
    // given
    when(gameState.getBombs()).thenReturn(Sets.newHashSet(FIRST_BOMB, SECOND_BOMB));
    when(gameState.getMaxSimultaneousBombs(PLAYER)).thenReturn(2);

    // when
    boolean result = bombAllocationFilter.filter(gameState, PLAYER);

    // then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueWhenPlayerHasPlacedBombsLessThanAllowedAmountOfBombs() {
    // given
    when(gameState.getBombs()).thenReturn(Sets.newHashSet(FIRST_BOMB));
    when(gameState.getMaxSimultaneousBombs(PLAYER)).thenReturn(2);

    // when
    boolean result = bombAllocationFilter.filter(gameState, PLAYER);

    // then
    assertThat(result).isTrue();
  }
}