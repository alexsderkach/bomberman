package io.bomberman.web.session;

import io.bomberman.web.aspect.SessionCleanupListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SessionManagerTest {

  private static final Duration SESSION_TTL = Duration.ofSeconds(5);
  private static final Clock FIXED_CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());
  private SessionManager sessionManager;

  @Mock
  private SessionCleanupListener someSessionCleanupListener;

  @Before
  public void setUp() {
    sessionManager = new SessionManager(singletonList(someSessionCleanupListener));
    sessionManager.clock = FIXED_CLOCK;
    sessionManager.sessionTimeToLive = SESSION_TTL;
  }

  @Test
  public void shouldGenerateUniqueSessionIdWhenCalledCreateMultipleTimes() {
    // when
    String firstId = sessionManager.create();
    String secondId = sessionManager.create();

    // then
    assertThat(firstId).isNotEqualTo(secondId);
  }

  @Test
  public void shouldInitSessionStoreWhenCalledCreate() {
    // when
    String sessionId = sessionManager.create();

    // then
    assertThat(sessionManager.storage(sessionId).isPresent()).isTrue();
  }

  @Test
  public void shouldAssignCurrentTimeToLastHeartbeatAtWhenCalledCreate() {
    // when
    String sessionId = sessionManager.create();

    // then
    LocalDateTime lastHeartbeat = sessionManager.storage(sessionId).map(SessionStorage::getLastHeartbeatAt).get();
    assertThat(lastHeartbeat).isEqualTo(LocalDateTime.now(FIXED_CLOCK));
  }

  @Test
  public void shouldRemoveSessionWhenSessionTTLHasPassed() {
    // given
    String sessionId = sessionManager.create();
    LocalDateTime lastHeartbeatAt = LocalDateTime.now(FIXED_CLOCK).minus(SESSION_TTL).minusNanos(1);
    sessionManager.storage(sessionId).get().setLastHeartbeatAt(lastHeartbeatAt);

    // when
    sessionManager.cleanup();

    // then
    assertThat(sessionManager.storage(sessionId).isPresent()).isFalse();
  }

  @Test
  public void shouldCallSessionCleanupListenersWhenSessionIsRemoved() {
    // given
    String sessionId = sessionManager.create();
    LocalDateTime lastHeartbeatAt = LocalDateTime.now(FIXED_CLOCK).minus(SESSION_TTL).minusNanos(1);
    SessionStorage sessionStorage = sessionManager.storage(sessionId).get();
    sessionStorage.setLastHeartbeatAt(lastHeartbeatAt);

    // when
    sessionManager.cleanup();

    // then
    verify(someSessionCleanupListener).handle(eq(sessionStorage));
  }
}