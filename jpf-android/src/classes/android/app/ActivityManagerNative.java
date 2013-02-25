package android.app;

import  android.app.ActivityManager.ActivityManagerProxy;

public class ActivityManagerNative {

  static ActivityManagerProxy proxy;

  public static ActivityManagerProxy getDefault() {
    if (proxy == null)
      return new ActivityManagerProxy();
    else
      return proxy;
  }

}
