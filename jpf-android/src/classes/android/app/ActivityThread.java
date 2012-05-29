package android.app;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;
import android.util.LogPrinter;
import android.view.Window;

/**
 * This manages the execution of the main thread in an application process,
 * scheduling and executing activities, broadcasts, and other operations on it
 * as the activity manager requests.
 * 
 * {@hide}
 */
public final class ActivityThread {
	public static final String TAG = "ActivityThread";
	public static boolean DEBUG = true;

	final Looper mLooper = Looper.myLooper();
	final H mH = new H();
	final HashMap<String, Activity> mActivities = new HashMap<String, Activity>();

	ActivityClientRecord currentActivity;

	static final class ActivityClientRecord {
		// IBinder token;
		int ident;
		// Intent intent;
		// Bundle state;
		Activity activity;
		Window window;
		Activity parent;
		String embeddedID;
		// Activity.NonConfigurationInstances lastNonConfigurationInstances;
		boolean paused;
		boolean stopped;

		// boolean hideForNow;
		// Configuration newConfig;
		// Configuration createdConfig;
		// ActivityClientRecord nextIdle;

		// String profileFile;
		// ParcelFileDescriptor profileFd;
		// boolean autoStopProfiler;

		// ActivityInfo activityInfo;
		// CompatibilityInfo compatInfo;
		// LoadedApk packageInfo;

		// List<ResultInfo> pendingResults;
		// List<Intent> pendingIntents;

		// boolean startsNotResumed;
		// boolean isForward;
		// int pendingConfigChanges;
		// boolean onlyLocalRequest;

		// View mPendingRemoveWindow;
		// WindowManager mPendingRemoveWindowManager;

		ActivityClientRecord() {
			parent = null;
			embeddedID = null;
			paused = false;
			stopped = false;
			// hideForNow = false;
			// nextIdle = null;
		}

		public String toString() {
			// ComponentName componentName = intent.getComponent();
			return "ActivityRecord{"
					+ (activity == null ? "no component name" : activity
							.getClass().getName()) + "}";
		}
	}

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
		public H() {
			// super(q);

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

	private void attach() {
		// parse manifest to get Activities/Services
		parseApplicationStructure(mActivities);

		// parse ui files to get UI models

	}

	private native void parseApplicationStructure(
			HashMap<String, Activity> activities);

	public ActivityThread() {
	}

	public static void main(String[] args) {
		Looper.prepareMainLooper();

		ActivityThread thread = new ActivityThread();
		thread.attach();

		if (DEBUG) {
			Looper.myLooper().setMessageLogging(new LogPrinter(Log.DEBUG, TAG));
		}

		Looper.loop();

		throw new RuntimeException("Main thread loop unexpectedly exited");
	}

}