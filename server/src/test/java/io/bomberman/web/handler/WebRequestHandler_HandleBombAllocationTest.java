package io.bomberman.web.handler;

import io.bomberman.service.BombAllocationService;
import io.bomberman.service.PlayerMovementService;
import io.bomberman.web.aspect.WebRequestListener;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebRequestHandler_HandleBombAllocationTest {

  private static final String SESSION_ID = "1";
  private static final String BOMB_REQUEST = "{\"sessionId\": \"" + SESSION_ID + "\"}";

  @Mock
  private RoutingContext routingContext;
  @Mock
  private HttpServerResponse response;
  @Mock
  private PlayerMovementService playerMovementService;
  @Mock
  private BombAllocationService bombAllocationService;
  @Mock
  private WebRequestListener someWebRequestListener;

  private WebRequestHandler webRequestHandler;

  @Before
  public void setUp() {
    webRequestHandler = new WebRequestHandler(playerMovementService, singletonList(someWebRequestListener), bombAllocationService);

    // given
    when(routingContext.getBodyAsString()).thenReturn(BOMB_REQUEST);
    when(routingContext.response()).thenReturn(response);
    when(response.setStatusCode(anyInt())).thenReturn(response);
  }


  @Test
  public void shouldForwardToBombAllocationServiceWhenHandleBombAllocationIsCalled() {
    // when
    webRequestHandler.handleBombAllocation(routingContext);

    // then
    verify(bombAllocationService).allocate(eq(SESSION_ID));
  }

  @Test
  public void shouldSet200StatusCodeWhenHandleBombAllocationIsCalled() {
    // when
    webRequestHandler.handleBombAllocation(routingContext);

    // then
    verify(response).setStatusCode(eq(200));
  }

  @Test
  public void shouldNotifyListenersWhenHandleBombAllocationIsCalled() {
    // when
    webRequestHandler.handleBombAllocation(routingContext);

    // then
    verify(someWebRequestListener).handle(eq(SESSION_ID));
  }
}