package com.android.server.am;

import java.util.HashSet;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;

class ServiceRecord extends Binder {

  final ComponentName name; // service component.
  final String shortName; // name.flattenToShortString().
  final Intent intent; // original intent used to find service.
  final ServiceInfo serviceInfo; // all information about the service.
  final ApplicationInfo appInfo; // information about service's app.
  final String packageName; // the package implementing intent's component
  String stringName; // caching of toString
  boolean started;
  HashSet<ConnectionRecord> conns;
  private int lastStartId; // identifier of most recent start request.

  ServiceRecord(ComponentName name, Intent intent, ServiceInfo sInfo) {
    this.name = name;
    shortName = name.flattenToShortString();
    this.intent = intent;
    serviceInfo = sInfo;
    appInfo = sInfo.applicationInfo;
    packageName = sInfo.applicationInfo.packageName;
    lastStartId = 0;

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

}