package com.debalin.characters;


import com.debalin.engine.MainEngine;
import com.debalin.engine.events.Event;
import com.debalin.engine.game_objects.GameObject;
import com.debalin.engine.util.EngineConstants;
import com.debalin.util.Collision;
import com.debalin.util.Constants;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Bullet extends MovingRectangle {

  Queue<GameObject> enemies;
  Player player;

  public Bullet(MainEngine engine, PVector bulletInitPosition, Queue<GameObject> enemies, Player player) {
    super(Constants.BULLET_COLOR, bulletInitPosition, Constants.BULLET_SIZE, Constants.BULLET_INIT_VEL, Constants.BULLET_INIT_ACC, engine);
    setVisible(true);

    this.enemies = enemies;
    this.player = player;
  }

  @Override
  public void update(float frameTicSize) {
    if (isVisible()) {
      position.add(velocity);
      checkEnemies();
      checkBounds();
    }
  }

  private void checkEnemies() {
    enemies.stream().filter(enemy -> Collision.hasCollidedRectangles(this, enemy)).forEach(enemy -> {
      String eventType = Constants.EVENT_TYPES.ENEMY_HIT.toString();
      List<Object> eventParameters = new ArrayList<>();

      eventParameters.add(((Enemy)enemy).getEnemyID());
      Event event = new Event(eventType, eventParameters, EngineConstants.DEFAULT_TIMELINES.GAME_MILLIS.toString(), MainEngine.controller.getClientConnectionID().intValue(), engine.gameTimelineInMillis.getTime(), true);

      engine.getEventManager().raiseEvent(event, true);
      setVisible(false);
      player.score += Math.pow(2, -player.deaths) * 1;
      return;
    });
  }

  private void checkBounds() {
    if (position.y < Constants.BULLET_PADDING) {
      setVisible(false);
    }
  }

}