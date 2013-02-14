package android.app;

import java.util.HashMap;
import java.util.Map;

import android.app.ActivityThread.ApplicationThread;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Slog;

public class ActivityManagerProxy {

  PackageInfo packageInfo; // this must be parsed form

  private class ServiceRecord {
  }

  Map<ComponentName, ServiceRecord> services = new HashMap<ComponentName, ServiceRecord>();

  public native void startActivity(Intent intent, int requestCode);

  public native void finishActivity(int resultCode, Intent resultData);

  public synchronized ComponentName startService(ApplicationThread caller, Intent service, String resolvedType)
      throws RemoteException {

    // resolve service from intent

    // lookup service in service map
    // if found
    // real start service(serviceRecord)
    // else
    // new service record
    // real start service(serviceRecord)
    return servicerecord.componentname;

  }

  private ServiceRecord retrieveServiceLocked(Intent service, String resolvedType) {
    ServiceRecord r = null;
    if (service.getComponent() != null) {
      r = services.get(service.getComponent());
    }
    return r;
  }

  private final void realStartServiceLocked(ServiceRecord r) throws RemoteException {
    if (app.thread == null) {
      throw new RemoteException();
    }
    if (r.app != null && r.app.thread != null) {
      sendServiceArgsLocked(r, false); // this means that service was alreay created so just send args
      return true;
    }
    // r.app = app;
    // r.restartTime = r.lastActivity = SystemClock.uptimeMillis();

    services.add(r.getCo, r); // this is a new servce and its record has to be added to the services map

    // bumpServiceExecutingLocked(r, "create");
    // updateLruProcessLocked(app, true, true);

    boolean created = false;
    try {
      app.thread.scheduleCreateService(r, r.serviceInfo,
          compatibilityInfoForPackageLocked(r.serviceInfo.applicationInfo));
      r.postNotification();
      created = true;
    } finally {
      if (!created) {
        app.services.remove(r);
        scheduleServiceRestartLocked(r, false);
      }
    }

    requestServiceBindingsLocked(r);

    // If the service is in the started state, and there are no
    // pending arguments, then fake up one so its onStartCommand() will
    // be called.
    if (r.startRequested && r.callStart && r.pendingStarts.size() == 0) {
      r.pendingStarts.add(new ServiceRecord.StartItem(r, false, r.makeNextStartId(), null, -1));
    }

    sendServiceArgsLocked(r, true);
  }

  public synchronized int stopService(ApplicationThread caller, Intent service, String resolvedType)
      throws RemoteException {
    return 0;

  }

  public synchronized int bindService(ApplicationThread caller, IBinder token, Intent service,
                                      String resolvedType, ServiceConnection connection, int flags)
      throws RemoteException {
    return flags;

  }

  public synchronized void unbindService(ServiceConnection conn) throws RemoteException {

  }

  private final void sendServiceArgsLocked(ServiceRecord r, boolean oomAdjusted) {
    final int N = r.pendingStarts.size();
    if (N == 0) {
      return;
    }

    while (r.pendingStarts.size() > 0) {
      try {
        ServiceRecord.StartItem si = r.pendingStarts.remove(0);
        if (DEBUG_SERVICE)
          Slog.v(TAG, "Sending arguments to: " + r + " " + r.intent + " args=" + si.intent);
        if (si.intent == null && N > 1) {
          // If somehow we got a dummy null intent in the middle,
          // then skip it. DO NOT skip a null intent when it is
          // the only one in the list -- this is to support the
          // onStartCommand(null) case.
          continue;
        }
        si.deliveredTime = SystemClock.uptimeMillis();
        r.deliveredStarts.add(si);
        si.deliveryCount++;
        if (si.targetPermissionUid >= 0 && si.intent != null) {
          grantUriPermissionUncheckedFromIntentLocked(si.targetPermissionUid, r.packageName, si.intent,
              si.getUriPermissionsLocked());
        }
        bumpServiceExecutingLocked(r, "start");
        if (!oomAdjusted) {
          oomAdjusted = true;
          updateOomAdjLocked(r.app);
        }
        int flags = 0;
        if (si.deliveryCount > 0) {
          flags |= Service.START_FLAG_RETRY;
        }
        if (si.doneExecutingCount > 0) {
          flags |= Service.START_FLAG_REDELIVERY;
        }
        r.app.thread.scheduleServiceArgs(r, si.taskRemoved, si.id, flags, si.intent);
      } catch (RemoteException e) {
        // Remote process gone... we'll let the normal cleanup take
        // care of this.
        if (DEBUG_SERVICE)
          Slog.v(TAG, "Crashed while scheduling start: " + r);
        break;
      } catch (Exception e) {
        Slog.w(TAG, "Unexpected exception", e);
        break;
      }
    }
  }

  public void unbindFinished(Object token, Intent intent, boolean doRebind) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void serviceDoneExecuting(Object token, int i, int j, int k) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void publishService(Object token, Intent intent, IBinder binder) throws RemoteException {
    // TODO Auto-generated method stub

  }
}
