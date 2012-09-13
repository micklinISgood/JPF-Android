package android.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LogPrinter;
import android.view.Window;

/**
 * This manages the execution of the main thread in an application process, scheduling and executing
 * activities, broadcasts, and other operations on it as the activity manager requests.
 * 
 * {@hide}
 */
public final class ActivityThread {
  public static final String TAG = "ActivityThread";
  public static boolean DEBUG = true;

  final Looper mLooper = Looper.myLooper();
  final H mH = new H();
  // stores a map of all activities that have been started and not destroyed
  final HashMap<Integer, ActivityClientRecord> mActivities = new HashMap<Integer, ActivityClientRecord>();

  // stores the current running Activity
  ActivityClientRecord currentActivity;

  final ApplicationThread mAppThread = new ApplicationThread();

  private class ApplicationThread {

    /**
     * Called to schedule the launch of an Activity for the first time
     * 
     * @param activityName
     *          the name of the Activity to start
     * @param intent
     */
    public final void scheduleLaunchActivity(String activityName, Intent intent) {
      ActivityClientRecord r = new ActivityClientRecord();
      r.name = activityName;
      r.intent = intent;
      queueOrSendMessage(H.LAUNCH_ACTIVITY, r, 0, 0);
    }

    public final void scheduleRelaunchActivity(String activityName, Intent intent) {
      // requestRelaunchActivity(token, pendingResults, pendingNewIntents, configChanges, notResumed, config,
      // true);
    }

    public final void scheduleResumeActivity(int ident) {
      queueOrSendMessage(H.RESUME_ACTIVITY, null, ident, 0);
    }

    public final void schedulePauseActivity(int ident) {
      queueOrSendMessage(H.PAUSE_ACTIVITY, null, ident, 0);
    }

    public final void scheduleStopActivity(int ident) {
      queueOrSendMessage(H.STOP_ACTIVITY_HIDE, null, ident, 0);
    }

    public final void scheduleDestroyActivity(int ident) {
      queueOrSendMessage(H.DESTROY_ACTIVITY, null, ident, 0);
    }

    public final void scheduleSendResult(Activity activity, List<ResultInfo> results) {
      // ResultData res = new ResultData();
      // res.token = token;
      // res.results = results;
      // queueOrSendMessage(H.SEND_RESULT, res);
    }

    private void performLaunchActivity(String activityname, Intent startingIntent) {
      ActivityClientRecord current = currentActivity;
      if (current != null)
        schedulePauseActivity(current.ident);
      scheduleLaunchActivity(activityname, startingIntent);
      if (current != null)
        scheduleStopActivity(current.ident);
    }

    private void performSleep() {

    }

    private void performOrientationChange() {

    }

    private void performBackPressed() {
      ActivityClientRecord current = currentActivity;
      if (current != null) {
        schedulePauseActivity(current.ident);
      }
      scheduleResumeActivity(current.parent.mIdent);
      if (current != null) {
        scheduleStopActivity(current.ident);
        scheduleDestroyActivity(current.ident);
      }
    }

    private void performHomePressed() {

      ActivityClientRecord current = currentActivity;
      if (current != null) {
        // schedulePauseActivity(current);
      }

    }

  }

  static final class ActivityClientRecord {
    // IBinder token;
    int ident;
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

    // ActivityInfo activityInfo; //parsed infor from android manifest
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
      return "ActivityRecord{" + (activity == null ? "no component name" : activity.getClass().getName())
          + "}";
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
    public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
    public static final int RELAUNCH_ACTIVITY = 126;

    public void handleMessage(Message msg) {
      switch (msg.what) {
      case LAUNCH_ACTIVITY: {
        ActivityClientRecord r = (ActivityClientRecord) msg.obj;
        handleLaunchActivity(r, null);
      }
        break;
      case RELAUNCH_ACTIVITY: {
        ActivityClientRecord r = (ActivityClientRecord) msg.obj;
        // handleRelaunchActivity(r);
      }
        break;
      case PAUSE_ACTIVITY:
        handlePauseActivity((IBinder) msg.obj, false, msg.arg1 != 0, msg.arg2);
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
        handleStopActivity(msg.arg1);
        break;
      case SHOW_WINDOW:
        // handleWindowVisibility((IBinder) msg.obj, true);
        break;
      case HIDE_WINDOW:
        // handleWindowVisibility((IBinder) msg.obj, false);
        break;
      case RESUME_ACTIVITY:
        handleResumeActivity(msg.arg1);
        break;
      case SEND_RESULT:
        // handleSendResult((ResultData) msg.obj);
        break;
      case DESTROY_ACTIVITY: {
        handleDestroyActivity(msg.arg1);
      }
        break;

      }

    }

  }

  private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {

    // Resolve Component
    // ComponentName component = r.intent.getComponent();
    // if (component == null) {
    // component = r.intent.resolveActivity(
    // mInitialApplication.getPackageManager());
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
      throw new RuntimeException("Unable to instantiate activity " + ": " + e.toString(), e);
    }

    try {
      // Application app = new
      // Application();//r.packageInfo.makeApplication(false,
      // mInstrumentation);

      if (activity != null) {
        // appContext.init(r.packageInfo, r.token, this);
        // appContext.setOuterContext(activity); //set this activity as
        // the main activity
        // CharSequence title = r.activityInfo.loadLabel(appContext
        // .getPackageManager());
        // Configuration config = new
        // Configuration(mCompatConfiguration);

        // activity.attach(this, null, ((currentActivity != null) ? currentActivity.activity : null),
        // r.intent);
        activity.attach(null, this, new Application(), r.intent, null, "",
            ((currentActivity != null) ? currentActivity.activity : null));

        activity.onCreate(r.state);
        r.activity = activity;
        activity.onStart();
        if (r.state != null) {
          activity.onRestoreInstanceState(r.state);
        }
        activity.onPostCreate(r.state);
        r.paused = true;

        mActivities.put(r.ident, r);

      }
    } catch (Exception e) {
      throw new RuntimeException("Unable to start activity " + r.getName() + ": " + e.toString(), e);
    }

    return activity;
  }

  private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent) {

    // Make sure we are running with the most recent config.
    // handleConfigurationChanged(null, null);

    Activity a = performLaunchActivity(r, customIntent);

    if (a != null) {
      // r.createdConfig = new Configuration(mConfiguration);
      Bundle oldState = r.state;
      handleResumeActivity(r);

      if (currentActivity != null) {
        currentActivity.activity.onStop();
        a.mParent = currentActivity.activity;
      }
      currentActivity = r;

    } else {
      // If there was an error, for any reason, tell the activity
      // manager to stop us.
      // try {
      // TODO ActivityManagerNative.getDefault().finishActivity(r.token, Activity.RESULT_CANCELED, null);
      // } catch (RemoteException ex) {
      // Ignore
      // }

    }
  }

  final void handleResumeActivity(ActivityClientRecord r) {
    performResumeActivity(r);
  }

  public final void performResumeActivity(ActivityClientRecord r) {
    if (r != null) {
      try {
        r.activity.onResume();
        r.activity.onPostResume();
        r.paused = false;
        r.stopped = false;
        r.state = null;
      } catch (Exception e) {
        throw new RuntimeException("Unable to resume activity " + r.intent.getComponent() + ": "
            + e.toString(), e);
      }
    }
  }

  private void handlePauseActivity(int ident) {
    ActivityClientRecord r = mActivities.get(ident);
    if (r != null) {
      // Slog.v(TAG, "userLeaving=" + userLeaving + " handling pause of " + r);
      // if (userLeaving) {
      // performUserLeavingActivity(r);
      // }

      // r.activity.mConfigChangeFlags |= configChanges;
      performPauseActivity(ident);

      // Make sure any pending writes are now committed.
      // if (r.isPreHoneycomb()) {
      // QueuedWork.waitToFinish();
      // }

      // Tell the activity manager we have paused.
      // try {
      // ActivityManagerNative.getDefault().activityPaused(token);
      // } catch (RemoteException ex) {
      // }
    }
  }

  final Bundle performPauseActivity(int ident) {
    ActivityClientRecord r = mActivities.get(ident);
    return performPauseActivity(r);
  }

  final Bundle performPauseActivity(ActivityClientRecord r) {
    // if (r.paused) {
    // if (r.activity.mFinished) {
    // // If we are finishing, we won't call onResume() in certain cases.
    // // So here we likewise don't want to call onPause() if the activity
    // // isn't resumed.
    // return null;
    // }
    // RuntimeException e = new RuntimeException("Performing pause of activity that is not resumed: "
    // + r.intent.getComponent());
    // // Slog.e(TAG, e.getMessage(), e);
    // }
    Bundle state = null;
    // if (finished) {
    // r.activity.mFinished = true;
    // }
    try {
      // Next have the activity save its current state and managed dialogs...
      if (!r.activity.mFinished && saveState) {
        state = new Bundle();
        state.setAllowFds(false);
        mInstrumentation.callActivityOnSaveInstanceState(r.activity, state);
        r.state = state;
      }
      // Now we are idle.
      r.activity.mCalled = false;
      mInstrumentation.callActivityOnPause(r.activity);
      EventLog.writeEvent(LOG_ON_PAUSE_CALLED, r.activity.getComponentName().getClassName());
      if (!r.activity.mCalled) {
        throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString()
            + " did not call through to super.onPause()");
      }

    } catch (SuperNotCalledException e) {
      throw e;

    } catch (Exception e) {
      if (!mInstrumentation.onException(r.activity, e)) {
        throw new RuntimeException("Unable to pause activity " + r.intent.getComponent().toShortString()
            + ": " + e.toString(), e);
      }
    }
    r.paused = true;

    // Notify any outstanding on paused listeners
    ArrayList<OnActivityPausedListener> listeners;
    synchronized (mOnPauseListeners) {
      listeners = mOnPauseListeners.remove(r.activity);
    }
    int size = (listeners != null ? listeners.size() : 0);
    for (int i = 0; i < size; i++) {
      listeners.get(i).onPaused(r.activity);
    }

    return state;
  }

  private void handleDestroyActivity(String obj) {
    currentActivity.activity.onPause();
    currentActivity.activity.onStop();

  }

  private void attach() {
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
  }

}