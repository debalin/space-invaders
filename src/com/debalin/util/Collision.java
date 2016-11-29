package com.debalin.util;

import com.debalin.engine.game_objects.GameObject;
import processing.core.PVector;

public class Collision {

  public static boolean hasCollidedRectangles(GameObject rect1, GameObject rect2) {
    PVector rect1Center = new PVector(rect1.getPosition().x + rect1.getSize().x / 2, rect1.getPosition().y + rect1.getSize().y / 2);
    PVector rect2Center = new PVector(rect2.getPosition().x + rect2.getSize().x / 2, rect2.getPosition().y + rect2.getSize().y / 2);

    float xSeparation = Math.abs(rect1Center.x - rect2Center.x);
    float ySeparation = Math.abs(rect1Center.y - rect2Center.y);

    float minXSeparation = rect1.getSize().x / 2 + rect2.getSize().x / 2;
    float minYSeparation = rect1.getSize().y / 2 + rect2.getSize().y / 2;

    if (xSeparation <= minXSeparation && ySeparation <= minYSeparation)
      return true;

    return false;
  }

}
