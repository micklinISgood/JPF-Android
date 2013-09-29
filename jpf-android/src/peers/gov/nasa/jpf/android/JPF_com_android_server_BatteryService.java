package gov.nasa.jpf.android;

import java.util.logging.Logger;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.util.script.UIAction;

public class JPF_com_android_server_BatteryService {
  private static final String TAG = "JPF_MessageQueue";
  static Logger log = JPF.getLogger(TAG);

  static int classRef = -1;

  public static void init0(MJIEnv env, int robj) {
    classRef = robj;
  }

  public static void setDeviceChargingUSB(MJIEnv env, int usb) {

  }

  public static void setDeviceChargingAC(MJIEnv env, int robj) {

  }

  public static void setBatteryLevelLow(MJIEnv env) {
    env.setIntField(classRef, "mBatteryLevel", 1);
  }

  public static void setBatteryLevelAverage(MJIEnv env) {
    env.setIntField(classRef, "mBatteryLevel", 50);
  }

  public static void setBatteryLevelFull(MJIEnv env) {
    env.setIntField(classRef, "mBatteryLevel", 100);
  }

  private static void setBatteryLevel(MJIEnv env, int level) {
    env.setIntField(classRef, "mBatteryLevel", level);
  }

  public static void handleAction(MJIEnv env, UIAction uiaction) {
    String action = uiaction.getAction().trim().toLowerCase();
    Object[] arguments = uiaction.getArguments();

    // set battery level
    if (action.equals("setbattery")) {
      // check syntax
      if (arguments == null || arguments[0] == null) {
        printError("The \"setBattery\" Action takes exactly one String parameter - the level of the battery between 0 - 100.");
        return;
      }
      // get level
      String level = ((String) arguments[0]).trim().toLowerCase();

      // handle level as a string
      if (level.equals("low")) {
        setBatteryLevelLow(env);
      } else if (level.equals("average")) {
        setBatteryLevelAverage(env);
      } else if (level.equals("full")) {
        setBatteryLevelFull(env);
      } else if (level.contains("%")) {
        // handle level as a percentage
        level = level.substring(0, level.length() - 1);
        try {
          int l = Integer.parseInt(level);
          if (l >= 0 && l <= 100)
            setBatteryLevel(env, Integer.parseInt(level));
          else {
            printError("The \"setBattery\" Action takes exactly one String parameter - the level of the battery between 0 - 100.");
            return;
          }
        } catch (NumberFormatException exception) {
          printError("The \"setBattery\" Action takes exactly one String parameter - the level of the battery between 0 - 100.");
          return;
        }
      } else {
        printError("The \"setBattery\" Action takes exactly one String parameter - the level of the battery.");
        return;
      }
      callMethod(env, classRef, "processValues()V", new int[] {});
    } else if (action.equals("setcharging")) {

    } else {
      printError("Unknown action " + uiaction.toString() + ".");

    }

  }

  private static void printError(String error) {
    log.warning("BatteryService: Could not execute action. " + error);
  }

  /**
   * Uses a direct call to call a method on the BatteryService
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
    MethodInfo stub = mi.createDirectCallStub("[UIACTION]");
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
