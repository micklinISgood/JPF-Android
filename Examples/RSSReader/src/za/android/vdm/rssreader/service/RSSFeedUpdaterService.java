package za.android.vdm.rssreader.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

/**
 * Service that runs in the back ground and fetches feed updates.
 * 
 * @author Heila van der Merwe
 * @date 24 May 2013
 * @version 2.0
 * 
 */
public class RSSFeedUpdaterService extends Service {
  static final String TAG = "RSSFeedUpdaterService";

  /** Intent sent when new status updates are found */
  public static final String NEW_STATUS_INTENT = "com.vdm.blogger.NEW_STATUS";
  public static final String NEW_STATUS_EXTRA_COUNT = "com.vdm.blogger.NEW_STATUS_EXTRA";

  /** This service will be allowed to send timeline notifications */
  public static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.vdm.blogger.RECEIVE_TIMELINE_NOTIFICATIONS";

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "oncreate method");
    startAutoUpdating();
  }


  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    Log.d(TAG, "onStartCommand method");

    new UpdaterThread(this).start();
    return Service.START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy method");
    stopAutoUpdating();
  }

  private void startAutoUpdating() {

  }

  private void stopAutoUpdating() {

  }

}