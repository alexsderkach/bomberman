package io.bomberman.web.handler;

import io.bomberman.model.Direction;
import io.bomberman.service.BombAllocationService;
import io.bomberman.service.PlayerMovementService;
import io.bomberman.web.aspect.WebRequestListener;
import io.bomberman.web.request.IdentifiedRequest;
import io.bomberman.web.request.MovementRequest;
import io.bomberman.web.request.PlaceBombRequest;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebRequestHandler {

  private final PlayerMovementService playerMovementService;
  private final List<WebRequestListener> webRequestListeners;
  private final BombAllocationService bombAllocationService;

  public void handleMovement(RoutingContext routingContext) {
    MovementRequest movementRequest = decodeRequest(routingContext, MovementRequest.class);
    String sessionId = movementRequest.getSessionId();
    Direction direction = Direction.of(movementRequest.getDirection());

    playerMovementService.move(sessionId, direction);

    routingContext.response().setStatusCode(200).end();

    notifyListeners(sessionId);
  }

  public void handleBombAllocation(RoutingContext routingContext) {
    PlaceBombRequest placeBombRequest = decodeRequest(routingContext, PlaceBombRequest.class);
    String sessionId = placeBombRequest.getSessionId();

    bombAllocationService.allocate(sessionId);

    routingContext.response().setStatusCode(200).end();

    notifyListeners(sessionId);
  }

  private void notifyListeners(String sessionId) {
    webRequestListeners.forEach(webRequestListener -> webRequestListener.handle(sessionId));
  }

  private <T extends IdentifiedRequest> T decodeRequest(RoutingContext routingContext, Class<T> klass) {
    return Json.decodeValue(routingContext.getBodyAsString(), klass);
  }
}
