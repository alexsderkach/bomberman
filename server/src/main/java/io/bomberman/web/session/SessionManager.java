package io.bomberman.web.session;

import io.bomberman.web.aspect.SessionCleanupListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SessionManager {

  @Value("#{T(java.time.Duration).ofMillis(${session_ttl})}")
  Duration sessionTimeToLive;
  Clock clock = Clock.systemDefaultZone();

  private final Map<String, SessionStorage> sessions = new ConcurrentHashMap<>();
  private final List<SessionCleanupListener> sessionCleanupListeners;

  public String create() {
    String sessionId = generateSessionId();
    SessionStorage storage = new SessionStorage();
    storage.setLastHeartbeatAt(LocalDateTime.now(clock));
    sessions.put(sessionId, storage);
    return sessionId;
  }

  public Optional<SessionStorage> storage(String sessionId) {
    return Optional.ofNullable(sessions.get(sessionId));
  }

  public void cleanup() {
    Iterator<SessionStorage> iterator = sessions.values().iterator();
    LocalDateTime lowerTimeBound = LocalDateTime.now(clock).minus(sessionTimeToLive);
    while (iterator.hasNext()) {
      SessionStorage sessionStorage = iterator.next();
      if (lowerTimeBound.isAfter(sessionStorage.getLastHeartbeatAt())) {
        notifyListeners(sessionStorage);
        iterator.remove();
      }
    }
  }

  private static String generateSessionId() {
    return UUID.randomUUID().toString();
  }

  private void notifyListeners(SessionStorage sessionStorage) {
    sessionCleanupListeners.stream().forEach(listener -> listener.handle(sessionStorage));
  }
}
