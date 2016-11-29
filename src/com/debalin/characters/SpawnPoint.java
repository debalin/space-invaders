package com.debalin.characters;

import com.debalin.engine.game_objects.StaticGameObject;
import processing.core.PVector;

public class SpawnPoint extends StaticGameObject {

  public SpawnPoint(PVector position) {
    this.position = new PVector(position.x, position.y);
  }

  public void draw(){}

}
