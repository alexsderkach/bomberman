package io.bomberman.web.aspect;

import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.http.ServerWebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketCleanupListenerTest {

  private WebSocketCleanupListener webSocketCleanupListener;

  @Mock
  private ServerWebSocket serverWebSocket;

  @Before
  public void setUp() {
    webSocketCleanupListener = new WebSocketCleanupListener();
  }

  @Test
  public void shouldCloseWebSocketWhenHandleIsCalled() {
    // given
    SessionStorage sessionStorage = new SessionStorage();
    sessionStorage.setServerWebSocket(serverWebSocket);

    // when
    webSocketCleanupListener.handle(sessionStorage);

    // then
    verify(serverWebSocket).close();
  }
}