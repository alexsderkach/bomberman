package io.bomberman.web.aspect;

import io.bomberman.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class HeartbeatService implements WebRequestListener {

  private final SessionManager sessionManager;
  Clock clock = Clock.systemDefaultZone();

  @Override
  public void handle(String sessionId) {
    sessionManager.storage(sessionId)
        .ifPresent(storage -> storage.setLastHeartbeatAt(LocalDateTime.now(clock)));
  }
}
