package gov.nasa.jpf.adroid;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;

public class TestLooper {

	public static void main(String[] args) {
		Looper.prepare();
		final Handler H = new Handler(new Callback() {
			public boolean handleMessage(Message msg) {
				System.out.println("Message " + msg.what + "\n");
				return false;
			}
		});

		Thread two = new Thread(new Runnable() {

			@Override
			public void run() {
				Message m;
				for (int i = 0; i < 10000000; i++) {
					m = Message.obtain(H, i);
					H.dispatchMessage(m);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		two.start();

		Looper.loop();

	}

}
