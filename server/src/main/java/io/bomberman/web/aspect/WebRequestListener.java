package io.bomberman.web.aspect;

import org.springframework.stereotype.Component;

@Component
public interface WebRequestListener {
  void handle(String sessionId);
}
