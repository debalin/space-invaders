package com.debalin;

import com.debalin.characters.Enemy;
import com.debalin.characters.Player;
import com.debalin.characters.SpawnPoint;
import com.debalin.engine.*;
import com.debalin.engine.events.Event;
import com.debalin.engine.events.EventHandler;
import com.debalin.engine.events.EventManager;
import com.debalin.engine.game_objects.GameObject;
import com.debalin.engine.network.GameClient;
import com.debalin.engine.network.GameServer;
import com.debalin.engine.util.EngineConstants;
import com.debalin.engine.util.TextRenderer;
import com.debalin.util.Constants;
import processing.core.PVector;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SpaceInvadersManager extends Controller implements TextRenderer {

  public Player player;
  public SpawnPoint playerSpawnPoint;
  public Queue<GameObject> enemies;
  public Map<Integer, GameObject> enemyMap;
  public Queue<GameObject> bullets;
  public boolean serverMode;
  public GameServer gameServer;
  public GameClient gameClient;

  public int enemiesObjectID;
  public int bulletsObjectID;
  public int playerObjectID;

  Map<Integer, Queue<Event>> fromServerWriteQueues;

  DecimalFormat decimalFormatter;

  private EventHandler eventHandler;

  public SpaceInvadersManager(boolean serverMode) {
    this.serverMode = serverMode;
    enemies = new ConcurrentLinkedQueue<>();
    enemyMap = new HashMap<>();
    bullets = new ConcurrentLinkedQueue<>();
    enemiesObjectID = playerObjectID = bulletsObjectID = -1;
    fromServerWriteQueues = new HashMap<>();

    decimalFormatter = new DecimalFormat();
    decimalFormatter.setMaximumFractionDigits(2);

    eventHandler = new GameEventHandler(this);
  }

  public static void main(String args[]) {
    SpaceInvadersManager spaceInvadersManager;

    if (args[0].toLowerCase().equals("s")) {
      System.out.println("Starting as server.");
      spaceInvadersManager = new SpaceInvadersManager(true);
    } else {
      System.out.println("Starting as client.");
      spaceInvadersManager = new SpaceInvadersManager(false);
    }

    spaceInvadersManager.startEngine();
  }

  @Override
  public Map<String, GameObject> bindObjects() {
    return null;
  }

  @Override
  public String getScriptPath() {
    return null;
  }

  @Override
  public String getScriptFunctionName() {
    return null;
  }

  @Override
  public void mirrorGameObjects(List<Queue<GameObject>> gameObjectsCluster) {

  }

  private void startEngine() {
    registerConstants();

    System.out.println("Starting engine.");
    MainEngine.startEngine(this);
  }

  public String getTextContent() {
    if (player == null)
      return "";

    String content = "Score: " + player.score;
    content += "\nAccuracy: " + decimalFormatter.format(player.accuracy);
    return content;
  }

  public PVector getTextPosition() {
    return Constants.SCORE_POSITION;
  }

  private void registerConstants() {
    System.out.println("Registering constants.");
    MainEngine.registerConstants(Constants.CLIENT_RESOLUTION, Constants.SERVER_RESOLUTION, Constants.SMOOTH_FACTOR, Constants.BACKGROUND_RGB, serverMode);
  }

  @Override
  public void setup() {
    registerEventTypes();

    if (!serverMode) {
      AtomicInteger clientConnectionID = getClientConnectionID();
      synchronized (clientConnectionID) {
        try {
          while (clientConnectionID.intValue() == -1)
            clientConnectionID.wait();
        } catch (InterruptedException ex) {
        }
      }
      System.out.println("Connection ID is " + getClientConnectionID() + ".");
      initializePlayer();
      registerTextRenderers();
    } else {
      spawnEnemies();
    }
  }

  public void registerServerOrClient() {
    if (serverMode) {
      System.out.println("Registering Server.");
      gameServer = engine.registerServer(Constants.SERVER_PORT, this);
    } else {
      System.out.println("Registering Client.");
      gameClient = engine.registerClient(Constants.SERVER_ADDRESS, Constants.SERVER_PORT, this);
    }
  }

  private void registerEventTypes() {
    engine.getEventManager().registerEventType(Constants.EVENT_TYPES.PLAYER_DEATH.toString(), EventManager.EventPriorities.HIGH);
    engine.getEventManager().registerEventType(Constants.EVENT_TYPES.ENEMY_SPAWN.toString(), EventManager.EventPriorities.MED);
    engine.getEventManager().registerEventType(Constants.EVENT_TYPES.PLAYER_FIRE.toString(), EventManager.EventPriorities.HIGH);
    engine.getEventManager().registerEventType(Constants.EVENT_TYPES.PLAYER_SPAWN.toString(), EventManager.EventPriorities.HIGH);
  }

  private void registerTextRenderers() {
    System.out.println("Registering text renderers.");
    engine.registerTextRenderer(this);
  }

  private void spawnEnemies() {
    for (int i = 1; i <= Constants.ENEMY_ROWS; i++) {
      for (int j = 1; j <= Constants.ENEMY_COLUMNS; j++) {
        float x = Constants.CLIENT_RESOLUTION.x - j * (Constants.ENEMY_PADDING + Constants.ENEMY_SIZE.x);
        float y = i * (Constants.ENEMY_PADDING + Constants.ENEMY_SIZE.y);
        PVector enemyInitPosition = new PVector(x, y);
        int enemyID = i * 10 + j;
        String eventType = Constants.EVENT_TYPES.ENEMY_SPAWN.toString();
        List<Object> eventParameters = new ArrayList<>();
        eventParameters.add(enemyID);
        eventParameters.add(enemyInitPosition);
        Event event = new Event(eventType, eventParameters, EngineConstants.DEFAULT_TIMELINES.GAME_MILLIS.toString(), getClientConnectionID().intValue(), engine.gameTimelineInMillis.getTime(), true);
        engine.getEventManager().raiseEvent(event, true);
      }
    }
  }

  public void manage() {
    removeGameObjects();
  }

  private void removeGameObjects() {
    synchronized (enemies) {
      Iterator<GameObject> i = enemies.iterator();
      while (i.hasNext()) {
        Enemy enemy = (Enemy) i.next();
        if (!enemy.isVisible())
          i.remove();
      }
    }
  }

  private void initializePlayer() {
    System.out.println("Initializing player.");
    playerSpawnPoint = new SpawnPoint(new PVector(engine.random(Constants.PLAYER_PADDING_X, Constants.CLIENT_RESOLUTION.x - Constants.PLAYER_PADDING_X), Constants.PLAYER_SPAWN_Y));

    String eventType = Constants.EVENT_TYPES.PLAYER_SPAWN.toString();
    List<Object> eventParameters = new ArrayList<>();

    eventParameters.add(playerSpawnPoint);
    Event event = new Event(eventType, eventParameters, EngineConstants.DEFAULT_TIMELINES.GAME_MILLIS.toString(), getClientConnectionID().intValue(), engine.gameTimelineInMillis.getTime(), true);

    engine.getEventManager().raiseEvent(event, true);
  }

  @Override
  public EventHandler getEventHandler() {
    return eventHandler;
  }

}