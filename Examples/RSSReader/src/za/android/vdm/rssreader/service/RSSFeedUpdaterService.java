package za.android.vdm.rssreader.service;

import gov.nasa.jpf.annotation.Checkpoint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Service that runs in the background and fetches feed updates.
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
	private boolean updateRunning = false;

	private UpdaterThread updaterThread;


	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "oncreate method");
		startAutoUpdating();
	}

	@Override
	@Checkpoint("startUpdate")
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStartCommand method");

		if (isRunning()) {
			notifyUserAlreadyRunning();
		} else if (batteryLow()) {
			notifyUserBatteryLow();
		} else if (!WifiConnected()) {
			notifyUserWifiOff();
		} else {
			runUpdate();
		}
		return Service.START_STICKY;
	}

 @Checkpoint("runUpdate")
	private synchronized void runUpdate() {
		setRunning();
		
		//create and start new update thread
		this.updaterThread = new UpdaterThread(this);
		this.updaterThread.start();
	}

	@Checkpoint("setRunning")
	private void setRunning() {
		updateRunning = true;
	}

	@Checkpoint("checkRunning")
	private synchronized boolean isRunning() {
		return updateRunning;
	}

	@Checkpoint(value = "finishUpdate", threadName = "RSSUpdaterThread")
	protected synchronized void finishUpdate() {
		updateRunning = false;
	}

	@Checkpoint("alreadyRunning")
	private void notifyUserAlreadyRunning() {
		// notify user update already running
		notifyUser("Error updating, already updating");
		Log.i(TAG, "Error updating, already updating");

	}

	@Checkpoint("checkWifi")
	private boolean WifiConnected() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null) {
			Log.i("RSSFEED Update Service",
					"ActiveNetwork: " + activeNetwork.getTypeName());
			boolean isConnected = activeNetwork.isConnectedOrConnecting();
			boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
			if (isConnected && isWiFi) {
				Log.i("RSSFEED Update Service", "Wifi: " + "connected");

				return true;
			}
		}
		Log.i("RSSFEED Update Service", "Wifi: " + "not connected");

		return false;

	}

	@Checkpoint("WifiDown")
	private void notifyUserWifiOff() {
	    runUpdate();
		notifyUser("Error updating, Wifi down");
		Log.i(TAG, "Error updating, Wifi down");

	}

	@Checkpoint("checkBattery")
	private boolean batteryLow() {
		Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		
		float pers = ((float) level / (float) scale) * 100.0f;
		
		Log.i("RSSFEED Update Service", "Battery: " + pers);

		if (pers < 10) {
			return true;
		} else {
			return false;
		}
	}

	@Checkpoint("batteryLow")
	private void notifyUserBatteryLow() {
		notifyUser("Error updating, battery too low");
		Log.i(TAG,"Error updating, battery too low");

	}
	
	@Checkpoint("notifyUser")
	private void notifyUser(String notification) {
		Toast.makeText(this, notification, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy method");
		// stop current update
		stopUpdate();
		// stop auto updating
		stopAutoUpdating();
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void startAutoUpdating() {

	}
	
	private void stopUpdate() {

	}

	private void stopAutoUpdating() {

	}

}