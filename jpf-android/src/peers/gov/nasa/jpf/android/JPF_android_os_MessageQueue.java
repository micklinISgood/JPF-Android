//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA). All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3. The NOSA has been approved by the Open Source
// Initiative. See the file NOSA-1.3-JPF at the top of the distribution
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
import gov.nasa.jpf.android.checkpoint.ChecklistManager;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.util.script.AndroidScriptEnvironment;
import gov.nasa.jpf.util.script.ESParserE;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

/**
 * Native counterpart of the MessageQueue class.
 * 
 * @author Heila van der Merwe
 * 
 */
public class JPF_android_os_MessageQueue {
  private static final String TAG = "JPF_MessageQueue";
  static Logger log = JPF.getLogger(TAG);
  static final String UIACTION = "[UIAction]";

  static AndroidScriptEnvironment scriptEnv;

  static ChecklistManager checkpointManager;

  /**
   * Called from the MesaageQueue Constructor, i.e. before each application run.
   * It opens and parses the input script.
   */
  public static void init____V(MJIEnv env, int objref) {
    Config conf = env.getConfig();

    String scriptName = conf.getString("android.script");
    log.info(TAG + ": Running script " + scriptName);

    if (scriptName == null) {
      scriptName = conf.getString("android.script");

      if (scriptName == null) {
        log.severe(TAG + ": No \"android.script\" property in JPF property file.");
        return;
      }
    }
    try {
      scriptEnv = new AndroidScriptEnvironment(scriptName);
      scriptEnv.registerListener(env.getJPF());
      scriptEnv.parseScript();

      checkpointManager = new ChecklistManager();
      checkpointManager.registerListener(env.getJPF());

    } catch (FileNotFoundException fnfx) {
      log.severe(TAG + ": Script file (.es) not found: " + scriptName);
    } catch (ESParserE.Exception e) {
      log.severe(TAG + ": Error: " + e.toString());
    }
  }

  /**
   * Called from within the message queue to retrieve new message when queue is
   * empty. If we return false, it means there is nothing else to check and we
   * are done
   */
  public static boolean processScriptAction(MJIEnv env, int objref, int count) {
    ThreadInfo ti = env.getThreadInfo();

    if (scriptEnv == null) {
      log.warning("ScriptEnvironment not created, terminating");
      return false;
    }

    if (!ti.hasReturnedFromDirectCall(UIACTION)) { // before direct call to
      String currentWindow = JPF_android_view_WindowManager.getCurrentWindow(env);
      
      UIAction action = scriptEnv.getNext("processScriptAction", currentWindow, env);
      if (action != null) {
        System.out.println("******************************* " + count);
        log.info(TAG + ": Processing action \"" + action.action + "\" on \"" + action.target
            + "\" on Window \"" + currentWindow + "\"");
        runAction(env, action, currentWindow);
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
  private static void runAction(MJIEnv env, UIAction action, String currentWindow) {
    if (!action.isNone()) {
      if (action.target == null) { // componentAction sendBroadcast() startActivity() startService() registerListener()
        if (action.action.equals("registerChecklist")) {
          checkpointManager.registerCheckList(action.getArguments(), action.getLine());
        } else if (action.action.equals("unregisterChecklist")) {
          checkpointManager.unregisterCheckList((String) action.getArguments()[0]);
        } else {
          JPF_com_android_server_am_ActivityManagerService.handleComponentAction(env, action);
        }
      } else if (action.target.startsWith("$")) { // viewAction includes: $back, $homeButton.onClick(), $powerButton.onClick(), $volumeButton.onClick(), $menuButton.onClick(), $orientation.onClick()
        JPF_android_view_WindowManager.handleViewAction(env, action);
      } else if (action.target.startsWith("@")) { // references annotation in code 
        JPF_com_android_server_am_ActivityManagerService.setIntent(env, action); //temp until we need this
      } else {      // intent action:  intent1.setAction("Hallo")
        JPF_com_android_server_am_ActivityManagerService.setIntent(env, action);
      }
    }
  }
}