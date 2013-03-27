package com.android.server.am;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Binder;

class ActivityRecord extends Binder {

  // Activity activity;
  Intent intent; // the original intent that generated us
  ComponentName realActivity; // the intent component, or target of an alias.
  ActivityRecord resultTo; // who started this entry, so will get our reply
  String resultWho; // additional identifier for use by resultTo.
  int requestCode; // code given by requester (resultTo)
  ArrayList results; // pending ActivityResult objs we have received
  ActivityInfo info; // all about me
  String stringName; // for caching of toString().

  public ActivityRecord(Intent intent, ComponentName realActivity, ActivityRecord resultTo,
      String resultWho, int requestCode, ActivityInfo info, String stringName) {
    super();
    this.intent = intent;
    this.realActivity = realActivity;
    this.resultTo = resultTo;
    this.resultWho = resultWho;
    this.requestCode = requestCode;
    this.info = info;
    this.stringName = stringName;

  }

}