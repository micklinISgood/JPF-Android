package gov.nasa.jpf.android;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;

public class TestLooper {
	public static void main(String[] args) {
		Looper.prepare();
		Handler H = new Handler(new Callback() {
			public boolean handleMessage(Message msg) {
				System.out.println("Message " + msg.arg1);
				return false;
			}
		});

		Message m1 = new Message();
		m1.arg1 = 1;
		H.sendMessage(m1);
		Message m2 = new Message();
		m2.arg2 = 2;
		H.sendMessage(m2);
		Looper.loop();
	}

}
