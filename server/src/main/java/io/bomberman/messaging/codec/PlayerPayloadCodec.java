package io.bomberman.messaging.codec;

import io.bomberman.messaging.payload.PlayerPayload;
import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;
import org.springframework.stereotype.Component;

import static io.vertx.core.json.Json.encode;

@Component
public class PlayerPayloadCodec extends AbstractPayloadCodec<PlayerPayload> {

  public PlayerPayloadCodec() {
    super(PlayerPayload.class);
  }
}
