package com.android.server.am;

import android.content.Intent;
import android.content.ServiceConnection;

class ConnectionRecord {
  final ActivityRecord activity; // If non-null, the owning activity.
  final ServiceConnection conn; // The client connection.
  final int flags; // Binding options.
  final int clientLabel; // String resource labeling this client.
  final Intent clientIntent; // How to launch the client.
  String stringName; // Caching of toString.
  boolean serviceDead; // Well is it?
  /** Binder published from service. */
  ServiceRecord service;

  public ConnectionRecord(ActivityRecord activity, ServiceConnection conn, int flags, int clientLabel,
      Intent clientIntent, String stringName, boolean serviceDead, ServiceRecord service) {
    super();
    this.activity = activity;
    this.conn = conn;
    this.flags = flags;
    this.clientLabel = clientLabel;
    this.clientIntent = clientIntent;
    this.stringName = stringName;
    this.serviceDead = serviceDead;
    this.service = service;
  }

}
