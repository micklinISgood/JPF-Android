package android.app;

import android.os.IBinder;

public class IIntentReceiver implements IBinder {
  public IBinder asBinder() {
    return (IBinder) this;
  }
}
