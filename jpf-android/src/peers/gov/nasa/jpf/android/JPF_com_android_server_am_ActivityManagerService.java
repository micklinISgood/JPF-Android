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

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import android.os.Bundle;

/**
 * Models the Android ActivityManagerService. This Service is traditionally run
 * in the system process and is
 * not part of the application process. That is why it is modelled in native
 * code. This class is not
 * scheduling relevant. It is necessary to model the communication between the
 * system and the application. For
 * testing purposes this class only supports this one application and assumes
 * that for now no other
 * applications are install
 * 
 * @see com.android.server.am.ActivityManagerService
 * @see android.app.ActivityManagerNative
 * 
 * @author Heila van der Merwe
 * 
 */
public class JPF_com_android_server_am_ActivityManagerService {
  public static final String TAG = "JPF_ActivityManagerService";
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  /**
   * Reference to the instance of this class. This is so that we can call its
   * modelled methods such as
   * startActivity() from the native side even if it is started from the native
   * massageQueue class.
   */
  private static int activityManagerRef;

  /** ID for stack frames pushed by direct calls from the native code */
  private static final String UIACTION = "[UIAction]";

  /** Stores details of Intent objects variables used in the scripting file */
  private static HashMap<String, IntentEntry> intentMap = new HashMap<String, IntentEntry>();

  /**
   * Stores reference this class so that we can call methods on the
   * ActivityManager from the native peer.
   * 
   * @param env
   * @param objectRef
   */
  public static void init0(MJIEnv env, int activityManagerRef) {
    JPF_com_android_server_am_ActivityManagerService.activityManagerRef = activityManagerRef;
    createPreDefinedIntents();

  }

  public static void createPreDefinedIntents() {
    IntentEntry intent = new IntentEntry();
    intent.setAction("android.net.conn.CONNECTION_CHANGE");
    intent.putExtraString("type", "WIFI");
    intent.putExtraString("state", "DISCONNECTED");
    intent.putExtraString("reason", "");
    intent.putExtraString("extra", "");
    intentMap.put("@WifiOffIntent", intent);

    intent = new IntentEntry();
    intent.setAction("android.net.conn.CONNECTION_CHANGE");
    intent.putExtraString("type", "WIFI");
    intent.putExtraString("state", "CONNECTED");
    intent.putExtraString("reason", "");
    intent.putExtraString("extra", "");
    intentMap.put("@WifiOnIntent", intent);

    intent = new IntentEntry();
    intent.setAction("android.net.conn.CONNECTION_CHANGE");
    intent.putExtraString("type", "WIFI");
    intent.putExtraString("state", "SUSPENDED");
    intent.putExtraString("reason", "");
    intent.putExtraString("extra", "");
    intentMap.put("@WifiSuspendedIntent", intent);

    intent = new IntentEntry();
    intent.setAction("android.net.conn.CONNECTION_CHANGE");
    intent.putExtraString("type", "MOBILE");
    intent.putExtraString("state", "DISCONNECTED");
    intent.putExtraString("reason", "");
    intent.putExtraString("extra", "");
    intentMap.put("@MobileDisconnectedIntent", intent);

    intent = new IntentEntry();
    intent.setAction("android.net.conn.CONNECTION_CHANGE");
    intent.putExtraString("type", "MOBILE");
    intent.putExtraString("state", "CONNECTED");
    intent.putExtraString("reason", "");
    intent.putExtraString("extra", "");
    intentMap.put("@MobileConnectedIntent", intent);

    intent = new IntentEntry();
    intent.setAction("android.net.conn.CONNECTION_CHANGE");
    intent.putExtraString("type", "MOBILE");
    intent.putExtraString("state", "SUSPENDED");
    intent.putExtraString("reason", "");
    intent.putExtraString("extra", "");
    intentMap.put("@WifiConnectedIntent", intent);

    intent = new IntentEntry();
    intent.setAction("android.net.conn.URL_INPUT_STREAM");
    intentMap.put("@urlInputStreamIntent", intent);

  }

  /**
   * Forwards script actions related to application components to the
   * ActivityManager model as if they were
   * called from the system. These actions include starting and stopping
   * activities and sending broadcasts
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
      String intentName = (String) action.arguments[0];
      sendBroadcast(env, intentName);
    } else if (action.action.equals("killActivity")) {
      // TODO
    } else if (action.action.equals("killService")) {
      // TODO
    }
  }

  /**
   * Handles all Broadcasts sent from the script.
   * 
   * @param env
   * @param intentName
   */
  private static void sendBroadcast(MJIEnv env, String intentName) {
    //get the actual intent from the map.
    IntentEntry intent = intentMap.get(intentName);

    // If we could find the intent with name intentName
    if (intent != null) {

      if (intent.getAction().equals("android.net.conn.CONNECTION_CHANGE")) {
        // send connection change events to the ConnectionManager

        String methodName = "changeNetworkState(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

        int[] args = { env.newString((String) intent.getExtra("type")),
            env.newString((String) intent.getExtra("state")),
            env.newString((String) intent.getExtra("reason")),
            env.newString((String) intent.getExtra("info")) };

        callMethod(env, JPF_android_os_ServiceManager.connectionManagerRef, methodName, args);

      } else if (intent.getAction().equals("android.net.conn.URL_INPUT_STREAM")) {
        // send connection change events to the ConnectionManager

        String url = (String) intent.getExtra("url");
        String input = (String) intent.getExtra("file");

        JPF_javax_xml_parsers_SAXParser.URLInput.put(url, input);
      } else {
        // TODO send Broadcast to system
        log.severe(TAG + ": Could not send broadcast, undefined action " + intentName);

      }
    } else {
      log.severe(TAG + ": Could not send broadcast, undefined intent " + intentName);
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

  /* ****************** System Methods ****************** */

  /**
   * Retrieves the reference to an intent in the intentMap using its name
   * specified by action.target. If no
   * such intent exists, an Intent object is created and stored in the
   * intentMap. It then sets the field of
   * the intent (specified by action.getAction()) with value action.arguments[0]
   * which is the value of the
   * field to set..
   * 
   * @param env
   * @param action
   *          - action as read from the script file
   */
  public static void setIntent(MJIEnv env, UIAction action) {
    IntentEntry intent = intentMap.get(action.getTarget());

    if (intent == null) {
      intent = new IntentEntry();
      intentMap.put(action.target, intent);
    }

    // Use reflection to call the setter method on the Intent object
    @SuppressWarnings("unchecked")
    Class<IntentEntry> intentClass = (Class<IntentEntry>) intent.getClass();

    try {
      Method method = null;
      Method[] methods = intentClass.getMethods();
      for (int i = 0; i < methods.length; i++) {
        method = methods[i];
        if (method.getName().equals(action.getAction())) {
          break;
        }
      }
      method.invoke(intent, action.getArguments());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the reference to an the Intent object. This has to be extended to
   * allow not only explicit intents
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

  protected static class IntentEntry {

    private String mPackage;
    private String mClass;
    private String action;
    private HashSet<String> mCategories;
    private Bundle mExtras;

    public IntentEntry() {

    }

    public void setComponent(String componentName) {
      String[] name = componentName.split("\\.");
      if (name.length > 1) {
        mPackage = componentName.substring(0, componentName.lastIndexOf("."));
        if (mPackage.length() == 0) {
          mPackage = getPackageName();
        }
      }
      mClass = name[name.length - 1]; // get the string after the last ".", this will be the name
    }

    private String getPackageName() {
      // TODO Auto-generated method stub
      return null;
    }

    public String getPackage() {
      return mPackage;
    }

    public void setPackage(String mPackage) {
      this.mPackage = mPackage;
    }

    public String getClassName() {
      return mClass;
    }

    public void setClassName(String mClass) {
      this.mClass = mClass;
    }

    public String getAction() {
      return action;
    }

    public void setAction(String action) {
      this.action = action;
    }

    public boolean hasCategory(String category) {
      boolean value = (mCategories != null) ? mCategories.contains(category) : false;
      return value;
    }

    public void addCategories(String mCategory) {
      if (this.mCategories == null) {
        this.mCategories = new HashSet<String>();
      }
      this.mCategories.add(mCategory);
    }

    public Bundle getExtras() {
      return mExtras;
    }

    public void setExtras(Bundle mExtras) {
      this.mExtras = mExtras;
    }

    public void putExtraBool(String name, boolean value) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      mExtras.putBoolean(name, value);
    }

    public void putExtraByte(String name, byte value) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      mExtras.putByte(name, value);
    }

    public void putExtraChar(String name, char value) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      mExtras.putChar(name, value);
    }

    public void putExtraShort(String name, short value) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      mExtras.putShort(name, value);
    }

    public void putExtraInt(String name, int value) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      mExtras.putInt(name, value);
    }

    public void putExtraLong(String name, long value) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      mExtras.putLong(name, value);
    }

    public void putExtraFloat(String name, float value) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      mExtras.putFloat(name, value);
    }

    public void putExtraDouble(String name, double value) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      mExtras.putDouble(name, value);
    }

    public void putExtraString(String name, String value) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      mExtras.putString(name, value);
    }

    public Object getExtra(String name) {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      return mExtras.get(name);
    }

  }

}
