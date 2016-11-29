package com.debalin.characters;


import com.debalin.engine.MainEngine;
import com.debalin.util.Constants;
import processing.core.PVector;

public class Bullet extends MovingRectangle {

  Player player;

  public Bullet(MainEngine engine, PVector bulletInitPosition) {
    super(Constants.BULLET_COLOR, bulletInitPosition, Constants.BULLET_SIZE, Constants.BULLET_INIT_VEL, Constants.BULLET_INIT_ACC, engine);
    setVisible(true);
  }

  @Override
  public void update(float frameTicSize) {
    position.add(velocity);
  }

}