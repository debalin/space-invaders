package com.debalin;

import com.debalin.characters.Bullet;
import com.debalin.characters.Enemy;
import com.debalin.characters.Player;
import com.debalin.characters.SpawnPoint;
import com.debalin.engine.MainEngine;
import com.debalin.engine.events.Event;
import com.debalin.engine.events.EventHandler;
import com.debalin.engine.scripting.ScriptManager;
import processing.core.PVector;

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
        handlePlayerDeath();
        break;
      case "ENEMY_SPAWN":
        handleEnemySpawn(event);
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
      case "PLAYER_FIRE":
        handlePlayerFire(event);
        break;
      case "ENEMY_HIT":
        handleEnemyHit(event);
        break;
      case "SCRIPT":
        handleScripts(event);
        break;
    }
  }

  private void handleScripts(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    String scriptFunctionName = (String) eventParameters.get(0);

    if (scriptFunctionName.equals(spaceInvadersManager.engine.scriptFunctionName)) {
      spaceInvadersManager.engine.bindScriptObjects();
      ScriptManager.loadScript(spaceInvadersManager.engine.scriptPath);
      ScriptManager.executeScript(spaceInvadersManager.engine.scriptFunctionName);
    }
  }

  private void handleEnemyHit(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    int enemyID = (Integer) eventParameters.get(0);

    ((Enemy)spaceInvadersManager.enemyMap.get(enemyID)).reduceHealth();
  }

  private void handlePlayerFire(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    PVector bulletInitPosition = (PVector) eventParameters.get(0);

    Bullet bullet = new Bullet(spaceInvadersManager.engine, bulletInitPosition, spaceInvadersManager.enemies, spaceInvadersManager.player);
    spaceInvadersManager.bullets.add(bullet);
    spaceInvadersManager.bulletsObjectID = spaceInvadersManager.engine.registerGameObject(bullet, spaceInvadersManager.bulletsObjectID, true);
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

    spaceInvadersManager.player = new Player(spaceInvadersManager.engine, (SpawnPoint) eventParameters.get(0), spaceInvadersManager.enemies);
    spaceInvadersManager.playerObjectID = spaceInvadersManager.engine.registerGameObject(spaceInvadersManager.player, spaceInvadersManager.playerObjectID, true);
    spaceInvadersManager.player.setConnectionID(spaceInvadersManager.getClientConnectionID().intValue());
  }

  private void handleEnemySpawn(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    int enemyID = (Integer) eventParameters.get(0);
    PVector position = (PVector) eventParameters.get(1);
    PVector maxVelocity = (PVector) eventParameters.get(2);
    int moveInterval = (Integer) eventParameters.get(3);

    Enemy enemy = new Enemy(spaceInvadersManager.engine, position, enemyID, maxVelocity, moveInterval);
    spaceInvadersManager.enemies.add(enemy);
    spaceInvadersManager.enemyMap.put(enemyID, enemy);
    spaceInvadersManager.enemiesObjectID = spaceInvadersManager.engine.registerGameObject(enemy, spaceInvadersManager.enemiesObjectID, true);
  }

  private void handlePlayerDeath() {
    spaceInvadersManager.engine.delay(3000);
    spaceInvadersManager.reset();
  }

  private void handleUserInput(Event event) {
    List<Object> eventParameters = event.getEventParameters();
    int key = (Integer) eventParameters.get(0);
    boolean set = (Boolean) eventParameters.get(1);
    Player player = spaceInvadersManager.player;

    player.handleKeypress(key, set);

  }

}
