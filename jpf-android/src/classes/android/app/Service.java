/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.app;

import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class Service extends ContextWrapper implements ComponentCallbacks2 {
  private static final String TAG = "Service";
  private static int uniqueID = 0;

  public Service() {
    super(null);
  }

  /** Return the application that owns this service. */
  public final Application getApplication() {
    return mApplication;
  }

  /**
   * Called by the system when the service is first created. Do not call this method directly.
   */
  public void onCreate() {
  }

  /**
   * @deprecated Implement {@link #onStartCommand(Intent, int, int)} instead.
   */
  @Deprecated
  public void onStart(Intent intent, int startId) {
  }

  /**
   * Bits returned by {@link #onStartCommand} describing how to continue the service if it is killed. May be
   * {@link #START_STICKY}, {@link #START_NOT_STICKY}, {@link #START_REDELIVER_INTENT}, or
   * {@link #START_STICKY_COMPATIBILITY}.
   */
  public static final int START_CONTINUATION_MASK = 0xf;

  /**
   * Constant to return from {@link #onStartCommand}: compatibility version of {@link #START_STICKY} that does
   * not guarantee that {@link #onStartCommand} will be called again after being killed.
   */
  public static final int START_STICKY_COMPATIBILITY = 0;

  /**
   * Constant to return from {@link #onStartCommand}: if this service's process is killed while it is started
   * (after returning from {@link #onStartCommand}), then leave it in the started state but don't retain this
   * delivered intent. Later the system will try to re-create the service. Because it is in the started state,
   * it will guarantee to call {@link #onStartCommand} after creating the new service instance; if there are
   * not any pending start commands to be delivered to the service, it will be called with a null intent
   * object, so you must take care to check for this.
   * 
   * <p>
   * This mode makes sense for things that will be explicitly started and stopped to run for arbitrary periods
   * of time, such as a service performing background music playback.
   */
  public static final int START_STICKY = 1;

  /**
   * Constant to return from {@link #onStartCommand}: if this service's process is killed while it is started
   * (after returning from {@link #onStartCommand}), and there are no new start intents to deliver to it, then
   * take the service out of the started state and don't recreate until a future explicit call to
   * {@link Context#startService Context.startService(Intent)}. The service will not receive a
   * {@link #onStartCommand(Intent, int, int)} call with a null Intent because it will not be re-started if
   * there are no pending Intents to deliver.
   * 
   * <p>
   * This mode makes sense for things that want to do some work as a result of being started, but can be
   * stopped when under memory pressure and will explicit start themselves again later to do more work. An
   * example of such a service would be one that polls for data from a server: it could schedule an alarm to
   * poll every N minutes by having the alarm start its service. When its {@link #onStartCommand} is called
   * from the alarm, it schedules a new alarm for N minutes later, and spawns a thread to do its networking.
   * If its process is killed while doing that check, the service will not be restarted until the alarm goes
   * off.
   */
  public static final int START_NOT_STICKY = 2;

  /**
   * Constant to return from {@link #onStartCommand}: if this service's process is killed while it is started
   * (after returning from {@link #onStartCommand}), then it will be scheduled for a restart and the last
   * delivered Intent re-delivered to it again via {@link #onStartCommand}. This Intent will remain scheduled
   * for redelivery until the service calls {@link #stopSelf(int)} with the start ID provided to
   * {@link #onStartCommand}. The service will not receive a {@link #onStartCommand(Intent, int, int)} call
   * with a null Intent because it will will only be re-started if it is not finished processing all Intents
   * sent to it (and any such pending events will be delivered at the point of restart).
   */
  public static final int START_REDELIVER_INTENT = 3;

  /**
   * Special constant for reporting that we are done processing {@link #onTaskRemoved(Intent)}.
   * 
   * @hide
   */
  public static final int START_TASK_REMOVED_COMPLETE = 1000;

  /**
   * This flag is set in {@link #onStartCommand} if the Intent is a re-delivery of a previously delivered
   * intent, because the service had previously returned {@link #START_REDELIVER_INTENT} but had been killed
   * before calling {@link #stopSelf(int)} for that Intent.
   */
  public static final int START_FLAG_REDELIVERY = 0x0001;

  /**
   * This flag is set in {@link #onStartCommand} if the Intent is a a retry because the original attempt never
   * got to or returned from {@link #onStartCommand(Intent, int, int)}.
   */
  public static final int START_FLAG_RETRY = 0x0002;

  /**
   * Called by the system every time a client explicitly starts the service by calling
   * {@link android.content.Context#startService}, providing the arguments it supplied and a unique integer
   * token representing the start request. Do not call this method directly.
   * 
   * <p>
   * For backwards compatibility, the default implementation calls {@link #onStart} and returns either
   * {@link #START_STICKY} or {@link #START_STICKY_COMPATIBILITY}.
   * 
   * <p>
   * If you need your application to run on platform versions prior to API level 5, you can use the following
   * model to handle the older {@link #onStart} callback in that case. The <code>handleCommand</code> method
   * is implemented by you as appropriate:
   * 
   * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
   * start_compatibility}
   * 
   * <p class="caution">
   * Note that the system calls this on your service's main thread. A service's main thread is the same thread
   * where UI operations take place for Activities running in the same process. You should always avoid
   * stalling the main thread's event loop. When doing long-running operations, network calls, or heavy disk
   * I/O, you should kick off a new thread, or use {@link android.os.AsyncTask}.
   * </p>
   * 
   * @param intent
   *          The Intent supplied to {@link android.content.Context#startService}, as given. This may be null
   *          if the service is being restarted after its process has gone away, and it had previously
   *          returned anything except {@link #START_STICKY_COMPATIBILITY}.
   * @param flags
   *          Additional data about this start request. Currently either 0, {@link #START_FLAG_REDELIVERY}, or
   *          {@link #START_FLAG_RETRY}.
   * @param startId
   *          A unique integer representing this specific request to start. Use with
   *          {@link #stopSelfResult(int)}.
   * 
   * @return The return value indicates what semantics the system should use for the service's current started
   *         state. It may be one of the constants associated with the {@link #START_CONTINUATION_MASK} bits.
   * 
   * @see #stopSelfResult(int)
   */
  public int onStartCommand(Intent intent, int flags, int startId) {
    onStart(intent, startId);
    return START_STICKY; // Assume is compatible
  }

  /**
   * Called by the system to notify a Service that it is no longer used and is being removed. The service
   * should clean up an resources it holds (threads, registered receivers, etc) at this point. Upon return,
   * there will be no more calls in to this Service object and it is effectively dead. Do not call this method
   * directly.
   */
  public void onDestroy() {
  }

  public void onConfigurationChanged(Configuration newConfig) {
  }

  public void onLowMemory() {
  }

  public void onTrimMemory(int level) {
  }

  /**
   * Return the communication channel to the service. May return null if clients can not bind to the service.
   * The returned {@link android.os.IBinder} is usually for a complex interface that has been <a
   * href="{@docRoot} guide/developing/tools/aidl.html">described using aidl</a>.
   * 
   * <p>
   * <em>Note that unlike other application components, calls on to the
   * IBinder interface returned here may not happen on the main thread
   * of the process</em>.  More information about the main thread can be found in
   * <a href="{@docRoot}. More information about the main thread can be found in <a href="{@docRoot}. More
   * information about the main thread can be found in <a href="{@docRoot}. More information about the main
   * thread can be found in <a href="{@docRoot}. More information about the main thread can be found in <a
   * href="{@docRoot}. More information about the main thread can be found in <a href="{@docRoot}. More
   * information about the main thread can be found in <a href="{@docRoot}. More information about the main
   * thread can be found in <a href="{@docRoot}. More information about the main thread can be found in <a
   * href="{@docRoot}. More information about the main thread can be found in <a href="{@docRoot}. More
   * information about the main thread can be found in <a href="{@docRoot}. More information about the main
   * thread can be found in <a href="{@docRoot}. More information about the main thread can be found in <a
   * href="{@docRoot}. More information about the main thread can be found in <a href="{@docRoot}. More
   * information about the main thread can be found in <a href="{@docRoot}. More information about the main
   * thread can be found in <a href="{@docRoot}
   * guide/topics/fundamentals/processes-and-threads.html">Processes and Threads</a>.
   * </p>
   * 
   * @param intent
   *          The Intent that was used to bind to this service, as given to
   *          {@link android.content.Context#bindService Context.bindService}. Note that any extras that were
   *          included with the Intent at that point will <em>not</em> be seen here.
   * 
   * @return Return an IBinder through which clients can call on to the service.
   */
  public abstract IBinder onBind(Intent intent);

  /**
   * Called when all clients have disconnected from a particular interface published by the service. The
   * default implementation does nothing and returns false.
   * 
   * @param intent
   *          The Intent that was used to bind to this service, as given to
   *          {@link android.content.Context#bindService Context.bindService}. Note that any extras that were
   *          included with the Intent at that point will <em>not</em> be seen here.
   * 
   * @return Return true if you would like to have the service's {@link #onRebind} method later called when
   *         new clients bind to it.
   */
  public boolean onUnbind(Intent intent) {
    return false;
  }

  /**
   * Called when new clients have connected to the service, after it had previously been notified that all had
   * disconnected in its {@link #onUnbind}. This will only be called if the implementation of
   * {@link #onUnbind} was overridden to return true.
   * 
   * @param intent
   *          The Intent that was used to bind to this service, as given to
   *          {@link android.content.Context#bindService Context.bindService}. Note that any extras that were
   *          included with the Intent at that point will <em>not</em> be seen here.
   */
  public void onRebind(Intent intent) {
  }

  /**
   * This is called if the service is currently running and the user has removed a task that comes from the
   * service's application. If you have set {@link android.content.pm.ServiceInfo#FLAG_STOP_WITH_TASK
   * ServiceInfo.FLAG_STOP_WITH_TASK} then you will not receive this callback; instead, the service will
   * simply be stopped.
   * 
   * @param rootIntent
   *          The original root Intent that was used to launch the task that is being removed.
   */
  public void onTaskRemoved(Intent rootIntent) {
  }

  /**
   * Stop the service, if it was previously started. This is the same as calling
   * {@link android.content.Context#stopService} for this particular service.
   * 
   * @see #stopSelfResult(int)
   */
  public final void stopSelf() {
    stopSelf(-1);
  }

  /**
   * Old version of {@link #stopSelfResult} that doesn't return a result.
   * 
   * @see #stopSelfResult
   */
  public final void stopSelf(int startId) {
    if (mActivityManager == null) {
      return;
    }
    try {
      mActivityManager.stopServiceToken(new ComponentName(this, mClassName), mToken, startId);
    } catch (RemoteException ex) {
    }
  }

  /**
   * Stop the service if the most recent time it was started was <var>startId</var>. This is the same as
   * calling {@link android.content.Context#stopService} for this particular service but allows you to safely
   * avoid stopping if there is a start request from a client that you haven't yet seen in {@link #onStart}.
   * 
   * <p>
   * <em>Be careful about ordering of your calls to this function.</em>. If you call this function with the
   * most-recently received ID before you have called it for previously received IDs, the service will be
   * immediately stopped anyway. If you may end up processing IDs out of order (such as by dispatching them on
   * separate threads), then you are responsible for stopping them in the same order you received them.
   * </p>
   * 
   * @param startId
   *          The most recent start identifier received in {@link #onStart}.
   * @return Returns true if the startId matches the last start request and the service will be stopped, else
   *         false.
   * 
   * @see #stopSelf()
   */
  public final boolean stopSelfResult(int startId) {
    // if (mActivityManager == null) {
    // return false;
    // }
    // try {
    // return mActivityManager.stopServiceToken(new ComponentName(this, mClassName), mToken, startId);
    // } catch (RemoteException ex) {
    // }
    return false;
  }

  /**
   * @deprecated This is a now a no-op, use {@link #startForeground(int, Notification)} instead. This method
   *             has been turned into a no-op rather than simply being deprecated because analysis of numerous
   *             poorly behaving devices has shown that increasingly often the trouble is being caused in part
   *             by applications that are abusing it. Thus, given a choice between introducing problems in
   *             existing applications using this API (by allowing them to be killed when they would like to
   *             avoid it), vs allowing the performance of the entire system to be decreased, this method was
   *             deemed less important.
   * 
   * @hide
   */
  @Deprecated
  public final void setForeground(boolean isForeground) {
    Log.w(TAG, "setForeground: ignoring old API call on " + getClass().getName());
  }

  /**
   * Make this service run in the foreground, supplying the ongoing notification to be shown to the user while
   * in this state. By default services are background, meaning that if the system needs to kill them to
   * reclaim more memory (such as to display a large page in a web browser), they can be killed without too
   * much harm. You can set this flag if killing your service would be disruptive to the user, such as if your
   * service is performing background music playback, so the user would notice if their music stopped playing.
   * 
   * <p>
   * If you need your application to run on platform versions prior to API level 5, you can use the following
   * model to call the the older setForeground() or this modern method as appropriate:
   * 
   * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
   * foreground_compatibility}
   * 
   * @param id
   *          The identifier for this notification as per
   *          {@link NotificationManager#notify(int, Notification) NotificationManager.notify(int,
   *          Notification)}.
   * @param notification
   *          The Notification to be displayed.
   * 
   * @see #stopForeground(boolean)
   */
  public final void startForeground(int id, Notification notification) {
    // try {
    // mActivityManager.setServiceForeground(new ComponentName(this, mClassName), mToken, id, notification,
    // true);
    // } catch (RemoteException ex) {
    // }
  }

  /**
   * Remove this service from foreground state, allowing it to be killed if more memory is needed.
   * 
   * @param removeNotification
   *          If true, the notification previously provided to {@link #startForeground} will be removed.
   *          Otherwise it will remain until a later call removes it (or the service is destroyed).
   * @see #startForeground(int, Notification)
   */
  public final void stopForeground(boolean removeNotification) {
    // try {
    // mActivityManager.setServiceForeground(new ComponentName(this, mClassName), mToken, 0, null,
    // removeNotification);
    // } catch (RemoteException ex) {
    // }
  }

  /**
   * Print the Service's state into the given stream. This gets invoked if you run
   * "adb shell dumpsys activity service <yourservicename>". This is distinct from "dumpsys <servicename>",
   * which only works for named system services and which invokes the {@link IBinder#dump} method on the
   * {@link IBinder} interface registered with ServiceManager.
   * 
   * @param fd
   *          The raw file descriptor that the dump is being sent to.
   * @param writer
   *          The PrintWriter to which you should dump your state. This will be closed for you after you
   *          return.
   * @param args
   *          additional arguments to the dump request.
   */
  protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
    writer.println("nothing to dump");
  }

  // ------------------ Internal API ------------------

  public final void attach(Context context, ActivityThread thread, String className, IBinder token,
                           Application application, Object activityManager) {
    attachBaseContext(context);
    mThread = thread; // NOTE: unused - remove?
    mClassName = className;
    mToken = token;
    mApplication = application;
    mActivityManager = (IActivityManager) activityManager;
    // mStartCompatibility = getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.ECLAIR;
    uniqueID++;
  }

  final String getClassName() {
    return mClassName;
  }

  public int getId() {
    return uniqueID;
  }

  // set by the thread after the constructor and before onCreate(Bundle icicle) is called.
  private ActivityThread mThread = null;
  private String mClassName = null;
  private IBinder mToken = null;
  private Application mApplication = null;
  private IActivityManager mActivityManager = null;
  private boolean mStartCompatibility = false;
}
