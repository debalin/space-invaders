package com.debalin.engine.network;

import com.debalin.engine.Controller;
import com.debalin.engine.MainEngine;
import com.debalin.engine.events.Event;
import com.debalin.engine.util.EngineConstants;

import java.net.*;
import java.io.*;
import java.util.*;

public class GameServer implements Runnable {

  private ServerSocket serverSocket;
  private int localServerPort;
  private Controller controller;
  private List<Socket> serverConnections;

  private MainEngine engine;

  public GameServer(int localServerPort, Controller controller, MainEngine engine) {
    this.localServerPort = localServerPort;
    this.controller = controller;
    this.serverConnections = new ArrayList<>();
    this.engine = engine;

    try {
      serverSocket = new ServerSocket(this.localServerPort);
      System.out.println("Server started at IP " + serverSocket.getLocalSocketAddress() + ".");
    } catch (IOException e) {
      System.out.println("Some error in GameServer.");
      e.printStackTrace();
    }
  }

  public void run() {
    while (true) {
      System.out.println("Looking for connections.");
      Socket serverConnection = acceptConnections();
      if (serverConnection != null) {
        serverConnections.add(serverConnection);
        new Thread(() -> maintainClientReadConnection(serverConnection, serverConnections.size() - 1)).start();
        new Thread(() -> maintainClientWriteConnection(serverConnection, serverConnections.size() - 1)).start();
      }
    }
  }

  private void maintainClientReadConnection(Socket serverConnection, int connectionID) {
    ObjectInputStream in = null;
    try {
      in = new ObjectInputStream(serverConnection.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true) {
      Event event;
      try {
        event = (Event) in.readObject();
        engine.getEventManager().broadcastEvent(event, connectionID);
      } catch (IOException e) {
        if (e.getMessage().equals(EngineConstants.READ_ERROR_MESSAGE)) {
          System.out.println("IO Exception. Connection lost with client " + serverConnection.getRemoteSocketAddress() + ", will stop server read thread.");
          List<Object> eventParameters = new ArrayList<>();
          eventParameters.add(connectionID);
          event = new Event(EngineConstants.DEFAULT_EVENT_TYPES.PLAYER_DISCONNECT.toString(), eventParameters, EngineConstants.DEFAULT_TIMELINES.GAME_MILLIS.toString(), connectionID, engine.gameTimelineInMillis.getTime(), true);
          synchronized (engine.getEventManager().fromServerWriteQueues) {
            engine.getEventManager().fromServerWriteQueues.remove(connectionID);
          }
          engine.getEventManager().broadcastEvent(event, connectionID);
          return;
        }
      } catch (Exception e) {
        System.out.println("Connection lost with client " + serverConnection.getRemoteSocketAddress() + ", will stop server read thread.");
        return;
      }
    }
  }

  private void maintainClientWriteConnection(Socket serverConnection, int connectionID) {
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(serverConnection.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }

    Queue<Event> writeQueue = engine.getEventManager().getWriteQueue(connectionID);
    boolean sendConnectionID = true;

    while (true) {
      try {
        synchronized (writeQueue) {
          if (sendConnectionID) {
            out.writeObject(new Integer(connectionID));
            out.writeObject(engine.realTimelineInMillis);
            sendConnectionID = false;
          }
          while (!writeQueue.isEmpty()) {
            out.writeObject(writeQueue.poll());
          }

          out.reset();

          while (writeQueue.isEmpty())
            writeQueue.wait();
        }
      } catch (IOException e) {
        if (e.getMessage().equals(EngineConstants.WRITE_ERROR_MESSAGE)) {
          System.out.println("Connection lost with client " + serverConnection.getRemoteSocketAddress() + ", will stop server write thread.");
          return;
        }
      } catch (InterruptedException e) {
        System.out.println("Some issue in writeQueue.");
      }
    }
  }

  private Socket acceptConnections() {
    Socket serverConnection = null;
    try {
      serverConnection = serverSocket.accept();
      System.out.println("Server accepted connection to " + serverConnection.getRemoteSocketAddress() + ".");
    } catch (IOException e) {
      e.printStackTrace();
    }

    return serverConnection;
  }

}
