package com.debalin.engine.network;

import com.debalin.engine.Controller;
import com.debalin.engine.MainEngine;
import com.debalin.engine.events.Event;
import com.debalin.engine.timeline.Timeline;
import com.debalin.engine.util.EngineConstants;
import processing.core.PApplet;

import java.net.*;
import java.io.*;
import java.util.Queue;

public class GameClient implements Runnable {

  private Controller controller;
  private Socket clientConnection;
  private int remoteServerPort;
  private String remoteServerAddress;

  private MainEngine engine;

  public GameClient(String remoteServerAddress, int remoteServerPort, Controller controller, MainEngine engine) {
    this.controller = controller;
    this.remoteServerPort = remoteServerPort;
    this.remoteServerAddress = remoteServerAddress;
    this.engine = engine;
  }

  public void run() {
    System.out.println("Trying to connect to server at " + remoteServerAddress + ":" + remoteServerPort + ".");
    connectToServer();
    new Thread(() -> maintainServerReadConnection()).start();
    new Thread(() -> maintainServerWriteConnection()).start();
  }

  private void maintainServerReadConnection() {
    ObjectInputStream in = null;
    try {
      in = new ObjectInputStream(clientConnection.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true) {
      Event event = null;
      try {
        Object object = in.readObject();
        if (object.getClass().getTypeName().equals(Integer.class.getTypeName())) {
          controller.setClientConnectionID((Integer) object);
        }
        else if (object.getClass().getTypeName().equals(Timeline.class.getTypeName())) {
          engine.realTimelineInMillis = (Timeline) object;
          engine.realTimelineInMillis.engine = engine;
        }
        else {
          event = (Event) object;
        }
      }
      catch (IOException e) {
        if (e.getMessage().equals(EngineConstants.READ_ERROR_MESSAGE)) {
          System.out.println("IO Exception. Connection lost with server, will stop client read thread.");
          return;
        }
      }
      catch (Exception e) {
        e.printStackTrace();
        System.out.println("Connection lost with server, will stop client read thread.");
        return;
      }

      if (event != null)
        engine.getEventManager().raiseEvent(event, false);
    }
  }

  private void maintainServerWriteConnection() {
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(clientConnection.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }

    Queue<Event> writeQueue = engine.getEventManager().getWriteQueue(-1);

    while (true) {
      try {
        synchronized (writeQueue) {
          while (!writeQueue.isEmpty()) {
            Event event = writeQueue.poll();
            event.setConnectionID(controller.getClientConnectionID().intValue());
            out.writeObject(event);
          }

          out.reset();

          while (writeQueue.isEmpty())
            writeQueue.wait();
        }
      } catch (IOException e) {
        if (e.getMessage().equals(EngineConstants.WRITE_ERROR_MESSAGE)) {
          System.out.println("Connection lost with server, will stop client write thread.");
          return;
        }
      }
      catch (InterruptedException e) {}
    }
  }

  private void connectToServer() {
    try {
      clientConnection = new Socket(remoteServerAddress, remoteServerPort);
      System.out.println("Client accepted connection to " + clientConnection.getRemoteSocketAddress() + ".");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
