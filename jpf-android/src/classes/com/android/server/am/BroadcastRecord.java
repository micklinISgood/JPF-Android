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

package com.android.server.am;

import java.io.PrintWriter;
import java.util.List;

import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.PrintWriterPrinter;

/**
 * Record of an active Intent Broadcast. Stores the original Intent to deliver
 * and a list of recipients (receivers).
 * 
 * Used to deliver the following broadcasts:
 * - initial sticky intent to a new registered receiver 
 * - an Intent to a list of matching unordered receivers 
 * - an Intent to a list of matching ordered receivers
 * 
 */
class BroadcastRecord extends Binder {
  final Intent intent;    // the original intent that generated us
  final String requiredPermission; // a permission the caller has required
  final List receivers;   // contains BroadcastFilter and ResolveInfo
  

  IBinder receiver;       // who is currently running, null if none.

  // The following are set when we are calling a receiver (one that
  // was found in our list of registered receivers).
  BroadcastFilter curFilter;

  // The following are set only when we are launching a receiver (one that was
  // found by querying the package manager).
  ComponentName curComponent; // the receiver class that is currently running.
  ActivityInfo curReceiver;   // info about the receiver that is currently
  // running.

  // ///////////////////////////////////////////////////
  // Used by Sticky Intents //
  // ///////////////////////////////////////////////////

  final boolean sticky;   // originated from existing sticky data?
  final boolean initialSticky; // initial broadcast from registerReciever to
                               // sticky?

  // ///////////////////////////////////////////////////
  // Used by Ordered Intents //
  // ///////////////////////////////////////////////////
  final boolean ordered;  // serialize the send to receivers?

  IIntentReceiver resultTo; // IBinder of Receiver that receives final result if
                            // non-null
  int resultCode;         // current result code value.
  String resultData;      // current result data value.
  Bundle resultExtras;    // current result extra data values.
  boolean resultAbort;    // current result abortBroadcast value.

  int nextReceiver;       // next receiver in the list to be executed.

  int state;              // ??
  int anrCount;           // ?? has this broadcast record hit any ANRs?

  static final int IDLE = 0;
  static final int APP_RECEIVE = 1;
  static final int CALL_IN_RECEIVE = 2;
  static final int CALL_DONE_RECEIVE = 3;

  BroadcastRecord(Intent _intent, String _requiredPermission, List _receivers, IIntentReceiver _resultTo,
      int _resultCode, String _resultData, Bundle _resultExtras, boolean _ordered, boolean _sticky,
      boolean _initialSticky) {
    intent = _intent;
    requiredPermission = _requiredPermission;
    receivers = _receivers;
    resultTo = _resultTo;
    resultCode = _resultCode;
    resultData = _resultData;
    resultExtras = _resultExtras;
    ordered = _ordered;
    sticky = _sticky;
    initialSticky = _initialSticky;
    nextReceiver = 0;
    state = IDLE;
  }

  void dump(PrintWriter pw, String prefix) {
    final long now = SystemClock.uptimeMillis();

    pw.print(prefix);
    pw.println(this);
    pw.print(prefix);
    pw.println(intent);
    if (sticky) {
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        pw.print(prefix);
        pw.print("extras: ");
        pw.println(bundle.toString());
      }
    }
    pw.print(prefix);
    pw.print("caller=");
    if (requiredPermission != null) {
      pw.print(prefix);
      pw.print("requiredPermission=");
      pw.println(requiredPermission);
    }
    pw.println("");
    if (anrCount != 0) {
      pw.print(prefix);
      pw.print("anrCount=");
      pw.println(anrCount);
    }
    if (resultTo != null || resultCode != -1 || resultData != null) {
      pw.print(prefix);
      pw.print("resultTo=");
      pw.print(resultTo);
      pw.print(" resultCode=");
      pw.print(resultCode);
      pw.print(" resultData=");
      pw.println(resultData);
    }
    if (resultExtras != null) {
      pw.print(prefix);
      pw.print("resultExtras=");
      pw.println(resultExtras);
    }
    if (resultAbort || ordered || sticky || initialSticky) {
      pw.print(prefix);
      pw.print("resultAbort=");
      pw.print(resultAbort);
      pw.print(" ordered=");
      pw.print(ordered);
      pw.print(" sticky=");
      pw.print(sticky);
      pw.print(" initialSticky=");
      pw.println(initialSticky);
    }
    if (nextReceiver != 0 || receiver != null) {
      pw.print(prefix);
      pw.print("nextReceiver=");
      pw.print(nextReceiver);
      pw.print(" receiver=");
      pw.println(receiver);
    }
    if (curFilter != null) {
      pw.print(prefix);
      pw.print("curFilter=");
      pw.println(curFilter);
    }
    if (curReceiver != null) {
      pw.print(prefix);
      pw.print("curReceiver=");
      pw.println(curReceiver);
    }
    String stateStr = " (?)";
    switch (state) {
    case IDLE:
      stateStr = " (IDLE)";
      break;
    case APP_RECEIVE:
      stateStr = " (APP_RECEIVE)";
      break;
    case CALL_IN_RECEIVE:
      stateStr = " (CALL_IN_RECEIVE)";
      break;
    case CALL_DONE_RECEIVE:
      stateStr = " (CALL_DONE_RECEIVE)";
      break;
    }
    pw.print(prefix);
    pw.print("state=");
    pw.print(state);
    pw.println(stateStr);
    final int N = receivers != null ? receivers.size() : 0;
    String p2 = prefix + "  ";
    PrintWriterPrinter printer = new PrintWriterPrinter(pw);
    for (int i = 0; i < N; i++) {
      Object o = receivers.get(i);
      pw.print(prefix);
      pw.print("Receiver #");
      pw.print(i);
      pw.print(": ");
      pw.println(o);
      if (o instanceof BroadcastFilter)
        ((BroadcastFilter) o).dumpBrief(pw, p2);
      else if (o instanceof ResolveInfo)
        ((ResolveInfo) o).dump(printer, p2);
    }
  }

  public String toString() {
    return "BroadcastRecord{" + Integer.toHexString(System.identityHashCode(this)) + " " + intent.getAction()
        + "}";
  }
}
