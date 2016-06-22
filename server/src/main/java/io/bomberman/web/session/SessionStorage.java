package io.bomberman.web.session;

import io.bomberman.model.GameState;
import io.vertx.rxjava.core.http.ServerWebSocket;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class SessionStorage {
  private ServerWebSocket serverWebSocket;
  private GameState gameState;
  private LocalDateTime lastHeartbeatAt;
  private LocalDateTime lastMoveRequestAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SessionStorage that = (SessionStorage) o;
    return Objects.equals(serverWebSocket, that.serverWebSocket);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serverWebSocket);
  }
}
