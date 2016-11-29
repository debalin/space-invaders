package com.debalin.engine.scripting;

import javax.script.*;

public class ScriptManager {

  private static ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
  private static Invocable jsInvocable = (Invocable) jsEngine;

  public static void bindArgument(String name, Object obj) {
    jsEngine.put(name, obj);
  }

  public static void loadScript(String scriptName) {
    try {
      jsEngine.eval(new java.io.FileReader(scriptName));
    } catch (ScriptException se) {
      se.printStackTrace();
    } catch (java.io.IOException iox) {
      iox.printStackTrace();
    }
  }

  public static void executeScript(String functionName) {
    try {
      jsInvocable.invokeFunction(functionName);
    } catch (ScriptException se) {
      se.printStackTrace();
    } catch (NoSuchMethodException nsme) {
      nsme.printStackTrace();
    }
  }

  public static void executeScript(String functionName, Object... args) {
    try {
      jsInvocable.invokeFunction(functionName, args);
    } catch (ScriptException se) {
      se.printStackTrace();
    } catch (NoSuchMethodException nsme) {
      nsme.printStackTrace();
    }
  }

}


