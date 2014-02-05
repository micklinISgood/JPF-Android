package gov.nasa.jpf.android;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.util.script.UIAction;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.ThreadInfo;
import android.net.ConnectivityManager;

public class JPF_android_net_ConnectivityManager extends NativePeer {

  private static int classRef = MJIEnv.NULL;

  @MJI
  public void init0(MJIEnv env, int ref) {
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

}