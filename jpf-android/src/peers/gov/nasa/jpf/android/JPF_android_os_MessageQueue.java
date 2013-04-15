//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.util.script.ESParserE;
import gov.nasa.jpf.util.script.ScriptingEnvironment;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

/**
 * Native counterpart of the MessageQueue class.
 * 
 * @author Heila van der Merwe
 * 
 */
public class JPF_android_os_MessageQueue {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");
  static final String UIACTION = "[UIAction]";

  static int counter; // the number of UIActionCGs generated so far

  // static UIActionGeneratorFactory cgFactory;
  static ScriptingEnvironment scriptEnv;

  /**
   * Called from the MesaageQueue Constructor, i.e. before each application run. It opens and parses the input
   * script.
   */
  public static void init____V(MJIEnv env, int objref) {
    Config conf = env.getConfig();

    counter = 0;

    String scriptName = conf.getString("android.script");
    log.info("Running script: " + scriptName);

    if (scriptName == null) {
      scriptName = conf.getString("android.script");

      if (scriptName == null) {
        log.severe("no \"android.script\" or \"inspect.script\" property");
        return;
      }
    }
    try {
      scriptEnv = new ScriptingEnvironment(scriptName);
      scriptEnv.registerListener(env.getJPF());
      scriptEnv.parseScript();
    } catch (FileNotFoundException fnfx) {
      log.severe("script file not found: " + scriptName);
    } catch (ESParserE.Exception e) {
      log.severe(e.toString());
    }
  }

  /**
   * Called from within the message queue to retrieve new message when queue is empty. If we return false, it
   * means there is nothing else to check and we are done
   */
  public static boolean processScriptAction(MJIEnv env, int objref) {
    ThreadInfo ti = env.getThreadInfo();
    SystemState ss = env.getSystemState();
    Instruction insn = ti.getPC();

    if (scriptEnv == null) {
      log.warning("no UIScriptEnvironment, terminating");
      return false;
    }
    if (!ti.hasReturnedFromDirectCall(UIACTION)) { // before direct call to
      String currentWindow = JPF_android_view_WindowManager.getCurrentWindow(env);
      System.out.println("Current Window: " + currentWindow);
      UIAction action = scriptEnv.getNext("processScriptAction", currentWindow, env);
      if (action != null) {
        System.out.println("[ACTION] " + action.action + " " + action.target);
        runAction(env, action);
        return true;
      }

      return false;
    }
    return true;
  }

  /**
   * Sends the action to the appropriate native peer to handle
   * 
   * @param env
   * @param action
   */
  private static void runAction(MJIEnv env, UIAction action) {
    System.out.println("*******************************");
    log.info("ProcessAction: " + action.action + " on " + action.target);

    if (!action.isNone()) {
      if (action.target == null) { // componentAction
        JPF_com_android_server_am_ActivityManager.handleComponentAction(env, action);
      } else if (action.target.startsWith("$")) { // viewAction
        JPF_android_view_WindowManager.handleViewAction(env, action);
      } else if (action.target.startsWith("@")) { // intentAction
        JPF_com_android_server_am_ActivityManager.setIntent(env, action);
      }
    }
  }

}