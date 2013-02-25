package android.app;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Slog;
import android.view.Window;
import android.view.WindowManager;

/**
 * This manages the execution of the main thread in an application process, scheduling and executing
 * activities, broadcasts, and other operations on it as the activity manager requests.
 * 
 * 
 */
public final class ActivityThread {
  public static final String TAG = "ActivityThread";
  public static boolean DEBUG = true;
  static final boolean localLOGV = true;

  final Looper mLooper = Looper.myLooper();
  final H mH = new H();
  // stores a map of all activities that have been started and not destroyed
  final HashMap<Integer, ActivityClientRecord> mActivities = new HashMap<Integer, ActivityClientRecord>();

  // stores the current running Activity
  // ActivityClientRecord currentActivity;

  final HashMap<IBinder, Service> mServices = new HashMap<IBinder, Service>();
  final ApplicationThread mAppThread = new ApplicationThread();
  Configuration mConfiguration;
  static final ThreadLocal<ActivityThread> sThreadLocal = new ThreadLocal<ActivityThread>();
  Application mInitialApplication;

  LoadedApk mPackage; // For testing we do not have to store multiple packages, only one as we are only
                      // testing one application

  Instrumentation mInstrumentation;

  class ApplicationThread {

    /**
     * Called to schedule the launch of an Activity for the first time
     * 
     * @param activityName
     *          the name of the Activity to start
     * @param intent
     */
    public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident, ActivityInfo info,
                                             Bundle state, Activity parent, int requestCode) {
      ActivityClientRecord r = new ActivityClientRecord();
      r.name = intent.getComponent().flattenToShortString();
      r.intent = intent;
      r.requestCode = requestCode;
      r.parent = parent; //used to restore activity stack
      r.activityInfo = info;
      r.state = state;
      r.token = token;
      queueOrSendMessage(H.LAUNCH_ACTIVITY, r, 0, 0);
    }

    public final void scheduleRelaunchActivity(String activityName, Intent intent) {
      // requestRelaunchActivity(token, pendingResults, pendingNewIntents,
      // configChanges, notResumed, config,
      // true);
    }

    public final void scheduleResumeActivity(int ident) {
      queueOrSendMessage(H.RESUME_ACTIVITY, null, ident, 0);
    }

    public final void schedulePauseActivity(int ident, boolean finished) {
      queueOrSendMessage(finished ? H.PAUSE_ACTIVITY_FINISHING : H.PAUSE_ACTIVITY, null, ident, 0);
    }

    public final void scheduleStopActivity(int token) {
      queueOrSendMessage(H.STOP_ACTIVITY_HIDE, null, token, 0);
    }

    public final void scheduleDestroyActivity(int token, boolean finishing) {
      queueOrSendMessage(H.DESTROY_ACTIVITY, token, finishing ? 1 : 0, 0);
    }

    public final void scheduleSendResult(int ident, ResultInfo result) {
      queueOrSendMessage(H.SEND_RESULT, result, ident, 0);
    }

    private void scheduleRelaunchActivity() {
      queueOrSendMessage(H.RELAUNCH_ACTIVITY, null, 0, 0);
    }

    public final void scheduleCreateService(IBinder token, ServiceInfo info, CompatibilityInfo compatInfo) {
      CreateServiceData s = new CreateServiceData();
      s.token = token;
      s.info = info;
      s.compatInfo = compatInfo;

      queueOrSendMessage(H.CREATE_SERVICE, s);
    }

    public final void scheduleBindService(IBinder token, Intent intent, boolean rebind) {
      BindServiceData s = new BindServiceData();
      s.token = token;
      s.intent = intent;
      s.rebind = rebind;

      queueOrSendMessage(H.BIND_SERVICE, s);
    }

    public final void scheduleUnbindService(IBinder token, Intent intent) {
      BindServiceData s = new BindServiceData();
      s.token = token;
      s.intent = intent;

      queueOrSendMessage(H.UNBIND_SERVICE, s);
    }

    public final void scheduleServiceArgs(IBinder token, boolean taskRemoved, int startId, int flags,
                                          Intent args) {
      ServiceArgsData s = new ServiceArgsData();
      s.token = token;
      s.taskRemoved = taskRemoved;
      s.startId = startId;
      s.flags = flags;
      s.args = args;

      queueOrSendMessage(H.SERVICE_ARGS, s);
    }

    public final void scheduleStopService(IBinder token) {
      queueOrSendMessage(H.STOP_SERVICE, token);
    }

  }

  static final class CreateServiceData {
    IBinder token;
    ServiceInfo info;
    CompatibilityInfo compatInfo;
    Intent intent;

    public String toString() {
      return "CreateServiceData{token=" + token + " className=" + info.name + " packageName="
          + info.packageName + " intent=" + intent + "}";
    }
  }

  static final class BindServiceData {
    IBinder token;
    Intent intent;
    boolean rebind;

    public String toString() {
      return "BindServiceData{token=" + token + " intent=" + intent + "}";
    }
  }

  static final class ServiceArgsData {
    IBinder token;
    boolean taskRemoved;
    int startId;
    int flags;
    Intent args;

    public String toString() {
      return "ServiceArgsData{token=" + token + " startId=" + startId + " args=" + args + "}";
    }
  }

  static final class ActivityClientRecord {
    public int requestCode;
    IBinder token;
    int ident;
    Intent intent;
    Bundle state;
    Activity activity;
    Window window;
    Activity parent;
    // String embeddedID;
    Activity.NonConfigurationInstances lastNonConfigurationInstances;
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

    ActivityInfo activityInfo; // parsed infor from android manifest
    // CompatibilityInfo compatInfo;
    LoadedApk packageInfo;

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

  // if the thread hasn't started yet, we don't have the handler, so just
  // save the messages until we're ready.
  private void queueOrSendMessage(int what, Object obj) {
    queueOrSendMessage(what, obj, 0, 0);
  }

  private void queueOrSendMessage(int what, Object obj, int arg1) {
    queueOrSendMessage(what, obj, arg1, 0);
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
    public static final int BIND_SERVICE = 121;
    public static final int UNBIND_SERVICE = 122;
    public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
    public static final int RELAUNCH_ACTIVITY = 126;

    public void handleMessage(Message msg) {
      switch (msg.what) {
      case LAUNCH_ACTIVITY: {
        ActivityClientRecord r = (ActivityClientRecord) msg.obj;
        // TODO r.packageInfo = getPackageInfoNoCheck(r.activityInfo.applicationInfo, r.compatInfo);
        handleLaunchActivity(r, null);
      }
        break;
      case RELAUNCH_ACTIVITY: {
        ActivityClientRecord r = currentActivity;
        handleRelaunchActivity(r);
      }
        break;
      case PAUSE_ACTIVITY:
        handlePauseActivity(msg.arg1, false);
        // maybeSnapshot();
        break;
      case PAUSE_ACTIVITY_FINISHING:
        handlePauseActivity(msg.arg1, true);
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
        handleSendResult((ResultInfo) msg.obj);
        break;
      case DESTROY_ACTIVITY:
        handleDestroyActivity(msg.arg1, msg.arg1 != 0, msg.arg2, false);
      case CREATE_SERVICE:
        handleCreateService((CreateServiceData) msg.obj);
        break;
      case BIND_SERVICE:
        handleBindService((BindServiceData) msg.obj);
        break;
      case UNBIND_SERVICE:
        handleUnbindService((BindServiceData) msg.obj);
        break;
      case SERVICE_ARGS:
        handleServiceArgs((ServiceArgsData) msg.obj);
        break;
      case STOP_SERVICE:
        handleStopService((IBinder) msg.obj);
        // maybeSnapshot();
        break;
      }

    }
  }

  public static ActivityThread currentActivityThread() {
    return sThreadLocal.get();
  }

  private void handleCreateService(CreateServiceData data) {
    // If we are getting ready to gc after going to the background, well
    // we are back active so skip it.
    unscheduleGcIdler();

    LoadedApk packageInfo = getPackageInfoNoCheck(data.info.applicationInfo, data.compatInfo);
    Service service = null;
    try {
      java.lang.ClassLoader cl = packageInfo.getClassLoader();
      service = (Service) cl.loadClass(data.info.name).newInstance();
    } catch (Exception e) {
      if (!mInstrumentation.onException(service, e)) {
        throw new RuntimeException("Unable to instantiate service " + data.info.name + ": " + e.toString(), e);
      }
    }

    try {
      if (localLOGV)
        Slog.v(TAG, "Creating service " + data.info.name);

      ContextImpl context = new ContextImpl();
      context.init(packageInfo, null, this);

      Application app = packageInfo.makeApplication(false, mInstrumentation);
      context.setOuterContext(service);
      service.attach(context, this, data.info.name, data.token, app, ActivityManagerNative.getDefault());
      service.onCreate();
      mServices.put(data.token, service);
      try {
        ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, 0, 0, 0);
      } catch (RemoteException e) {
        // nothing to do.
      }
    } catch (Exception e) {
      if (!mInstrumentation.onException(service, e)) {
        throw new RuntimeException("Unable to create service " + data.info.name + ": " + e.toString(), e);
      }
    }
  }

  private void handleBindService(BindServiceData data) {
    Service s = mServices.get(data.token);
    if (s != null) {
      try {
        data.intent.setExtrasClassLoader(s.getClassLoader());
        try {
          if (!data.rebind) {
            IBinder binder = s.onBind(data.intent);
            ActivityManagerNative.getDefault().publishService(data.token, data.intent, binder);
          } else {
            s.onRebind(data.intent);
            ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, 0, 0, 0);
          }
          ensureJitEnabled();
        } catch (RemoteException ex) {
        }
      } catch (Exception e) {
        if (!mInstrumentation.onException(s, e)) {
          throw new RuntimeException("Unable to bind to service " + s + " with " + data.intent + ": "
              + e.toString(), e);
        }
      }
    }
  }

  private void handleServiceArgs(ServiceArgsData data) {
    Service s = mServices.get(data.token);
    if (s != null) {
      try {
        if (data.args != null) {
          data.args.setExtrasClassLoader(s.getClassLoader());
        }
        int res;
        if (!data.taskRemoved) {
          res = s.onStartCommand(data.args, data.flags, data.startId);
        } else {
          s.onTaskRemoved(data.args);
          res = Service.START_TASK_REMOVED_COMPLETE;
        }

        QueuedWork.waitToFinish();

        try {
          ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, 1, data.startId, res);
        } catch (RemoteException e) {
          // nothing to do.
        }
        ensureJitEnabled();
      } catch (Exception e) {
        if (!mInstrumentation.onException(s, e)) {
          throw new RuntimeException("Unable to start service " + s + " with " + data.args + ": "
              + e.toString(), e);
        }
      }
    }
  }

  private void handleStopService(IBinder token) {
    Service s = mServices.remove(token);
    if (s != null) {
      try {
        if (localLOGV)
          Slog.v(TAG, "Destroying service " + s);
        s.onDestroy();
        Context context = s.getBaseContext();
        if (context instanceof ContextImpl) {
          final String who = s.getClassName();
          ((ContextImpl) context).scheduleFinalCleanup(who, "Service");
        }

        QueuedWork.waitToFinish();

        try {
          ActivityManagerNative.getDefault().serviceDoneExecuting(token, 0, 0, 0);
        } catch (RemoteException e) {
          // nothing to do.
        }
      } catch (Exception e) {
        if (!mInstrumentation.onException(s, e)) {
          throw new RuntimeException("Unable to stop service " + s + ": " + e.toString(), e);
        }
      }
    }
    // Slog.i(TAG, "Running services: " + mServices);
  }

  private void handleUnbindService(BindServiceData data) {
    Service s = mServices.get(data.token);
    if (s != null) {
      try {
        data.intent.setExtrasClassLoader(s.getClassLoader());
        boolean doRebind = s.onUnbind(data.intent);
        try {
          if (doRebind) {
            ActivityManagerNative.getDefault().unbindFinished(data.token, data.intent, doRebind);
          } else {
            ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, 0, 0, 0);
          }
        } catch (RemoteException ex) {
        }
      } catch (Exception e) {
        if (!mInstrumentation.onException(s, e)) {
          throw new RuntimeException("Unable to unbind to service " + s + " with " + data.intent + ": "
              + e.toString(), e);
        }
      }
    }
  }

  private void handleSendResult(ResultInfo res, int ident) {
    ActivityClientRecord r = mActivities.get(ident);
    System.out.println(r);
    if (r != null) {
      // final boolean resumed = !r.paused;
      // if (!r.activity.mFinished && r.activity.mDecor != null &&
      // r.hideForNow && resumed) {
      // // We had hidden the activity because it started another
      // // one... we have gotten a result back and we are not
      // // paused, so make sure our window is visible.
      // updateVisibility(r, true);
      // }
      // if (resumed) {
      try {
        // Now we are idle.
        // r.activity.mCalled = false;
        r.activity.mTemporaryPause = true;
        // r.activity.onPause();
        // if (!r.activity.mCalled) {
        // throw new SuperNotCalledException("Activity " +
        // r.intent.getComponent()
        // + " did not call through to super.onPause()");
        // }
      } catch (SuperNotCalledException e) {
        throw e;
      } catch (Exception e) {
        // if (!mInstrumentation.onException(r.activity, e)) {
        throw new RuntimeException("Unable to pause activity " + r.intent.getComponent() + ": "
            + e.toString(), e);
        // }
      }
      deliverResults(r, res);
      System.out.println("deliver results");
      // if (resumed) {
      r.activity.performResume();
      System.out.println("performResume");

      r.activity.mTemporaryPause = false;
      // }
    }
  }

  private void deliverResults(ActivityClientRecord r, ResultInfo result) {
    r.activity.dispatchActivityResult(result.mResultWho, result.mRequestCode, result.mResultCode,
        result.mData);
  }

  final void handleResumeActivity(int arg1) {
    // If we are getting ready to gc after going to the background, well
    // we are back active so skip it.
    // unscheduleGcIdler();

    ActivityClientRecord r = performResumeActivity(arg1);
    if (r != null) {
      currentActivity = r;
      final Activity a = r.activity;

      // final int forwardBit = isForward ?
      // WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION : 0;

      // If the window hasn't yet been added to the window manager,
      // and this guy didn't finish itself or start another activity,
      // then go ahead and add the window.
      // boolean willBeVisible = !a.mStartedActivity;
      // if (!willBeVisible) {
      // try {
      // willBeVisible =
      // ActivityManagerNative.getDefault().willActivityBeVisible(a.getActivityToken());
      // } catch (RemoteException e) {
      // }
      // }
      // if (r.window == null && !a.mFinished && willBeVisible) {
      // / r.window = r.activity.getWindow();
      // View decor = r.window.getDecorView();
      // decor.setVisibility(View.INVISIBLE);
      // ViewManager wm = a.getWindowManager();
      // WindowManager.LayoutParams l = r.window.getAttributes();
      // a.mDecor = decor;
      // l.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
      // l.softInputMode |= forwardBit;
      // if (a.mVisibleFromClient) {
      // a.mWindowAdded = true;
      // wm.addView(decor, l);
      // }

      // If the window has already been added, but during resume
      // we started another activity, then don't yet make the
      // window visible.
      // } else if (!willBeVisible) {
      // if (localLOGV)
      // Slog.v(TAG, "Launch " + r + " mStartedActivity set");
      // r.hideForNow = true;
      // }

      // Get rid of anything left hanging around.
      // cleanUpPendingRemoveWindows(r);

      // The window is now visible if it has been added, we are not
      // simply finishing, and we are not starting another activity.
      // if (!r.activity.mFinished && willBeVisible && r.activity.mDecor
      // != null && !r.hideForNow) {
      // if (r.newConfig != null) {
      // if (DEBUG_CONFIGURATION)
      // Slog.v(TAG, "Resuming activity " + r.activityInfo.name +
      // " with newConfig " + r.newConfig);
      // performConfigurationChanged(r.activity, r.newConfig);
      // r.newConfig = null;
      // }
      // if (localLOGV)
      // Slog.v(TAG, "Resuming " + r + " with isForward=" + isForward);
      // WindowManager.LayoutParams l = r.window.getAttributes();
      // if ((l.softInputMode &
      // WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION) !=
      // forwardBit) {
      // l.softInputMode = (l.softInputMode &
      // (~WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION))
      // | forwardBit;
      // if (r.activity.mVisibleFromClient) {
      // ViewManager wm = a.getWindowManager();
      // View decor = r.window.getDecorView();
      // wm.updateViewLayout(decor, l);
      // }
      // }
      // r.activity.mVisibleFromServer = true;
      // mNumVisibleActivities++;
      // if (r.activity.mVisibleFromClient) {
      r.activity.makeVisible();
      // }
      // }

      // if (!r.onlyLocalRequest) {
      // r.nextIdle = mNewActivities;
      // mNewActivities = r;
      // if (localLOGV)
      // Slog.v(TAG, "Scheduling idle handler for " + r);
      // Looper.myQueue().addIdleHandler(new Idler());
      // }
      // r.onlyLocalRequest = false;

      // } else {
      // If an exception was thrown when trying to resume, then
      // just end this activity.
      // try {
      // ActivityManagerNative.getDefault().finishActivity(token,
      // Activity.RESULT_CANCELED, null);
      // } catch (RemoteException ex) {
      // }
    }

  }

  public final ActivityClientRecord performResumeActivity(int token) {
    ActivityClientRecord r = mActivities.get(token);
    // if (localLOGV)
    // Slog.v(TAG, "Performing resume of " + r + " finished=" +
    // r.activity.mFinished);
    if (r != null && !r.activity.mFinished) {
      // if (clearHide) {
      // r.hideForNow = false;
      r.activity.mStartedActivity = false;
      // }
      // try {
      // if (r.pendingIntents != null) {
      // deliverNewIntents(r, r.pendingIntents);
      // r.pendingIntents = null;
      // }
      // if (r.pendingResults != null) {
      // deliverResults(r, r.pendingResults);
      // r.pendingResults = null;
      // }
      r.activity.performResume();

      // EventLog.writeEvent(LOG_ON_RESUME_CALLED,
      // r.activity.getComponentName().getClassName());

      r.paused = false;
      r.stopped = false;
      r.state = null;
      // } catch (Exception e) {
      // / if (!mInstrumentation.onException(r.activity, e)) {
      // throw new RuntimeException("Unable to resume activity " +
      // r.intent.getComponent().toShortString()
      // + ": " + e.toString(), e);
      // }
      // }
    }
    return r;
  }

  final void handleStopActivity(int arg1) {
    ActivityClientRecord r = mActivities.get(arg1);
    // r.activity.mConfigChangeFlags |= configChanges;

    // StopInfo info = new StopInfo();
    performStopActivityInner(r);

    // if (localLOGV)
    // Slog.v(TAG, "Finishing stop of " + r + ": show=" + show + " win=" +
    // r.window);

    // updateVisibility(r, show);

    // Make sure any pending writes are now committed.
    // if (!r.isPreHoneycomb()) {
    // QueuedWork.waitToFinish();
    // / }

    // Tell activity manager we have been stopped.
    // // try {
    // ActivityManagerNative.getDefault().activityStopped(r.token, r.state,
    // info.thumbnail, info.description);
    // } catch (RemoteException ex) {
    // }

  }

  /**
   * Core implementation of stopping an activity. Note this is a little tricky because the server's meaning of
   * stop is slightly different than our client -- for the server, stop means to save state and give it the
   * result when it is done, but the window may still be visible. For the client, we want to call
   * onStop()/onStart() to indicate when the activity's UI visibillity changes.
   */
  private void performStopActivityInner(ActivityClientRecord r) {
    // if (localLOGV) Slog.v(TAG, "Performing stop of " + r);
    Bundle state = null;
    // if (r != null) {
    // if (!keepShown && r.stopped) {
    // if (r.activity.mFinished) {
    // If we are finishing, we won't call onResume() in certain
    // cases. So here we likewise don't want to call onStop()
    // if the activity isn't resumed.
    // return;
    // }
    // RuntimeException e = new RuntimeException(
    // "Performing stop of activity that is not resumed: "
    // + r.intent.getComponent().toShortString());
    // S//log.e(TAG, e.getMessage(), e);
    // }

    // if (info != null) {
    // try {
    // First create a thumbnail for the activity...
    // For now, don't create the thumbnail here; we are
    // doing that by doing a screen snapshot.
    // info.thumbnail = null; //createThumbnailBitmap(r);
    // info.description = r.activity.onCreateDescription();
    // } catch (Exception e) {
    // if (!mInstrumentation.onException(r.activity, e)) {
    // throw new RuntimeException(
    // "Unable to save state of activity "
    // + r.intent.getComponent().toShortString()
    // + ": " + e.toString(), e);
    // }
    // }
    // }

    // Next have the activity save its current state and managed dialogs...
    if (!r.activity.mFinished) {
      if (r.state == null) {
        state = new Bundle();
        // state.setAllowFds(false);
        r.activity.onSaveInstanceState(state);
        r.state = state;
      } else {
        state = r.state;
      }
    }

    // if (!keepShown) {
    // try {
    // Now we are idle.
    r.activity.performStop();
    // } catch (Exception e) {
    // if (!mInstrumentation.onException(r.activity, e)) {
    // throw new RuntimeException(
    // "Unable to stop activity "
    // + r.intent.getComponent().toShortString()
    // + ": " + e.toString(), e);
    // }
    // }
    r.stopped = true;
    // }

    r.paused = true;
    // / }
  }

  private void handleRelaunchActivity(ActivityClientRecord tmp) {
    // If we are getting ready to gc after going to the background, well
    // we are back active so skip it.
    // unscheduleGcIdler();

    // Configuration changedConfig = null;
    // int configChanges = 0;

    // First: make sure we have the most recent configuration and most
    // recent version of the activity, or skip it if some previous call
    // had taken a more recent version.
    // synchronized (mPackages) {
    // int N = mRelaunchingActivities.size();
    // IBinder token = tmp.token;
    // tmp = null;
    // for (int i = 0; i < N; i++) {
    // ActivityClientRecord r = mRelaunchingActivities.get(i);
    // if (r.token == token) {
    // tmp = r;
    // // configChanges |= tmp.pendingConfigChanges;
    // mRelaunchingActivities.remove(i);
    // i--;
    // / N--;
    // }
    // }

    // if (tmp == null) {
    // if (DEBUG_CONFIGURATION)
    // Slog.v(TAG, "Abort, activity not relaunching!");
    // return;
    // }
    //
    // if (DEBUG_CONFIGURATION)
    // Slog.v(
    // TAG,
    // "Relaunching activity " + tmp.token + " with configChanges=0x"
    // + Integer.toHexString(configChanges));
    //
    // if (mPendingConfiguration != null) {
    // changedConfig = mPendingConfiguration;
    // mPendingConfiguration = null;
    // }
    // }

    // if (tmp.createdConfig != null) {
    // // If the activity manager is passing us its current config,
    // // assume that is really what we want regardless of what we
    // // may have pending.
    // if (mConfiguration == null
    // || (tmp.createdConfig.isOtherSeqNewer(mConfiguration) &&
    // mConfiguration.diff(tmp.createdConfig) != 0))
    // {
    // if (changedConfig == null ||
    // tmp.createdConfig.isOtherSeqNewer(changedConfig)) {
    // changedConfig = tmp.createdConfig;
    // }
    // }
    // }
    //
    // if (DEBUG_CONFIGURATION)
    // Slog.v(TAG, "Relaunching activity " + tmp.token + ": changedConfig="
    // + changedConfig);
    //
    // // If there was a pending configuration change, execute it first.
    // if (changedConfig != null) {
    // handleConfigurationChanged(changedConfig, null);
    // }

    ActivityClientRecord r = currentActivity;
    // if (DEBUG_CONFIGURATION)
    // Slog.v(TAG, "Handling relaunch of " + r);
    // if (r == null) {
    // return;
    // }

    // r.activity.mConfigChangeFlags |= configChanges;
    // r.onlyLocalRequest = tmp.onlyLocalRequest;
    Intent currentIntent = r.activity.mIntent;

    r.activity.mChangingConfigurations = true;

    // Need to ensure state is saved.
    if (!r.paused) {
      performPauseActivity(r.ident, true);
    }
    if (r.state == null && !r.stopped) {
      r.state = new Bundle();
      r.state.setAllowFds(false);
      r.activity.onSaveInstanceState(r.state);
    }

    handleDestroyActivity(r.ident, false, 0, true);

    r.activity = null;
    r.window = null;
    r.hideForNow = false;
    // r.nextIdle = null;
    // Merge any pending results and pending intents; don't just replace
    // them
    // if (tmp.pendingResults != null) {
    // if (r.pendingResults == null) {
    // r.pendingResults = tmp.pendingResults;
    // } else {
    // r.pendingResults.addAll(tmp.pendingResults);
    // }
    // }
    // if (tmp.pendingIntents != null) {
    // if (r.pendingIntents == null) {
    // r.pendingIntents = tmp.pendingIntents;
    // } else {
    // r.pendingIntents.addAll(tmp.pendingIntents);
    // }
    // }
    // r.startsNotResumed = tmp.startsNotResumed;

    handleLaunchActivity(r, currentIntent);
  }

  private void handleDestroyActivity(int token, boolean finishing, int configChanges,
                                     boolean getNonConfigInstance) {
    ActivityClientRecord r = performDestroyActivity(token, finishing, configChanges, getNonConfigInstance);
    if (r != null) {
      // cleanUpPendingRemoveWindows(r);
      // WindowManager wm = r.activity.getWindowManager();
      // View v = r.activity.mDecor;
      // if (v != null) {
      // // if (r.activity.mVisibleFromServer) {
      // mNumVisibleActivities--;
      // }
      // IBinder wtoken = v.getWindowToken();
      // if (r.activity.mWindowAdded) {
      // if (r.onlyLocalRequest) {
      // Hold off on removing this until the new activity's
      // window is being added.
      // r.mPendingRemoveWindow = v;
      // r.mPendingRemoveWindowManager = wm;
      // } else {
      // wm.removeViewImmediate(v);
      // }
      // }
      // if (wtoken != null && r.mPendingRemoveWindow == null) {
      // WindowManagerImpl.getDefault().closeAll(wtoken,
      // r.activity.getClass().getName(), "Activity");
      // }
      // r.activity.mDecor = null;
      // }
      // if (r.mPendingRemoveWindow == null) {
      // If we are delaying the removal of the activity window, then
      // we can't clean up all windows here. Note that we can't do
      // so later either, which means any windows that aren't closed
      // by the app will leak. Well we try to warning them a lot
      // about leaking windows, because that is a bug, so if they are
      // using this recreate facility then they get to live with leaks.
      // WindowManagerImpl.getDefault().closeAll(token,
      // r.activity.getClass().getName(), "Activity");
    }

    // Mocked out contexts won't be participating in the normal
    // process lifecycle, but if we're running with a proper
    // ApplicationContext we need to have it tear down things
    // cleanly.
    // / Context c = r.activity.getBaseContext();
    // if (c instanceof ContextImpl) {
    // ((ContextImpl)
    // c).scheduleFinalCleanup(r.activity.getClass().getName(), "Activity");
    // }
    // }
    // if (finishing) {
    // try {
    // / ActivityManagerNative.getDefault().activityDestroyed(token);
    // } catch (RemoteException ex) {
    // If the system process has died, it's game over for everyone.
    // }
    // }

  }

  private ActivityClientRecord performDestroyActivity(int token, boolean finishing, int configChanges,
                                                      boolean getNonConfigInstance) {
    ActivityClientRecord r = mActivities.get(token);
    if (r != null) {
      // r.activity.mConfigChangeFlags |= configChanges;
      if (finishing) {
        r.activity.mFinished = true;
      }
      if (!r.paused) {
        try {
          r.activity.mCalled = false;
          mInstrumentation.callActivityOnPause(r.activity);
          if (!r.activity.mCalled) {
            throw new SuperNotCalledException("Activity " + r.intent.getComponent()
                + " did not call through to super.onPause()");
          }
        } catch (SuperNotCalledException e) {
          throw e;
        } catch (Exception e) {
          if (!mInstrumentation.onException(r.activity, e)) {
            throw new RuntimeException("Unable to pause activity " + r.intent.getComponent() + ": "
                + e.toString(), e);
          }
        }
        r.paused = true;
      }
      if (!r.stopped) {
        try {
          r.activity.performStop();
        } catch (SuperNotCalledException e) {
          throw e;
        } catch (Exception e) {
          if (!mInstrumentation.onException(r.activity, e)) {
            throw new RuntimeException("Unable to stop activity " + r.intent.getComponent() + ": "
                + e.toString(), e);
          }
        }
        r.stopped = true;
      }
      if (getNonConfigInstance) {
        try {
          r.lastNonConfigurationInstances = r.activity.retainNonConfigurationInstances();
        } catch (Exception e) {
          if (!mInstrumentation.onException(r.activity, e)) {
            throw new RuntimeException("Unable to retain activity " + r.intent.getComponent() + ": "
                + e.toString(), e);
          }
        }
      }
      try {
        r.activity.mCalled = false;
        mInstrumentation.callActivityOnDestroy(r.activity);
        if (!r.activity.mCalled) {
          throw new SuperNotCalledException("Activity " + r.intent.getComponent()
              + " did not call through to super.onDestroy()");
        }
        // if (r.window != null) {
        // r.window.closeAllPanels();
        // }
      } catch (SuperNotCalledException e) {
        throw e;
      } catch (Exception e) {
        if (!mInstrumentation.onException(r.activity, e)) {
          throw new RuntimeException("Unable to destroy activity " + r.intent.getComponent() + ": "
              + e.toString(), e);
        }
      }
    }
    mActivities.remove(token);
    // StrictMode.decrementExpectedActivityCount(activityClass);
    return r;
  }

  private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {

    // Resolve Component
    String component = r.intent.getComponent().getPackageName() + "."
        + r.intent.getComponent().getClassName();
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
      // java.lang.ClassLoader cl = r.packageInfo.getClassLoader();
      activity = mInstrumentation.newActivity(null, component, r.intent);
    } catch (Exception e) {
      if (!mInstrumentation.onException(activity, e)) {
        throw new RuntimeException("Unable to instantiate activity " + component + ": " + e.toString(), e);
      }
    }

    try {
      Application app = new Application();

      if (activity != null) {
        ContextImpl appContext = new ContextImpl();
        appContext.init(r.packageInfo, null, this);
        appContext.setOuterContext(activity); // set this activity as main
        CharSequence title = activity.getClass().getName();
        r.ident = activity.mIdent;
        activity.attach((Context) appContext, this, getInstrumentation(), (IBinder) null, app, r.intent,
            (ActivityInfo) null, title, r.parent, "0", r.lastNonConfigurationInstances, new Configuration());
        r.ident = activity.mIdent;
        if (customIntent != null) {
          activity.mIntent = customIntent;
        }
        r.lastNonConfigurationInstances = null;
        activity.mStartedActivity = false;
        // int theme = r.activityInfo.getThemeResource();
        // if (theme != 0) {
        // activity.setTheme(theme);
        // }
        activity.mCalled = false;
        mInstrumentation.callActivityOnCreate(activity, r.state);
        if (!activity.mCalled) {
          throw new SuperNotCalledException("Activity " + r.intent.getComponent()
              + " did not call through to super.onCreate()");
        }
        r.activity = activity;
        r.stopped = true;
        if (!r.activity.mFinished) {
          activity.performStart();
          r.stopped = false;
        }
        if (!r.activity.mFinished) {
          if (r.state != null) {
            mInstrumentation.callActivityOnRestoreInstanceState(activity, r.state);
          }
        }
        if (!r.activity.mFinished) {
          activity.mCalled = false;
          mInstrumentation.callActivityOnPostCreate(activity, r.state);
          if (!activity.mCalled) {
            throw new SuperNotCalledException("Activity " + r.intent.getComponent()
                + " did not call through to super.onPostCreate()");
          }
        }
      }
      r.paused = true;

      mActivities.put(r.ident, r);
    } catch (SuperNotCalledException e) {
      throw e;
    } catch (Exception e) {
      if (!mInstrumentation.onException(activity, e)) {
        throw new RuntimeException("Unable to start activity " + component + ": " + e.toString(), e);
      }
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
      // if (currentActivity != null) {
      // a.mParent = currentActivity.activity;
      // r.parent = currentActivity.activity;
      //
      // }

      handleResumeActivity(r.ident);

    } else {
      // If there was an error, for any reason, tell the activity
      // manager to stop us.
      // try {
      // TODO ActivityManagerNative.getDefault().finishActivity(r.token,
      // Activity.RESULT_CANCELED, null);
      // } catch (RemoteException ex) {
      // Ignore
      // }

    }
  }

  private void handlePauseActivity(int ident, boolean finished) {
    ActivityClientRecord r = mActivities.get(ident);
    if (r != null) {
      // Slog.v(TAG, "userLeaving=" + userLeaving + " handling pause of "
      // + r);
      // if (userLeaving) {
      // performUserLeavingActivity(r);
      // }

      // r.activity.mConfigChangeFlags |= configChanges;
      performPauseActivity(ident, finished);

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

  final Bundle performPauseActivity(int ident, boolean finished) {
    ActivityClientRecord r = mActivities.get(ident);
    return performPauseActivity(r, finished);
  }

  final Bundle performPauseActivity(ActivityClientRecord r, boolean finished) {
    if (r.paused) {
      if (r.activity.mFinished) {
        // If we are finishing, we won't call onResume() in certain
        // cases.
        // So here we likewise don't want to call onPause() if the
        // activity
        // isn't resumed.
        return null;
      }
      RuntimeException e = new RuntimeException("Performing pause of activity that is not resumed: "
          + r.intent.getComponent());
      // Slog.e(TAG, e.getMessage(), e);
    }
    Bundle state = null;
    if (finished) {
      r.activity.mFinished = true;
    }
    try {
      // Next have the activity save its current state and managed
      // dialogs...
      // if (!r.activity.mFinished) {
      // state = new Bundle();
      // state.setAllowFds(false);
      // r.activity.onSaveInstanceState(state);
      // r.state = state;
      // }
      // Now we are idle.
      r.activity.mCalled = false;
      r.activity.onPause();
      if (!r.activity.mCalled) {
        throw new SuperNotCalledException("Activity " + r.intent.getComponent()
            + " did not call through to super.onPause()");
      }

    } catch (SuperNotCalledException e) {
      throw e;

    } catch (Exception e) {
      // if (!mInstrumentation.onException(r.activity, e)) {
      throw new RuntimeException("Unable to pause activity " + r.intent.getComponent() + ": " + e.toString(),
          e);
      // }
    }
    r.paused = true;

    // Notify any outstanding on paused listeners
    // ArrayList<OnActivityPausedListener> listeners;
    // synchronized (mOnPauseListeners) {
    // listeners = mOnPauseListeners.remove(r.activity);
    // }
    // int size = (listeners != null ? listeners.size() : 0);
    // for (int i = 0; i < size; i++) {
    // listeners.get(i).onPaused(r.activity);
    // }

    return state;
  }

  void unscheduleGcIdler() {
    // if (mGcIdlerScheduled) {
    // mGcIdlerScheduled = false;
    // Looper.myQueue().removeIdleHandler(mGcIdler);
    // }
    // mH.removeMessages(H.GC_WHEN_IDLE);
    // TODO STUB\
  }

  void ensureJitEnabled() {
    // if (!mJitEnabled) {
    // mJitEnabled = true;
    // dalvik.system.VMRuntime.getRuntime().startJitCompilation();
    // }
    // TODO STUB
  }

  public final LoadedApk getPackageInfoNoCheck(ApplicationInfo aInfo, CompatibilityInfo compatInfo) {
    // return getPackageInfo(ai, compatInfo, null, false, true);
    // TODO STUB
    // if(mPackage == null)
    // mPackage = new LoadedApk(this, aInfo, compatInfo, mainThread, baseLoader, securityViolation,
    // includeCode)
    return mPackage;
  }

  public Instrumentation getInstrumentation() {
    return mInstrumentation;
  }

  public ApplicationThread getApplicationThread() {
    return mAppThread;
  }

  public Looper getLooper() {
    return mLooper;
  }

  public Application getApplication() {
    return mInitialApplication;
  }

  private void handleBindApplication() {// AppBindData data) {
    mInstrumentation = new Instrumentation();
    mInitialApplication = new Application();
    try {
      mInstrumentation.callApplicationOnCreate(mInitialApplication);
    } catch (Exception e) {
      if (!mInstrumentation.onException(mInitialApplication, e)) {
        throw new RuntimeException("Unable to create application " + mInitialApplication.getClass().getName()
            + ": " + e.toString(), e);
      }
    }

  }

  private void attach() {
    init0(); // startUp ActivityThread
    sThreadLocal.set(this); // so that this class can be reached anywhere in this thread

    handleBindApplication();

    WindowManager.init0(); // startup WindowManager
    ActivityManager.init(this.mAppThread); // startup ActvityManager
  }

  private native void init0();

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