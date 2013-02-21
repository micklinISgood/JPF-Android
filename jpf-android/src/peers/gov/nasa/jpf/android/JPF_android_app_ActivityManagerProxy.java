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
 * @see com.android.server.amn.ActivityManagerService
 * @see android.app.ActivityManagerProxy
 * @see android.app.ActivityManagerNative
 * 
 * @author Heila van der Merwe
 * 
 */
public class JPF_android_app_ActivityManagerProxy {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  /** ID for stack frames pushed by direct calls from the native code */
  private static final String UIACTION = "[UIAction]";

  /** Stores details of Intent objects variables used in the scripting file */
  private static HashMap<String, IntentEntry> intentMap = new HashMap<String, IntentEntry>();

  private static void init() {
    // parse Android.xml

  }

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
   * Handles actions related to application component such as starting and stopping activities and sending
   * broadcasts etc.
   * 
   * @param env
   * @param action
   *          - the component action to handle
   */
  static void handleComponentAction(MJIEnv env, UIAction action) {
    log.fine("Handling component action: " + action.toString());

    if (action.action.equals("startActivity")) {
      String intentName = (String) action.arguments[0];
      int intentref = getJPFIntent(env, intentName);
      startActivityMethod(env, 0, intentref, -1);
    } else if (action.action.equals("changeLayout")) {
      String layout = (String) action.arguments[0];

      changeLayout(env, 0, 1);

    } else if (action.action.equals("backButton")) {
      backButton(env);
    } else if (action.action.equals("homeButton")) {
      homeButton(env);
    }
  }

  /**
   * This is always called to start an Activity
   * 
   * @param env
   * @param orientation
   *          the new orientation
   */
  private static void changeLayout(MJIEnv env, int clsRef, int orientation) {
    log.fine("changing layout to " + orientation);

    // schedule launch of activity
    int appRef = JPF_android_app_ActivityThread.getApplicationRef();
    String methodName = "performConfigurationChange()V";
    int[] args = {};

    callMethod(env, appRef, methodName, args);

  }

  private static void backButton(MJIEnv env) {
    // schedule launch of activity
    int appRef = JPF_android_app_ActivityThread.getApplicationRef();
    String methodName = "performBackPressed()V";
    int[] args = {};
    callMethod(env, appRef, methodName, args);

  }

  private static void homeButton(MJIEnv env) {
    // schedule launch of activity
    int appRef = JPF_android_app_ActivityThread.getApplicationRef();
    String methodName = "performHomePressed()V";
    int[] args = {};
    callMethod(env, appRef, methodName, args);

  }

  /**
   * Returns the reference to an the Intent object
   * 
   * @param env
   * @param intent
   * 
   * @return
   */
  public static int getJPFIntent(MJIEnv env, String intentName) {
    IntentEntry intent = intentMap.get(intentName);

    int intentRef = env.newObject("android.content.Intent");
    int componentRef = env.newObject("android.content.ComponentName");
    ElementInfo eiComp = env.getElementInfo(componentRef);
    
    int nameRef = env.newString(intent.getComponent());
    int packageRef = env.newString(intent.get);
    
    eiComp.setReferenceField(fname, value);
    eiComp.setReferenceField(fname, value);
    
    ElementInfo ei = env.getElementInfo(intentRef);
    ei.setReferenceField("mComponent", componentRef);
    return intentRef;

  }

  /**
   * Used by Activity to start an activity
   * 
   * @param env
   * @param clsRef
   * @param intentRef
   */
  public static void startActivity(MJIEnv env, int clsRef, int intentRef, int requestcode) {
    ThreadInfo ti = env.getThreadInfo();

    // so that the method is not called twice on return from making direct
    // call
    if (!ti.hasReturnedFromDirectCall(UIACTION)) {
      startActivityMethod(env, clsRef, intentRef, requestcode);
    }
  }

  /**
   * Used by Activity to start an activity
   * 
   * @param env
   * @param clsRef
   * @param intentRef
   */
  public static void finishActivity(MJIEnv env, int clsRef, int resultCode, int resultDataRef) {
    ThreadInfo ti = env.getThreadInfo();

    // so that the method is not called twice on return from making direct
    // call
    if (!ti.hasReturnedFromDirectCall(UIACTION)) {
      finishMethod(env, clsRef, resultCode, resultDataRef);
    }
  }

  private static void finishMethod(MJIEnv env, int clsRef, int resultCode, int resultDataRef) {
    // Lookup the name of the activity to launch
    // int activityNameRef = getActivity(env, intentRef);
    // String activityName = env.getStringObject(activityNameRef);
    log.fine("Finishing activity ");

    // schedule launch of activity
    int appRef = JPF_android_app_ActivityThread.getApplicationRef();
    String methodName = "performFinishActivity(ILandroid/content/Intent;)V";
    int[] args = { resultCode, resultDataRef };

    callMethod(env, appRef, methodName, args);

  }

  /**
   * This is always called to start an Activity
   * 
   * @param env
   * @param intentRef
   *          the reference to the intent starting the activity
   */
  private static void startActivityMethod(MJIEnv env, int clsRef, int intentRef, int requestCode) {
    // Lookup the name of the activity to launch
    // int activityNameRef = getActivity(env, intentRef);
    // String activityName = env.getStringObject(activityNameRef);
    // log.fine("Start activity " + activityName);

    // schedule launch of activity
    int appRef = JPF_android_app_ActivityThread.getApplicationRef();
    String methodName = "performLaunchActivity(Landroid/content/Intent;I)V";
    int[] args = { intentRef, requestCode };

    callMethod(env, appRef, methodName, args);

  }

  private static int getActivity(MJIEnv env, int intentRef) {
    // TODO lookup other fields through intent filters
    int i = env.getReferenceField(intentRef, "mComponent");

    return i;

  }

  private static void stopActivity(MJIEnv env, int clsRef, int intentRef) {
    // TODO
    @SuppressWarnings("unused")
    String methodName = "scheduleDestroyActivity(Ljava/lang/String;)V";
  }

  /**
   * Link to thew Application. Uses a direct call to call a method on ApplicationThread scheduling certain
   * events to be handled by the application's main thread.
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
