package io.bomberman.validation;

import com.google.common.collect.Sets;
import io.bomberman.model.Bomb;
import io.bomberman.model.Cell;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.model.Position;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttemptValidatorTest {

  private static final int WIDTH = 13;
  private static final int HEIGHT = 14;
  private static final Position BOMB_POSITION = new Position(0, 0);
  private static final Player BOMB_OWNER = new Player("1");
  private static final Player SECOND_PLAYER = new Player("2");
  private static final Bomb BOMB = new Bomb(BOMB_OWNER, BOMB_POSITION);
  private static final HashSet<Bomb> BOMBS = Sets.newHashSet(BOMB);

  private AttemptValidator attemptValidator;

  @Mock
  private GameState gameState;

  @Before
  public void setUp() {
    attemptValidator = new AttemptValidator();

    when(gameState.getWidth()).thenReturn(WIDTH);
    when(gameState.getHeight()).thenReturn(HEIGHT);
  }

  @Test
  public void shouldNotMoveToPositionOutsideSite() {
    assertThat(attemptValidator.canMoveTo(gameState, new Position(-1, 0))).isFalse();
    assertThat(attemptValidator.canMoveTo(gameState, new Position(WIDTH, 0))).isFalse();
    assertThat(attemptValidator.canMoveTo(gameState, new Position(0, -1))).isFalse();
    assertThat(attemptValidator.canMoveTo(gameState, new Position(0, HEIGHT))).isFalse();
  }

  @Test
  public void shouldNotMoveToPositionIfThereIsABomb() {
    when(gameState.getBombs()).thenReturn(BOMBS);

    assertThat(attemptValidator.canMoveTo(gameState, BOMB_POSITION)).isFalse();
  }

  @Test
  public void shouldNotMoveToPositionIfItIsPermanentBlock() {
    Position position = new Position(0, 0);
    Cell[][] site = new Cell[WIDTH][HEIGHT];
    site[position.getX()][position.getY()] = Cell.PERMANENT_BLOCK;
    when(gameState.getSite()).thenReturn(site);

    assertThat(attemptValidator.canMoveTo(gameState, position)).isFalse();
  }

  @Test
  public void shouldNotMoveToPositionIfItIsVanishingBlock() {
    Position position = new Position(0, 0);
    Cell[][] site = new Cell[WIDTH][HEIGHT];
    site[position.getX()][position.getY()] = Cell.VANISHING_BLOCK;
    when(gameState.getSite()).thenReturn(site);

    assertThat(attemptValidator.canMoveTo(gameState, position)).isFalse();
  }

  @Test
  public void shouldNotPlaceBombAtPositionOutsideSite() {
    assertThat(attemptValidator.canPlaceBombAt(gameState, BOMB_OWNER, new Position(-1, 0))).isFalse();
    assertThat(attemptValidator.canPlaceBombAt(gameState, BOMB_OWNER, new Position(WIDTH, 0))).isFalse();
    assertThat(attemptValidator.canPlaceBombAt(gameState, BOMB_OWNER, new Position(0, -1))).isFalse();
    assertThat(attemptValidator.canPlaceBombAt(gameState, BOMB_OWNER, new Position(0, HEIGHT))).isFalse();
  }

  @Test
  public void shouldNotPlaceBombAtPositionIfThereIsABomb() {
    when(gameState.getBombs()).thenReturn(BOMBS);

    assertThat(attemptValidator.canPlaceBombAt(gameState, BOMB_OWNER, BOMB_POSITION)).isFalse();
  }

  @Test
  public void shouldNotPlaceBombAtPositionIfItIsPermanentBlock() {
    Position position = new Position(0, 0);
    Cell[][] site = new Cell[WIDTH][HEIGHT];
    site[position.getX()][position.getY()] = Cell.PERMANENT_BLOCK;
    when(gameState.getSite()).thenReturn(site);

    assertThat(attemptValidator.canPlaceBombAt(gameState, BOMB_OWNER, position)).isFalse();
  }

  @Test
  public void shouldNotPlaceBombAtPositionIfItIsVanishingBlock() {
    Position position = new Position(0, 0);
    Cell[][] site = new Cell[WIDTH][HEIGHT];
    site[position.getX()][position.getY()] = Cell.VANISHING_BLOCK;
    when(gameState.getSite()).thenReturn(site);

    assertThat(attemptValidator.canPlaceBombAt(gameState, BOMB_OWNER, position)).isFalse();
  }

  @Test
  public void shouldNotPlaceBombAtPositionIfItHasPlayerOnItNotEqualToOwner() {
    Map<Player, Position> playerPositions = new HashMap<>();
    Position position = new Position(0, 0);
    playerPositions.put(BOMB_OWNER, position);
    playerPositions.put(SECOND_PLAYER, position);

    Cell[][] site = new Cell[WIDTH][HEIGHT];
    site[position.getX()][position.getY()] = Cell.EMPTY;
    when(gameState.getSite()).thenReturn(site);
    when(gameState.getPlayerPositionMap()).thenReturn(playerPositions);

    assertThat(attemptValidator.canPlaceBombAt(gameState, BOMB_OWNER, position)).isFalse();
  }

  @Test
  public void shouldPlaceBombAtPositionIfItHasOnlyMe() {
    Map<Player, Position> playerPositions = new HashMap<>();
    Position myPosition = new Position(0, 0);
    Position nextPosition = new Position(0, 1);
    playerPositions.put(BOMB_OWNER, myPosition);
    playerPositions.put(SECOND_PLAYER, nextPosition);

    Cell[][] site = new Cell[WIDTH][HEIGHT];
    site[myPosition.getX()][myPosition.getY()] = Cell.EMPTY;
    when(gameState.getSite()).thenReturn(site);
    when(gameState.getPlayerPositionMap()).thenReturn(playerPositions);

    assertThat(attemptValidator.canPlaceBombAt(gameState, BOMB_OWNER, myPosition)).isTrue();
  }
}