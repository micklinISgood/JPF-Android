package android.content;

import android.os.Bundle;
import android.os.IBinder;

/**
 * System private API for dispatching intent broadcasts. This is given to the
 * activity manager as part of registering for an intent broadcasts, and is
 * called when it receives intents.
 * 
 * {@hide}
 */
public abstract class IIntentReceiver implements IBinder {
  public abstract void performReceive(Intent intent, int resultCode, String data, Bundle extras,
                                      boolean ordered, boolean sticky);

  public IBinder asBinder() {
    return (IBinder) this;
  }
}
