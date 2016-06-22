package io.bomberman.messaging.codec;

import io.bomberman.messaging.payload.BombPayload;
import org.springframework.stereotype.Component;

@Component
public class BombPayloadCodec extends AbstractPayloadCodec<BombPayload> {

  public BombPayloadCodec() {
    super(BombPayload.class);
  }
}
