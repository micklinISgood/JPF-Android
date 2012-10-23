package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.SystemState;

import java.util.logging.Logger;

/**
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class JPF_android_app_ActivityThread {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  static int activityThreadRef = -1;
  static int applicationRef = -1;
  private static ApplicationInfo appInfo;

  public static void init0(MJIEnv env, int objectRef) {
    appInfo = new ApplicationInfo();
    activityThreadRef = objectRef;
    appInfo.init(env);
  }

  public static void setApplicationRef(MJIEnv env, int objectRef, int appRef) {
    applicationRef = appRef;
  }

  public static int getApplicationRef() {
    return applicationRef;
  }

  public static String getCurrentActivity(MJIEnv env) {
    SystemState ss = env.getSystemState();
    ElementInfo info = env.getElementInfo(activityThreadRef);
    int ref = -1;
    if (info != null) {
      ref = info.getReferenceField("currentActivity");
    }
    if (ref == -1) {
      return "default";
    } else {
      ElementInfo info2 = env.getElementInfo(ref);
      return info2.getStringField("name");
    }
  }

  public static String getPackageName() {
    return appInfo.getPackageName();

  }

}
