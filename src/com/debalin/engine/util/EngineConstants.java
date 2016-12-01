package com.debalin.engine.util;

public class EngineConstants {

  public static final String WRITE_ERROR_MESSAGE = "Connection reset by peer: socket write error";
  public static final String READ_ERROR_MESSAGE = "Connection reset";

  public enum DEFAULT_EVENT_TYPES {
    USER_INPUT, RECORD_START, RECORD_STOP, RECORD_PLAY, PLAYER_DISCONNECT
  }

  public enum DEFAULT_TIMELINES {
    REAL_MILLIS, GAME_MILLIS, GAME_LOOPS
  }

  public static final boolean REPLAY_ON = false;
  public static final boolean SCRIPT_ON = false;

}
