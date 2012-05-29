package android.app;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;

/**
 * This manages the execution of the main thread in an application process,
 * scheduling and executing activities, broadcasts, and other operations on it
 * as the activity manager requests.
 * 
 * {@hide}
 */
public final class ActivityThread {
	Looper mLooper;
	H mH;
	Activity currentActivity;

	private void queueOrSendMessage(int what, Object obj, int arg1, int arg2) {
		synchronized (this) {
			Message msg = new Message();
			msg.what = what;
			msg.obj = obj;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			mH.sendMessage(msg);
		}
	}

	private class H extends Handler {
		public H(MessageQueue q) {
			//super(q);

		}

		public static final int LAUNCH_ACTIVITY = 100;

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LAUNCH_ACTIVITY: {
				System.out.println("Launching_activity "
						+ ((Activity) msg.obj).toString());
				((Activity) msg.obj).onCreate(null);
			}
				break;
			}
		}
	}

	
	
	
	
	
	
//	
//	public ActivityThread(Activity main) {
//		mLooper = new Looper();
//		// Activities = new HashMap<String, Activity>();
//		this.mH = new H(mLooper.getMessageQueue());
//		queueOrSendMessage(H.LAUNCH_ACTIVITY, main, 0, 0);
//
//		mLooper.start();
//	}
//
//	public static void main(String[] args) {
//		Looper.prepareMainLooper();
//		if (sMainThreadHandler == null) {
//			sMainThreadHandler = new Handler();
//		}
//
//		ActivityThread thread = new ActivityThread();
//		thread.attach(false);
//
//		if (false) {
//			Looper.myLooper().setMessageLogging(
//					new LogPrinter(Log.DEBUG, "ActivityThread"));
//		}
//
//		Looper.loop();
//
//		throw new RuntimeException("Main thread loop unexpectedly exited");
//	}

}