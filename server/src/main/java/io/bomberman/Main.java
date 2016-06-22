package io.bomberman;

import io.vertx.rxjava.core.Vertx;

public class Main {
  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(MainVerticle.class.getName());
  }
}