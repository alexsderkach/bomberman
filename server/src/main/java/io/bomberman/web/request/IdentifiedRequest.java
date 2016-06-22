package io.bomberman.web.request;

import lombok.Data;

@Data
public class IdentifiedRequest {
  private String sessionId;
}
