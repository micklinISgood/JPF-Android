package android.app;

import java.util.HashMap;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
	final HashMap<String, ActivityClientRecord> mActivities = new HashMap<String, ActivityClientRecord>();

	ActivityClientRecord currentActivity;
	boolean mSystemThread = false;
	static final ThreadLocal<ActivityThread> sThreadLocal = new ThreadLocal<ActivityThread>();
	final ApplicationThread mAppThread = new ApplicationThread();

	private class ApplicationThread {
		// we use token to identify this activity without having to send the
		// activity itself back to the activity manager. (matters more with ipc)
		public final void scheduleDestroyActivity(String activityName) {
			queueOrSendMessage(H.DESTROY_ACTIVITY, activityName, 0, 0);
		}

		public final void scheduleLaunchActivity(String activityName,
				Intent intent) {
			ActivityClientRecord r = new ActivityClientRecord();
			r.name = activityName;
			r.intent = intent;
			queueOrSendMessage(H.LAUNCH_ACTIVITY, r, 0, 0);
		}
	}

	static final class ActivityClientRecord {
		// IBinder token;
		// int ident;
		Intent intent;
		Bundle state;
		Activity activity;
		Window window;
		Activity parent;
		// String embeddedID;
		// Activity.NonConfigurationInstances lastNonConfigurationInstances;
		boolean paused;
		boolean stopped;
		String name;
		boolean hideForNow;

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
			// embeddedID = null;
			paused = false;
			stopped = false;
			hideForNow = false;
			// nextIdle = null;
		}

		public String toString() {
			// ComponentName componentName = intent.getComponent();
			return "ActivityRecord{"
					+ (activity == null ? "no component name" : activity
							.getClass().getName()) + "}";
		}

		public String getName() {
			return name;
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

		public static final int LAUNCH_ACTIVITY = 100;
		public static final int PAUSE_ACTIVITY = 101;
		public static final int PAUSE_ACTIVITY_FINISHING = 102;
		public static final int STOP_ACTIVITY_SHOW = 103;
		public static final int STOP_ACTIVITY_HIDE = 104;
		public static final int SHOW_WINDOW = 105;
		public static final int HIDE_WINDOW = 106;
		public static final int RESUME_ACTIVITY = 107;
		public static final int SEND_RESULT = 108;
		public static final int DESTROY_ACTIVITY = 109;
		public static final int NEW_INTENT = 112;
		public static final int CREATE_SERVICE = 114;
		public static final int SERVICE_ARGS = 115;
		public static final int STOP_SERVICE = 116;
		public static final int RELAUNCH_ACTIVITY = 126;

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LAUNCH_ACTIVITY: {
				ActivityClientRecord r = (ActivityClientRecord) msg.obj;
				handleLaunchActivity(r, null);
			}
				break;
			case RELAUNCH_ACTIVITY: {
				// ActivityClientRecord r = (ActivityClientRecord) msg.obj;
				// handleRelaunchActivity(r);
			}
				break;
			case PAUSE_ACTIVITY:
				// handlePauseActivity((IBinder) msg.obj, false, msg.arg1 != 0,
				// msg.arg2);
				// maybeSnapshot();
				break;
			case PAUSE_ACTIVITY_FINISHING:
				// handlePauseActivity((IBinder) msg.obj, true, msg.arg1 != 0,
				// msg.arg2);
				break;
			case STOP_ACTIVITY_SHOW:
				// handleStopActivity((IBinder) msg.obj, true, msg.arg2);
				break;
			case STOP_ACTIVITY_HIDE:
				// handleStopActivity((IBinder) msg.obj, false, msg.arg2);
				break;
			case SHOW_WINDOW:
				// handleWindowVisibility((IBinder) msg.obj, true);
				break;
			case HIDE_WINDOW:
				// handleWindowVisibility((IBinder) msg.obj, false);
				break;
			case RESUME_ACTIVITY:
				// handleResumeActivity((IBinder) msg.obj, true, msg.arg1 != 0);
				break;
			case SEND_RESULT:
				// handleSendResult((ResultData) msg.obj);
				break;
			case DESTROY_ACTIVITY: {

				handleDestroyActivity((String) msg.obj);
			}
				break;

			}

		}

		private void handleDestroyActivity(String obj) {
			// TODO Auto-generated method stub

		}
	}

	private Activity performLaunchActivity(ActivityClientRecord r,
			Intent customIntent) {

		// ComponentName component = r.intent.getComponent();
		// if (component == null) {
		// component = r.intent.resolveActivity(mInitialApplication
		// .getPackageManager());
		// r.intent.setComponent(component);
		// }
		//
		// if (r.activityInfo.targetActivity != null) {
		// component = new ComponentName(r.activityInfo.packageName,
		// r.activityInfo.targetActivity);
		// }

		// Make new instance of the Activity class
		Activity activity = null;
		try {
			Class<Activity> cls = (Class<Activity>) Class.forName(r.getName());
			activity = cls.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Unable to instantiate activity " + ": "
					+ e.toString(), e);
		}

		// try {
		// Application app = new
		// Application();//r.packageInfo.makeApplication(false,
		// mInstrumentation);

		// if (localLOGV)
		// Slog.v(TAG, "Performing launch of " + r);
		// if (localLOGV)
		// Slog.v(TAG,
		// r + ": app=" + app + ", appName="
		// + app.getPackageName() + ", pkg="
		// + r.packageInfo.getPackageName() + ", comp="
		// + r.intent.getComponent().toShortString()
		// + ", dir=" + r.packageInfo.getAppDir());

		// if (activity != null) {
		// appContext.init(r.packageInfo, r.token, this);
		// appContext.setOuterContext(activity); //set this activity as the main
		// activity
		// CharSequence title = r.activityInfo.loadLabel(appContext
		// .getPackageManager());
		// Configuration config = new Configuration(mCompatConfiguration);
		// if (DEBUG_CONFIGURATION)
		// log.v(TAG, "Launching activity " + r.activityInfo.name+
		// " with config " + config);
		activity.attach(this, null, null);

		// appContext, this, getInstrumentation(),
		// r.token, r.ident, app, r.intent, r.activityInfo, title,
		// r.parent, r.embeddedID,
		// /r.lastNonConfigurationInstances, config);

		// if (customIntent != null) {
		// activity.mIntent = customIntent;
		// }
		// r.lastNonConfigurationInstances = null;
		// activity.mStartedActivity = false;
		// int theme = r.activityInfo.getThemeResource();
		// if (theme != 0) {
		// activity.setTheme(theme);
		// }

		// activity.mCalled = false;
		activity.onCreate(null);
		// if (!activity.mCalled) {
		// throw new SuperNotCalledException("Activity "
		// + r.intent.getComponent().toShortString()
		// + " did not call through to super.onCreate()");
		// }
		r.activity = activity;
		// r.stopped = true;
		// if (!r.activity.mFinished) {
		// activity.performStart();
		// r.stopped = false;
		// }
		// if (!r.activity.mFinished) {
		// if (r.state != null) {
		// mInstrumentation.callActivityOnRestoreInstanceState(
		// activity, r.state);
		// }
		// }
		// // if (!r.activity.mFinished) {
		// activity.mCalled = false;
		// mInstrumentation
		// .callActivityOnPostCreate(activity, r.state);
		// // if (!activity.mCalled) {
		// throw new SuperNotCalledException(
		// "Activity "
		// / + r.intent.getComponent()
		// .toShortString()
		// + " did not call through to super.onPostCreate()");
		// }
		// }
		// }
		// r.paused = true;

		mActivities.put(r.getName(), r);

		// } catch (SuperNotCalledException e) {
		// throw e;
		//
		// } catch (Exception e) {
		// //if (!mInstrumentation.onException(activity, e)) {
		// throw new RuntimeException("Unable to start activity "
		// + r.getName() + ": " + e.toString(), e);
		// //}
		// }

		return activity;
	}

	private void handleLaunchActivity(ActivityClientRecord r,
			Intent customIntent) {

		// Make sure we are running with the most recent config.
		// handleConfigurationChanged(null, null);

		Activity a = performLaunchActivity(r, customIntent);

		// if (a != null) {
		// r.createdConfig = new Configuration(mConfiguration);
		// Bundle oldState = r.state;
		// handleResumeActivity(r.token, false, r.isForward);

		// if (!r.activity.mFinished && r.startsNotResumed) {
		// The activity manager actually wants this one to start out
		// paused, because it needs to be visible but isn't in the
		// foreground. We accomplish this by going through the
		// normal startup (because activities expect to go through
		// onResume() the first time they run, before their window
		// is displayed), and then pausing it. However, in this case
		// we do -not- need to do the full pause cycle (of freezing
		// and such) because the activity manager assumes it can just
		// retain the current state it has.
		// try {
		// r.activity.mCalled = false;
		// mInstrumentation.callActivityOnPause(r.activity);
		// We need to keep around the original state, in case
		// we need to be created again.
		// r.state = oldState;
		// if (!r.activity.mCalled) {
		// throw new SuperNotCalledException("Activity "
		// + r.intent.getComponent().toShortString()
		// + " did not call through to super.onPause()");
		// }

		// } catch (SuperNotCalledException e) {
		// throw e;

		// /} catch (Exception e) {
		// if (!mInstrumentation.onException(r.activity, e)) {
		// throw new RuntimeException("Unable to pause activity "
		// + r.intent.getComponent().toShortString()
		// + ": " + e.toString(), e);
		// }
		// }
		// r.paused = true;
		// }
		// } else {
		// If there was an error, for any reason, tell the activity
		// manager to stop us.
		// try {
		// ActivityManagerNative.getDefault().finishActivity(r.token,
		// Activity.RESULT_CANCELED, null);
		// } catch (RemoteException ex) {
		// Ignore
		// }
		// }
	}

	private void attach() {
		sThreadLocal.set(this);
		mSystemThread = false;
		setApplicationRef(this.mAppThread);
		init0();

		// this.mAppThread.scheduleLaunchActivity("com.vdm.DeadlockActivity");

		/** give reference of ApplicationThread to system */
		// RuntimeInit.setApplicationObject(mAppThread.asBinder());
		/** Attach ActivityThread to ActivityManager */
		// IActivityManager mgr = ActivityManagerNative.getDefault();
		// try {
		// mgr.attachApplication(mAppThread);
		// } catch (RemoteException ex) {
		// // Ignore
		// }
		/** create callback for configuration changed */
		// ViewRootImpl.addConfigCallback(new ComponentCallbacks2() {
		// public void onConfigurationChanged(Configuration newConfig) {
		// synchronized (mPackages) {
		// // We need to apply this change to the resources
		// // immediately, because upon returning the view
		// // hierarchy will be informed about it.
		// if (applyConfigurationToResourcesLocked(newConfig, null)) {
		// // This actually changed the resources! Tell
		// // everyone about it.
		// if (mPendingConfiguration == null ||
		// mPendingConfiguration.isOtherSeqNewer(newConfig)) {
		// mPendingConfiguration = newConfig;
		//
		// queueOrSendMessage(H.CONFIGURATION_CHANGED, newConfig);
		// }
		// }
		// }
		// }
		// public void onLowMemory() {
		// }
		// public void onTrimMemory(int level) {
		// }
		// });

	}

	native private void init0();

	native void setApplicationRef(ApplicationThread mAppThread2);

	public static void main(String[] args) {
		Looper.prepareMainLooper();

		ActivityThread thread = new ActivityThread();
		thread.attach();

		if (DEBUG) {
			Looper.myLooper().setMessageLogging(new LogPrinter(Log.DEBUG, TAG));
		}

		Looper.loop();

		// throw new RuntimeException("Main thread loop unexpectedly exited");
	}

}