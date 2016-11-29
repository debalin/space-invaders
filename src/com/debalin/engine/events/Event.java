package com.debalin.engine.events;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {

  private String eventType;
  private List<Object> eventParameters;
  private String timelineName;
  private int connectionID;
  private float time;
  private boolean backup;
  public float frame;

  public Event(String eventType, List<Object> eventParameters, String timeline, int connectionID, float time, boolean backup) {
    this.eventType = eventType;
    this.eventParameters = eventParameters;
    this.timelineName = timeline;
    this.connectionID = connectionID;
    this.time = time;
    this.backup = backup;
  }

  public String getEventType() {
    return eventType;
  }

  public List<Object> getEventParameters() {
    return eventParameters;
  }

  public String getTimelineName() {
    return timelineName;
  }

  public int getConnectionID() {
    return connectionID;
  }

  public void setConnectionID(int connectionID) {
    this.connectionID = connectionID;
  }

  public float getTime() {
    return time;
  }

  public boolean isBackup() {
    return backup;
  }

}
