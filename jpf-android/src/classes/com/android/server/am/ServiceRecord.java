package com.android.server.am;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.ActivityThread.ApplicationThread;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;

class ServiceRecord extends Binder {
  ApplicationThread thread; //set if this service has been created
  final ComponentName name; // service component.
  final String shortName; // name.flattenToShortString().
  final Intent intent; // original intent used to find service.
  final ServiceInfo serviceInfo; // all information about the service.
  final ApplicationInfo appInfo; // information about service's app.
  final String packageName; // the package implementing intent's component
  final String permission;// permission needed to access service
  final boolean exported; // from ServiceInfo.exported

  boolean isForeground; // is service currently in foreground mode?
  int foregroundId; // Notification ID of last foreground req.

  String stringName; // caching of toString
  boolean startRequested; // someone explicitly called start?
  boolean stopIfKilled; // last onStart() said to stop if service killed?
  boolean callStart; // last onStart() has asked to alway be called on restart.
  int executeNesting; // number of outstanding operations keeping foreground.
  int crashCount; // number of times proc has crashed with service running
  int totalRestartCount; // number of times we have had to restart.
  int restartCount; // number of restarts performed in a row.

  HashSet<ConnectionRecord> connections;
  private int lastStartId; // identifier of most recent start request.

  static class StartItem {
    final ServiceRecord sr;
    final boolean taskRemoved;
    final int id;
    final Intent intent;
    final int targetPermissionUid;
    int deliveryCount;
    int doneExecutingCount;
//    UriPermissionOwner uriPermissions;

    String stringName; // caching of toString

    StartItem(ServiceRecord _sr, boolean _taskRemoved, int _id, Intent _intent, int _targetPermissionUid) {
      sr = _sr;
      taskRemoved = _taskRemoved;
      id = _id;
      intent = _intent;
      targetPermissionUid = _targetPermissionUid;
    }

    // UriPermissionOwner getUriPermissionsLocked() {
    // if (uriPermissions == null) {
    // uriPermissions = new UriPermissionOwner(sr.ams, this);
    // }
    // return uriPermissions;
    // }
    //
    // void removeUriPermissionsLocked() {
    // if (uriPermissions != null) {
    // uriPermissions.removeUriPermissionsLocked();
    // uriPermissions = null;
    // }
    // }

    public String toString() {
      if (stringName != null) {
        return stringName;
      }
      StringBuilder sb = new StringBuilder(128);
      sb.append("ServiceRecord{").append(Integer.toHexString(System.identityHashCode(sr))).append(' ')
          .append(sr.shortName).append(" StartItem ")
          .append(Integer.toHexString(System.identityHashCode(this))).append(" id=").append(id).append('}');
      return stringName = sb.toString();
    }
  }

  final ArrayList<StartItem> deliveredStarts = new ArrayList<StartItem>();
  // start() arguments which been delivered.
  final ArrayList<StartItem> pendingStarts = new ArrayList<StartItem>();

  // start() arguments that haven't yet been delivered.

  ServiceRecord(ComponentName name, Intent intent, ServiceInfo sInfo) {
    this.name = name;
    shortName = name.flattenToShortString();
    this.intent = intent;
    serviceInfo = sInfo;
    appInfo = sInfo.applicationInfo;
    packageName = sInfo.applicationInfo.packageName;
    permission = sInfo.permission;
    exported = sInfo.exported;

  }

  public void resetRestartCounter() {
    restartCount = 0;
  }

  public StartItem findDeliveredStart(int id, boolean remove) {
    final int N = deliveredStarts.size();
    for (int i = 0; i < N; i++) {
      StartItem si = deliveredStarts.get(i);
      if (si.id == id) {
        if (remove)
          deliveredStarts.remove(i);
        return si;
      }
    }

    return null;
  }

  public int getLastStartId() {
    return lastStartId;
  }

  public int makeNextStartId() {
    lastStartId++;
    if (lastStartId < 1) {
      lastStartId = 1;
    }
    return lastStartId;
  }

  public void clearDeliveredStartsLocked() {
//    for (int i = deliveredStarts.size() - 1; i >= 0; i--) {
//      deliveredStarts.get(i).removeUriPermissionsLocked();
//    }
    deliveredStarts.clear();
  }

  public String toString() {
    if (stringName != null) {
      return stringName;
    }
    StringBuilder sb = new StringBuilder(128);
    sb.append("ServiceRecord{").append(Integer.toHexString(System.identityHashCode(this))).append(' ')
        .append(shortName).append('}');
    return stringName = sb.toString();
  }
}