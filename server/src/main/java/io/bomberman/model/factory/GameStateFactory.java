package io.bomberman.model.factory;

import io.bomberman.model.Cell;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.model.Position;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GameStateFactory {

  private static final Random RANDOM = new Random();
  private static final String USER_PLAYER_ID = "You";
  private static final String FIRST_BOT_ID = "Bot 1";
  private static final String SECOND_BOT_ID = "Bot 2";
  private static final String THIRD_BOT_ID = "Bot 3";

  @Value("${default_map_height}")
  private int defaultMapHeight;
  @Value("${default_map_width}")
  private int defaultMapWidth;
  @Value("${vanishing_block_probability}")
  private int vanishingBlockProbability;
  @Value("${bomb_radius}")
  private int bombRadius;
  @Value("${initial_max_simultaneous_bombs_count}")
  private int initialMaxSimultaneousBombsCount;

  public GameState create() {
    GameState gameState = new GameState(defaultMapWidth, defaultMapHeight);
    createPlayers(gameState);
    fillMap(gameState);
    gameState.setUserPlayerIndex(0);
    gameState.setBombRadius(bombRadius);
    return gameState;
  }

  private void createPlayers(GameState gameState) {
    createPlayer(gameState, 0, 0, USER_PLAYER_ID);
    createPlayer(gameState, 0, gameState.getHeight() - 1, FIRST_BOT_ID);
    createPlayer(gameState, gameState.getWidth() - 1, 0, SECOND_BOT_ID);
    createPlayer(gameState, gameState.getWidth() - 1, gameState.getHeight() - 1, THIRD_BOT_ID);
  }

  private void createPlayer(GameState gameState, int x, int y, String id) {
    Player player = new Player(id);
    gameState.getPlayers().add(player);
    gameState.setPlayerPosition(player, new Position(x, y));
    gameState.setMaxSimultaneousBombs(player, initialMaxSimultaneousBombsCount);
  }

  private void fillMap(GameState gameState) {
    int width = gameState.getWidth();
    int height = gameState.getHeight();
    Cell[][] site = gameState.getSite();
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int randomValue = RANDOM.nextInt(100);
        if (canPlacePermanentBlock(x, y)) {
          site[x][y] = Cell.PERMANENT_BLOCK;
        } else if (canPlaceVanishingBlock(width, height, x, y, randomValue)) {
          site[x][y] = Cell.VANISHING_BLOCK;
        } else {
          site[x][y] = Cell.EMPTY;
        }
      }
    }
  }

  private boolean canPlacePermanentBlock(int x, int y) {
    return (x % 2 == 1) && (y % 2 == 1);
  }

  private boolean canPlaceVanishingBlock(int width, int height, int x, int y, int randomValue) {
    return randomValue < vanishingBlockProbability && !isPlayerPosition(width, height, x, y);
  }

  private boolean isPlayerPosition(int width, int height, int x, int y) {
    return (x == 0 && y == 0) ||
        (x == 0 && y == height - 1) ||
        (x == width - 1 && y == 0 ) ||
        (x == width - 1 && y == height - 1);
  }
}
