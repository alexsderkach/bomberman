package io.bomberman.messaging.notification;

import io.bomberman.messaging.payload.BombPayload;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.web.event.support.ClientInitEvent;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.ServerWebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static io.vertx.core.json.Json.encode;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientInitNotifierTest {
  private static final String SESSION_ID = "1";
  private static final ClientInitEvent CLIENT_INIT_EVENT = new ClientInitEvent(SESSION_ID);

  @Mock
  private SessionManager sessionManager;
  @Mock
  private Message<SessionPayload> message;
  @Mock
  private SessionStorage sessionStorage;
  @Mock
  private ServerWebSocket serverWebSocket;

  private ClientInitNotifier clientInitNotifier;

  @Before
  public void setUp() {
    clientInitNotifier = new ClientInitNotifier(sessionManager);
  }

  @Test
  public void shouldNotifyClientWithClientInitEventWhenMessageIsPassed() {
    // given
    when(message.body()).thenReturn(new SessionPayload(SESSION_ID));
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(sessionStorage.getServerWebSocket()).thenReturn(serverWebSocket);

    // when
    clientInitNotifier.call(message);

    // then
    verify(serverWebSocket).writeFinalTextFrame(eq(encode(CLIENT_INIT_EVENT)));
  }
}