package android.app;

import android.content.Intent;

public class ActivityManagerProxy {

  public static native void startActivityProxy(Intent intent);

  public static native void stopServiceProxy();

}
