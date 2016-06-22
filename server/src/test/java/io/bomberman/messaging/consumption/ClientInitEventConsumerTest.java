package io.bomberman.messaging.consumption;

import io.bomberman.messaging.Channels;
import io.bomberman.messaging.payload.SessionPayload;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientInitEventConsumerTest {
  private static final String SESSION_ID = "1";

  @Mock
  private EventBus eventBus;
  @Mock
  private Message<SessionPayload> message;

  private ClientInitEventConsumer clientInitEventConsumer;

  @Before
  public void setUp() {
    clientInitEventConsumer = new ClientInitEventConsumer(eventBus);
  }

  @Test
  public void shouldPublishToGameCreateChannelWhenMessageIsConsumed() {
    // given
    when(message.body()).thenReturn(new SessionPayload(SESSION_ID));

    // when
    clientInitEventConsumer.call(message);

    // then
    verify(eventBus).publish(eq(Channels.GAME_CREATION_CHANNEL), eq(new SessionPayload(SESSION_ID)));
  }
}