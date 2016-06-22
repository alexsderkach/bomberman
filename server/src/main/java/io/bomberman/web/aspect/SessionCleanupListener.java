package io.bomberman.web.aspect;

import io.bomberman.web.session.SessionStorage;

public interface SessionCleanupListener {
  void handle(SessionStorage sessionStorage);
}
