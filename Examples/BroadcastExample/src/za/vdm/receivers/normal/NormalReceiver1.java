package za.vdm.receivers.normal;

import gov.nasa.jpf.annotation.Checkpoint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NormalReceiver1 extends BroadcastReceiver {
  private final static String tag = "NormalReceiver1";

  @Override
  @Checkpoint("NormalReceiver")
  public void onReceive(Context context, Intent intent) {
    System.out.println(tag + ".onReceive");
    Bundle bundle = intent.getExtras();
    if (bundle != null) {
      System.out.println(tag + ".onReceive: " + ((Integer) bundle.get("value")).toString());
    } else
      System.out.println(tag + ".onReceive: no bundle");
  }

}
