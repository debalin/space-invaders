package com.debalin.characters;

import com.debalin.engine.MainEngine;
import com.debalin.util.Constants;
import processing.core.PVector;

public class Enemy extends MovingRectangle {

  private long enemyID;
  private int rowID, columnID;
  public int signToggle = -1;

  public Enemy(MainEngine engine, PVector enemyInitPosition, long enemyID) {
    super(Constants.ENEMY_COLOR, enemyInitPosition, Constants.ENEMY_SIZE, Constants.ENEMY_INIT_VEL, Constants.ENEMY_INIT_ACC, engine);
    setVisible(true);

    this.enemyID = enemyID;
    this.rowID = (int) enemyID / 10;
    this.columnID = (int) enemyID % 10;
  }

  public void update(float frameTicSize) {
    if (engine.gameTimelineInFrames.getTime() % Constants.ENEMY_MOVE_INTERVAL == 0) {
      position.x += signToggle * Constants.ENEMY_MAX_VEL.x;
      checkBounds();
    }
  }

  public void checkBounds() {
    int columnsLeft = Constants.ENEMY_COLUMNS - columnID;
    if (position.x + signToggle * Constants.ENEMY_MAX_VEL.x - (columnsLeft * (Constants.ENEMY_SIZE.x + Constants.ENEMY_PADDING)) < Constants.ENEMY_PADDING) {
      signToggle *= -1;
      position.y += Constants.ENEMY_MAX_VEL.y;
    }
    else if (position.x + signToggle * Constants.ENEMY_MAX_VEL.x + Constants.ENEMY_SIZE.x + (columnID - 1) * (Constants.ENEMY_SIZE.x + Constants.ENEMY_PADDING) > Constants.CLIENT_RESOLUTION.x - Constants.ENEMY_PADDING) {
      signToggle *= -1;
      position.y += Constants.ENEMY_MAX_VEL.y;
    }
  }

  @Override
  public void draw() {
    engine.pushMatrix();

    engine.noStroke();
    engine.fill(color.x, color.y, color.z);
    engine.rect(position.x, position.y, size.x, size.y);

    engine.popMatrix();
  }

  public long getEnemyID() {
    return enemyID;
  }
}