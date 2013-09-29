package za.vdm.receivers.ordered;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OrderedReceiver2 extends BroadcastReceiver {
	private final static String tag = "OR2";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(tag, "onReceive");
	}

}
