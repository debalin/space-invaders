package com.debalin.engine.game_objects;

import com.debalin.engine.MainEngine;
import processing.core.PVector;

import java.io.Serializable;

public abstract class GameObject implements Serializable {

  protected PVector color;
  protected PVector position;
  protected PVector size;
  private boolean visible;

  private int connectionID;
  transient public MainEngine engine;

  public PVector getPosition() { return position; }
  public PVector getSize() {
    return size;
  }
  public int getConnectionID() { return connectionID; }

  public void setPosition(PVector position) {
    this.position = position;
  }
  public void setSize(PVector size) {
    this.size = size;
  }
  public void setConnectionID(int connectionID) {
    this.connectionID = connectionID;
  }

  public abstract void update(float frameTicSize);
  public abstract void draw();

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}
