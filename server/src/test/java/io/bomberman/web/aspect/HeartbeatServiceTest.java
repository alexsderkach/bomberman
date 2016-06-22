package io.bomberman.web.aspect;

import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HeartbeatServiceTest {
  private static final String SESSION_ID = "1";
  private static final Clock FIXED_CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());

  @Mock
  private SessionManager sessionManager;
  @Mock
  private SessionStorage sessionStorage;

  private HeartbeatService heartbeatService;

  @Before
  public void setUp() {
    heartbeatService = new HeartbeatService(sessionManager);
    heartbeatService.clock = FIXED_CLOCK;
  }

  @Test
  public void shouldSetLastHeartbeatToCurrentTimeWhenHandleIsCalled() {
    // given
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));

    // when
    heartbeatService.handle(SESSION_ID);

    //then
    verify(sessionStorage).setLastHeartbeatAt(eq(LocalDateTime.now(FIXED_CLOCK)));;

  }
}