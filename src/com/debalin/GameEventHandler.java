package com.debalin;

import com.debalin.characters.Enemy;
import com.debalin.characters.Player;
import com.debalin.characters.SpawnPoint;
import com.debalin.engine.MainEngine;
import com.debalin.engine.events.Event;
import com.debalin.engine.events.EventHandler;
import com.debalin.engine.game_objects.GameObject;
import com.debalin.engine.scripting.ScriptManager;
import com.debalin.engine.util.EngineConstants;
import com.debalin.util.Constants;
import processing.core.PVector;

import java.util.LinkedList;
import java.util.List;

public class GameEventHandler implements EventHandler {

  SpaceInvadersManager spaceInvadersManager;

  public GameEventHandler(SpaceInvadersManager spaceInvadersManager) {
    this.spaceInvadersManager = spaceInvadersManager;
  }

  public void onEvent(Event event) {
    switch (event.getEventType()) {
      case "USER_INPUT":
        handleUserInput(event);
        break;
      case "PLAYER_DEATH":
        handlePlayerDeath(event);
        break;
      case "ENEMY_SPAWN":
        handleStairSpawn(event);
        break;
      case "PLAYER_SPAWN":
        handlePlayerSpawn(event);
        break;
      case "NULL":
        break;
      case "RECORD_START":
        startRecording();
        break;
      case "RECORD_STOP":
        stopRecording();
        break;
      case "RECORD_PLAY":
        playRecording(event);
        break;
      case "PLAYER_DISCONNECT":
        handlePlayerDisconnect(event);
        break;
      case "SCRIPT":
        handleScripts(event);
        break;
    }
  }

  private void handleScripts(Event event) {

  }

  private void handlePlayerDisconnect(Event event) {

  }

  private void startRecording() {
    spaceInvadersManager.engine.takeSnapshot();
    spaceInvadersManager.engine.getEventManager().setRecording(true);
  }

  private void stopRecording() {
    spaceInvadersManager.engine.getEventManager().setRecording(false);
  }

  private void playRecording(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    String replaySpeed = (String) eventParameters.get(0);

    switch (replaySpeed) {
      case "NORMAL":
        spaceInvadersManager.engine.replaySpeed = MainEngine.ReplaySpeed.NORMAL;
        spaceInvadersManager.engine.playRecordedGameObjects(1f);
        break;
      case "SLOW":
        spaceInvadersManager.engine.replaySpeed = MainEngine.ReplaySpeed.SLOW;
        spaceInvadersManager.engine.playRecordedGameObjects(2f);
        break;
      case "FAST":
        spaceInvadersManager.engine.replaySpeed = MainEngine.ReplaySpeed.FAST;
        spaceInvadersManager.engine.playRecordedGameObjects(0.5f);
        break;
    }
  }

  private void handlePlayerSpawn(Event event) {
    List<Object> eventParameters = event.getEventParameters();

    spaceInvadersManager.player = new Player(spaceInvadersManager.engine, (SpawnPoint) eventParameters.get(0));
    spaceInvadersManager.playerObjectID = spaceInvadersManager.engine.registerGameObject(spaceInvadersManager.player, spaceInvadersManager.playerObjectID, true);
    spaceInvadersManager.player.setConnectionID(spaceInvadersManager.getClientConnectionID().intValue());
  }

  private void handleStairSpawn(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    int enemyID = (Integer) eventParameters.get(0);
    PVector position = (PVector) eventParameters.get(1);

    Enemy enemy = new Enemy(spaceInvadersManager.engine, position, enemyID);
    spaceInvadersManager.enemies.add(enemy);
    spaceInvadersManager.enemiesObjectID = spaceInvadersManager.engine.registerGameObject(enemy, spaceInvadersManager.enemiesObjectID, true);
  }

  private void handlePlayerDeath(Event event) {

  }

  private void handleUserInput(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    int key = (Integer) eventParameters.get(0);
    boolean set = (Boolean) eventParameters.get(1);
    Player player = spaceInvadersManager.player;

    player.handleKeypress(key, set);

  }

}
