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
import gov.nasa.jpf.android.checkpoint.ChecklistException;
import gov.nasa.jpf.android.checkpoint.ChecklistManager;
import gov.nasa.jpf.android.checkpoint.ParseException;
import gov.nasa.jpf.android.checkpoint.Path;
import gov.nasa.jpf.android.checkpoint.ThreadListener;
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.util.script.AndroidScriptEnvironment;
import gov.nasa.jpf.util.script.ScriptException;
import gov.nasa.jpf.util.script.UIAction;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.VM;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

/**
 * Native counterpart of the MessageQueue class.
 * 
 * @author Heila van der Merwe
 * 
 */
public class JPF_android_os_MessageQueue extends NativePeer {
  private static final String TAG = "JPF_MessageQueue";
  static Logger log = JPF.getLogger(TAG);

  /** Counts the *total* number of *script* actions that has been processed */
  private static int actionCount = 0;

  private static AndroidScriptEnvironment scriptEnv;

  /**
   * The MesaageQueue Constructor, i.e. before each application run.
   * It opens and parses the input script and parses and setups the Checklists
   * environment.
   */
  @MJI
  public void init0____V(MJIEnv env, int robj) {

    //TODO make SURE this the only done for the main thread's msq queue
    // if (env.getThreadInfo().getName().equals("main"))
    JPF jpf = env.getJPF();

    // in case of rescheduling of msq creation
    if (scriptEnv == null) {
      try {
        setupScriptEnv(jpf);
        setupChecklistEnv(jpf);
//        jpf.addListener(new ThreadTerminationListener());

      } catch (ScriptException e) {
        log.severe(AndroidScriptEnvironment.TAG + ": " + e.getMessage());
        throw new RuntimeException(AndroidScriptEnvironment.TAG + ": " + e.getMessage());
      } catch (ChecklistException e) {
        log.severe(ChecklistManager.TAG + ": " + e.getMessage());
        throw new RuntimeException(ChecklistManager.TAG + ": " + e.getMessage());
      }
    }
  }

  /**
   * This method is responsible for setting up the scripting environment
   * 
   * @param jpf
   *          used to register the StateExtension and Publisher listeners.
   * @throws Exception
   */
  private static void setupScriptEnv(JPF jpf) throws ScriptException {
    Config conf = jpf.getConfig();

    String scriptName = conf.getString("android.script");

    // check if scriptname set in config
    if (scriptName == null) {
      throw new ScriptException("No \"android.script\" property in JPF property file.");
    }

    try {

      scriptEnv = new AndroidScriptEnvironment(scriptName);
      scriptEnv.registerListener(jpf);
      scriptEnv.parseScript();
      log.info(AndroidScriptEnvironment.TAG + ": Running script " + scriptName);

    } catch (FileNotFoundException fnfx) {
      throw new ScriptException("Script file (.es) not found: " + scriptName);
    } catch (gov.nasa.jpf.util.script.ESParser.Exception e) {
      throw new ScriptException("Error parsing input script: " + e.getMessage());
    }

  }

  /**
   * 
   * @param jpf
   * @throws ParseException
   */
  private static void setupChecklistEnv(JPF jpf) throws ChecklistException {
    Config config = jpf.getConfig();
    String checkProperty = config.getProperty("android.checklist_enabled");

    if (checkProperty.equals("true")) {
      try {
        log.info(ChecklistManager.TAG + ": " + "Checklist verification active.");

        // get checklist definition filename
        String filename = config.getProperty("android.checklist");

        // get active checklists to check
        String[] activeChecklists = config.getStringArray("android.active_checklists");

        ChecklistManager checklistManager = new ChecklistManager(filename);
        checklistManager.activateChecklists(activeChecklists);
        checklistManager.createReporter(scriptEnv);
        checklistManager.registerListener(jpf);

      } catch (ParseException e) {
        throw new ChecklistException(e.getMessage());
      }

    } else {
      log.warning(ChecklistManager.TAG + ": " + "Checklist verification not active.");
    }

  }

  /**
   * Called from within the message queue to retrieve new message when queue is
   * empty. If we return false, it means there is nothing else to check and we
   * are done
   */
  @MJI
  public int getNextScriptAction(MJIEnv env, int objref, int count) {
    ThreadInfo ti = env.getThreadInfo();
    UIAction action = null;

    DirectCallStackFrame frame = ti.getReturnedDirectCall();
    if (frame == null
        || (!frame.getMethodName().contains("perform") && !frame.getMethodName().contains("handle")
            && !frame.getMethodName().contains("processValues") && !frame.getMethodName().contains(
            "changeNetworkState"))) {

      action = getNextAction(env, actionCount);

      // execute this action
      if (action != null) {
        // increase next action count
        actionCount++;

        //set this event as the new event
        ClassInfo ci = ClassLoaderInfo.getCurrentResolvedClassInfo("android.os.MessageQueue");

        // change path of main thread
        ci.getModifiableStaticElementInfo().setIntField("currentEvent", actionCount);
        ci.getModifiableStaticElementInfo().setReferenceField("currentPath", env.newString("0"));

        log.info(TAG +  ": *************** MSG #:" + count + " eventID:" + actionCount + " pathID:"
            + "0" + " action:" + action);     
        
        // execute this action (better to add msg to queue but might be difficult in terms of runnables)     
        executeAction(env, action);

        // return the string representation of the action that *was* executed
        return env.newString(action.toString());

      } else {
        // there was no next action to return
        return MJIEnv.NULL;
      }
    } else {
      // does not matter what we return this is only executed when if UIAction direct call has
      // returned. We are not yet finished so we do not return NULL we return an empty string to
      // indicate this.
      return (action != null) ? env.newString(action.toString()) : env.newString("");
    }
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

  @MJI
  public boolean hasOtherRunningThreads(MJIEnv env, int ref) {
    int count = 0;
    ThreadInfo[] list = env.getVM().getLiveThreads();
    for (ThreadInfo info : list) {
      if (info.isRunnable()) {
        count++;
      }
    }

    if (count > 1)
      return true;
    else
      return false;

  }

  /**
   * 
   * @param env
   * @param rThread
   * @return
   */
  @MJI
  public int getCurrentPath(MJIEnv env, int objRef, int rThread) {
    // check if this thread has a looper
    ThreadInfo ti = env.getThreadInfo();
    if (ti.getName().equals("main")) {

      ClassInfo ci = ClassLoaderInfo.getCurrentResolvedClassInfo("android.os.MessageQueue");
      int path = ci.getStaticElementInfo().getReferenceField("currentPath");
      return path;
    } else {
      //else lookup info of this thread
      Path path = ThreadListener.getPath(ti.getGlobalId());
      String newPath = path.getPathID() + "1";
      path.setPathID(path.getPathID() + "0");
      return env.newString(newPath);
    }
  }

  /**
   * 
   * @param env
   * @param rThread
   * @return
   */
  @MJI
  public int getCurrentEvent(MJIEnv env, int objRef, int rThread) {
    // check if this thread has a looper
    ThreadInfo ti = env.getThreadInfo();
    if (ti.getName().equals("main")) {

      ClassInfo ci = ClassLoaderInfo.getCurrentResolvedClassInfo("android.os.MessageQueue");
      int event = ci.getStaticElementInfo().getIntField("currentEvent");
      return event;
    } else {
      //else lookup info of this thread
      return ThreadListener.getPath(ti.getGlobalId()).getEventID();
    }

  }

//  protected static class ThreadTerminationListener extends ListenerAdapter {
//
//    @Override
//    public void threadTerminated(VM vm, ThreadInfo terminatedThread) {
//      ThreadList infos = vm.getThreadList();
//      ThreadInfo mainInfo = infos.getThreadInfoForId(0);
//      if (vm.getAliveThreadCount() == 1 && mainInfo.isWaiting()) {
//        mainInfo.setRunning();
//      }
//    }
//  }

  //  public static void notifyMSQ(VM vm) {
  //    ThreadInfo ti = vm.getCurrentThread();
  //    MethodInfo mi = vm.getClassInfo(msqRef).getMethod("enqueueStop()V", true);
  //
  //    // Create direct call stub with identifier [UIAction]
  //    MethodInfo stub = mi.createDirectCallStub("[UIACTION]");
  //    DirectCallStackFrame frame = new DirectCallStackFrame(stub);
  //
  //    // if the method is not static the reference to the object is pushed to
  //    // allow access to fields
  //    if (!mi.isStatic()) {
  //      frame.push(msqRef, true);
  //    }
  //    // frame is pushed to the execution thread
  //    ti.pushFrame(frame);
  //  }
}