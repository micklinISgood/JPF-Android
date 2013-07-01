package za.android.vdm.rssreader.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent batteryStatus) {
//		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
//				|| status == BatteryManager.BATTERY_STATUS_FULL;
//
//		int chargePlug = batteryStatus.getIntExtra(
//				BatteryManager.EXTRA_PLUGGED, -1);
//		boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
//		boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
//
//		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//
//		float batteryPct = level / (float) scale;

	}
}
