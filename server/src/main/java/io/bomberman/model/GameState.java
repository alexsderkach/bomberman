package io.bomberman.model;

import io.vertx.core.impl.ConcurrentHashSet;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class GameState implements Cloneable {

  private int bombRadius;
  private int userPlayerIndex;
  private Cell[][] site;
  private int width;
  private int height;
  private List<Player> players;
  private Map<Player, LocalDateTime> playerLastMoveTimeMap;
  private Map<Player, Integer> playerMaxSimultaneousBombsMap;
  private Set<Bomb> bombs;
  private Map<Player, Position> playerPositionMap;

  public GameState(int width, int height) {
    this.players = new ArrayList<>();
    this.site = new Cell[width][height];
    this.width = width;
    this.height = height;
    this.playerLastMoveTimeMap = new ConcurrentHashMap<>();
    this.playerMaxSimultaneousBombsMap = new ConcurrentHashMap<>();
    this.bombs = new ConcurrentHashSet<>();
    this.playerPositionMap = new ConcurrentHashMap<>();
  }

  public LocalDateTime getLastMoveTime(Player player) {
    return playerLastMoveTimeMap.get(player);
  }

  public LocalDateTime setLastMoveTime(Player player, LocalDateTime localDateTime) {
    return playerLastMoveTimeMap.put(player, localDateTime);
  }

  public int getMaxSimultaneousBombs(Player player) {
    return playerMaxSimultaneousBombsMap.get(player);
  }

  public void setMaxSimultaneousBombs(Player player, int count) {
    playerMaxSimultaneousBombsMap.put(player, count);
  }

  public Position getPlayerPosition(Player player) {
    return playerPositionMap.get(player);
  }

  public void setPlayerPosition(Player player, Position position) {
    playerPositionMap.put(player, position);
  }

  public Optional<Player> getPlayer(String id) {
    return players.stream().filter(player -> player.getId().equals(id)).findAny();
  }

  public Optional<Player> getUserPlayer() {
    return Optional.ofNullable(players.size() > userPlayerIndex ? players.get(userPlayerIndex) : null);
  }

  public List<Player> getBots() {
    return getPlayers().subList(1, getPlayers().size());
  }

  public void removePlayer(Player player) {
    players.remove(player);
    playerLastMoveTimeMap.remove(player);
    playerMaxSimultaneousBombsMap.remove(player);
    playerPositionMap.remove(player);
  }
}
