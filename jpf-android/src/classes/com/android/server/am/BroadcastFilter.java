package com.android.server.am;

import android.content.IntentFilter;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import java.io.PrintWriter;

class BroadcastFilter extends IntentFilter {
  final ReceiverList receiverList;
  final String packageName;
  final String requiredPermission;

  BroadcastFilter(IntentFilter _filter, ReceiverList _receiverList, String _packageName,
      String _requiredPermission) {
    super(_filter);
    this.receiverList = _receiverList;
    this.packageName = _packageName;
    this.requiredPermission = _requiredPermission;
  }

  public void dump(PrintWriter pw, String prefix) {
    dumpInReceiverList(pw, new PrintWriterPrinter(pw), prefix);
//    this.receiverList.dumpLocal(pw, prefix);
  }

  public void dumpBrief(PrintWriter pw, String prefix) {
    dumpBroadcastFilterState(pw, prefix);
  }

  public void dumpInReceiverList(PrintWriter pw, Printer pr, String prefix) {
    super.dump(pr, prefix);
    dumpBroadcastFilterState(pw, prefix);
  }

  void dumpBroadcastFilterState(PrintWriter pw, String prefix) {
    if (this.requiredPermission != null) {
      pw.print(prefix);
      pw.print("requiredPermission=");
      pw.println(this.requiredPermission);
    }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("BroadcastFilter{");
    sb.append(Integer.toHexString(System.identityHashCode(this)));
    sb.append(' ');
    sb.append(this.receiverList);
    sb.append('}');
    return sb.toString();
  }
}