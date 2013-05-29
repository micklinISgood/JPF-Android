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

package com.android.server.am;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.app.ActivityManagerNative;
import android.app.ActivityThread.ApplicationThread;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.IServiceConnection;
import android.app.ResultInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;

import com.android.server.IntentResolver;

/**
 * Models the ActivitManagerService. This service in the Android OS is
 * responsible for managing the life-cycle of all applications' components in
 * the system. As we are only modeling a single application, this service is
 * greatly simplified.
 * 
 * @author Heila van der Merwe
 * 
 */
public class ActivityManagerService {
  public static final String TAG = "ActivityManager";
  // public static final boolean DEBUG = true;
  public static final boolean DEBUG_BROADCAST = false;

  public static final int BROADCAST_SUCCESS = 0;
  public static final int BROADCAST_STICKY_CANT_HAVE_PERMISSION = -1;

  /**
   * Reference to the application-under-test. Used to put messages into the
   * applications's messageQueue.
   */
  private static ApplicationThread thread;

  /**
   * Stores info on the application and its components parsed from the
   * AndroidManifest.xml file.
   */
  public static PackageInfo packageInfo;

  /** All currently running services. */
  final Map<ComponentName, ServiceRecord> services = new HashMap<ComponentName, ServiceRecord>();

  /** All currently bound service connections. */
  final Map<IServiceConnection, List<ConnectionRecord>> mServiceConnections = new HashMap<IServiceConnection, List<ConnectionRecord>>();

  final Stack<ActivityRecord> activityStack = new Stack<ActivityRecord>();

  /////////////////////////////////////////////
  //          Broadcast datastructures       //
  /////////////////////////////////////////////

  /**
   * Historical data of past broadcasts, for debugging.
   */
  static final int MAX_BROADCAST_HISTORY = 25;
  final BroadcastRecord[] mBroadcastHistory = new BroadcastRecord[MAX_BROADCAST_HISTORY];

  /**
   * Stores a list of all the unique BroadcastRecievers and a list of their
   * registered IntentFilters
   */
  final HashMap<IIntentReceiver, ReceiverList> mRegisteredReceivers = new HashMap<IIntentReceiver, ReceiverList>();

  /** List of the ordered Broadcasts that have to be delivered sequentially */
  public List<BroadcastRecord> mOrderedBroadcasts = new ArrayList<BroadcastRecord>();

  /** List of the Broadcasts that can be delivered in parallel */
  public List<BroadcastRecord> mParallelBroadcasts = new ArrayList<BroadcastRecord>();

  /**
   * State of all active sticky broadcasts. Keys are the action of the sticky
   * Intent, values are an ArrayList of all broadcasted intents with that action
   * (which should usually be one).
   */
  final HashMap<String, ArrayList<Intent>> mStickyBroadcasts = new HashMap<String, ArrayList<Intent>>();

  /**
   * Resolver for broadcast intents to registered receivers.
   * Holds BroadcastFilter (subclass of IntentFilter).
   */
  final IntentResolver<BroadcastFilter, BroadcastFilter> mReceiverResolver = new IntentResolver<BroadcastFilter, BroadcastFilter>() {
    @Override
    protected boolean allowFilterResult(BroadcastFilter filter, List<BroadcastFilter> dest) {
      IBinder target = filter.receiverList.receiver.asBinder();
      for (int i = dest.size() - 1; i >= 0; i--) {
        if (dest.get(i).receiverList.receiver.asBinder() == target) {
          return false;
        }
      }
      return true;
    }

    @Override
    protected String packageForFilter(BroadcastFilter filter) {
      return filter.packageName;
    }
  };

  public ActivityManagerService(PackageInfo pi) {
    init0();
    packageInfo = pi;
    ActivityManagerNative.mgr = new IActivityManager(this);
    Log.i(TAG, "Started ActivityManager");

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
   * 3. if successful: schedule stop of previous Activity else resume current
   * Activity on top of stack
   * 
   * It receives two parameters:
   * 
   * @param startingIntent
   *          - the Intent used to identify the starting Activity, can contain
   *          bundle with extra args
   * @param requestCode
   *          - indicating the custom type of the request (-1 if not started for
   *          result)
   */
  public void performLaunchActivity(Intent startingIntent, int requestCode) {

    String activityName = getActivityName(startingIntent);
    Log.i(TAG, "Scheduling launch of Activity \"" + activityName + "\"");

    // TODO we have a problem if the application starts activities that are
    // not part of this application
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

    // create Activity record for this activity (When launching new activity
    // we ALWAYS create new instance)
    ActivityRecord newActivity = new ActivityRecord(startingIntent, startingIntent.getComponent(), current,
        "", requestCode, info, startingIntent.getComponent().getClassName());

    List<ResultInfo> result = new ArrayList<ResultInfo>();
    if (current != null) {
      result.add(new ResultInfo(current.stringName, requestCode, 0, null));
    }

    thread.scheduleLaunchActivity(startingIntent, newActivity, -1, info, null, null, null, result, null,
        false, true);
    activityStack.push(newActivity);

    // TODO if launch was not successful we must resume prev/else stop prev
    if (current != null) {
      thread.scheduleStopActivity(current, false, 0);
    }
  }

  /**
   * Can only be called from back button pressed or finish() called from code or
   * activity killed by system
   * 
   * @param token
   * @param resultCode
   * @param resultData
   */
  public void performFinishActivity(IBinder token, int resultCode, Intent resultData) {
    ActivityRecord current = null;
    if (!activityStack.isEmpty()) {
      current = activityStack.pop();
      Log.i(TAG, "Scheduling finish of Activity \"" + current.stringName + "\"");

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
    thread.scheduleWindowVisibility(activityStack.peek(), true); // restarts
    // activity
    thread.scheduleResumeActivity(activityStack.peek(), true); // resumes
    // activity
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
      // this activity has launched successfully so stop previous activity
      // on stack
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

  /* ********************** Service Methods ********************* */

  /**
   * Resolves the Intent to the appropriate Service's ComponentName object.
   * 
   * @param service
   * @param resolvedType
   * @return
   */
  private ComponentName getServiceComponent(Intent service, String resolvedType) {
    ComponentName name = service.getComponent();

    // If name not explicitly set
    if (name == null) {
      throw new UnsupportedOperationException(
          "jpf-android does not currently support starting services ouside of the application or using implcit intents");
    }
    return name;
  }

  /**
   * Retrieves the ServiceRecord of a service. If the Service is run for the
   * first time, a new ServiceRecord is generated.
   * 
   * @param serviceIntent
   * @param resolvedType
   * @return
   */
  private ServiceRecord retrieveService(Intent serviceIntent, String resolvedType) {

    // resolve ComponentName from Intent
    ComponentName cn = getServiceComponent(serviceIntent, resolvedType);

    // lookup ServiceRecord in servicesMap
    ServiceRecord record = services.get(cn); // make sure hashcode is
    // correct
    // if ServiceRecord not found
    if (record == null) {
      ServiceInfo info = null;

      // lookup ServiceInfo of Service from the PackageInfo
      for (ServiceInfo p : packageInfo.services) {
        System.out.println("#####################" + p.packageName + p.name + " " + cn.getClassName());
        if ((p.packageName + p.name).contains(cn.getClassName())) {
          info = p;
          break;
        }
      }
      // if ServiceInfo not found
      if (info == null) {
        throw new UnsupportedOperationException("jpf-android could not find the ServiceInfo for "
            + cn.getClassName());
      }

      // create new Service record for this activity (When launching new
      // Services we ALWAYS create new record)
      record = new ServiceRecord(cn, serviceIntent, info);
      services.put(cn, record);
    }
    return record;
  }

  /**
   * Retrieves the ServiceRecord of a Service from its ComponentName.
   * 
   * @param name
   * @param token
   * @return
   */
  private final ServiceRecord retrieveService(ComponentName name, IBinder token) {
    ServiceRecord r = services.get(name);
    return r == token ? r : null;
  }

  /**
   * Starts a service described in serviceIntent. This is done in the following
   * way:
   * 
   * 1. Retrieve the ServiceRecord for the Service 2. If the Service has been
   * created, check to see if it has been started. 3. If the Service has been
   * started, just send the ServiceArgs to the Service (calls onStartCommand()).
   * 4. If the Service has not been started, start the Service and deliver the
   * service's arguments. 5. If the Service has not been created before, create
   * a new ServiceRecord and then start the Service.
   * 
   * @param serviceIntent
   * @param resolvedType
   * @return the ComponentName of the Service that was started.
   */
  public ComponentName performStartService(Intent serviceIntent, String resolvedType) {

    // retrieve ServiceRecord
    ServiceRecord record = retrieveService(serviceIntent, resolvedType);

    // indicates that this Service is started explicitly by the application and
    // not
    // restarted by the system after it was killed due to resource shortage
    record.startRequested = true;

    // last onStartCommand did not asked to be called on restart due to resource
    // shortage
    record.callStart = false;

    // add a new StartItem to the Service's ServiceRecord
    record.pendingStarts.add(new ServiceRecord.StartItem(record, false, record.makeNextStartId(),
        serviceIntent, 0));

    // service has been started before by the application identified by
    // thread
    if (record.thread != null) {
      // send only the arguments to the Service
      // TODO if intent null generate intent, this will be onRestart
      sendServiceArgsLocked(record);
      return record.name;
    }

    // service is started for the first time
    record.thread = thread;
    boolean created = false;
    try {
      Log.i(TAG, "Scheduling start of Service \"" + record.stringName + "\"");
      thread.scheduleCreateService(record, record.serviceInfo, null);
      created = true;
    } finally {
      if (!created) {
        services.remove(record);
        scheduleServiceRestartLocked(record, false);
      }
    }

    // the Service creation was scheduled, so schedule sending the arguments
    sendServiceArgsLocked(record);
    return record.name;
  }

  private void scheduleServiceRestartLocked(ServiceRecord record, boolean b) {
    // TODO called when service start could not be scheduled
  }

  /**
   * Delivers the Service's arguments that is stored in the Intent used to start
   * the Service in its ServiceRecord r.
   * 
   * @param r
   */
  private void sendServiceArgsLocked(ServiceRecord r) {
    // if there are no pending start Service operations
    final int N = r.pendingStarts.size();
    if (N == 0) {
      return;
    }

    while (r.pendingStarts.size() > 0) {
      ServiceRecord.StartItem si = r.pendingStarts.remove(0);
      r.deliveredStarts.add(si);
      si.deliveryCount++;
      int flags = 0;
      if (si.deliveryCount > 0) {
        flags |= Service.START_FLAG_RETRY;
      }
      if (si.doneExecutingCount > 0) {
        flags |= Service.START_FLAG_REDELIVERY;
      }
      thread.scheduleServiceArgs(r, false, si.id, flags, r.intent);
    }

  }

  /**
   * Called from stopService() in contextImpl to stop a service.
   * 
   * First we lookup the service's ServiceRecord. We then check to see if the
   * service is bound. If not we stop the service, else we notify the user that
   * the service could not be destroyed as it is currently bound.
   * 
   * @param serviceIntent
   * @param resolvedType
   * @return 0 if the service could be stopped.
   */
  public int performStopService(Intent serviceIntent, String resolvedType) {

    // lookup service in service map
    ServiceRecord record = retrieveService(serviceIntent, resolvedType);

    Log.i(TAG, "Scheduling stop of Service \"" + record.stringName + "\"");

    if (record != null) {
      // service exists
      return bringDownServiceLocked(record);
    } else
      return -1;
  }

  private int bringDownServiceLocked(ServiceRecord record) {
    // reset start requested, only true when service was explicitly asked
    // to be started by application and not by system
    record.startRequested = false;
    // reset if the onStartCommand method must be called when the service
    // has to be restarted.
    record.callStart = false;

    // check that service is not current bound
    if (record.connections == null || record.connections.size() == 0) {
      // delete ServiceRecord
      services.remove(record.name);

      // reset config
      record.totalRestartCount = 0;
      record.isForeground = false;
      record.foregroundId = 0;

      // clear start entries.
      record.clearDeliveredStartsLocked();
      record.pendingStarts.clear();

      // if started, schedule stop on the application's main thread
      if (record.thread != null) {
        record.thread.scheduleStopService(record);
        return 1;
      }
    }
    return 0;
  }

  /**
   * Stop after a specific call to onStartCommand()
   * 
   * @param componentName
   * @param mToken
   * @param startId
   * @return
   */
  public boolean performStopServiceToken(ComponentName componentName, IBinder mToken, int startId) {
    // retrieve ServiceRecord of the Service
    ServiceRecord r = retrieveService(componentName, mToken);

    // if the service exists
    if (r != null) {

      // if we don't just want to stop the Service immediately
      if (startId >= 0) {

        // asked to only stop if done with all work. Note that
        // to avoid leaks, we will take this as dropping all
        // start items up to and including this one.

        ServiceRecord.StartItem si = r.findDeliveredStart(startId, false);
        if (si != null) {
          while (r.deliveredStarts.size() > 0) {
            ServiceRecord.StartItem cur = r.deliveredStarts.remove(0);
            if (cur == si) {
              break;
            }
          }
        }

        // more starts were scheduled after me
        if (r.getLastStartId() != startId) {
          return false;
        }

        if (r.deliveredStarts.size() > 0) {
          Slog.w(TAG,
              "stopServiceToken startId " + startId + " is last, but have " + r.deliveredStarts.size()
                  + " remaining args");
        }
      }
      int result = bringDownServiceLocked(r);
      return result == 1;
    }
    return false;
  }

  /**
   * 
   * @param token
   * @param serviceIntent
   * @param resolvedType
   * @param connection
   * @param flags
   * @return
   */
  public int performBindService(IBinder token, Intent serviceIntent, String resolvedType,
                                IServiceConnection connection, int flags) {

    // ServiceRecord record = retrieveService(serviceIntent, resolvedType);
    // ConnectionRecord connRecord = new ConnectionRecord(
    // (ActivityRecord) token, connection, flags, 0, serviceIntent,
    // "", false, record);
    //
    // Map<ConnectionRecord> conns = mServiceConnections.get(connection);
    // if (conns == null) {
    // conns = new ArrayList<ConnectionRecord>();
    // mServiceConnections.put(connection, conns);
    // }
    // conns.add(connRecord);
    //
    // if (record.connections == null) {
    // record.connections = new HashSet<ConnectionRecord>();
    // }
    // record.connections.add(connRecord);
    //
    // thread.scheduleBindService(record, serviceIntent, true);
    return -1;

  }

  /**
   * 
   * @param conn
   */
  public void performUnbindService(ServiceConnection conn) {
    // ArrayList<ConnectionRecord> conns = mServiceConnections.get(conn);
    // if (conns != null) {
    // for (ConnectionRecord cr : conns) {
    // thread.scheduleUnbindService(cr.service, cr.clientIntent);
    // // if no more bounds to this service and it is not started, we
    // // destroy it
    // cr.service.connections.remove(cr);
    // // if (cr.service.connections.size() <= 0 && cr.service.started
    // // == false) {
    // // thread.scheduleStopService(cr.service);
    // // services.remove(cr.service);
    // // }TODO
    // }
    // }
    // mServiceConnections.remove(conn);
  }

  /**
   * Called on handleBind service when a service is not rebindable
   * 
   * @param token
   * @param intent
   * @param binder
   */
  public void performPublishService(Object token, Intent intent, IBinder binder) {
    // TODO Auto-generated method stub

  }

  /**
   * Called from handleUnbindService:
   * 
   * <pre>
   * if (doRebind) {
   *   ActivityManagerNative.getDefault().unbindFinished(data.token, data.intent, doRebind);
   * } else {
   *   ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, 0, 0, 0);
   * }
   * </pre>
   * 
   * @param token
   * @param intent
   * @param doRebind
   */
  public void performUnbindFinished(Object token, Intent intent, boolean doRebind) {
    // TODO Auto-generated method stub

  }

  /**
   * Called each time a service start, stop serviceargs,bind unbind is called.
   * Type is important type 1
   * 
   * @param token
   * @param type
   * @param startId
   * @param res
   */
  public void performServiceDoneExecuting(IBinder token, int type, int startId, int res) {
    if (!(token instanceof ServiceRecord)) {
      throw new IllegalArgumentException("Invalid service token");
    }
    ServiceRecord r = (ServiceRecord) token;
    if (r != null) {
      if (r != token) {
        Slog.w(TAG, "Done executing service " + r.name + " with incorrect token: given " + token
            + ", expected " + r);
        return;
      }

      if (type == 1) {
        // This is a call from a service start... take care of
        // book-keeping.
        r.callStart = true;
        switch (res) {
        case Service.START_STICKY_COMPATIBILITY:
        case Service.START_STICKY: {
          // We are done with the associated start arguments.
          r.findDeliveredStart(startId, true);
          // Don't stop if killed.
          r.stopIfKilled = false;
          break;
        }
        case Service.START_NOT_STICKY: {
          // We are done with the associated start arguments.
          r.findDeliveredStart(startId, true);
          if (r.getLastStartId() == startId) {
            // There is no more work, and this service
            // doesn't want to hang around if killed.
            r.stopIfKilled = true;
          }
          break;
        }
        case Service.START_REDELIVER_INTENT: {
          // We'll keep this item until they explicitly
          // call stop for it, but keep track of the fact
          // that it was delivered.
          ServiceRecord.StartItem si = r.findDeliveredStart(startId, false);
          if (si != null) {
            si.deliveryCount = 0;
            si.doneExecutingCount++;
            // Don't stop if killed.
            r.stopIfKilled = true;
          }
          break;
        }
        case Service.START_TASK_REMOVED_COMPLETE: {
          // Special processing for onTaskRemoved(). Don't
          // impact normal onStartCommand() processing.
          r.findDeliveredStart(startId, true);
          break;
        }
        default:
          throw new IllegalArgumentException("Unknown service start result: " + res);
        }
        if (res == Service.START_STICKY_COMPATIBILITY) {
          r.callStart = false;
        }
      }
    } else {
      Slog.w(TAG, "Done executing unknown service from pid");
    }
  }

  public void performSetServiceForeground(ComponentName componentName, IBinder mToken, int i, Object object,
                                          boolean removeNotification) {
    // TODO Auto-generated method stub

  }

  // =========================================================
  // BROADCASTS
  // =========================================================

  /**
   * Used when debugging
   * 
   * @param r
   */
  private final void addBroadcastToHistoryLocked(BroadcastRecord r) {
    System.arraycopy(mBroadcastHistory, 0, mBroadcastHistory, 1, MAX_BROADCAST_HISTORY - 1);
    mBroadcastHistory[0] = r;
  }

  private final List getStickiesLocked(String action, IntentFilter filter, List cur) {
    final ArrayList<Intent> list = mStickyBroadcasts.get(action);
    if (list == null) {
      return cur;
    }
    int N = list.size();
    for (int i = 0; i < N; i++) {
      Intent intent = list.get(i);
      if (filter.match(null, intent, true, TAG) >= 0) { // TODO CONTENT RESOLVER
                                                        // MISSING
        if (cur == null) {
          cur = new ArrayList<Intent>();
        }
        cur.add(intent);
      }
    }
    return cur;
  }

  /**
   * Registers a new Broadcast receiver. Does not directly have reference to the
   * BR but has IBinder link to the IIntentReceiver stored for this BR in the
   * application side.
   * 
   * First we go and look up a list of all sticky Intents that match one of the
   * IntentFilter's Actions. We need to do this because we need to return a
   * sticky Intent in this case.
   * 
   * If the receiver parameter passed through is null, we know the app only
   * wants the matching sticky intent so we return the first sticky intent.
   * 
   * Else we lookup the list of filters that is currently registered for this
   * receiver. And we add the new filter to this list.
   * 
   * Lastly we deliver all matching sticky Intents to the new BroadcastReceiver
   * and return the first matching sticky Intent.
   * 
   * 
   * - add new filter in ReceiverList for the BR (when is this used? - when BR
   * dies - notify these? if BR is unregistered) - add Broadcast filter with
   * pointer to the BR to Intent Resolution (Get BR to deliver intent to)
   * 
   * @param caller
   * @param callerPackage
   * @param receiver
   * @param filter
   * @param permission
   * @return
   * @throws RemoteException
   */
  public Intent performRegisterReceiver(ApplicationThread caller, String callerPackage,
                                        IIntentReceiver receiver, IntentFilter filter, String permission)
      throws RemoteException {

    if (DEBUG_BROADCAST)
      Log.i(TAG, "Registering " + receiver + " with IntentFilter [ action=" + filter.getAction(0) + "]");

    // changes the state of the ActivityManager - have to synchronize
    synchronized (this) {

      // Iterate through all the IntentFilter's Actions and store all matching
      // sticky Intents in all Sticky
      List allSticky = null;

      Iterator actions = filter.actionsIterator();
      if (actions != null) {
        while (actions.hasNext()) {
          String action = (String) actions.next();
          allSticky = getStickiesLocked(action, filter, allSticky);
        }
      } else {
        allSticky = getStickiesLocked(null, filter, allSticky);
      }

      // The first sticky in the list is returned directly back to the client.
      Intent sticky = allSticky != null ? (Intent) allSticky.get(0) : null;

      // In this case no BroadcastReceiver was registered (receiver == null), we
      // only used
      // "register" to get the sticky intent matching the IntentFilter
      if (receiver == null) {
        if (DEBUG_BROADCAST)
          Log.i(TAG, "Receiver == null so returning matching sticky Intent " + sticky);
        return (Intent) sticky.clone();
      }

      // Get the list of all currently BroadcastFilters(IntentFilters) that where dynamically
      // registered for this BroadcastReceiver (receiver)
      ReceiverList rl = (ReceiverList) mRegisteredReceivers.get(receiver.asBinder());

      // If the list is empty no Filters have been registered for the BR, make
      // new entry in the registeredReceiversList
      if (rl == null) {
        rl = new ReceiverList(receiver);
        mRegisteredReceivers.put(receiver, rl);
      }

      // Make new BroadcastFilter record for this registration
      BroadcastFilter bf = new BroadcastFilter(filter, rl, callerPackage, permission);
      rl.add(bf);

      // Register the filter for intent resolution
      mReceiverResolver.addFilter(bf);

      // We still need to deliver all stickies that match this new filter.
      if (allSticky != null) {

        // Make list of receivers that the sticky intent must be delivered to
        // (only the current Receiver)
        ArrayList<BroadcastFilter> receivers = new ArrayList<BroadcastFilter>();
        receivers.add(bf);

        int N = allSticky.size();
        Intent stickyIntent = null;
        for (int i = 0; i < N; i++) {
          // for each sticky deliver it to the BroadcastReceiver
          stickyIntent = (Intent) allSticky.get(i);

          BroadcastRecord r = new BroadcastRecord(stickyIntent, null, receivers, null, 0, null, null, false,
              true, true);
          mParallelBroadcasts.add(r);

        }
        if (mParallelBroadcasts.size() != 0) {

          if (DEBUG_BROADCAST)
            Log.i(TAG, "Delivering matching sticky Intents: # = " + N + " to " + receiver);

          processNextBroadcast();
        }
      }

      if (DEBUG_BROADCAST)
        Log.i(TAG, "Returning " + sticky + " for registered receiver " + receiver);

      // return the first matching sticky or null if no stickies matched
      return (sticky == null) ? null : (Intent) sticky.clone();
    }
  }

  /**
   * Called when a new Intent has to be broadcast to the system.
   * 
   * @param caller
   * @param intent
   * @param resolvedType
   * @param resultTo
   * @param resultCode
   * @param resultData
   * @param map
   * @param requiredPermission
   * @param ordered
   * @param sticky
   * @return
   * @throws RemoteException
   */
  public final int performBroadcastIntent(ApplicationThread caller, Intent intent, String resolvedType,
                                          IIntentReceiver resultTo, int resultCode, String resultData,
                                          Bundle map, String requiredPermission, boolean ordered,
                                          boolean sticky) throws RemoteException {

    // make a copy of the intent to ensure that if it is changed we have a copy
    // of the original
    intent = new Intent(intent);

    // Logging
    if (DEBUG_BROADCAST)
      Log.i(TAG, "Broadcast " + intent + " ordered=" + ordered + " sticky=" + sticky + " permission="
          + requiredPermission);

    if ((resultTo != null) && !ordered) {
      Log.w(TAG, "Broadcast " + intent + " not ordered but result callback requested!");
    }

    /*
     * Prevent non-system code (defined here to be non-persistent processes)
     * from sending protected broadcasts.
     */
    // TODO check that is sent from the script

    // ///////////////////////////////////////
    //                                      //
    // Add to the sticky list if requested. //
    //                                      //
    // ///////////////////////////////////////

    if (sticky) {

      // TODO check that this app has BROADCAST_STICKY permission
      // if (checkPermission(android.Manifest.permission.BROADCAST_STICKY,
      // callingPid, callingUid)!= PackageManager.PERMISSION_GRANTED) {
      // String msg =
      // "Permission Denial: broadcastIntent() requesting a sticky broadcast from pid="
      // + callingPid + ", uid=" + callingUid + " requires " +
      // android.Manifest.permission.BROADCAST_STICKY;
      // Slog.w(TAG, msg);
      // throw new SecurityException(msg);}

      if (requiredPermission != null) {
        Log.w(TAG, "Can't broadcast sticky intent " + intent + " and enforce permission "
            + requiredPermission);
        return BROADCAST_STICKY_CANT_HAVE_PERMISSION;
      }

      if (intent.getComponent() != null) {
        throw new SecurityException("Sticky broadcasts can't target a specific component");
      }

      // get all sticky intents matching this intent's action
      ArrayList<Intent> list = mStickyBroadcasts.get(intent.getAction());

      // if list is null we need to add a new list
      if (list == null) {
        list = new ArrayList<Intent>();
        mStickyBroadcasts.put(intent.getAction(), list);
      }

      // search through list of intent for matches, replace if found
      int N = list.size();
      int i;
      for (i = 0; i < N; i++) {
        if (intent.filterEquals(list.get(i))) {
          // This sticky already exists, replace it.
          list.set(i, new Intent(intent));
          break;
        }
      }

      // if not found add to end of list
      if (i >= N) {
        list.add(new Intent(intent));
      }

    }

    //////////////////////////////////////////////////////
    //                                                  //
    // Figure out who all will receive this broadcast.  //
    //                                                  //
    //////////////////////////////////////////////////////

    List receivers = null; // list of receivers to deliver contains ResolveInfo an BroadcastFilters
    List<BroadcastFilter> registeredReceivers = null;

    if (intent.getComponent() != null) {
      // Broadcast is going to one specific receiver class...
      ActivityInfo ai = null;
      try {
        ai = AppGlobals.getPackageManager().getReceiverInfo(intent.getComponent(), 0x00000400);
      } catch (NameNotFoundException e) {
        e.printStackTrace();
      }

      if (ai != null) {
        receivers = new ArrayList();
        ResolveInfo ri = new ResolveInfo();
        ri.activityInfo = ai;
        receivers.add(ri);
      }
    } else {
      // Need to resolve the intent to interested receivers...
      if ((intent.getFlags() & Intent.FLAG_RECEIVER_REGISTERED_ONLY) == 0) {
        receivers = AppGlobals.getPackageManager().queryBroadcastReceivers(intent, 0x00000400);
      }
      registeredReceivers = mReceiverResolver.queryIntent(intent, resolvedType, false);
      if (DEBUG_BROADCAST)
        Log.i(TAG, "Found BR to deliver to " + receivers + " " + registeredReceivers);
    }

    // Used by Sticky intents
    final boolean replacePending = (intent.getFlags() & Intent.FLAG_RECEIVER_REPLACE_PENDING) != 0;

    if (DEBUG_BROADCAST)
      Log.i(TAG, "Enqueue broadcast: " + intent.getAction() + " replacePending=" + replacePending);

    int NR = registeredReceivers != null ? registeredReceivers.size() : 0;

    ////////////////////////////////////////////////////////
    //                                                    //
    // Create a BroadcastRecord for !ordered intents.     //
    // Add the record to mParallelBroadcasts list to be   //
    // delivered.                                         //
    //                                                    //
    ////////////////////////////////////////////////////////

    if (!ordered && NR > 0) {
      // If we are not serializing this broadcast, then send the
      // registered receivers separately so they don't wait for the
      // components to be launched.
      BroadcastRecord r = new BroadcastRecord(intent, requiredPermission, registeredReceivers, resultTo,
          resultCode, resultData, map, ordered, sticky, false);
      if (DEBUG_BROADCAST)
        Log.i(TAG, "Enqueueing parallel broadcast " + r + ": prev had " + mParallelBroadcasts.size());
      boolean replaced = false;
      if (replacePending) { // Used by  Sticky Intents
        for (int i = mParallelBroadcasts.size() - 1; i >= 0; i--) {
          if (intent.filterEquals(mParallelBroadcasts.get(i).intent)) {
            if (DEBUG_BROADCAST)
              Log.i(TAG, "***** DROPPING PARALLEL: " + intent);
            mParallelBroadcasts.set(i, r);
            replaced = true;
            break;
          }
        }
      }
      if (!replaced) {
        mParallelBroadcasts.add(r);
      }
      registeredReceivers = null;
      NR = 0;
    }

    ///////////////////////////////////////////////////////
    // Handle all non-parallel intents:                  //
    // - Intent for specific component                   //
    // - Intent for !ordered Manifest registered BR      //
    // - Intent for ordered BR                           //
    // - Intent for ordered manifest registered BR       //
    ///////////////////////////////////////////////////////

    // Merge receivers and registeredReceivers into one list(receivers) to be delivered sequentially
    int ir = 0;
    if (receivers != null) {
      int NT = receivers != null ? receivers.size() : 0;
      int it = 0;
      ResolveInfo curt = null;
      BroadcastFilter curr = null;
      while (it < NT && ir < NR) {
        if (curt == null) {
          curt = (ResolveInfo) receivers.get(it);
        }
        if (curr == null) {
          curr = registeredReceivers.get(ir);
        }
        if (curr.getPriority() >= curt.priority) {
          // Insert this broadcast record into the final list.
          receivers.add(it, curr);
          ir++;
          curr = null;
          it++;
          NT++;
        } else {
          // Skip to the next ResolveInfo in the final list.
          it++;
          curt = null;
        }
      }
    }

    while (ir < NR) {
      if (receivers == null) {
        receivers = new ArrayList();
      }
      receivers.add(registeredReceivers.get(ir));
      ir++;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    // Schedule and deliver all non-parallel intents:    //
    // Add the record to mOrderedBroadcasts list to be   //
    // delivered.                                        //
    //                                                   //
    ///////////////////////////////////////////////////////

    if ((receivers != null && receivers.size() > 0) || resultTo != null) {
      BroadcastRecord r = new BroadcastRecord(intent, requiredPermission, receivers, resultTo, resultCode,
          resultData, map, ordered, sticky, false);
      if (DEBUG_BROADCAST) {
        Log.i(TAG, "Enqueueing ordered broadcast " + r + ": prev had " + mOrderedBroadcasts.size());
        int seq = r.intent.getIntExtra("seq", -1);
        Slog.i(TAG, "Broadcast " + r.intent.getAction() + " seq=" + seq + " enqueued.");
      }
      boolean replaced = false;
      if (replacePending) { // for sticky Intents
        for (int i = mOrderedBroadcasts.size() - 1; i > 0; i--) {
          if (intent.filterEquals(mOrderedBroadcasts.get(i).intent)) {
            if (DEBUG_BROADCAST)
              Log.i(TAG, "***** DROPPING ORDERED: " + intent);
            mOrderedBroadcasts.set(i, r);
            replaced = true;
            break;
          }
        }
      }
      if (!replaced) {
        mOrderedBroadcasts.add(r);
      }
    }
    processNextBroadcast();
    return BROADCAST_SUCCESS;
  }

  private void deliverToRegisteredReceiverLocked(BroadcastRecord r, BroadcastFilter filter, boolean ordered) {
    // If this is not being sent as an ordered broadcast, then we
    // don't want to touch the fields that keep track of the current
    // state of ordered broadcasts.
    if (ordered) {
      r.receiver = filter.receiverList.receiver.asBinder();
      r.curFilter = filter;
      filter.receiverList.curBroadcast = r;
      r.state = BroadcastRecord.CALL_IN_RECEIVE;
      if (DEBUG_BROADCAST) {
        int seq = r.intent.getIntExtra("seq", -1);
        Log.i(TAG, "Delivering to " + filter + " (seq=" + seq + "): " + r);
      }
    }
    thread.scheduleRegisteredReceiver(filter.receiverList.receiver, new Intent(r.intent), r.resultCode,
        r.resultData, r.resultExtras, r.ordered, r.initialSticky);

    if (ordered) {
      r.state = BroadcastRecord.CALL_DONE_RECEIVE;
    }
  }

  /**
   * Deliver any non-serialized (not ordered) broadcasts to the corresponding
   * Broadcast Receivers.
   */
  private final void deliverParallelBroadcasts() {
    BroadcastRecord r;

    while (mParallelBroadcasts.size() > 0) {
      // While we have not delivered all parallel broadcast Intents

      // Get the first broadcast in the queue
      r = mParallelBroadcasts.remove(0);

      // Get the number of receivers to deliver the broadcast to
      final int numReceivers = r.receivers.size();

      if (DEBUG_BROADCAST)
        Log.i(TAG, "Processing parallel broadcast " + r);

      // Deliver the Intent that is broadcast to each receiver 
      for (int i = 0; i < numReceivers; i++) {

        Object target = r.receivers.get(i);

        if (DEBUG_BROADCAST)
          Log.i(TAG, "Delivering non-ordered to registered " + target + ": " + r);

        // Deliver BroadcastRecord r to BroadcastFilter target
        deliverToRegisteredReceiverLocked(r, (BroadcastFilter) target, false);

      }

      //debug
      addBroadcastToHistoryLocked(r);

      if (DEBUG_BROADCAST)
        Log.i(TAG, "Done with parallel broadcast " + r);
    }

  }

  /**
   * Runs through the list of BroadcastRecords in the mOrderedBroadcasts queue.
   * Searches for the next BroadcastRecord to process. It removes
   * BroadcastRecords that have delivered their BroarcastIntent to each of their
   * receivers and delivers their result if requested.
   * 
   * @return This will either be null if the current receiver has not jet
   *         finished or if all ordered receivers have finished or it will
   *         contain mOrderedBroadcasts[0] - the BroadcastRecord next to
   *         be processed.
   */
  private final BroadcastRecord getNextOrderedBroadcast() {
    BroadcastRecord r = null;
    int numReceivers = 0;
    if (DEBUG_BROADCAST)
      Log.i(TAG, "Get next Ordered Broadcast " + r);

    // Get the first idle BroadcastRecord in the queue
    while (r == null && mOrderedBroadcasts.size() != 0) {
      r = mOrderedBroadcasts.get(0);
      if (DEBUG_BROADCAST)
        Log.i(TAG, "Get next Ordered Broadcast " + r);

      if (r.state != BroadcastRecord.IDLE) {
        if (DEBUG_BROADCAST)
          Log.i(TAG, "processNextBroadcast() called when not idle (state=" + r.state + ")");
        return null;
      }

      // Get the number of receivers to deliver the broadcast to
      numReceivers = (r.receivers != null) ? r.receivers.size() : 0;
      if (DEBUG_BROADCAST) {
        Log.i(TAG, "Get Broadcast  receivers" + r.receivers);
        Log.i(TAG, "Get Broadcast next receiver: " + r.nextReceiver + " num receivers:" + numReceivers);
      }
      // If this receiver has finished delivering to all its receivers, deliver 
      // its result and remove it from the queue
      if (numReceivers == 0 || r.nextReceiver >= numReceivers || r.resultAbort) {

        // No more receivers for this broadcast! Send the final result if requested...
        if (r.resultTo != null) {
          if (DEBUG_BROADCAST)
            Log.i(TAG, "Recievers resulto != null");

          if (DEBUG_BROADCAST) {
            int seq = r.intent.getIntExtra("seq", -1);
            Log.i(TAG, "Finishing broadcast " + r.intent.getAction() + " seq=" + seq);
          }
          //deliver result
          thread.scheduleRegisteredReceiver(r.resultTo, new Intent(r.intent), r.resultCode, r.resultData,
              r.resultExtras, false, false);

          // Set this to null so that the reference (local and remote)
          // isnt kept in the mBroadcastHistory.
          r.resultTo = null;

        }

        if (DEBUG_BROADCAST)
          Log.i(TAG, "Finished with ordered broadcast " + r);

        // ... and on to the next...
        mOrderedBroadcasts.remove(0);
        r = null;
        continue;
      }
    }

    return r;
  }

  /**
   * Delivers all broadcasts currently in the queues
   */
  private final void processNextBroadcast() {
    Log.i(TAG, "Processesing Next Broadcast " + mParallelBroadcasts.size() + " broadcasts, "
        + mOrderedBroadcasts.size() + " ordered broadcasts");

    // Deliver all the parallel Broadcast Intents
    deliverParallelBroadcasts();

    // Get the next Ordered Broadcast to process
    BroadcastRecord r = getNextOrderedBroadcast();
    if (r == null)
      return;

    // Get the next receiver to deliver the Intent to.
    int recIdx = r.nextReceiver++;
    Object nextReceiver = r.receivers.get(recIdx);

    // Simple case: this is a registered receiver who gets a direct call.
    if (nextReceiver instanceof BroadcastFilter) {

      BroadcastFilter filter = (BroadcastFilter) nextReceiver;

      if (DEBUG_BROADCAST)
        Log.i(TAG, "Delivering ordered BR to registered " + filter + ": " + r);

      deliverToRegisteredReceiverLocked(r, filter, r.ordered);

      if (r.receiver == null || !r.ordered) {
        // The receiver has already finished, so schedule to
        // process the next one.

        if (DEBUG_BROADCAST)
          Log.i(TAG, "Quick finishing: ordered=" + r.ordered + " receiver=" + r.receiver);

        r.state = BroadcastRecord.IDLE;

        processNextBroadcast();
      }
    } else {
      // Hard case: need to instantiate the receiver, possibly
      // starting its application process to host it.

      ResolveInfo info = (ResolveInfo) nextReceiver;

      r.state = BroadcastRecord.APP_RECEIVE;
      r.curComponent = new ComponentName(info.activityInfo.applicationInfo.packageName,
          info.activityInfo.name);
      r.curReceiver = info.activityInfo;

      if (DEBUG_BROADCAST)
        Log.i(TAG, "Process cur broadcast " + r);

      // Tell the application to launch this receiver.
      r.intent.setComponent(r.curComponent);
      boolean started = false;
      try {
        if (DEBUG_BROADCAST)
          Log.i(TAG, "Delivering to component " + r.curComponent + ": " + r);
        thread.scheduleReceiver(new Intent(r.intent), r.curReceiver, null, r.resultCode, r.resultData,
            r.resultExtras, r.ordered);

        if (DEBUG_BROADCAST)
          Log.i(TAG, "Process cur broadcast " + r + " DELIVERED to app ");
        started = true;
      } finally {
        if (!started) {
          if (DEBUG_BROADCAST)
            Log.i(TAG, "Process cur broadcast " + r + ": NOT STARTED!");
          r.receiver = null;

        }
      }
    }
  }

  public void performUnbroadcastIntent(ApplicationThread applicationThread, Intent intent)
      throws RemoteException {
  }

  public void performFinishReceiver(IBinder who, int resultCode, String resultData, Bundle resultExtras,
                                    boolean resultAbort) {
    if (DEBUG_BROADCAST)
      Slog.v(TAG, "Finish receiver: " + who);

    // Refuse possible leaked file descriptors
    if (resultExtras != null && resultExtras.hasFileDescriptors()) {
      throw new IllegalArgumentException("File descriptors passed in Bundle");
    }

    boolean doNext;

    if (mOrderedBroadcasts.size() == 0) {
      if (DEBUG_BROADCAST) {
        Slog.w(TAG, "finishReceiver called but no pending broadcasts");
      }
      return;
    }
    BroadcastRecord r = mOrderedBroadcasts.get(0);
    if (r.receiver == null) {
      r.state = BroadcastRecord.IDLE;
      r.curFilter = null;
      r.curComponent = null;
      r.curReceiver = null;
      //TODO mPendingBroadcast = null;

      r.resultCode = resultCode;
      r.resultData = resultData;
      r.resultExtras = resultExtras;
      r.resultAbort = resultAbort;
      processNextBroadcast();
      return;
    }
    if (r.receiver != who) {
      Slog.w(TAG, "finishReceiver called but active receiver is different");
      return;
    }
    //
    int state = r.state;
    r.state = BroadcastRecord.IDLE;
    if (state == BroadcastRecord.IDLE) {
      if (DEBUG_BROADCAST) {
        Slog.w(TAG, "finishReceiver called but state is IDLE");
      }
    }
    r.receiver = null;
    r.intent.setComponent(null);
    if (r.curFilter != null) {
      r.curFilter.receiverList.curBroadcast = null;
    }
    r.curFilter = null;
    r.curComponent = null;
    r.curReceiver = null;
    //TODO mPendingBroadcast = null;

    r.resultCode = resultCode;
    r.resultData = resultData;
    r.resultExtras = resultExtras;
    r.resultAbort = resultAbort;

    // We will process the next receiver right now if this is finishing
    // an app receiver (which is always asynchronous) or after we have
    // come back from calling a receiver.
    // if (state == BroadcastRecord.APP_RECEIVE || state == BroadcastRecord.CALL_DONE_RECEIVE)
    processNextBroadcast();

  }

  public void performUnregisterReceiver(IIntentReceiver receiver) throws RemoteException {
    if (DEBUG_BROADCAST)
      Slog.v(TAG, "Unregister receiver: " + receiver);

    boolean doNext = false;

    synchronized (this) {
      ReceiverList rl = (ReceiverList) mRegisteredReceivers.get(receiver.asBinder());
      if (rl != null) {
        if (rl.curBroadcast != null) {
          BroadcastRecord r = rl.curBroadcast;
          performFinishReceiver(receiver.asBinder(), r.resultCode, r.resultData, r.resultExtras,
              r.resultAbort);
        }
        removeReceiverLocked(rl);
      }
    }
    if (!doNext) {
      return;
    }
    processNextBroadcast();
  }

  void removeReceiverLocked(ReceiverList rl) {
    mRegisteredReceivers.remove(rl.receiver.asBinder());
    int N = rl.size();
    for (int i = 0; i < N; i++) {
      mReceiverResolver.removeFilter(rl.get(i));
    }
  }
}