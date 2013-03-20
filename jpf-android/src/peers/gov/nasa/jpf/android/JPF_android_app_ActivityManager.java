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

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Models the Android ActivityManagerService. This Service is traditionally run in the system process and is
 * not part of the application process. That is why it is modelled in native code. This class is not
 * scheduling relevant. It is necessary to model the communication between the system and the application. For
 * testing purposes this class only supports this one application and assumes that for now no other
 * applications are install
 * 
 * @see com.android.server.am.ActivityManagerService
 * @see android.app.ActivityManagerProxy
 * @see android.app.ActivityManagerNative
 * 
 * @author Heila van der Merwe
 * 
 */
public class JPF_android_app_ActivityManager {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  /**
   * Reference to the instance of this class. This is so that we can call its modelled methods such as
   * startActivity() from the native side even if it is started from the native massageQueue class.
   */
  private static int activityManagerRef;

  /** ID for stack frames pushed by direct calls from the native code */
  private static final String UIACTION = "[UIAction]";

  /** Stores details of Intent objects variables used in the scripting file */
  private static HashMap<String, IntentEntry> intentMap = new HashMap<String, IntentEntry>();

  /**
   * Setup method that parses the Application's packageInfo.
   * 
   * @param env
   * @param objectRef
   */
  public static void init0(MJIEnv env, int objectRef) {
    activityManagerRef = objectRef;
  }

  /**
   * Forwards script actions related to application components to the ActivityManager model as if they were
   * called from the system. These actions include starting and stopping activities and sending broadcasts
   * etc. that are scripted in the application's *.es file.
   * 
   * @param env
   * @param action
   *          - the component action to handle
   */
  static void handleComponentAction(MJIEnv env, UIAction action) {
    log.fine("Handling component action: " + action.toString());

    if (action.action.equals("startActivity")) {
      String intentName = (String) action.arguments[0];
      int intentref = createJPFIntent(env, intentName);
      startActivityNative(env, 0, intentref, -1);
    } else if (action.action.equals("changeLayout")) {
      String layout = (String) action.arguments[0]; // TODO layout is not used
      changeLayout(env, 0, 1);
    } else if (action.action.equals("homeButton")) {
      homeButton(env);
    } else if (action.action.equals("sendBroadcast")) {
      // TODO
    } else if (action.action.equals("lowBattery")) {
      // TODO
    } else if (action.action.equals("networkDown")) {
      // TODO
    } else if (action.action.equals("killActivity")) {
      // TODO
    } else if (action.action.equals("killService")) {
      // TODO
    }
  }

  /**
   * Used to start an Activity from the MessageQueue class.
   * 
   * @param env
   * @param intentRef
   *          the reference to the intent starting the activity
   */
  private static void startActivityNative(MJIEnv env, int clsRef, int intentRef, int requestCode) {
    String methodName = "performLaunchActivity(Landroid/content/Intent;I)V";
    int[] args = { intentRef, requestCode };
    callMethod(env, activityManagerRef, methodName, args);

  }

  private static void finishActivityNative(MJIEnv env, int clsRef, int resultCode, int resultDataRef) {
    // Lookup the name of the activity to launch
    // int activityNameRef = getActivity(env, intentRef);
    // String activityName = env.getStringObject(activityNameRef);
    log.fine("Finishing activity ");

    // schedule launch of activity
    String methodName = "performFinishActivity(ILandroid/content/Intent;)V";
    int[] args = { resultCode, resultDataRef };

    callMethod(env, activityManagerRef, methodName, args);
  }

  /**
   * 
   * @param env
   * @param orientation
   *          the new orientation
   */
  private static void changeLayout(MJIEnv env, int clsRef, int orientation) {
    log.fine("changing layout to " + orientation);

    // schedule launch of activity
    String methodName = "performConfigurationChange()V";
    int[] args = {};

    callMethod(env, activityManagerRef, methodName, args);

  }

  private static void homeButton(MJIEnv env) {
    // schedule launch of activity
    String methodName = "performHomePressed()V";
    int[] args = {};
    callMethod(env, activityManagerRef, methodName, args);

  }

  /* ***************************** System Methods ******************************* */

  /**
   * Retrieves the reference to an intent in the intentMap using its name specified by action.target. If no
   * such intent exists, an Intent object is created and stored in the intentMap. It then sets the field of
   * the intent (specified by action.getAction()) with value action.arguments[0] which is the value of the
   * field to set..
   * 
   * @param env
   * @param action
   *          - action as read from the script file
   */
  public static void setIntent(MJIEnv env, UIAction action) {
    System.out.println("Setting " + action.toString());
    IntentEntry intent = intentMap.get(action.getTarget());

    if (intent == null) {
      intent = new IntentEntry();
      intentMap.put(action.target, intent);
    }

    // Use reflection to call the setter method on the Intent object
    @SuppressWarnings("unchecked")
    Class<IntentEntry> intentClass = (Class<IntentEntry>) intent.getClass();
    try {
      Method m = intentClass.getMethod(action.getAction(), String.class);
      m.invoke(intent, action.getArguments());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the reference to an the Intent object. This has to be extended to allow not only explicit intents
   * but also implicit intents.
   * 
   * @param env
   * @param intent
   * 
   * @return
   */
  private static int createJPFIntent(MJIEnv env, String intentName) {
    IntentEntry intent = intentMap.get(intentName);
    int intentRef = env.newObject("android.content.Intent");
    int componentRef = env.newObject("android.content.ComponentName");
    ElementInfo eiComp = env.getElementInfo(componentRef);
    intent.setClassName(intent.getClassName());
    intent.setPackage(intent.getPackage());
    int packageRef = env.newString(intent.getPackage());
    int classRef = env.newString(intent.getClassName());
    eiComp.setReferenceField("mPackage", packageRef);
    eiComp.setReferenceField("mClass", classRef);
    ElementInfo ei = env.getElementInfo(intentRef);
    ei.setReferenceField("mComponent", componentRef);
    return intentRef;

  }

  /**
   * Uses a direct call to call a method on ActivityManagerProxy.
   * 
   * @param env
   * @param methodName
   *          the method signature of the method to call directly
   * @param args
   *          the arguments of the method
   */
  private static void callMethod(MJIEnv env, int classRef, String methodName, int[] argsRefs) {

    ThreadInfo ti = env.getThreadInfo();
    MethodInfo mi = env.getClassInfo(classRef).getMethod(methodName, true);

    // Create direct call stub with identifier [UIAction]
    MethodInfo stub = mi.createDirectCallStub(UIACTION);
    DirectCallStackFrame frame = new DirectCallStackFrame(stub);

    // if the method is not static the reference to the object is pushed to
    // allow access to fields
    if (!mi.isStatic()) {
      frame.push(classRef, true);
    }

    // arguments for the method is pushed on the frame
    if (argsRefs != null) {
      for (int i = 0; i < argsRefs.length; i++) {
        frame.push(argsRefs[i], true);
      }
    }
    // frame is pushed to the execution thread
    ti.pushFrame(frame);
  }

}
