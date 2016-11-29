package com.debalin.engine.events;

import com.debalin.SpaceInvadersManager;
import com.debalin.engine.MainEngine;
import com.debalin.engine.game_objects.GameObject;
import com.debalin.engine.timeline.Timeline;
import com.debalin.engine.util.EngineConstants;

import java.util.*;

public class EventManager implements Runnable {

  Map<String, EventPriorities> eventTypePriorities;
  Map<String, Timeline> timelines;
  MainEngine engine;
  Map<String, Map<Integer, PriorityQueue<OrderedEvent>>> timelineQueues;
  Map<String, Map<Integer, PriorityQueue<OrderedEvent>>> timelineQueuesBackup;
  Map<String, Map<Integer, PriorityQueue<OrderedEvent>>> recordedTimelineQueues;

  public Map<Integer, Queue<Event>> fromServerWriteQueues;
  public Queue<Event> fromClientWriteQueue;
  Queue<Event> startupEvents;

  public Timeline replayTimelineInFrames;
  public float replayStartFrame;

  public boolean recording = false;
  public boolean playingRecording = false;

  public void setRecording(boolean recording) {
    if (recording) {
      replayStartFrame = engine.gameTimelineInFrames.getTime();
    }
    this.recording = recording;
  }

  public void removePlayer(int connectionID) {
    synchronized (timelineQueues) {
      for (Map<Integer, PriorityQueue<OrderedEvent>> eventQueues : timelineQueues.values()) {
        eventQueues.remove(connectionID);
      }
    }

    synchronized (timelineQueuesBackup) {
      for (Map<Integer, PriorityQueue<OrderedEvent>> eventQueues : timelineQueuesBackup.values()) {
        eventQueues.remove(connectionID);
      }
    }

    synchronized (recordedTimelineQueues) {
      for (Map<Integer, PriorityQueue<OrderedEvent>> eventQueues : recordedTimelineQueues.values()) {
        eventQueues.remove(connectionID);
      }
    }
  }

  private class OrderedEvent {
    public Event event;
    public float score;

    public OrderedEvent(Event event, float score) {
      this.event = event;
      this.score = score;
    }
  }

  private class EventComparator implements Comparator<OrderedEvent> {
    public int compare(OrderedEvent event1, OrderedEvent event2) {
      if (event1.score < event2.score) {
        return -1;
      } else if (event1.score > event2.score) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  public enum EventPriorities {
    HIGH, MED, LOW
  }

  public Queue<Event> getWriteQueue(int connectionID) {
    if (connectionID == -1) {
      return fromClientWriteQueue;
    } else {
      if (fromServerWriteQueues.get(connectionID) == null) {
        Queue<Event> fromServerWriteQueue = new LinkedList<>();
        synchronized (fromServerWriteQueues) {
          fromServerWriteQueues.put(connectionID, fromServerWriteQueue);
          synchronized (fromServerWriteQueue) {
            synchronized (startupEvents) {
              fromServerWriteQueue.addAll(startupEvents);
            }
          }
        }
      }
      return fromServerWriteQueues.get(connectionID);
    }
  }

  public EventManager(MainEngine engine) {
    fromServerWriteQueues = new HashMap<>();
    fromClientWriteQueue = new LinkedList<>();
    startupEvents = new LinkedList<>();
    eventTypePriorities = new HashMap<>();
    timelines = new HashMap<>();
    timelineQueues = new HashMap<>();
    timelineQueuesBackup = new HashMap<>();
    recordedTimelineQueues = new HashMap<>();
    this.engine = engine;
    getDefaultEventTypes();
  }

  private void getDefaultEventTypes() {
    for (EngineConstants.DEFAULT_EVENT_TYPES defaultEventType : EngineConstants.DEFAULT_EVENT_TYPES.values()) {
      eventTypePriorities.put(defaultEventType.toString(), EventPriorities.HIGH);
    }
  }

  public void registerEventType(String eventType, EventPriorities defaultEventPriority) {
    eventTypePriorities.put(eventType, defaultEventPriority);
  }

  public void registerTimeline(Timeline timeline, String timelineName) {
    timelines.put(timelineName, timeline);

    Map<Integer, PriorityQueue<OrderedEvent>> eventQueue = new HashMap();
    synchronized (timelineQueues) {
      timelineQueues.put(timelineName, eventQueue);
    }
    eventQueue.put(-1, new PriorityQueue<>(new EventComparator()));
    eventQueue.put(MainEngine.controller.getClientConnectionID().intValue(), new PriorityQueue<>(new EventComparator()));

    Map<Integer, PriorityQueue<OrderedEvent>> recordedEventQueue = new HashMap();
    synchronized (recordedTimelineQueues) {
      recordedTimelineQueues.put(timelineName, recordedEventQueue);
    }
    recordedEventQueue.put(-1, new PriorityQueue<>(new EventComparator()));
    recordedEventQueue.put(MainEngine.controller.getClientConnectionID().intValue(), new PriorityQueue<>(new EventComparator()));

  }

  public void raiseEvent(Event event, boolean toBroadcast) {
    if (playingRecording)
      return;

    if (MainEngine.serverMode) {
      broadcastEvent(event, -1);
      return;
    }

    Timeline timeline = timelines.get(event.getTimelineName());
    if (timeline == null) {
      System.out.println("Timeline not registered, please do it first.");
      return;
    }
    String eventType = event.getEventType();
    EventPriorities eventDefaultPriority = eventTypePriorities.get(eventType);

    if (eventDefaultPriority == null) {
      eventDefaultPriority = EventPriorities.LOW;
    }

    switch (eventDefaultPriority) {
      case HIGH:
        putInEventQueues(event, event.getTime() - 100, timelineQueues);
        break;
      case MED:
        putInEventQueues(event, event.getTime() - 50, timelineQueues);
        break;
      case LOW:
        putInEventQueues(event, event.getTime(), timelineQueues);
        break;
    }

    if (toBroadcast) {
      synchronized (fromClientWriteQueue) {
        fromClientWriteQueue.add(event);
        fromClientWriteQueue.notify();
      }
    }
  }

  public void putInEventQueues(Event event, float score, Map<String, Map<Integer, PriorityQueue<OrderedEvent>>> queues) {
    synchronized (queues) {
      if (queues.get(event.getTimelineName()).containsKey(event.getConnectionID()))
        queues.get(event.getTimelineName()).get(event.getConnectionID()).add(new OrderedEvent(event, score));
      else {
        queues.get(event.getTimelineName()).put(event.getConnectionID(), new PriorityQueue<>(new EventComparator()));
        queues.get(event.getTimelineName()).get(event.getConnectionID()).add(new OrderedEvent(event, score));
      }
    }
  }

  public Event whichEvent(Map<Integer, PriorityQueue<OrderedEvent>> eventQueues) {
    Event event = null;
    float bestScore = -1;

    Iterator it = eventQueues.entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry<Integer, PriorityQueue<OrderedEvent>> pair = (Map.Entry) it.next();
      int connectionID = pair.getKey();
      PriorityQueue<OrderedEvent> eventQueue = pair.getValue();

      OrderedEvent orderedEvent = eventQueue.peek();
      if (connectionID != -1 && eventQueue.size() == 0) {
        event = null;
        break;
      }
      if (bestScore == -1 && orderedEvent != null) {
        event = orderedEvent.event;
        bestScore = orderedEvent.score;
        continue;
      } else if (orderedEvent != null && orderedEvent.score < bestScore) {
        event = orderedEvent.event;
        bestScore = orderedEvent.score;
      }
    }
    return event;
  }

  public void handleEvents() {
    try {
      Iterator it;
      synchronized (timelineQueues) {
        it = timelineQueues.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<String, Map<Integer, PriorityQueue<OrderedEvent>>> pair = (Map.Entry) it.next();
          Map<Integer, PriorityQueue<OrderedEvent>> eventQueues = pair.getValue();
          Event handleEvent;
          while (true) {
            handleEvent = whichEvent(eventQueues);
            if (handleEvent == null)
              break;
            if (playingRecording) {
              if (handleEvent.frame - replayStartFrame <= replayTimelineInFrames.getTime()) {
                eventQueues.get(handleEvent.getConnectionID()).poll();
                MainEngine.controller.getEventHandler().onEvent(handleEvent);
              } else {
                break;
              }
              if (isEventQueuesEmpty(eventQueues)) {
                finishPlayingRecordedEvents();
                break;
              }
            } else {
              OrderedEvent orderedEvent = eventQueues.get(handleEvent.getConnectionID()).poll();
              if (recording) {
                recordEvent(orderedEvent);
              }
              MainEngine.controller.getEventHandler().onEvent(orderedEvent.event);
              if (orderedEvent.event.getEventType().equals(EngineConstants.DEFAULT_EVENT_TYPES.RECORD_PLAY.toString())) {
                break;
              }
            }
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public boolean isEventQueuesEmpty(Map<Integer, PriorityQueue<OrderedEvent>> eventQueues) {
    int totalSize = 0;
    int nullSize = 0;

    for (PriorityQueue<OrderedEvent> eventQueue : eventQueues.values()) {
      totalSize += eventQueue.size();
      for (OrderedEvent orderedEvent : eventQueue) {
        if (orderedEvent.event.getEventType().equals("NULL"))
          nullSize++;
      }
    }

    if (totalSize == nullSize && totalSize < 5)
      return true;
    else
      return false;
  }

  public void finishPlayingRecordedEvents() {
    playingRecording = false;
    timelineQueues.clear();
    synchronized (recordedTimelineQueues) {
      for (String timelineName : recordedTimelineQueues.keySet()) {
        recordedTimelineQueues.get(timelineName).clear();
      }
    }
    for (String timelineName : timelineQueuesBackup.keySet()) {
      timelineQueues.put(timelineName, timelineQueuesBackup.get(timelineName));
    }
    engine.stopPlayingRecordedGameObjects();
  }

  public void playRecordedEvents(float frameTicSize) {
    playingRecording = true;
    synchronized (timelineQueuesBackup) {
      timelineQueuesBackup.clear();
      System.out.println("Backing up event queue.");
      for (String timelineName : timelineQueues.keySet()) {
        timelineQueuesBackup.put(timelineName, timelineQueues.get(timelineName));
      }
    }
    synchronized (timelineQueues) {
      timelineQueues.clear();
      for (String timelineName : recordedTimelineQueues.keySet()) {
        timelineQueues.put(timelineName, recordedTimelineQueues.get(timelineName));
      }
    }
    replayTimelineInFrames = new Timeline(engine.gameTimelineInFrames, frameTicSize, Timeline.TimelineIterationTypes.LOOP, engine);
    engine.registerGameObject(replayTimelineInFrames, -1, true);
  }

  private void recordEvent(OrderedEvent orderedEvent) {
    if (orderedEvent.event.getEventType().equals("RECORD_START") || orderedEvent.event.getEventType().equals("RECORD_STOP") || orderedEvent.event.getEventType().equals("RECORD_PLAY"))
      return;
    orderedEvent.event.frame = engine.gameTimelineInFrames.getTime();
    putInEventQueues(orderedEvent.event, orderedEvent.score, recordedTimelineQueues);
  }

  public void run() {
    System.out.println("Starting event handling.");
    handleEvents();
  }

  public void broadcastEvent(Event event, int connectionID) {
    if (event.isBackup()) {
      synchronized (startupEvents) {
        startupEvents.add(event);
      }
    }
    synchronized (fromServerWriteQueues) {
      Iterator it = fromServerWriteQueues.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<Integer, Queue<Event>> pair = (Map.Entry) it.next();
        if (pair.getKey() == connectionID)
          continue;
        else {
          Queue<Event> fromServerWriteQueue = pair.getValue();
          synchronized (fromServerWriteQueue) {
            fromServerWriteQueue.add(event);
            fromServerWriteQueue.notify();
          }
        }
      }
    }
  }

}
