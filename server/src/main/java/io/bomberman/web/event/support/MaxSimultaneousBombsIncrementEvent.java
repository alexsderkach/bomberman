package io.bomberman.web.event.support;

import io.bomberman.web.event.Event;
import io.bomberman.web.event.Type;
import lombok.EqualsAndHashCode;
import lombok.Value;

import static com.google.common.collect.ImmutableMap.of;

@Value
@EqualsAndHashCode(callSuper = true)
public class MaxSimultaneousBombsIncrementEvent extends Event {
  public MaxSimultaneousBombsIncrementEvent(int maxConcurrentBombs) {
    super(Type.MAX_SIMULTANEOUS_BOMBS_INCREMENT, of("maxSimultaneousBombs", maxConcurrentBombs));
  }
}
