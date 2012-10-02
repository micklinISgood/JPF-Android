package android.app;

import android.content.Intent;

public class ActivityManagerProxy {

  public static native void startActivityProxy(Intent intent, int requestCode);

  public static native void stopServiceProxy();

}
