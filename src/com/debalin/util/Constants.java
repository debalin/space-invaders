package com.debalin.util;

import processing.core.PVector;

public class Constants {

  public static final PVector CLIENT_RESOLUTION = new PVector(600, 700);
  public static final PVector SERVER_RESOLUTION = new PVector(1, 1);
  public static final int SMOOTH_FACTOR = 4;
  public static final PVector BACKGROUND_RGB = new PVector(20, 20, 20);

  public static final PVector PLAYER_COLOR = new PVector(0, 240, 0);
  public static final PVector PLAYER_SIZE = new PVector(22, 22);
  public static final float PLAYER_SPAWN_Y = CLIENT_RESOLUTION.y - 100;
  public static final float PLAYER_PADDING_X = 30;
  public static final PVector PLAYER_INIT_VEL = new PVector(0, 0);
  public static final PVector PLAYER_MAX_VEL = new PVector(5, 5);

  public static final int ENEMY_ROWS = 9;
  public static final int ENEMY_COLUMNS = 9;
  public static final PVector ENEMY_INIT_VEL = new PVector(0, 0);
  public static final PVector ENEMY_MAX_VEL = new PVector(24, 24);
  public static final PVector ENEMY_INIT_ACC = new PVector(0, 0);
  public static final PVector ENEMY_COLOR = new PVector(240, 240, 240);
  public static final PVector ENEMY_SIZE = new PVector(18, 18);
  public static final float ENEMY_PADDING = 18;
  public static final int ENEMY_MOVE_INTERVAL = 40;

  public static final int SERVER_PORT = 5678;
  public static final String SERVER_ADDRESS = "localhost";

  public static final PVector SCORE_POSITION = new PVector(CLIENT_RESOLUTION.x - 150, 50);
  public static final int SCORE_INCREMENT_INTERVAL = 500;

  public enum EVENT_TYPES {
    PLAYER_DEATH, ENEMY_SPAWN, PLAYER_SPAWN, PLAYER_FIRE, NULL, SCRIPT
  }

}
