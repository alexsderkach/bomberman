package io.bomberman.service;

import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.factory.GameStateFactory;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.eventbus.Message;
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
public class GameCreationServiceTest {
  private static final String SESSION_ID = "1";
  private static final SessionPayload SESSION_PAYLOAD = new SessionPayload(SESSION_ID);

  @Mock
  private SessionManager sessionManager;
  @Mock
  private SessionStorage sessionStorage;
  @Mock
  private GameStateFactory gameStateFactory;
  @Mock
  private GameState gameState;
  @Mock
  private Message<SessionPayload> message;

  private GameCreationService gameCreationService;

  @Before
  public void setUp() {
    gameCreationService = new GameCreationService(sessionManager, gameStateFactory);
  }

  @Test
  public void shouldSetGameStateWhenHandleIsCalled() {
    //given
    when(message.body()).thenReturn(SESSION_PAYLOAD);
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(gameStateFactory.create()).thenReturn(gameState);

    // when
    gameCreationService.call(message);

    // then
    verify(sessionStorage).setGameState(eq(gameState));
  }
}