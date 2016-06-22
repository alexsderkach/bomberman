package io.bomberman.web.handler;

import io.bomberman.web.session.SessionManager;
import io.bomberman.messaging.payload.SessionPayload;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.http.ServerWebSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.bomberman.messaging.Channels.CLIENT_INIT_CHANNEL;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSocketHandler implements Handler<ServerWebSocket> {
  private final SessionManager sessionManager;
  private final EventBus eventBus;

  @Override
  public void handle(ServerWebSocket serverWebSocket) {
    String sessionId = sessionManager.create();
    sessionManager.storage(sessionId)
        .ifPresent(sessionStorage -> {
          sessionStorage.setServerWebSocket(serverWebSocket);
          eventBus.publish(CLIENT_INIT_CHANNEL, new SessionPayload(sessionId));
        });
  }
}
