package io.bomberman.messaging.notification;

import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.web.event.Event;
import io.vertx.rxjava.core.eventbus.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import rx.functions.Action1;

import static io.vertx.core.json.Json.encode;

@RequiredArgsConstructor
abstract class AbstractNotifier<T extends SessionPayload> implements Action1<Message<T>> {

  final SessionManager sessionManager;

  void notify(String sessionId, Event event) {
    sessionManager.storage(sessionId)
        .map(SessionStorage::getServerWebSocket)
        .ifPresent(socket -> {
          try {
            socket.writeFinalTextFrame(encode(event));
          } catch (IllegalStateException ignored) {
            // socket may be disconnected, but session still present
          }
        });
  }
}
