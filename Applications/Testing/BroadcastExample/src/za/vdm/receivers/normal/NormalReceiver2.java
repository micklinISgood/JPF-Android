package za.vdm.receivers.normal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NormalReceiver2 extends BroadcastReceiver{
	private final static String tag = "BR2";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(tag, "onReceive");
	}

}
