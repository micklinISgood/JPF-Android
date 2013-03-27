package com.android.server.am;

import java.util.ArrayList;

import android.app.IIntentReceiver;
import android.content.Intent;

class ReceiverList extends ArrayList<BroadcastFilter> {

  String stringName; // for caching of toString().
  String permission;
  boolean sticky;
  Intent stickyIntent;
  IIntentReceiver receiver;

  ReceiverList(IIntentReceiver _receiver) {
    receiver = _receiver;
  }

}