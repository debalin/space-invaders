package com.debalin.characters;

import com.debalin.engine.MainEngine;
import com.debalin.engine.events.Event;
import com.debalin.engine.util.EngineConstants;
import com.debalin.util.Constants;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Player extends MovingRectangle {

  public boolean LEFT, RIGHT;
  public int score = 0;
  public float accuracy = 0;
  public int bulletsFired = 0;

  private SpawnPoint spawnPoint;

  public Player(MainEngine engine, SpawnPoint spawnPoint) {
    super(Constants.PLAYER_COLOR, spawnPoint.getPosition(), Constants.PLAYER_SIZE, Constants.PLAYER_INIT_VEL, null, engine);
    LEFT = RIGHT = false;
    setVisible(true);

    this.spawnPoint = spawnPoint;
  }

  public synchronized void update(float frameTicSize) {
    moveAndJump();

    if (!checkBounds())
      position.add(PVector.mult(velocity, frameTicSize));

    if (bulletsFired != 0)
      accuracy = (score / (float) bulletsFired) * 100;
  }

  private void checkDeath() {

  }

  public void regenerate() {
    System.out.println("Player is regenerating.");
    position = spawnPoint.getPosition().copy();
  }

  private void moveAndJump() {
    if (LEFT)
      velocity.x = -Constants.PLAYER_MAX_VEL.x;
    else if (RIGHT)
      velocity.x = Constants.PLAYER_MAX_VEL.x;
    else
      velocity.x = 0f;
  }

  private boolean checkBounds() {
    if (position.x + velocity.x + Constants.PLAYER_SIZE.x > Constants.CLIENT_RESOLUTION.x - Constants.PLAYER_PADDING_X) {
      return true;
    } else if (position.x + velocity.x <= Constants.PLAYER_PADDING_X) {
      return true;
    }
    return false;
  }

  public void handleKeypress(int key, boolean set) {
    switch (key) {
      case 'A':
      case 'a':
        LEFT = set;
        break;
      case 'D':
      case 'd':
        RIGHT = set;
        break;
      case 32:
        fireBullet();
        break;
    }
  }

  public void fireBullet() {
    String eventType = Constants.EVENT_TYPES.PLAYER_FIRE.toString();
    List<Object> eventParameters = new ArrayList<>();

    PVector bulletInitPosition = new PVector(position.x + Constants.PLAYER_SIZE.x / 2, position.y);

    eventParameters.add(bulletInitPosition);
    Event event = new Event(eventType, eventParameters, EngineConstants.DEFAULT_TIMELINES.GAME_MILLIS.toString(), MainEngine.controller.getClientConnectionID().intValue(), engine.gameTimelineInMillis.getTime(), true);

    engine.getEventManager().raiseEvent(event, true);

    bulletsFired++;
  }

}
