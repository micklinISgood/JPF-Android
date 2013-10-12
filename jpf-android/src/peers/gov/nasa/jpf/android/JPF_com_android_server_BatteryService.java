package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.util.script.UIAction;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.logging.Logger;

public class JPF_com_android_server_BatteryService extends NativePeer {
  private static final String TAG = "JPF_MessageQueue";
  static Logger log = JPF.getLogger(TAG);

  static int classRef = -1;

  @MJI
  public void init0(MJIEnv env, int robj) {
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

  static boolean callMethod(MJIEnv env, int classRef, String methodName, int[] argsRefs) {

    ThreadInfo ti = env.getThreadInfo();
    ClassInfo info = env.getClassInfo(classRef);
    MethodInfo mi = info.getMethod(methodName, true);

    if (mi == null)
      return false;

    // Create direct call stub with identifier [UIAction]
    DirectCallStackFrame stub = mi.createDirectCallStackFrame(ti, argsRefs.length);

    // if the method is not static the reference to the object is pushed to
    // allow access to fields
    if (!mi.isStatic()) {
      stub.push(classRef, true);
    }

    // arguments for the method is pushed on the frame
    if (argsRefs != null) {
      for (int i = 0; i < argsRefs.length; i++) {
        stub.push(argsRefs[i], true);
      }
    }
    // frame is pushed to the execution thread
    ti.pushFrame(stub);

    return true;
  }

  private static void printError(String error) {
    log.warning("BatteryService: Could not execute action. " + error);
  }

}
