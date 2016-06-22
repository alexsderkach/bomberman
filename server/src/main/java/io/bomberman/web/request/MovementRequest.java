package io.bomberman.web.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MovementRequest extends IdentifiedRequest {
  private String direction;
}
