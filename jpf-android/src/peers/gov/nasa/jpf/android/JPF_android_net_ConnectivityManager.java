package gov.nasa.jpf.android;

import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.util.script.UIAction;
import android.net.ConnectivityManager;

public class JPF_android_net_ConnectivityManager {

  private static int classRef = -1;

  public static void init0(MJIEnv env, int ref) {
    classRef = ref;
  }

  public static void handleAction(MJIEnv env, UIAction uiaction) {
    String methodName = "changeNetworkState(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

    String action = uiaction.getAction().toLowerCase().trim();
    Object[] params = uiaction.getArguments();

    boolean isConnected = ((String) params[0]).toLowerCase().trim().equals("on");

    int type = (action.contains("wifi")) ? ConnectivityManager.TYPE_WIFI
        : (action.contains("mobile")) ? ConnectivityManager.TYPE_MOBILE : -1;

    String reason = (isConnected) ? "dataEnabled" : "dataDisabled";
    String state = (isConnected) ? "connected" : "disconnected";
    String info = "";

    int[] args = { type, env.newString(state), env.newString(reason), env.newString(info) };

    callMethod(env, classRef, methodName, args);
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