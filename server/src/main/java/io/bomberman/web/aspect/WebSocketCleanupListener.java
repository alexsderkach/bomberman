package io.bomberman.web.aspect;

import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.http.ServerWebSocket;
import org.springframework.stereotype.Component;

@Component
public class WebSocketCleanupListener implements SessionCleanupListener {
  @Override
  public void handle(SessionStorage sessionStorage) {
    ServerWebSocket socket = sessionStorage.getServerWebSocket();
    try {
      socket.close();
    } catch (IllegalStateException ignored) {
      // socket may be disconnected, but session still present
    }
  }
}
