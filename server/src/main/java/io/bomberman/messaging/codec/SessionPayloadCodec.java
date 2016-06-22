package io.bomberman.messaging.codec;

import io.bomberman.messaging.payload.SessionPayload;
import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;
import org.springframework.stereotype.Component;

import static com.google.common.collect.ImmutableMap.of;
import static io.vertx.core.json.Json.encode;

@Component
public class SessionPayloadCodec extends AbstractPayloadCodec<SessionPayload> {

  public SessionPayloadCodec() {
    super(SessionPayload.class);
  }
}
