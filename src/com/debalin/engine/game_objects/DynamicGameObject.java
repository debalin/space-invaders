package com.debalin.engine.game_objects;

import processing.core.PVector;

public abstract class DynamicGameObject extends GameObject {

  protected PVector velocity;
  protected PVector acceleration;

  public PVector getVelocity() {
    return velocity;
  }
  public PVector getAcceleration() {
    return acceleration;
  }

}
