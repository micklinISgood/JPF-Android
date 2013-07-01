package za.android.vdm.rssreader.receivers;

import gov.nasa.jpf.annotation.Checkpoint;
import za.android.vdm.rssreader.service.RSSFeedUpdaterService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
  public static final String TAG = "NetworkReceiver";
  boolean b = false;

  @Checkpoint("networkStatusChange")
  @Override
  public void onReceive(Context context, Intent intent) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (activeNetwork != null) {
      boolean isConnected = activeNetwork.isConnectedOrConnecting();
      boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

      if (activeNetwork != null && isWiFi && isConnected) {
        Log.d(TAG, "onReceive: connected, starting UpdaterService");
        context.startService(new Intent(context, RSSFeedUpdaterService.class));
      }
    }
  }

}