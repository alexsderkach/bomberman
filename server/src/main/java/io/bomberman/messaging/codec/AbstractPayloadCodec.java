package io.bomberman.messaging.codec;

import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

import static io.vertx.core.json.Json.encode;

abstract class AbstractPayloadCodec<T> implements MessageCodec<T, T> {

  private final Class<? extends T> typeToken;

  AbstractPayloadCodec(Class<? extends T> typeToken) {
    this.typeToken = typeToken;
  }

  @Override
  public void encodeToWire(Buffer buffer, T payload) {
    String s = encode(payload);
    byte[] strBytes = s.getBytes(CharsetUtil.UTF_8);
    buffer.appendInt(strBytes.length);
    buffer.appendBytes(strBytes);
  }

  @Override
  public T decodeFromWire(int pos, Buffer buffer) {
    int length = buffer.getInt(pos);
    pos += 4;
    byte[] bytes = buffer.getBytes(pos, pos + length);
    String s = new String(bytes, CharsetUtil.UTF_8);
    return Json.decodeValue(s, typeToken);
  }

  @Override
  public T transform(T payload) {
    return payload;
  }

  @Override
  public String name() {
    return getClass().getCanonicalName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
