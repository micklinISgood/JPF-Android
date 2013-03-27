//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
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

package com.android.server.am;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.app.ActivityManagerNative;
import android.app.ActivityThread.ApplicationThread;
import android.app.IActivityManager;
import android.app.IIntentReceiver;
import android.app.ResultInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Models the ActivitManagerService. This service in the Android OS is responsible for managing the lifecycle
 * of all applications' components in the system. As we are only modeling a single application, this service
 * is greatly simplified.
 * 
 * @author Heila van der Merwe
 * 
 */
public class ActivityManager {
  public static final String TAG = "ActivityManager";

  /**
   * Reference to the application-under-test. Used to put messages into the applications's messageQueue.
   */
  private static ApplicationThread thread;

  /**
   * Stores info on the application and its components parsed from the AndroidManifest.xml file.
   */
  public static PackageInfo packageInfo;

  /** Stores info on the running services */
  Map<ComponentName, ServiceRecord> services = new HashMap<ComponentName, ServiceRecord>();

  /**
   * All currently running services.
   */
  final HashMap<ComponentName, ServiceRecord> mServices = new HashMap<ComponentName, ServiceRecord>();

  /**
   * All currently bound service connections. Keys are the IBinder of the client's IServiceConnection.
   */
  final HashMap<ServiceConnection, ArrayList<ConnectionRecord>> mServiceConnections = new HashMap<ServiceConnection, ArrayList<ConnectionRecord>>();

  Stack<ActivityRecord> activityStack = new Stack<ActivityRecord>();

  final HashMap<IBinder, ReceiverList> mRegisteredReceivers = new HashMap<IBinder, ReceiverList>();

  public BroadcastRecord orderedBroadcast;

  public ActivityManager(PackageInfo pi) {
    init0();
    packageInfo = pi;
    ActivityManagerNative.mgr = new IActivityManager(this);
  }

  native void init0();

  public void attachApplication(ApplicationThread mAppThread) throws RemoteException {
    thread = mAppThread;

    mAppThread.bindApplication(packageInfo.applicationInfo.packageName, packageInfo.applicationInfo, null,
        new Configuration(), null, null);
  }

  private String getActivityName(Intent intent) {
    // TODO resolve Intent's componentName
    ComponentName cn = intent.getComponent();

    String[] name = cn.getClassName().split("\\.");
    if (name.length <= 2) { // package also given
      cn.setClassName(packageInfo.packageName + "." + cn.getClassName());
      // TODO might have to lookup
    }
    return cn.getClassName();
  }

  /**
   * This method modes the process of starting/launching a new Activity.
   * 
   * Its process is as follows
   * 
   * 1. pause activity on top of stack (currently running)
   * 
   * 2. start new Activity
   * 
   * 3. if successful: schedule stop of previous Activity else resume current Activity on top of stack
   * 
   * It receives two parameters:
   * 
   * @param startingIntent
   *          - the Intent used to identify the starting Activity, can contain bundle with extra args
   * @param requestCode
   *          - indicating the custom type of the request (-1 if not started for result)
   */
  public void performLaunchActivity(Intent startingIntent, int requestCode) {

    String activityName = getActivityName(startingIntent);
    System.out.println("Launching Activity " + activityName);

    // TODO we have a problem if the application starts activities that are not part of this application
    ActivityInfo info = null;
    for (ActivityInfo p : packageInfo.activities) {
      if ((p.packageName + p.name).contains(activityName)) {
        info = p;
        break;
      }
    }

    if (info == null) {
      throw new UnsupportedOperationException(
          "jpf-android does not support starting activities ouside of the application or the starting of activities using implcit intents:"
              + activityName);
    }

    // get current active activity
    ActivityRecord current = null;
    if (!activityStack.isEmpty())
      current = activityStack.peek();

    // create Activity record for this activity (When launching new activity we ALWAYS create new instance)
    ActivityRecord newActivity = new ActivityRecord(startingIntent, startingIntent.getComponent(), current,
        "", requestCode, info, startingIntent.getComponent().getClassName());

    // if (current != null) { // there are no active activities
    // thread.schedulePauseActivity(current, false, true, 0);
    // }

    List<ResultInfo> result = new ArrayList<ResultInfo>();
    if (current != null) {
      result.add(new ResultInfo(current.stringName, requestCode, 0, null));
    }

    thread.scheduleLaunchActivity(startingIntent, newActivity, -1, info, null, null, null, result, null,
        false, true);
    activityStack.push(newActivity);

    // TODO if launch was not successful we must resume prev/else stop prev
    if (current != null) {
      System.out.println("scheduling stop");
      thread.scheduleStopActivity(current, false, 0);
    }
  }

  /**
   * Can only be called from back button pressed or finish() called from code or activity killed by system
   * 
   * @param token
   * @param resultCode
   * @param resultData
   */
  public void performFinishActivity(IBinder token, int resultCode, Intent resultData) {
    System.out.println("Finishing Activity " + ((ActivityRecord) token).stringName);

    ActivityRecord current = null;
    if (!activityStack.isEmpty()) {
      current = activityStack.pop();
      if (current != null && token != null && current != token) {
        throw new UnsupportedOperationException(
            "jpf-android does not support the finishing of a actvity that is not currently the active activity");
      }
    }

    thread.schedulePauseActivity(token, true, true, 0);
    if (current.requestCode != -1) {
      ResultInfo res = new ResultInfo(current.stringName, current.requestCode, resultCode, resultData);
      ArrayList<ResultInfo> resultInfos = new ArrayList<ResultInfo>(1);
      resultInfos.add(res);
      thread.scheduleSendResult(activityStack.peek(), resultInfos);
    }
    thread.scheduleWindowVisibility(activityStack.peek(), true); // restarts activity
    thread.scheduleResumeActivity(activityStack.peek(), true); // resumes activity
    thread.scheduleDestroyActivity(token, true, 0);
    // TODO stop all binds to services

  }

  public void performActivityPaused(IBinder token) {
    // TODO Auto-generated method stub

  }

  public void performActivityStopped(IBinder token, Bundle state, Object object, CharSequence description) {
    // TODO Auto-generated method stub

  }

  public void performActivityDestroyed(IBinder token) {
    // TODO Auto-generated method stub

  }

  public boolean performWillActivityBeVisible(IBinder token) {
    ActivityRecord record = activityStack.peek();
    if (record.equals((ActivityRecord) token)) {
      // this activity has launched successfully so stop previous activity on stack
    }

    return false;
  }

  /**
   * 
   */
  private void performConfigurationChange() {
    // ActivityClientRecord current = currentActivity;
    // // mConfiguration.orientation = Configuration.ORIENTATION_LANDSCAPE;
    // if (current != null) {
    // thread.scheduleRelaunchActivity();
    // }
  }

  private void performHomePressed() {
    // ActivityClientRecord current = currentActivity;
    // if (current != null) {
    // thread.schedulePauseActivity(current.ident, false);
    // thread.scheduleStopActivity(current.ident);
    // }
  }

  private void performSleep() {

  }

  private void performWake() {

  }

  private void killApp() {

  }

  /* ********************************* Service methods ******************************** */

  private ComponentName getServiceComponent(Intent service, String resolvedType) {
    // TODO Auto-generated method stub
    return service.getComponent();
  }

  public ComponentName performStartService(Intent serviceIntent, String resolvedType) {
    // resolve componentName from intent
    ComponentName cn = getServiceComponent(serviceIntent, resolvedType);

    ServiceRecord record = createService(cn, serviceIntent);
    record.started = true;
    sendServiceArgsLocked(record); // this means that service was alreay created so just send args
    return cn;
  }

  private void sendServiceArgsLocked(ServiceRecord r) {
    thread.scheduleServiceArgs(r, false, r.makeNextStartId(), r.intent.getFlags(), r.intent);
  }

  public int performStopService(Intent serviceIntent, String resolvedType) {
    // resolve componentName from intent
    ComponentName cn = getServiceComponent(serviceIntent, resolvedType);

    // lookup service in service map
    ServiceRecord record = services.get(cn); // make sure hashcode is correct
    // if found
    if (record != null && record.conns.size() < 1) {
      record.started = false;
      services.remove(record.name);
      thread.scheduleStopService(record);
    }
    return -1;

  }

  public int performBindService(IBinder token, Intent serviceIntent, String resolvedType,
                                ServiceConnection connection, int flags) {

    // resolve componentName from intent
    ComponentName cn = getServiceComponent(serviceIntent, resolvedType);

    ServiceRecord record = createService(cn, serviceIntent);
    ConnectionRecord connRecord = new ConnectionRecord((ActivityRecord) token, connection, flags, 0,
        serviceIntent, "", false, record);

    ArrayList<ConnectionRecord> conns = mServiceConnections.get(connection);
    if (conns == null) {
      conns = new ArrayList<ConnectionRecord>();
      mServiceConnections.put(connection, conns);
    }
    conns.add(connRecord);

    if (record.conns == null) {
      record.conns = new HashSet<ConnectionRecord>();
    }
    record.conns.add(connRecord);

    thread.scheduleBindService(record, serviceIntent, true);
    return -1;

  }

  public void performUnbindService(ServiceConnection conn) {
    ArrayList<ConnectionRecord> conns = mServiceConnections.get(conn);
    if (conns != null) {
      for (ConnectionRecord cr : conns) {
        thread.scheduleUnbindService(cr.service, cr.clientIntent);
        // if no more bounds to this service and it is not started, we destroy it
        cr.service.conns.remove(cr);
        if (cr.service.conns.size() <= 0 && cr.service.started == false) {
          thread.scheduleStopService(cr.service);
          services.remove(cr.service);
        }
      }
    }
    mServiceConnections.remove(conn);
  }

  private ServiceRecord createService(ComponentName cn, Intent serviceIntent) {

    // lookup service in service map
    ServiceRecord record = services.get(cn); // make sure hashcode is correct
    // if found
    if (record == null) {
      ServiceInfo info = null;
      for (ServiceInfo p : packageInfo.services) {

        if ((p.packageName + "." + p.name).contains(cn.getClassName())) {
          info = p;
          break;
        }
      }

      if (info == null) {
        throw new UnsupportedOperationException(
            "jpf-android does not support starting activities ouside of the application or the starting of activities using implcit intents");
      }

      // create Activity record for this activity (When launching new activity we ALWAYS create new instance)
      record = new ServiceRecord(cn, serviceIntent, info);
      services.put(cn, record); // this is a new service and its record has
      thread.scheduleCreateService(record, info, null);
    }
    return record;
  }

  public void performPublishService(Object token, Intent intent, IBinder binder) {
    // TODO Auto-generated method stub

  }

  public void performUnbindFinished(Object token, Intent intent, boolean doRebind) {
    // TODO Auto-generated method stub

  }

  public void performServiceDoneExecuting(Object token, int i, int j, int k) {
    // TODO Auto-generated method stub

  }

  public void performStopServiceToken(ComponentName componentName, IBinder mToken, int startId) {
    // TODO Auto-generated method stub

  }

  /* **************************** Receiver methods ******************************** */

  public Intent performRegisterReceiver(ApplicationThread caller, String callerPackage,
                                        IIntentReceiver receiver, IntentFilter filter, String permission)
      throws RemoteException {

    // List allSticky = null;
    //
    // // Look for any matching sticky broadcasts...
    // Iterator actions = filter.actionsIterator();
    // if (actions != null) {
    // while (actions.hasNext()) {
    // String action = (String) actions.next();
    // allSticky = getStickiesLocked(action, filter, allSticky);
    // }
    // } else {
    // allSticky = getStickiesLocked(null, filter, allSticky);
    // }
    //
    // // The first sticky in the list is returned directly back to
    // // the client.
    // Intent sticky = allSticky != null ? (Intent) allSticky.get(0) : null;
    //
    // if (DEBUG_BROADCAST)
    // Slog.v(TAG, "Register receiver " + filter + ": " + sticky);
    //
    // if (receiver == null) {
    // return sticky;
    // }
    //
    // ReceiverList rl = (ReceiverList) mRegisteredReceivers.get(receiver.asBinder());
    // if (rl == null) {
    // rl = new ReceiverList(receiver);
    // mRegisteredReceivers.put(receiver.asBinder(), rl);
    // }
    // BroadcastFilter bf = new BroadcastFilter(filter, rl, callerPackage, permission);
    // rl.add(bf);
    // if (!bf.debugCheck()) {
    // Slog.w(TAG, "==> For Dynamic broadast");
    // }
    // mReceiverResolver.addFilter(bf);
    //
    // // Enqueue broadcasts for all existing stickies that match
    // // this filter.
    // if (allSticky != null) {
    // ArrayList receivers = new ArrayList();
    // receivers.add(bf);
    //
    // int N = allSticky.size();
    // for (int i = 0; i < N; i++) {
    // Intent intent = (Intent) allSticky.get(i);
    // BroadcastRecord r = new BroadcastRecord(intent, null, null, -1, -1, null, receivers, null, 0, null,
    // null, false, true, true);
    // if (mParallelBroadcasts.size() == 0) {
    // scheduleBroadcastsLocked();
    // }
    // mParallelBroadcasts.add(r);
    // }
    // }
    //
    // return sticky;
    return null;
  }

  public final int performBroadcastIntent(ApplicationThread caller, Intent intent, String resolvedType,
                                          IIntentReceiver resultTo, int resultCode, String resultData,
                                          Bundle map, String requiredPermission, boolean serialized,
                                          boolean sticky) throws RemoteException {
    return -1;
  }

  public void performUnbroadcastIntent(ApplicationThread applicationThread, Intent intent)
      throws RemoteException {
  }

  public void performFinishReceiver(IBinder who, int resultCode, String resultData, Bundle resultExtras,
                                    boolean resultAbort) throws RemoteException {
  }

  public void performUnregisterReceiver(IIntentReceiver receiver) throws RemoteException {
  }
}