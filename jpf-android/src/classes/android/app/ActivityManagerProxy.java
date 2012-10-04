package android.app;

import android.content.Intent;

public class ActivityManagerProxy {

  public native void startActivity(Intent intent, int requestCode);

  public native void stopService();

  public native void finishActivity(int resultCode, Intent resultData);

}
