package android.app;

public class ActivityManagerNative {

  static ActivityManagerProxy proxy;

  public static ActivityManagerProxy getDefault() {
    if (proxy == null)
      return new ActivityManagerProxy();
    else
      return proxy;
  }

}
