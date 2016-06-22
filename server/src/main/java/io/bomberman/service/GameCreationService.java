package io.bomberman.service;

import io.bomberman.web.session.SessionManager;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.factory.GameStateFactory;
import io.vertx.rxjava.core.eventbus.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.functions.Action1;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameCreationService implements Action1<Message<SessionPayload>> {

  private final SessionManager sessionManager;
  private final GameStateFactory gameStateFactory;

  @Override
  public void call(Message<SessionPayload> message) {
    String sessionId = message.body().getSessionId();
    sessionManager.storage(sessionId)
        .ifPresent(storage -> storage.setGameState(gameStateFactory.create()));
  }
}
