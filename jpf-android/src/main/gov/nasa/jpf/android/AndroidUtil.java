package gov.nasa.jpf.android;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class AndroidUtil {
  /**
   * Uses a direct call to call a method on the BatteryService
   * 
   * @param env
   * @param methodName
   *          the method signature of the method to call directly
   * @param args
   *          the arguments of the method
   */
  static boolean callMethod(MJIEnv env, ClassInfo classInfo, String methodName, int[] argsRefs) {

    ThreadInfo ti = env.getThreadInfo();
    MethodInfo mi = classInfo.getMethod(methodName, true);

    if (mi == null)
      return false;

    // Create direct call stub with identifier [UIAction]
    DirectCallStackFrame stub = mi.createDirectCallStackFrame(ti, argsRefs.length);

    // if the method is not static the reference to the object is pushed to
    // allow access to fields
    if (!mi.isStatic()) {
      stub.push(classInfo.getClassObjectRef(), true);
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

  // <2do> very simplistic argument handling for now
  void pushArg(MJIEnv env, Object arg, byte typeCode, StackFrame frame) {
    if (arg == null) {
      frame.push(MJIEnv.NULL, false);
    } else if (arg instanceof String) {
      int sRef = env.newString((String) arg);
      frame.push(sRef, true);
    } else if (arg instanceof Double) {
      frame.pushDouble((Double) arg);
    } else if (arg instanceof Integer) {
      frame.push((Integer) arg);
    } else if (arg instanceof Boolean) {
      frame.push(((Boolean) arg) ? 1 : 0, false);
    } else {
      throw new UnsupportedOperationException("argument type not supported: " + arg);
    }
  }
}
