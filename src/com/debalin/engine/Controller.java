package com.debalin.engine;

import com.debalin.engine.events.EventHandler;
import com.debalin.engine.game_objects.GameObject;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Controller {

  public MainEngine engine;
  private AtomicInteger clientConnectionID;

  public Controller() {
    clientConnectionID = new AtomicInteger(-1);
  }

  public void setEngine(MainEngine engine) {
    this.engine = engine;
  }

  public abstract void setup();

  public abstract void manage();

  public abstract EventHandler getEventHandler();

  public abstract void registerServerOrClient();

  public AtomicInteger getClientConnectionID() {
    return clientConnectionID;
  }

  public void setClientConnectionID(int clientConnectionID) {
    synchronized (this.clientConnectionID) {
      this.clientConnectionID.set(clientConnectionID);
      this.clientConnectionID.notify();
    }
  }

  public abstract void mirrorGameObjects(List<Queue<GameObject>> gameObjectsCluster);

  public abstract Map<String, GameObject> bindObjects();

  public abstract String getScriptPath();
  public abstract String getScriptFunctionName();

}
