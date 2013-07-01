package gov.nasa.jpf.android;

import gov.nasa.jpf.jvm.MJIEnv;

public class JPF_android_os_ServiceManager {
  public static int connectionManagerRef;

  public static void init0(MJIEnv env, int classRef, int conref) {
    connectionManagerRef = conref;
  }

}
