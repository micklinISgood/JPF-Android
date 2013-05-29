//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA). All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3. The NOSA has been approved by the Open Source
// Initiative. See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package android.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Slog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.server.am.ActivityManagerService;

/**
 * This manages the execution of the main thread in an application process,
 * scheduling and executing activities, broadcasts, and other operations on it
 * as the activity manager requests. Adapted from Android 4.1. The purpose of
 * this class is to synchronize all execution on the main thread. For example
 * calling life-cycle methods of a service/ Activity. Especially Activities as
 * they interact with the GUI that is not thread-safe.
 * 
 * We simplified it to support the testing of a single application. This
 * includes the following simplifications:
 * 
 * 1. Package Information & Class loading
 * 
 * Instead of storing the list mPackages of package information, we only store
 * one LoadedApk representing the loaded Android APK package of the system under
 * test. As each loaded APK package has each own Application class, we only
 * store one Application class instead of the list mAllApplications. In other
 * words we do not support multiple applications running in the same process.
 * 
 * 2. ClassLoader - see LoadedApk
 * 
 * We use the default (current) classloader in Loaded APK as the SUT's classes
 * were already loaded by JPF
 * 
 * 3. Removed system classes
 * 
 * GCIdler Profiler BackupAgent BackupAgent CompatitblityInfo CloseGuard
 * CompatibilityInfo
 * 
 * 4. WindowManager
 * 
 * 
 * 5. ActivityThread for system application support
 * 
 * 
 * TODO:
 * 
 * - restart of activity due to config change - resource loading - receiver -
 * test application - refine resume activity
 * 
 * * @author Heila van der Merwe
 */
public final class ActivityThread {
  public static final String TAG = "ActivityThread";
  static final boolean localLOGV = false;
  static final boolean DEBUG_MESSAGES = true;
  /** @hide */
  public static final boolean DEBUG_BROADCAST = false;
  private static final boolean DEBUG_RESULTS = false;
  private static final boolean DEBUG_BACKUP = true;
  private static final boolean DEBUG_CONFIGURATION = false;
  private static final long MIN_TIME_BETWEEN_GCS = 5 * 1000;
  private static final Pattern PATTERN_SEMICOLON = Pattern.compile(";");
  private static final int SQLITE_MEM_RELEASED_EVENT_LOG_TAG = 75003;
  private static final int LOG_ON_PAUSE_CALLED = 30021;
  private static final int LOG_ON_RESUME_CALLED = 30022;

  final Looper mLooper = Looper.myLooper();
  final H mH = new H();

  // stores a map of all activities that have been started and not destroyed
  final HashMap<IBinder, ActivityClientRecord> mActivities = new HashMap<IBinder, ActivityClientRecord>();

  final HashMap<IBinder, Service> mServices = new HashMap<IBinder, Service>();
  AppBindData mBoundApplication;

  final ApplicationThread mAppThread = new ApplicationThread();
  Configuration mConfiguration;
  Configuration mCompatConfiguration;
  Configuration mResConfiguration;

  static final ThreadLocal<ActivityThread> sThreadLocal = new ThreadLocal<ActivityThread>();
  boolean mSystemThread = false;
  Application mInitialApplication;

  LoadedApk mPackage; // For testing we do not have to store multiple packages,
                      // only one as we are only
                      // testing a single application

  static ContextImpl mSystemContext = null;
  static PackageManager sPackageManager;

  Instrumentation mInstrumentation;
  static Handler sMainThreadHandler; // set once in main()

  static final class AppBindData {
    LoadedApk info;
    String processName;
    ApplicationInfo appInfo;
    List<ProviderInfo> providers; // TODO not used yet
    Configuration config;

    public String toString() {
      return "AppBindData{appInfo=" + appInfo + "}";
    }
  }

  static final class ContextCleanupInfo {
    ContextImpl context;
    String what;
    String who;
  }

  public class ApplicationThread extends Binder {

    public final void bindApplication(String processName, ApplicationInfo appInfo,
                                      List<ProviderInfo> providers, Configuration config,
                                      Map<String, IBinder> services, Bundle coreSettings) {

      // TODO if (services != null) {
      // // Setup the service cache in the ServiceManager
      // ServiceManager.initServiceCache(services);
      // }

      AppBindData data = new AppBindData();
      data.processName = processName;
      data.appInfo = appInfo;
      data.providers = providers;
      data.config = config;
      queueOrSendMessage(H.BIND_APPLICATION, data);
    }

    private void updatePendingConfiguration(Configuration config) {
      // synchronized (mPackages) {
      // if (mPendingConfiguration == null ||
      // mPendingConfiguration.isOtherSeqNewer(config)) {
      // mPendingConfiguration = config;
      // }
      // }
      // TODO
    }

    //////////////////////////////////////
    //          Activity Methods        //    
    //////////////////////////////////////

    /**
     * Called by the ActivityManagerService to schedule the launch of a new
     * instance of an Activity.
     * 
     * @param intent
     *          - the intent containing the starting activity component
     * @param token
     *          we use token to identify this activity without having to send
     *          the activity itself back to the activity manager. (matters more
     *          with ipc)
     * 
     * @param ident
     * @param info
     *          - activity info of this Activity as parsed from manifest
     * @param curConfig
     *          -
     * @param compatInfo
     * @param state
     *          - previous state that was stored
     * @param pendingResults
     *          - list of resultinfo objects that hav eto be delivered to this
     *          Actvity
     * @param pendingNewIntents
     * @param notResumed
     * @param isForward
     */
    public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident, ActivityInfo info,
                                             Configuration curConfig, CompatibilityInfo compatInfo,
                                             Bundle state, List<ResultInfo> pendingResults,
                                             List<Intent> pendingNewIntents, boolean notResumed,
                                             boolean isForward) {// , String
                                                                 // profileName,
      // ParcelFileDescriptor profileFd, boolean autoStopProfiler) {
      ActivityClientRecord r = new ActivityClientRecord();

      r.token = token;
      r.ident = ident;
      r.intent = intent;
      r.activityInfo = info;
      r.compatInfo = compatInfo;
      r.state = state;

      r.pendingResults = pendingResults;
      r.pendingIntents = pendingNewIntents;

      r.startsNotResumed = notResumed;
      r.isForward = isForward;

      // r.profileFile = profileName;
      // r.profileFd = profileFd;
      // r.autoStopProfiler = autoStopProfiler;

      updatePendingConfiguration(curConfig);

      queueOrSendMessage(H.LAUNCH_ACTIVITY, r);
    }

    public final void schedulePauseActivity(IBinder token, boolean finished, boolean userLeaving,
                                            int configChanges) {
      queueOrSendMessage(finished ? H.PAUSE_ACTIVITY_FINISHING : H.PAUSE_ACTIVITY, token, (userLeaving ? 1
          : 0), configChanges);
    }

    public final void scheduleStopActivity(IBinder token, boolean showWindow, int configChanges) {
      queueOrSendMessage(showWindow ? H.STOP_ACTIVITY_SHOW : H.STOP_ACTIVITY_HIDE, token, 0, configChanges);
    }

    // public final void scheduleRelaunchActivity(IBinder token,
    // List<ResultInfo> pendingResults,
    // List<Intent> pendingNewIntents, int configChanges,
    // boolean notResumed, Configuration config) {
    // // requestRelaunchActivity(token, pendingResults, pendingNewIntents,
    // configChanges, notResumed, config,
    // // true);
    // queueOrSendMessage(H.RELAUNCH_ACTIVITY, null, 0, 0);
    //
    // }

    public final void scheduleResumeActivity(IBinder token, boolean isForward) {
      queueOrSendMessage(H.RESUME_ACTIVITY, token, isForward ? 1 : 0);
    }

    public final void scheduleSendResult(IBinder token, List<ResultInfo> results) {
      ResultData res = new ResultData();
      res.token = token;
      res.results = results;
      queueOrSendMessage(H.SEND_RESULT, res);
    }

    public final void scheduleDestroyActivity(IBinder token, boolean finishing, int configChanges) {
      queueOrSendMessage(H.DESTROY_ACTIVITY, token, finishing ? 1 : 0, configChanges);
    }

    public final void scheduleWindowVisibility(IBinder token, boolean showWindow) {
      queueOrSendMessage(showWindow ? H.SHOW_WINDOW : H.HIDE_WINDOW, token);
    }

    //////////////////////////////////////
    //          Receiver Methods        //    
    //////////////////////////////////////

    /**
     * Schedules a receiver to be executes on the main looper.
     * 
     * @param intent
     * @param info
     * @param compatInfo
     * @param resultCode
     * @param data
     * @param extras
     * @param sync
     */
    public final void scheduleReceiver(Intent intent, ActivityInfo info, CompatibilityInfo compatInfo,
                                       int resultCode, String data, Bundle extras, boolean sync) {
      ReceiverData r = new ReceiverData(intent, resultCode, data, extras, sync, false, mAppThread);// TODO
                                                                                                   // .asBinder());
      r.info = info;
      r.compatInfo = compatInfo;
      queueOrSendMessage(H.RECEIVER, r);
    }

    // This function exists to make sure all receiver dispatching is
    // correctly ordered, since these are one-way calls and the binder driver
    // applies transaction ordering per object for such calls.
    public void scheduleRegisteredReceiver(IIntentReceiver receiver, Intent intent, int resultCode,
                                           String dataStr, Bundle extras, boolean ordered, boolean sticky) {
      receiver.performReceive(intent, resultCode, dataStr, extras, ordered, sticky);
    }

    //////////////////////////////////////
    //          Service Methods         //    
    //////////////////////////////////////

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

    //////////////////////////////////////
    //          Other Methods           //    
    //////////////////////////////////////

    public void scheduleLowMemory() {
      queueOrSendMessage(H.LOW_MEMORY, null);
    }

    public void scheduleActivityConfigurationChanged(IBinder token) {
      queueOrSendMessage(H.ACTIVITY_CONFIGURATION_CHANGED, token);
    }

    public final void scheduleNewIntent(List<Intent> intents, IBinder token) {
      // NewIntentData data = new NewIntentData();
      // data.intents = intents;
      // data.token = token;
      //
      // queueOrSendMessage(H.NEW_INTENT, data);
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
    String embeddedID;
    Activity.NonConfigurationInstances lastNonConfigurationInstances;
    boolean paused;
    boolean stopped;
    String name;
    boolean hideForNow;
    Configuration newConfig;
    Configuration createdConfig;

    // ActivityClientRecord nextIdle;

    // String profileFile;
    // ParcelFileDescriptor profileFd;
    // boolean autoStopProfiler;

    ActivityInfo activityInfo; // parsed infor from android manifest
    CompatibilityInfo compatInfo;
    LoadedApk packageInfo;

    List<ResultInfo> pendingResults;
    List<Intent> pendingIntents;

    boolean startsNotResumed;

    boolean isForward;

    // int pendingConfigChanges;
    // boolean onlyLocalRequest;

    // View mPendingRemoveWindow;
    // WindowManager mPendingRemoveWindowManager;

    ActivityClientRecord() {
      parent = null;
      embeddedID = null;
      paused = false;
      stopped = false;
      hideForNow = false;
      // nextIdle = null;
    }

    public String toString() {
      ComponentName componentName = intent.getComponent();
      return "ActivityRecord{" + (activity == null ? "no component name" : componentName.getClassName())
          + "}";
    }

    public String getName() {
      return name;
    }
  }

  static final class ResultData {
    IBinder token;
    List<ResultInfo> results;

    public String toString() {
      return "ResultData{token=" + token + " results" + results + "}";
    }
  }

  private static class StopInfo {
    // Bitmap thumbnail;
    CharSequence description;
  }

  static final class ReceiverData extends BroadcastReceiver.PendingResult {
    public ReceiverData(Intent intent, int resultCode, String resultData, Bundle resultExtras,
        boolean ordered, boolean sticky, IBinder token) {
      super(resultCode, resultData, resultExtras, TYPE_COMPONENT, ordered, sticky, token);
      this.intent = intent;
    }

    Intent intent;
    ActivityInfo info;
    CompatibilityInfo compatInfo;

    public String toString() {
      return "ReceiverData{intent=" + intent + " packageName=" + info.packageName + " resultCode="
          + getResultCode() + " resultData=" + getResultData() + " resultExtras=" + getResultExtras(false)
          + "}";
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
    public static final int BIND_APPLICATION = 110;
    public static final int NEW_INTENT = 112;
    public static final int RECEIVER = 113;
    public static final int CREATE_SERVICE = 114;
    public static final int SERVICE_ARGS = 115;
    public static final int STOP_SERVICE = 116;
    public static final int CLEAN_UP_CONTEXT = 119;
    public static final int BIND_SERVICE = 121;
    public static final int UNBIND_SERVICE = 122;
    public static final int LOW_MEMORY = 124;
    public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
    public static final int RELAUNCH_ACTIVITY = 126;

    String codeToString(int code) {
      if (DEBUG_MESSAGES) {
        switch (code) {
        case LAUNCH_ACTIVITY:
          return "LAUNCH_ACTIVITY";
        case PAUSE_ACTIVITY:
          return "PAUSE_ACTIVITY";
        case PAUSE_ACTIVITY_FINISHING:
          return "PAUSE_ACTIVITY_FINISHING";
        case STOP_ACTIVITY_SHOW:
          return "STOP_ACTIVITY_SHOW";
        case STOP_ACTIVITY_HIDE:
          return "STOP_ACTIVITY_HIDE";
        case SHOW_WINDOW:
          return "SHOW_WINDOW";
        case HIDE_WINDOW:
          return "HIDE_WINDOW";
        case RESUME_ACTIVITY:
          return "RESUME_ACTIVITY";
        case SEND_RESULT:
          return "SEND_RESULT";
        case DESTROY_ACTIVITY:
          return "DESTROY_ACTIVITY";
        case BIND_APPLICATION:
          return "BIND_APPLICATION";
          // case EXIT_APPLICATION: return "EXIT_APPLICATION";
        case NEW_INTENT:
          return "NEW_INTENT";
        case RECEIVER:
          return "RECEIVER";
        case CREATE_SERVICE:
          return "CREATE_SERVICE";
        case SERVICE_ARGS:
          return "SERVICE_ARGS";
        case STOP_SERVICE:
          return "STOP_SERVICE";
          // case REQUEST_THUMBNAIL: return "REQUEST_THUMBNAIL";
          // case CONFIGURATION_CHANGED: return "CONFIGURATION_CHANGED";
        case CLEAN_UP_CONTEXT:
          return "CLEAN_UP_CONTEXT";
          // case GC_WHEN_IDLE: return "GC_WHEN_IDLE";
        case BIND_SERVICE:
          return "BIND_SERVICE";
        case UNBIND_SERVICE:
          return "UNBIND_SERVICE";
          // case DUMP_SERVICE: return "DUMP_SERVICE";
        case LOW_MEMORY:
          return "LOW_MEMORY";
        case ACTIVITY_CONFIGURATION_CHANGED:
          return "ACTIVITY_CONFIGURATION_CHANGED";
        case RELAUNCH_ACTIVITY:
          return "RELAUNCH_ACTIVITY";
          // case PROFILER_CONTROL: return "PROFILER_CONTROL";
          // case CREATE_BACKUP_AGENT: return "CREATE_BACKUP_AGENT";
          // case DESTROY_BACKUP_AGENT: return "DESTROY_BACKUP_AGENT";
          // case SUICIDE: return "SUICIDE";
          // case REMOVE_PROVIDER: return "REMOVE_PROVIDER";
          // case ENABLE_JIT: return "ENABLE_JIT";
          // case DISPATCH_PACKAGE_BROADCAST: return
          // "DISPATCH_PACKAGE_BROADCAST";
          // case SCHEDULE_CRASH: return "SCHEDULE_CRASH";
          // case DUMP_HEAP: return "DUMP_HEAP";
          // case DUMP_ACTIVITY: return "DUMP_ACTIVITY";
          // case SLEEPING: return "SLEEPING";
          // case SET_CORE_SETTINGS: return "SET_CORE_SETTINGS";
          // case UPDATE_PACKAGE_COMPATIBILITY_INFO: return
          // "UPDATE_PACKAGE_COMPATIBILITY_INFO";
          // case TRIM_MEMORY: return "TRIM_MEMORY";
        }
      }
      return "(unknown)";
    }

    public void handleMessage(Message msg) {
      Log.i(TAG, "Processing message " + codeToString(msg.what));

      switch (msg.what) {
      case LAUNCH_ACTIVITY: {
        ActivityClientRecord r = (ActivityClientRecord) msg.obj;
        r.packageInfo = getPackageInfoNoCheck(r.activityInfo.applicationInfo);
        handleLaunchActivity(r, null);
      }
        break;
      // case RELAUNCH_ACTIVITY: {
      // ActivityClientRecord r = (ActivityClientRecord) msg.obj;
      // handleRelaunchActivity(r);
      // }
      // break;
      case PAUSE_ACTIVITY:
        handlePauseActivity((IBinder) msg.obj, false, msg.arg1 != 0, msg.arg2);
        maybeSnapshot();
        break;
      case PAUSE_ACTIVITY_FINISHING:
        handlePauseActivity((IBinder) msg.obj, true, msg.arg1 != 0, msg.arg2);
        break;
      case STOP_ACTIVITY_SHOW:
        handleStopActivity((IBinder) msg.obj, true, msg.arg2);
        break;
      case STOP_ACTIVITY_HIDE:
        handleStopActivity((IBinder) msg.obj, false, msg.arg2);
        break;
      case SHOW_WINDOW:
        handleWindowVisibility((IBinder) msg.obj, true);
        break;
      case HIDE_WINDOW:
        handleWindowVisibility((IBinder) msg.obj, false);
        break;
      case RESUME_ACTIVITY:
        handleResumeActivity((IBinder) msg.obj, true, msg.arg1 != 0);
        break;
      case SEND_RESULT:
        handleSendResult((ResultData) msg.obj);
        break;
      case DESTROY_ACTIVITY:
        handleDestroyActivity((IBinder) msg.obj, msg.arg1 != 0, msg.arg2, false);
        break;
      case BIND_APPLICATION:
        AppBindData data = (AppBindData) msg.obj;
        handleBindApplication(data);
        break;
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
        maybeSnapshot();
        break;
      case CLEAN_UP_CONTEXT:
        ContextCleanupInfo cci = (ContextCleanupInfo) msg.obj;
        cci.context.performFinalCleanup(cci.who, cci.what);
        break;
      case RECEIVER:
        handleReceiver((ReceiverData) msg.obj);
        maybeSnapshot();
        break;
      case LOW_MEMORY:
        //  handleLowMemory();
        break;
      }

    }

    private void maybeSnapshot() {
      // not modelled
    }
  }

  public static ActivityThread currentActivityThread() {
    return sThreadLocal.get();
  }

  public static String currentPackageName() {
    ActivityThread am = currentActivityThread();
    return (am != null && am.mBoundApplication != null) ? am.mBoundApplication.processName : null;
  }

  public static Application currentApplication() {
    ActivityThread am = currentActivityThread();
    return am != null ? am.mInitialApplication : null;
  }

  public static PackageManager getPackageManager() {
    if (sPackageManager == null) {
      // sanity check
      throw new RuntimeException("sPackageManager must have been set already");
    }
    return sPackageManager;
  }

  static Configuration applyConfigCompat(Configuration config, CompatibilityInfo compat) {
    if (config == null) {
      return null;
    }
    if (compat != null && !compat.supportsScreen()) {
      config = new Configuration(config);
      compat.applyToConfiguration(config);
    }
    return config;
  }

  private Configuration mMainThreadConfig = new Configuration();

  Configuration applyConfigCompatMainThread(Configuration config, CompatibilityInfo compat) {
    if (config == null) {
      return null;
    }
    if (compat != null && !compat.supportsScreen()) {
      mMainThreadConfig.setTo(config);
      config = mMainThreadConfig;
      compat.applyToConfiguration(config);
    }
    return config;
  }

  final Handler getHandler() {
    return mH;
  }

  /**
   * Simplified to only support one package: mPackage instead of looking up the
   * package in mPackages.
   * 
   * @param packageName
   * @param flags
   * @return
   */
  public final LoadedApk getPackageInfo(String packageName) {

    if (mPackage != null) {
      // sanity check
      if (!packageName.equals(mPackage.mPackageName))
        throw new RuntimeException("This should not happen: trying to get LoadedApk for " + packageName
            + "when default LoadedApk is for " + mPackage.mPackageName);
      return mPackage;
    }

    ApplicationInfo ai = null;
    ai = getPackageManager().getApplicationInfo(packageName);

    if (ai != null) {
      return getPackageInfo(ai);
    }

    return null;
  }

  public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai) {
    return getPackageInfo(ai);
  }

  public final LoadedApk peekPackageInfo(String packageName) {
    return mPackage;

  }

  private LoadedApk getPackageInfo(ApplicationInfo aInfo) {
    LoadedApk packageInfo = mPackage;

    if (packageInfo == null) {
      if (localLOGV)
        Slog.v(TAG, "Loading code package " + aInfo.packageName + " (in "
            + (mBoundApplication != null ? mBoundApplication.processName : null) + ")");
      packageInfo = new LoadedApk(this, aInfo, this);
      mPackage = packageInfo;
    }
    return packageInfo;
  }

  public final void applyConfigurationToResources(Configuration config) {
    applyConfigurationToResourcesLocked(config, null);
  }

  final boolean applyConfigurationToResourcesLocked(Configuration config, CompatibilityInfo compat) {
    int changes = 0;
    // TODO NB
    if (mResConfiguration == null) {
      mResConfiguration = new Configuration();
    }
    // if (!mResConfiguration.isOtherSeqNewer(config) && compat == null) {
    // if (DEBUG_CONFIGURATION) Slog.v(TAG, "Skipping new config: curSeq="
    // + mResConfiguration.seq + ", newSeq=" + config.seq);
    // return false;
    // }
    // int changes = mResConfiguration.updateFrom(config);
    // DisplayMetrics dm = getDisplayMetricsLocked(null, true);
    //
    // if (compat != null && (mResCompatibilityInfo == null ||
    // !mResCompatibilityInfo.equals(compat))) {
    // mResCompatibilityInfo = compat;
    // changes |= ActivityInfo.CONFIG_SCREEN_LAYOUT
    // | ActivityInfo.CONFIG_SCREEN_SIZE
    // | ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE;
    // }
    //
    // // set it for java, this also affects newly created Resources
    // if (config.locale != null) {
    // Locale.setDefault(config.locale);
    // }
    //
    // Resources.updateSystemConfiguration(config, dm, compat);
    //
    // ApplicationPackageManager.configurationChanged();
    // //Slog.i(TAG, "Configuration changed in " + currentPackageName());
    //
    // Iterator<WeakReference<Resources>> it =
    // mActiveResources.values().iterator();
    // //Iterator<Map.Entry<String, WeakReference<Resources>>> it =
    // // mActiveResources.entrySet().iterator();
    // while (it.hasNext()) {
    // WeakReference<Resources> v = it.next();
    // Resources r = v.get();
    // if (r != null) {
    // if (DEBUG_CONFIGURATION) Slog.v(TAG, "Changing resources "
    // + r + " config to: " + config);
    // r.updateConfiguration(config, dm, compat);
    // //Slog.i(TAG, "Updated app resources " + v.getKey()
    // // + " " + r + ": " + r.getConfiguration());
    // } else {
    // //Slog.i(TAG, "Removing old resources " + v.getKey());
    // it.remove();
    // }
    // }
    //
    return changes != 0;
  }

  public ApplicationThread getApplicationThread() {
    return mAppThread;
  }

  public Instrumentation getInstrumentation() {
    return mInstrumentation;
  }

  public Configuration getConfiguration() {
    return mResConfiguration;
  }

  public boolean isProfiling() {
    return false;
  }

  public String getProfileFilePath() {
    return "";
  }

  public Looper getLooper() {
    return mLooper;
  }

  public Application getApplication() {
    return mInitialApplication;
  }

  public String getProcessName() {
    return mBoundApplication.processName;
  }

  public ContextImpl getSystemContext() {
    if (mSystemContext == null) {
      throw new RuntimeException("Context sould have been set in LoadedApk.");
    }
    return mSystemContext;
  }

  /* ****************** Broadcast Receiver *************** */

  private void handleReceiver(ReceiverData data) {
    // If we are getting ready to gc after going to the background, well
    // we are back active so skip it.
    unscheduleGcIdler();

    String component = data.intent.getComponent().getClassName();

    LoadedApk packageInfo = getPackageInfoNoCheck(data.info.applicationInfo);

    IActivityManager mgr = ActivityManagerNative.getDefault();

    BroadcastReceiver receiver;
    try {
      java.lang.ClassLoader cl = packageInfo.getClassLoader();
      data.intent.setExtrasClassLoader(cl);
      data.setExtrasClassLoader(cl);
      receiver = (BroadcastReceiver) cl.loadClass(component).newInstance();
    } catch (Exception e) {
      if (DEBUG_BROADCAST)
        Slog.i(TAG, "Finishing failed broadcast to " + data.intent.getComponent());
      data.sendFinished(mgr);
      throw new RuntimeException("Unable to instantiate receiver " + component + ": " + e.toString(), e);
    }

    try {
      Application app = packageInfo.makeApplication(false, mInstrumentation);

      if (localLOGV)
        Slog.v(TAG,
            "Performing receive of " + data.intent + ": app=" + app + ", appName=" + app.getPackageName()
                + ", pkg=" + packageInfo.getPackageName() + ", comp="
                + data.intent.getComponent().toShortString());

      ContextImpl context = (ContextImpl) app.getBaseContext();
      receiver.setPendingResult(data);
      Log.i("BroadcastReceiver", receiver.getClass().getName() + ".onReceive with " + data.intent);
      receiver.onReceive(context.getReceiverRestrictedContext(), data.intent);
    } catch (Exception e) {
      if (DEBUG_BROADCAST)
        Slog.i(TAG, "Finishing failed broadcast to " + data.intent.getComponent());
      data.sendFinished(mgr);

      if (!mInstrumentation.onException(receiver, e)) {
        throw new RuntimeException("Unable to start receiver " + component + ": " + e.toString(), e);
      }
    }
    // the pending result can only by null if goAsync is called in which case
    // the pending intent will be finished by the Async call
    if (receiver.getPendingResult() != null) {
      data.finish();
    }

  }

  /* ************************** Service ************************************* */

  private void handleCreateService(CreateServiceData data) {
    // If we are getting ready to gc after going to the background, well
    // we are back active so skip it.
    unscheduleGcIdler();

    LoadedApk packageInfo = getPackageInfoNoCheck(data.info.applicationInfo);
    Service service = null;
    try {
      java.lang.ClassLoader cl = packageInfo.getClassLoader();
      service = (Service) cl.loadClass(data.info.packageName + data.info.name).newInstance();
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
    Slog.i(TAG, "Running services: " + mServices);
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

  /*
   * ****************************** Activity Methods
   * ***********************************
   */
  private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
    // System.out.println("##### [" + System.currentTimeMillis() +
    // "] ActivityThread.performLaunchActivity(" +
    // r + ")");

    ActivityInfo aInfo = r.activityInfo;
    if (r.packageInfo == null) {
      r.packageInfo = getPackageInfo(aInfo.applicationInfo);
    }

    ComponentName component = r.intent.getComponent();
    if (component == null) {
      component = r.intent.resolveActivity(mInitialApplication.getPackageManager());
      r.intent.setComponent(component);
    }

    if (r.activityInfo.targetActivity != null) {
      component = new ComponentName(r.activityInfo.packageName, r.activityInfo.targetActivity);
    }

    Activity activity = null;
    try {
      java.lang.ClassLoader cl = r.packageInfo.getClassLoader();
      activity = mInstrumentation.newActivity(cl, component.getClassName(), r.intent);
      StrictMode.incrementExpectedActivityCount(activity.getClass());
      r.intent.setExtrasClassLoader(cl);
      if (r.state != null) {
        r.state.setClassLoader(cl);
      }
    } catch (Exception e) {
      if (!mInstrumentation.onException(activity, e)) {
        throw new RuntimeException("Unable to instantiate activity " + component + ": " + e.toString(), e);
      }
    }

    try {
      Application app = r.packageInfo.makeApplication(false, mInstrumentation);

      if (localLOGV)
        Slog.v(TAG, "Performing launch of " + r);
      // if (localLOGV)
      // Slog.v(
      // TAG,
      // r + ": app=" + app + ", appName=" + app.getPackageName() + ", pkg="
      // + r.packageInfo.getPackageName() + ", comp=" +
      // r.intent.getComponent().toShortString()
      // + ", dir=" + r.packageInfo.getAppDir());

      if (activity != null) {
        ContextImpl appContext = new ContextImpl();
        appContext.init(r.packageInfo, r.token, this);
        appContext.setOuterContext(activity);
        CharSequence title = r.activityInfo.loadLabel(appContext.getPackageManager());
        Configuration config = new Configuration(mCompatConfiguration);
        if (DEBUG_CONFIGURATION)
          Log.i(TAG, "Launching " + r.activityInfo.name + " with config " + config);
        activity.attach(appContext, this, getInstrumentation(), r.token, r.ident, app, r.intent,
            r.activityInfo, title, r.parent, r.embeddedID, r.lastNonConfigurationInstances, config);

        if (customIntent != null) {
          activity.mIntent = customIntent;
        }
        r.lastNonConfigurationInstances = null;
        activity.mStartedActivity = false;
        int theme = r.activityInfo.getThemeResource();
        if (theme != 0) {
          activity.setTheme(theme);
        }

        activity.mCalled = false;
        mInstrumentation.callActivityOnCreate(activity, r.state);
        if (!activity.mCalled) {
          throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString()
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
            throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString()
                + " did not call through to super.onPostCreate()");
          }
        }
      }
      r.paused = true;

      mActivities.put(r.token, r);

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
    // If we are getting ready to gc after going to the background, well
    // we are back active so skip it.
    unscheduleGcIdler();

    // if (r.profileFd != null) {
    // mProfiler.setProfiler(r.profileFile, r.profileFd);
    // mProfiler.startProfiling();
    // mProfiler.autoStopProfiler = r.autoStopProfiler;
    // }

    // Make sure we are running with the most recent config.
    handleConfigurationChanged(null, null);

    if (localLOGV)
      Slog.v(TAG, "Handling launch of " + r);
    Activity a = performLaunchActivity(r, customIntent);

    if (a != null) {
      r.createdConfig = new Configuration(mConfiguration);
      Bundle oldState = r.state;
      handleResumeActivity(r.token, false, r.isForward);

      if (!r.activity.mFinished && r.startsNotResumed) {
        // The activity manager actually wants this one to start out
        // paused, because it needs to be visible but isn't in the
        // foreground. We accomplish this by going through the
        // normal startup (because activities expect to go through
        // onResume() the first time they run, before their window
        // is displayed), and then pausing it. However, in this case
        // we do -not- need to do the full pause cycle (of freezing
        // and such) because the activity manager assumes it can just
        // retain the current state it has.
        try {
          r.activity.mCalled = false;
          mInstrumentation.callActivityOnPause(r.activity);
          // We need to keep around the original state, in case
          // we need to be created again.
          r.state = oldState;
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
      }
    } else {
      // If there was an error, for any reason, tell the activity
      // manager to stop us.
      ActivityManagerNative.getDefault().finishActivity(r.token, Activity.RESULT_CANCELED, null);
    }
  }

  public final ActivityClientRecord performResumeActivity(IBinder token, boolean clearHide) {
    ActivityClientRecord r = mActivities.get(token);
    if (localLOGV)
      Slog.v(TAG, "Performing resume of " + r + " finished=" + r.activity.mFinished);
    if (r != null && !r.activity.mFinished) {
      if (clearHide) {
        r.hideForNow = false;
        r.activity.mStartedActivity = false;
      }
      try {
        // if (r.pendingIntents != null) {
        // deliverNewIntents(r, r.pendingIntents);
        // r.pendingIntents = null;
        // }TODO
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
      } catch (Exception e) {
        if (!mInstrumentation.onException(r.activity, e)) {
          throw new RuntimeException("Unable to resume activity " + r.intent.getComponent().toShortString()
              + ": " + e.toString(), e);
        }
      }
    }
    return r;
  }

  private void deliverResults(ActivityClientRecord r, List<ResultInfo> results) {
    final int N = results.size();
    for (int i = 0; i < N; i++) {
      ResultInfo ri = results.get(i);
      try {
        if (ri.mData != null) {
          ri.mData.setExtrasClassLoader(r.activity.getClassLoader());
        }
        if (DEBUG_RESULTS)
          Slog.v(TAG, "Delivering result to activity " + r + " : " + ri);
        r.activity.dispatchActivityResult(ri.mResultWho, ri.mRequestCode, ri.mResultCode, ri.mData);
      } catch (Exception e) {
        if (!mInstrumentation.onException(r.activity, e)) {
          throw new RuntimeException("Failure delivering result " + ri + " to activity "
              + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
        }
      }
    }
  }

  private final void handleResumeActivity(IBinder token, boolean clearHide, boolean isForward) {
    // If we are getting ready to gc after going to the background, well
    // we are back active so skip it.
    unscheduleGcIdler();

    ActivityClientRecord r = performResumeActivity(token, clearHide);

    if (r != null) {
      final Activity a = r.activity;

      if (localLOGV)
        Slog.v(TAG, "Resume " + r + " started activity: " + a.mStartedActivity + ", hideForNow: "
            + r.hideForNow + ", finished: " + a.mFinished);

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
      // r.window = r.activity.getWindow();
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
      // if (!r.activity.mFinished && willBeVisible && r.activity.mDecor != null
      // && !r.hideForNow) {
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

    } else {
      // If an exception was thrown when trying to resume, then
      // just end this activity.
      ActivityManagerNative.getDefault().finishActivity(token, Activity.RESULT_CANCELED, null);
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

  /**
   * Core implementation of stopping an activity. Note this is a little tricky
   * because the server's meaning of stop is slightly different than our client
   * -- for the server, stop means to save state and give it the result when it
   * is done, but the window may still be visible. For the client, we want to
   * call onStop()/onStart() to indicate when the activity's UI visibillity
   * changes.
   */
  private void performStopActivityInner(ActivityClientRecord r, StopInfo info, boolean keepShown,
                                        boolean saveState) {
    if (localLOGV)
      Slog.v(TAG, "Performing stop of " + r);
    Bundle state = null;
    if (r != null) {
      if (!keepShown && r.stopped) {
        if (r.activity.mFinished) {
          // If we are finishing, we won't call onResume() in certain
          // cases. So here we likewise don't want to call onStop()
          // if the activity isn't resumed.
          return;
        }
        RuntimeException e = new RuntimeException("Performing stop of activity that is not resumed: "
            + r.intent.getComponent().toShortString());
        Slog.e(TAG, e.getMessage(), e);
      }

      if (info != null) {
        try {
          // First create a thumbnail for the activity...
          // For now, don't create the thumbnail here; we are
          // doing that by doing a screen snapshot.
          // info.thumbnail = null; // createThumbnailBitmap(r);
          info.description = r.activity.onCreateDescription();
        } catch (Exception e) {
          if (!mInstrumentation.onException(r.activity, e)) {
            throw new RuntimeException("Unable to save state of activity "
                + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
          }
        }
      }

      // Next have the activity save its current state and managed dialogs...
      if (!r.activity.mFinished && saveState) {
        if (r.state == null) {
          state = new Bundle();
          state.setAllowFds(false);
          mInstrumentation.callActivityOnSaveInstanceState(r.activity, state);
          r.state = state;
        } else {
          state = r.state;
        }
      }

      if (!keepShown) {
        try {
          // Now we are idle.
          r.activity.performStop();
        } catch (Exception e) {
          if (!mInstrumentation.onException(r.activity, e)) {
            throw new RuntimeException("Unable to stop activity " + r.intent.getComponent().toShortString()
                + ": " + e.toString(), e);
          }
        }
        r.stopped = true;
      }

      r.paused = true;
    }
  }

  private void updateVisibility(ActivityClientRecord r, boolean show) {
    // View v = r.activity.mDecor;
    // if (v != null) {
    if (show) {
      // if (!r.activity.mVisibleFromServer) {
      // r.activity.mVisibleFromServer = true;
      // mNumVisibleActivities++;
      // if (r.activity.mVisibleFromClient) {
      r.activity.makeVisible();
      // }
      // }
      // if (r.newConfig != null) {
      // if (DEBUG_CONFIGURATION) Slog.v(TAG, "Updating activity vis "
      // + r.activityInfo.name + " with new config " + r.newConfig);
      // performConfigurationChanged(r.activity, r.newConfig);
      // r.newConfig = null;
      // }
    } else {
      // if (r.activity.mVisibleFromServer) {
      // r.activity.mVisibleFromServer = false;
      // mNumVisibleActivities--;
      r.activity.mWindow.setVisibility(View.INVISIBLE);
      // }
      // }
    }
  }

  private void handleStopActivity(IBinder token, boolean show, int configChanges) {
    ActivityClientRecord r = mActivities.get(token);
    r.activity.mConfigChangeFlags |= configChanges;

    StopInfo info = new StopInfo();
    performStopActivityInner(r, info, show, true);

    if (localLOGV)
      Slog.v(TAG, "Finishing stop of " + r + ": show=" + show + " win=" + r.window);

    updateVisibility(r, show);

    // Make sure any pending writes are now committed.
    // if (!r.isPreHoneycomb()) {
    // QueuedWork.waitToFinish();
    // }

    // Tell activity manager we have been stopped.
    // try {
    ActivityManagerNative.getDefault().activityStopped(r.token, r.state, null, info.description);
    // } catch (RemoteException ex) {
    // }
  }

  final void performRestartActivity(IBinder token) {
    ActivityClientRecord r = mActivities.get(token);
    if (r.stopped) {
      r.activity.performRestart();
      r.stopped = false;
    }
  }

  private void handleWindowVisibility(IBinder token, boolean show) {
    ActivityClientRecord r = mActivities.get(token);

    if (r == null) {
      Log.w(TAG, "handleWindowVisibility: no activity for token " + token);
      return;
    }

    if (!show && !r.stopped) {
      performStopActivityInner(r, null, show, false);
    } else if (show && r.stopped) {
      // If we are getting ready to gc after going to the background, well
      // we are back active so skip it.
      unscheduleGcIdler();

      r.activity.performRestart();
      r.stopped = false;
    }
    if (r.activity.mDecor != null) {
      if (false)
        Slog.v(TAG, "Handle window " + r + " visibility: " + show);
      updateVisibility(r, show);
    }
  }

  private void handleDestroyActivity(IBinder token, boolean finishing, int configChanges,
                                     boolean getNonConfigInstance) {
    ActivityClientRecord r = performDestroyActivity(token, finishing, configChanges, getNonConfigInstance);
    if (r != null) {
      // cleanUpPendingRemoveWindows(r);
      WindowManager wm = r.activity.getWindowManager();
      // TODO destroy view

      // View v = r.activity.mDecor;
      // if (v != null) {
      // if (r.activity.mVisibleFromServer) {
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
    // Context c = r.activity.getBaseContext();
    // / if (c instanceof ContextImpl) {
    // ((ContextImpl) c).scheduleFinalCleanup(r.activity.getClass().getName(),
    // "Activity");
    // }
    // }
    if (finishing) {
      ActivityManagerNative.getDefault().activityDestroyed(token);
    }
  }

  private ActivityClientRecord performDestroyActivity(IBinder token, boolean finishing, int configChanges,
                                                      boolean getNonConfigInstance) {
    ActivityClientRecord r = mActivities.get(token);
    Class activityClass = null;
    if (localLOGV)
      Slog.v(TAG, "Performing finish of " + r);
    if (r != null) {
      activityClass = r.activity.getClass();
      // r.activity.mConfigChangeFlags |= configChanges;
      if (finishing) {
        r.activity.mFinished = true;
      }
      if (!r.paused) {
        try {
          r.activity.mCalled = false;
          mInstrumentation.callActivityOnPause(r.activity);
          // EventLog.writeEvent(LOG_ON_PAUSE_CALLED,
          // r.activity.getComponentName().getClassName());
          if (!r.activity.mCalled) {
            throw new SuperNotCalledException("Activity " + r.intent.getComponent().getClassName()
                + " did not call through to super.onPause()");
          }
        } catch (SuperNotCalledException e) {
          throw e;
        } catch (Exception e) {
          if (!mInstrumentation.onException(r.activity, e)) {
            throw new RuntimeException("Unable to pause activity " + r.intent.getComponent().getClassName()
                + ": " + e.toString(), e);
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
            throw new RuntimeException("Unable to stop activity " + r.intent.getComponent().getClassName()
                + ": " + e.toString(), e);
          }
        }
        r.stopped = true;
      }
      if (getNonConfigInstance) {
        try {
          r.lastNonConfigurationInstances = r.activity.retainNonConfigurationInstances();
        } catch (Exception e) {
          if (!mInstrumentation.onException(r.activity, e)) {
            throw new RuntimeException("Unable to retain activity " + r.intent.getComponent().toShortString()
                + ": " + e.toString(), e);
          }
        }
      }
      try {
        r.activity.mCalled = false;
        mInstrumentation.callActivityOnDestroy(r.activity);
        if (!r.activity.mCalled) {
          throw new SuperNotCalledException("Activity " + r.intent.getComponent().getClassName()
              + " did not call through to super.onDestroy()");
        }
        // if (r.window != null) {
        // TODO r.window.closeAllPanels();
        // }
      } catch (SuperNotCalledException e) {
        throw e;
      } catch (Exception e) {
        if (!mInstrumentation.onException(r.activity, e)) {
          throw new RuntimeException("Unable to destroy activity " + r.intent.getComponent().getClassName()
              + ": " + e.toString(), e);
        }
      }
    }
    mActivities.remove(token);
    // StrictMode.decrementExpectedActivityCount(activityClass);
    return r;
  }

  private void handlePauseActivity(IBinder token, boolean finished, boolean userLeaving, int configChanges) {
    ActivityClientRecord r = mActivities.get(token);
    if (r != null) {
      // Slog.v(TAG, "userLeaving=" + userLeaving + " handling pause of " + r);
      if (userLeaving) {
        performUserLeavingActivity(r);
      }

      r.activity.mConfigChangeFlags |= configChanges;
      performPauseActivity(token, finished, false);

      // Make sure any pending writes are now committed.
      // if (r.isPreHoneycomb()) {
      // QueuedWork.waitToFinish();
      // }

      // Tell the activity manager we have paused.
      // try {
      ActivityManagerNative.getDefault().activityPaused(token);
      // } catch (RemoteException ex) {
      // }
    }
  }

  final void performUserLeavingActivity(ActivityClientRecord r) {
    mInstrumentation.callActivityOnUserLeaving(r.activity);
  }

  final Bundle performPauseActivity(IBinder token, boolean finished, boolean saveState) {
    ActivityClientRecord r = mActivities.get(token);
    return r != null ? performPauseActivity(r, finished, saveState) : null;
  }

  final Bundle performPauseActivity(ActivityClientRecord r, boolean finished, boolean saveState) {
    if (r.paused) {
      if (r.activity.mFinished) {
        // If we are finishing, we won't call onResume() in certain cases.
        // So here we likewise don't want to call onPause() if the activity
        // isn't resumed.
        return null;
      }
      RuntimeException e = new RuntimeException("Performing pause of activity that is not resumed: "
          + r.intent.getComponent().toShortString());
      Slog.e(TAG, e.getMessage(), e);
    }
    Bundle state = null;
    if (finished) {
      r.activity.mFinished = true;
    }
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
      // EventLog.writeEvent(LOG_ON_PAUSE_CALLED,
      // r.activity.getComponentName().getClassName());
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
    // TODO ArrayList<OnActivityPausedListener> listeners;
    // synchronized (mOnPauseListeners) {
    // listeners = mOnPauseListeners.remove(r.activity);
    // }
    // int size = (listeners != null ? listeners.size() : 0);
    // for (int i = 0; i < size; i++) {
    // listeners.get(i).onPaused(r.activity);
    // }

    return state;
  }

  private void handleSendResult(ResultData res) {
    ActivityClientRecord r = mActivities.get(res.token);
    if (DEBUG_RESULTS)
      Slog.v(TAG, "Handling send result to " + r);
    if (r != null) {
      final boolean resumed = !r.paused;
      if (!r.activity.mFinished && r.activity.mDecor != null && r.hideForNow && resumed) {
        // We had hidden the activity because it started another
        // one... we have gotten a result back and we are not
        // paused, so make sure our window is visible.
        updateVisibility(r, true);
      }
      if (resumed) {
        try {
          // Now we are idle.
          r.activity.mCalled = false;
          r.activity.mTemporaryPause = true;
          mInstrumentation.callActivityOnPause(r.activity);
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
      }
      deliverResults(r, res.results);
      if (resumed) {
        r.activity.performResume();
        r.activity.mTemporaryPause = false;
      }
    }
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

    // if (DEBUG_CONFIGURATION)
    // Slog.v(TAG, "Handling relaunch of " + r);
    // if (r == null) {
    // return;
    // }

    // r.activity.mConfigChangeFlags |= configChanges;
    // r.onlyLocalRequest = tmp.onlyLocalRequest;
    // Intent currentIntent = r.activity.mIntent;
    //
    // r.activity.mChangingConfigurations = true;
    //
    // // Need to ensure state is saved.
    // if (!r.paused) {
    // performPauseActivity(r.ident, true);
    // }
    // if (r.state == null && !r.stopped) {
    // r.state = new Bundle();
    // r.state.setAllowFds(false);
    // r.activity.onSaveInstanceState(r.state);
    // }
    //
    // handleDestroyActivity(r.ident, false, 0, true);
    //
    // r.activity = null;
    // r.window = null;
    // r.hideForNow = false;
    // // r.nextIdle = null;
    // // Merge any pending results and pending intents; don't just replace
    // // them
    // // if (tmp.pendingResults != null) {
    // // if (r.pendingResults == null) {
    // // r.pendingResults = tmp.pendingResults;
    // // } else {
    // // r.pendingResults.addAll(tmp.pendingResults);
    // // }
    // // }
    // // if (tmp.pendingIntents != null) {
    // // if (r.pendingIntents == null) {
    // // r.pendingIntents = tmp.pendingIntents;
    // // } else {
    // // r.pendingIntents.addAll(tmp.pendingIntents);
    // // }
    // // }
    // // r.startsNotResumed = tmp.startsNotResumed;
    //
    // handleLaunchActivity(r, currentIntent);
  }

  //////////////////////////////////////
  //          System Methods        //    
  //////////////////////////////////////

  final void scheduleContextCleanup(ContextImpl context, String who, String what) {
    ContextCleanupInfo cci = new ContextCleanupInfo();
    cci.context = context;
    cci.who = who;
    cci.what = what;
    queueOrSendMessage(H.CLEAN_UP_CONTEXT, cci);
  }

  final void handleConfigurationChanged(Configuration config, CompatibilityInfo compat) {
    // TODO
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

  private void handleBindApplication(AppBindData data) {
    mBoundApplication = data;

    mConfiguration = new Configuration(data.config);
    mCompatConfiguration = new Configuration(data.config);

    /*
     * Update the system configuration since its preloaded and might not reflect
     * configuration changes. The configuration object passed in AppBindData can
     * be safely assumed to be up to date
     */
    // TODO resources applyConfigurationToResourcesLocked(data.config);
    // applyCompatConfiguration();

    data.info = getPackageInfoNoCheck(data.appInfo);

    mInstrumentation = new Instrumentation(); // currently we dont support
                                              // custom impl of instrumentation

    // If the app is being launched for full backup or restore, bring it up in
    // a restricted environment with the base application class.
    Application app = data.info.makeApplication(false, mInstrumentation);
    mInitialApplication = app;

    try {
      mInstrumentation.callApplicationOnCreate(app);
    } catch (Exception e) {
      if (!mInstrumentation.onException(app, e)) {
        throw new RuntimeException("Unable to create application " + app.getClass().getName() + ": "
            + e.toString(), e);
      }
    }
  }

  /**
   * Attach application to the ActivityManagerService
   * 
   * @throws Exception
   * 
   */
  private void attach() throws Exception {
    IActivityManager mgr = ActivityManagerNative.getDefault();
    try {
      if (mAppThread == null)
        throw new Exception("App thread null");
      mgr.attachApplication(mAppThread);
    } catch (RemoteException ex) {
      // Ignore
    }
    sThreadLocal.set(this); // so that this class can be reached anywhere in
                            // this thread
    // TODO ViewRootImpl.addConfigCallback(new ComponentCallbacks2() {
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
    //
  }

  public ActivityThread() {
    Log.i(TAG, "Creating new ActivityThread");

    init0();
  }

  public native void init0();

  public static void start(String[] args) {
    Log.i(TAG, "Starting up...");

    Looper.prepareMainLooper();
    if (sMainThreadHandler == null) {
      sMainThreadHandler = new Handler();
    }

    // Create new ActivityThread for this pplication
    ActivityThread thread = new ActivityThread();

    // Setup PackageManager
    sPackageManager = new PackageManager();

    // Setup ActivityManager
    new ActivityManagerService(sPackageManager.getPackageInfo());

    // Setup WindowManager
    WindowManager.getInstance();

    try {
      thread.attach();

      if (DEBUG_MESSAGES) {
        Looper.myLooper().setMessageLogging(new LogPrinter(Log.DEBUG, TAG));
      }

      Looper.loop();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}