package android.app;


public class ActivityManagerNative {

  public static IActivityManager mgr;

  public static IActivityManager getDefault() {
    
    return mgr;
  }

}
