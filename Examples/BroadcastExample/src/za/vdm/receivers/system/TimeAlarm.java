package za.vdm.receivers.system;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeAlarm extends BroadcastReceiver {

	NotificationManager nm;

	@Override
	public void onReceive(Context context, Intent intent) {
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence from = "Nithin";
		CharSequence message = "Crazy About Android...";
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(), 0);
		// Notification notif = new Notification(R.drawable.icon,
		// "Crazy About Android...", System.currentTimeMillis());
		// notif.setLatestEventInfo(context, from, message, contentIntent);
		// nm.notify(1, notif);
	}
}