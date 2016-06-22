package io.bomberman.messaging.notification;

import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.bomberman.service.BombActivationService;
import io.bomberman.messaging.payload.BombPayload;
import io.bomberman.web.event.Event;
import io.vertx.rxjava.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BombActivationNotifier extends AbstractNotifier<BombPayload> {

  private final BombActivationService bombActivationService;

  @Autowired
  public BombActivationNotifier(SessionManager sessionManager, BombActivationService bombActivationService) {
    super(sessionManager);
    this.bombActivationService = bombActivationService;
  }

  @Override
  public void call(Message<BombPayload> message) {
    BombPayload payload = message.body();
    String sessionId = payload.getSessionId();
    int x = payload.getX();
    int y = payload.getY();

    sessionManager.storage(sessionId)
        .map(SessionStorage::getGameState)
        .map(state -> bombActivationService.activateBombAndCollectEvents(state, x, y))
        .ifPresent(events -> notifyEvents(events, sessionId));
  }

  private void notifyEvents(List<Event> events, String sessionId) {
    events.stream().forEach(event -> notify(sessionId, event));
  }
}
