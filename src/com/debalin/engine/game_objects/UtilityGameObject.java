package com.debalin.engine.game_objects;

import processing.core.PVector;

public abstract class UtilityGameObject extends GameObject {

  protected transient PVector position;
  protected transient PVector size;

  public boolean isVisible() {
    return false;
  }

}
