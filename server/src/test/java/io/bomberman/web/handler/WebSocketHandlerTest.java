package io.bomberman.web.handler;

import io.bomberman.messaging.Channels;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.http.ServerWebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketHandlerTest {
  private static final String SESSION_ID = "1";
  private static final SessionPayload SESSION_PAYLOAD = new SessionPayload(SESSION_ID);

  @Mock
  private SessionStorage sessionStorage;
  @Mock
  private ServerWebSocket serverWebSocket;
  @Mock
  private EventBus eventBus;
  @Mock
  private SessionManager sessionManager;

  private WebSocketHandler webSocketHandler;

  @Before
  public void setUp() {
    webSocketHandler = new WebSocketHandler(sessionManager, eventBus);
  }

  @Test
  public void shouldCreateSessionWhenMessageIsConsumed() {
    // given
    when(sessionManager.create()).thenReturn(SESSION_ID);
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.empty());

    // when
    webSocketHandler.handle(serverWebSocket);

    // then
    verify(sessionManager).create();
  }

  @Test
  public void shouldSetServerWebSocketInSessionStorageWhenMessageIsConsumed() {
    // given
    when(sessionManager.create()).thenReturn(SESSION_ID);
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));

    // when
    webSocketHandler.handle(serverWebSocket);

    // then
    verify(sessionStorage).setServerWebSocket(eq(serverWebSocket));
  }

  @Test
  public void shouldPublishToClientInitChannelWhenMessageIsConsumed() {
    // given
    when(sessionManager.create()).thenReturn(SESSION_ID);
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));

    // when
    webSocketHandler.handle(serverWebSocket);

    // then
    verify(eventBus).publish(eq(Channels.CLIENT_INIT_CHANNEL), eq(SESSION_PAYLOAD));
  }
}