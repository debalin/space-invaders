package com.debalin.engine.timeline;

import com.debalin.engine.MainEngine;
import com.debalin.engine.game_objects.DynamicGameObject;

import java.io.Serializable;

public class Timeline extends DynamicGameObject implements Serializable {

  private float anchor;
  private float ticSize;
  private float time;
  private TimelineIterationTypes timelineIterationType;

  public enum TimelineIterationTypes {
    REAL, LOOP
  }

  public Timeline(Timeline anchorTimeline, float ticSize, TimelineIterationTypes timelineIterationType, MainEngine engine) {
    setVisible(true);
    if (anchorTimeline == null)
      this.anchor = 0;
    else
      this.anchor = anchorTimeline.getTime();
    this.time = this.anchor;
    this.ticSize = ticSize;
    this.timelineIterationType = timelineIterationType;
    this.engine = engine;
    this.update(1f);
  }

  public void update(float frameTicSize) {
    switch (timelineIterationType) {
      case REAL:
        time = (System.nanoTime() - anchor) / ticSize;
        break;
      case LOOP:
        time = (engine.frameCount - anchor) / ticSize;
    }
  }

  public void draw() {
  }

  public float getTime() {
    return time;
  }
}
