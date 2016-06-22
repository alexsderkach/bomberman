package io.bomberman.messaging.consumption;

import io.bomberman.messaging.Channels;
import io.bomberman.messaging.payload.PlayerPayload;
import io.bomberman.messaging.payload.SessionPayload;
import io.bomberman.model.GameState;
import io.bomberman.model.Player;
import io.bomberman.web.session.SessionManager;
import io.bomberman.web.session.SessionStorage;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameCreationEventConsumerTest {

  private static final String SESSION_ID = "1";
  private static final String FIRST_PLAYER_ID = "1";
  private static final String SECOND_PLAYER_ID = "2";
  private static final String THIRD_PLAYER_ID = "3";
  private static final Player FIRST_PLAYER = new Player(FIRST_PLAYER_ID);
  private static final Player SECOND_PLAYER = new Player(SECOND_PLAYER_ID);
  private static final Player THIRD_PLAYER = new Player(THIRD_PLAYER_ID);

  @Mock
  private EventBus eventBus;
  @Mock
  private SessionManager sessionManager;
  @Mock
  private Message<SessionPayload> message;
  @Mock
  private GameState gameState;
  @Mock
  private SessionStorage sessionStorage;

  private GameCreationEventConsumer gameCreationEventConsumer;

  @Before
  public void setUp() {
    gameCreationEventConsumer = new GameCreationEventConsumer(eventBus, sessionManager);
  }

  @Test
  public void shouldPublishToArtificialMovementChannel3TimesWhenMessageIsConsumedAndGameHas3Bots() {
    // given
    when(message.body()).thenReturn(new SessionPayload(SESSION_ID));
    when(sessionManager.storage(eq(SESSION_ID))).thenReturn(Optional.of(sessionStorage));
    when(sessionStorage.getGameState()).thenReturn(gameState);
    when(gameState.getBots()).thenReturn(asList(FIRST_PLAYER, SECOND_PLAYER, THIRD_PLAYER));

    // when
    gameCreationEventConsumer.call(message);

    //then
    verify(eventBus).publish(eq(Channels.ARTIFICIAL_MOVEMENT_CHANNEL), eq(new PlayerPayload(SESSION_ID, FIRST_PLAYER_ID)));
    verify(eventBus).publish(eq(Channels.ARTIFICIAL_MOVEMENT_CHANNEL), eq(new PlayerPayload(SESSION_ID, SECOND_PLAYER_ID)));
    verify(eventBus).publish(eq(Channels.ARTIFICIAL_MOVEMENT_CHANNEL), eq(new PlayerPayload(SESSION_ID, THIRD_PLAYER_ID)));
  }
}