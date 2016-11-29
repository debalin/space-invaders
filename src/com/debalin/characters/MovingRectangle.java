package com.debalin.characters;

import com.debalin.engine.game_objects.DynamicGameObject;
import com.debalin.engine.MainEngine;
import processing.core.PVector;

public abstract class MovingRectangle extends DynamicGameObject {

  public MovingRectangle(PVector color, PVector position, PVector size, PVector velocity, PVector acceleration, MainEngine engine) {
    this.color = new PVector(color.x, color.y, color.z);
    this.position = new PVector(position.x, position.y);
    this.size = new PVector(size.x, size.y);
    this.velocity = (velocity != null) ? new PVector(velocity.x, velocity.y): null;
    this.acceleration = (acceleration != null) ? new PVector(acceleration.x, acceleration.y) : null;

    this.engine = engine;
  }

  public void draw() {
    engine.pushMatrix();
    engine.fill(color.x, color.y, color.z);
    engine.noStroke();
    engine.rect(position.x, position.y, size.x, size.y);
    engine.popMatrix();
  }
}
