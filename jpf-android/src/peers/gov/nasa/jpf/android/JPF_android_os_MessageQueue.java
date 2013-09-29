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
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.android.checkpoint.ChecklistManager;
import gov.nasa.jpf.android.checkpoint.ParseException;
import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.ThreadList;
import gov.nasa.jpf.util.script.AndroidScriptEnvironment;
import gov.nasa.jpf.util.script.UIAction;

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

  private static final String UIACTION = "[UIAction]";

  /** Counts the total number of script actions that has been returned */
  private static int actionCount = 0;

  private static AndroidScriptEnvironment scriptEnv;

  private static int msqRef;

  /**
   * Called from the MesaageQueue Constructor, i.e. before each application run.
   * It opens and parses the input script and parses and setups the Checklists
   * environment.
   */
  public static void init____V(MJIEnv env, int objref) {
    //TODO make SURE this the only done for the main thread
    msqRef = objref;
    JPF jpf = env.getJPF();
    try {
      setupScriptEnv(jpf);
      setupChecklistEnv(jpf);

      jpf.addVMListener(new ThreadTerminationListener());

    } catch (Exception e) {
      log.severe(TAG + ": " + e.getMessage());
    }
  }

  /**
   * This method is responsible for setting up the scripting environment
   * 
   * @param jpf
   *          used to register the StateExtension and Publisher listeners.
   * @throws Exception
   */
  private static void setupScriptEnv(JPF jpf) throws Exception {
    Config conf = jpf.getConfig();

    String scriptName = conf.getString("android.script");
    if (scriptName == null) {
      throw new Exception("No \"android.script\" property in JPF property file.");
    }
    log.info(TAG + ": Running script " + scriptName);

    try {
      scriptEnv = new AndroidScriptEnvironment(scriptName);
      scriptEnv.registerListener(jpf);
      scriptEnv.parseScript();
    } catch (FileNotFoundException fnfx) {
      throw new Exception("Script file (.es) not found: " + scriptName);
    }

  }

  /**
   * 
   * @param jpf
   * @throws ParseException
   */
  private static void setupChecklistEnv(JPF jpf) throws ParseException {
    Config config = jpf.getConfig();
    String checkProperty = config.getProperty("android.checklist_enabled");

    if (checkProperty.equals("true")) {
      log.info(TAG + ": " + "Checklist verification active.");

      // get checklist definition filename
      String filename = config.getProperty("android.checklist");
      // get active checklists to check
      String[] active = config.getStringArray("android.active_checklists");

      ChecklistManager checklistManager = new ChecklistManager(filename, active);
      checklistManager.createReporter(scriptEnv);
      checklistManager.registerListener(jpf);
      JPF_android_os_Handler.checkpointManager = checklistManager;
    } else {
      log.info(TAG + ": " + "Checklist verification inactive.");
    }

  }

  /**
   * Called from within the message queue to retrieve new message when queue is
   * empty. If we return false, it means there is nothing else to check and we
   * are done
   */
  public static int processScriptAction(MJIEnv env, int objref, int count) {
    ThreadInfo ti = env.getThreadInfo();

    if (!ti.hasReturnedFromDirectCall(UIACTION)) {
      UIAction action = getNextAction(env, actionCount);

      // execute this action
      if (action != null) {

        // increase next action count
        actionCount++;

        //set this event as the new event
        env.setStaticIntField("android.os.MessageQueue", "eventID", actionCount);
        env.setStaticIntField("android.os.MessageQueue", "pathID", 0);

        log.info(TAG + ": ******************************* MSQ: " + count + "  eventID:" + actionCount
            + " pathID:" + 0);
        // execute this action (better to add msg to queue but might be difficult in terms of runnables)     
        executeAction(env, action);
        return env.newString(action.toString());
      }
      // there was no next action to return
      return MJIEnv.NULL;

    }
    // does not matter what we return this is only executed when if UIAction direct call has returned.
    return env.newString("");
  }

  private static UIAction getNextAction(MJIEnv env, int actionCount) {

    //get current window
    String window = JPF_android_view_WindowManager.getCurrentWindow(env);

    //sanitycheck
    if (scriptEnv == null) {
      log.severe(TAG + ": ScriptEnvironment not created, terminating");
      return null;
    }

    // get next script actions
    UIAction action = scriptEnv.getNext(actionCount + 1, window, env);

    return action;
  }

  /**
   * Sends the action to the appropriate native peer to handle
   * 
   * @param env
   * @param action
   */
  private static void executeAction(MJIEnv env, UIAction action) {
    //get current window
    String currentWindow = JPF_android_view_WindowManager.getCurrentWindow(env);
    log.info(TAG + ": Processing action \"" + action.action + "\" on \"" + action.target + "\" on Window \""
        + currentWindow + "\"");

    if (!action.isNone()) {
      if (action.target == null) {
        // componentAction:  startMainActivity() startService() killApplication()
        JPF_com_android_server_am_ActivityManagerService.handleComponentAction(env, action);

      } else if (action.target.startsWith("$")) {
        // viewAction includes: $buttonOK.click() $cbxAccept.check(), $cbxAccept.unCheck() $listView.selectItem(5) $radioList.selectItem(5), $textbox.type("Hallo"), $textbox.setText("Hallo")  
        JPF_android_view_WindowManager.handleViewAction(env, action);

      } else if (action.target.startsWith("@")) {
        // intent building
        JPF_com_android_server_am_ActivityManagerService.setIntent(env, action);

      } else if (action.target.equals("device")) {
        // device actions: device.pressHome(), device.pressPower(), device.pressVolume("up")
        //                 device.pressMenu(), device.rotate("landscape"), device.setWifi("off"),
        //                 device.setBattery("50%"), device.setGPS("on"), device.inCommingCall(intent)
        //                 device.setCharging("USB")
        JPF_android_os_ServiceManager.performDeviceAction(env, action);

      } else {
        log.info(TAG + ": Unknown action \"" + action.action + ".");
      }
    }
  }

  public static void notifyMSQ(JVM vm) {
    ThreadInfo ti = vm.getCurrentThread();

    MethodInfo mi = vm.getClassInfo(msqRef).getMethod("enqueueStop()V", true);

    // Create direct call stub with identifier [UIAction]
    MethodInfo stub = mi.createDirectCallStub("[UIACTION]");
    DirectCallStackFrame frame = new DirectCallStackFrame(stub);

    // if the method is not static the reference to the object is pushed to
    // allow access to fields
    if (!mi.isStatic()) {
      frame.push(msqRef, true);
    }
    // frame is pushed to the execution thread
    ti.pushFrame(frame);
  }

  public static boolean isRunningThreads(MJIEnv env, int ref) {
    if (env.getVM().getAliveThreadCount() > 1)
      return true;
    else
      return false;

  }

  protected static class ThreadTerminationListener extends ListenerAdapter {

    @Override
    public void threadTerminated(JVM vm) {
      ThreadList infos = vm.getThreadList();
      ThreadInfo mainInfo = infos.getThreadInfoForId(0);
      if (vm.getAliveThreadCount() == 1 && mainInfo.isWaiting()) {
        notifyMSQ(vm);
      }
    }
  }
}