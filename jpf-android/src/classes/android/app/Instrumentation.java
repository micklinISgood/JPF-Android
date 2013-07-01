package android.app;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.MessageQueue;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class Instrumentation {
  private static final String TAG = "Instrumentation";

  private ActivityThread mThread = null;
  private MessageQueue mMessageQueue = null;
  private Context mInstrContext;
  private Context mAppContext;
  private ComponentName mComponent;
  private Thread mRunner;

  public Instrumentation() {
  }

  public void onCreate(Bundle arguments) {
  }

  /**
   * Method where the instrumentation thread enters execution. This allows you to run your instrumentation
   * code in a separate thread than the application, so that it can perform blocking operation such as
   * {@link #sendKeySync} or {@link #startActivitySync}.
   * 
   * <p>
   * You will typically want to call finish() when this function is done, to end your instrumentation.
   */
  public void onStart() {
  }

  /**
   * This is called whenever the system captures an unhandled exception that was thrown by the application.
   * The default implementation simply returns false, allowing normal system handling of the exception to take
   * place.
   * 
   * @param obj
   *          The client object that generated the exception. May be an Application, Activity,
   *          BroadcastReceiver, Service, or null.
   * @param e
   *          The exception that was thrown.
   * 
   * @return To allow normal system exception process to occur, return false. If true is returned, the system
   *         will proceed as if the exception didn't happen.
   */
  public boolean onException(Object obj, Throwable e) {
    return false;
  }

  public void sendStatus(int resultCode, Bundle results) {
    // not supported
  }

  public void finish(int resultCode, Bundle results) {
    // not supported
  }

  public void setAutomaticPerformanceSnapshots() {
    // not supported
  }

  public void startPerformanceSnapshot() {
    // not supported
  }

  public void endPerformanceSnapshot() {
    // not supported
  }

  public void onDestroy() {
  }

  public Context getContext() {
    return mInstrContext;
  }

  public ComponentName getComponentName() {
    return mComponent;
  }

  public Context getTargetContext() {
    return mAppContext;
  }

  public boolean isProfiling() {
    return false;
  }

  public void startProfiling() {
    // not supported
  }

  public void stopProfiling() {
    // not supported
  }

  public void setInTouchMode(boolean inTouch) {
    // not supported
  }

  public void waitForIdle(Runnable recipient) {
    // not supported
  }

  public void waitForIdleSync() {
    // not supported
  }

  public void runOnMainSync(Runnable runner) {
    // not supported
  }

  public Activity startActivitySync(Intent intent) {
    // not supported
    return null;
  }

  /*
   * Not supported
   */
  public static class ActivityMonitor {
    public ActivityMonitor(IntentFilter which, ActivityResult result, boolean block) {
    }

    public ActivityMonitor(String cls, ActivityResult result, boolean block) {
    }

    /**
     * Retrieve the filter associated with this ActivityMonitor.
     */
    public final IntentFilter getFilter() {
      return null;
    }

    /**
     * Retrieve the result associated with this ActivityMonitor, or null if none.
     */
    public final ActivityResult getResult() {
      return null;
    }

    /**
     * Check whether this monitor blocks activity starts (not allowing the actual activity to run) or allows
     * them to execute normally.
     */
    public final boolean isBlocking() {
      return false;
    }

    /**
     * Retrieve the number of times the monitor has been hit so far.
     */
    public final int getHits() {
      return 0;
    }

    /**
     * Retrieve the most recent activity class that was seen by this monitor.
     */
    public final Activity getLastActivity() {
      return null;
    }

    /**
     * Block until an Activity is created that matches this monitor, returning the resulting activity.
     * 
     * @return Activity
     */
    public final Activity waitForActivity() {
      return null;
    }

    /**
     * Block until an Activity is created that matches this monitor, returning the resulting activity or till
     * the timeOut period expires. If the timeOut expires before the activity is started, return null.
     * 
     * @param timeOut
     *          Time to wait before the activity is created.
     * 
     * @return Activity
     */
    public final Activity waitForActivityWithTimeout(long timeOut) {
      return null;
    }

    final boolean match(Context who, Activity activity, Intent intent) {
      return false;
    }
  }

  public void addMonitor(ActivityMonitor monitor) {
    // not supported
  }

  public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
    return null;
  }

  public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
    return null;
  }

  public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
    return true;
  }

  public Activity waitForMonitor(ActivityMonitor monitor) {
    return null;
  }

  public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
    return null;
  }

  public void removeMonitor(ActivityMonitor monitor) {
  }

  public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
    // not supported
    return false;
  }

  public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
    // not supported
    return false;

  }

  public void sendStringSync(String text) {

  }

  public void sendKeySync(KeyEvent event) {

  }

  public void sendKeyDownUpSync(int key) {

  }

  public void sendCharacterSync(int keyCode) {

  }

  public void sendPointerSync(MotionEvent event) {

  }

  public void sendTrackballEventSync(MotionEvent event) {

  }

  /**
   * Perform instantiation of the process's {@link Application} object. The default implementation provides
   * the normal system behavior.
   * 
   * @param cl
   *          The ClassLoader with which to instantiate the object.
   * @param className
   *          The name of the class implementing the Application object.
   * @param context
   *          The context to initialize the application with
   * 
   * @return The newly instantiated Application object.
   */
  public Application newApplication(ClassLoader cl, String className, Context context)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    return newApplication(cl.loadClass(className), context);
  }

  /**
   * Perform instantiation of the process's {@link Application} object. The default implementation provides
   * the normal system behavior.
   * 
   * @param clazz
   *          The class used to create an Application object from.
   * @param context
   *          The context to initialize the application with
   * 
   * @return The newly instantiated Application object.
   */
  static public Application newApplication(Class<?> clazz, Context context) throws InstantiationException,
      IllegalAccessException, ClassNotFoundException {

    Application app = (Application) clazz.newInstance();
    app.attach(context);
    return app;
  }

  /**
   * Perform calling of the application's {@link Application#onCreate} method. The default implementation
   * simply calls through to that method.
   * 
   * @param app
   *          The application being created.
   */
  public void callApplicationOnCreate(Application app) {
    app.onCreate();
  }

  /**
   * Perform instantiation of an {@link Activity} object. This method is intended for use with unit tests,
   * such as android.test.ActivityUnitTestCase. The activity will be useable locally but will be missing some
   * of the linkages necessary for use within the sytem.
   * 
   */
  public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application,
                              Intent intent, ActivityInfo info, CharSequence title, Activity parent,
                              String id, Object lastNonConfigurationInstance) throws InstantiationException,
      IllegalAccessException {
    Activity activity = (Activity) clazz.newInstance();
    // ActivityThread aThread = null;
    // activity.attach(context, aThread, this, token, application, intent,
    // info, title, parent, id,
    // (Activity.NonConfigurationInstances)lastNonConfigurationInstance,
    // new Configuration());
    return activity;
  }

  /**
   * Perform instantiation of the process's {@link Activity} object. The default implementation provides the
   * normal system behaviour.
   * 
   * @param cl
   *          assumes null
   * @param className
   *          The name of the class implementing the Activity object.
   * @param intent
   *          The Intent object that specified the activity class being instantiated.
   * 
   * @return The newly instantiated Activity object.
   */
  public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException,
      IllegalAccessException, ClassNotFoundException {
    Class<Activity> cls = (Class<Activity>) Class.forName(className);
    return (Activity) cls.newInstance();
  }

  /**
   * Perform calling of an activity's {@link Activity#onCreate} method. The default implementation simply
   * calls through to that method.
   * 
   * @param activity
   *          The activity being created.
   * @param icicle
   *          The previously frozen state (or null) to pass through to onCreate().
   */
  public void callActivityOnCreate(Activity activity, Bundle icicle) {
    activity.performCreate(icicle);
  }

  public void callActivityOnDestroy(Activity activity) {
    activity.performDestroy();
  }

  /**
   * Perform calling of an activity's {@link Activity#onRestoreInstanceState} method. The default
   * implementation simply calls through to that method.
   * 
   * @param activity
   *          The activity being restored.
   * @param savedInstanceState
   *          The previously saved state being restored.
   */
  public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
    activity.performRestoreInstanceState(savedInstanceState);
  }

  /**
   * Perform calling of an activity's {@link Activity#onPostCreate} method. The default implementation simply
   * calls through to that method.
   * 
   * @param activity
   *          The activity being created.
   * @param icicle
   *          The previously frozen state (or null) to pass through to onPostCreate().
   */
  public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
    activity.onPostCreate(icicle);
  }

  /**
   * Perform calling of an activity's {@link Activity#onNewIntent} method. The default implementation simply
   * calls through to that method.
   * 
   * @param activity
   *          The activity receiving a new Intent.
   * @param intent
   *          The new intent being received.
   */
  public void callActivityOnNewIntent(Activity activity, Intent intent) {
    activity.onNewIntent(intent);
  }

  /**
   * Perform calling of an activity's {@link Activity#onStart} method. The default implementation simply calls
   * through to that method.
   * 
   * @param activity
   *          The activity being started.
   */
  public void callActivityOnStart(Activity activity) {
    activity.onStart();
  }

  /**
   * Perform calling of an activity's {@link Activity#onRestart} method. The default implementation simply
   * calls through to that method.
   * 
   * @param activity
   *          The activity being restarted.
   */
  public void callActivityOnRestart(Activity activity) {
    activity.onRestart();
  }

  /**
   * Perform calling of an activity's {@link Activity#onResume} method. The default implementation simply
   * calls through to that method.
   * 
   * @param activity
   *          The activity being resumed.
   */
  public void callActivityOnResume(Activity activity) {
    activity.mResumed = true;
    activity.onResume();
  }

  /**
   * Perform calling of an activity's {@link Activity#onStop} method. The default implementation simply calls
   * through to that method.
   * 
   * @param activity
   *          The activity being stopped.
   */
  public void callActivityOnStop(Activity activity) {
    activity.onStop();
  }

  /**
   * Perform calling of an activity's {@link Activity#onPause} method. The default implementation simply calls
   * through to that method.
   * 
   * @param activity
   *          The activity being saved.
   * @param outState
   *          The bundle to pass to the call.
   */
  public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
    activity.performSaveInstanceState(outState);
  }

  /**
   * Perform calling of an activity's {@link Activity#onPause} method. The default implementation simply calls
   * through to that method.
   * 
   * @param activity
   *          The activity being paused.
   */
  public void callActivityOnPause(Activity activity) {
    activity.performPause();
  }

  /**
   * Perform calling of an activity's {@link Activity#onUserLeaveHint} method. The default implementation
   * simply calls through to that method.
   * 
   * @param activity
   *          The activity being notified that the user has navigated away
   */
  public void callActivityOnUserLeaving(Activity activity) {
    activity.performUserLeaving();
  }

  /*
   * Starts allocation counting. This triggers a gc and resets the counts.
   */
  public void startAllocCounting() {

  }

  /*
   * Stops allocation counting.
   */
  public void stopAllocCounting() {

  }

  /**
   * If Results already contains Key, it appends Value to the key's ArrayList associated with the key. If the
   * key doesn't already exist in results, it adds the key/value pair to results.
   */
  private void addValue(String key, int value, Bundle results) {
    if (results.containsKey(key)) {
      List<Integer> list = results.getIntegerArrayList(key);
      if (list != null) {
        list.add(value);
      }
    } else {
      ArrayList<Integer> list = new ArrayList<Integer>();
      list.add(value);
      results.putIntegerArrayList(key, list);
    }
  }

  public Bundle getAllocCounts() {
    return null;
  }

  public Bundle getBinderCounts() {
    return null;
  }

  public static final class ActivityResult {
    public ActivityResult(int resultCode, Intent resultData) {
      mResultCode = resultCode;
      mResultData = resultData;
    }

    /**
     * Retrieve the result code contained in this result.
     */
    public int getResultCode() {
      return mResultCode;
    }

    /**
     * Retrieve the data contained in this result.
     */
    public Intent getResultData() {
      return mResultData;
    }

    private final int mResultCode;
    private final Intent mResultData;
  }

  public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                          Intent intent, int requestCode) {
    ActivityManagerNative.getDefault().startActivity(intent, requestCode);
    return null;
    // int result = ActivityManagerNative.getDefault().startActivity(whoThread, intent,
    // intent.resolveTypeIfNeeded(who.getContentResolver()), null, 0, token,
    // target != null ? target.mEmbeddedID : null, requestCode, false, false, null, null, false);
    // checkStartActivityResult(result, intent);
    // } catch (RemoteException e) {
    // }
  }

  public void execStartActivities(Context who, IBinder contextThread, IBinder token, Activity target,
                                  Intent[] intents) {
    // not supported
  }

  public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Fragment target,
                                          Intent intent, int requestCode) {
    IApplicationThread whoThread = (IApplicationThread) contextThread;
    // try {
    // int result = ActivityManagerNative.getDefault().startActivity(whoThread, intent,
    // intent.resolveTypeIfNeeded(who.getContentResolver()), null, 0, token,
    // target != null ? target.mWho : null, requestCode, false, false, null, null, false);
    // checkStartActivityResult(result, intent);
    // } catch (RemoteException e) {
    // }
    // TODO
    ActivityManagerNative.getDefault().startActivity(intent, requestCode);
    return null;
  }

  // /* package */static void checkStartActivityResult(int res, Object intent) {
  // if (res >= IActivityManager.START_SUCCESS) {
  // return;
  // }
  //
  // switch (res) {
  // case IActivityManager.START_INTENT_NOT_RESOLVED:
  // case IActivityManager.START_CLASS_NOT_FOUND:
  // if (intent instanceof Intent && ((Intent) intent).getComponent() != null)
  // throw new ActivityNotFoundException("Unable to find explicit activity class "
  // + ((Intent) intent).getComponent()
  // + "; have you declared this activity in your AndroidManifest.xml?");
  // throw new ActivityNotFoundException("No Activity found to handle " + intent);
  // case IActivityManager.START_PERMISSION_DENIED:
  // throw new SecurityException("Not allowed to start activity " + intent);
  // case IActivityManager.START_FORWARD_AND_REQUEST_CONFLICT:
  // throw new AndroidRuntimeException("FORWARD_RESULT_FLAG used while also requesting a result");
  // case IActivityManager.START_NOT_ACTIVITY:
  // throw new IllegalArgumentException("PendingIntent is not an activity");
  // default:
  // throw new AndroidRuntimeException("Unknown error code " + res + " when starting " + intent);
  // }
  // }

  private final void validateNotAppThread() {
    if (ActivityThread.currentActivityThread() != null) {
      throw new RuntimeException("This method can not be called from the main application thread");
    }
  }

}
