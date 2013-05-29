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

import java.util.ArrayList;

import android.content.IIntentReceiver;

/**
 * A receiver object that has registered for one or more broadcasts. The
 * ArrayList holds BroadcastFilter objects.
 */
class ReceiverList extends ArrayList<BroadcastFilter> {
  public final IIntentReceiver receiver;
  BroadcastRecord curBroadcast = null;

  String stringName;

  ReceiverList(IIntentReceiver _receiver) {
    receiver = _receiver;

  }

  // Want object identity, not the array identity we are inheriting.
  public boolean equals(Object o) {
    return this == o;
  }

  public int hashCode() {
    return System.identityHashCode(this);
  }

  public String toString() {
    if (stringName != null) {
      return stringName;
    }
    StringBuilder sb = new StringBuilder(128);
    sb.append("ReceiverList{");
    sb.append(Integer.toHexString(System.identityHashCode(this)));
    sb.append(' ');
    sb.append('}');
    return stringName = sb.toString();
  }
}
