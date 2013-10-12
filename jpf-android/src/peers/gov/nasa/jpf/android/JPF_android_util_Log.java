package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

import java.util.logging.Logger;

import android.util.Log;

/**
 * Implements the native methods of android.util.Log. Writes the log as a JPF
 * log with sort of the same log level.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class JPF_android_util_Log  extends NativePeer {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  @MJI
  public int println_native(MJIEnv env, int clsObjRef, int v0, int type, int stag, int smsg) {

    String msg = env.getStringObject(smsg);
    String tag = env.getStringObject(stag);

    switch (type) {
    case Log.INFO:
      log.info(tag + ": " + msg);
      break;
    case Log.DEBUG:
      log.fine(tag + ": " + msg);
      break;
    case Log.ERROR:
      log.severe(tag + ": " + msg);
      break;
    case Log.WARN:
      log.warning(tag + ": " + msg);
      break;
    case Log.VERBOSE:
      log.fine(tag + ": " + msg);
      break;
    case Log.ASSERT:
      log.fine(tag + ": " + msg);
      break;
    default:
      log.fine(tag + ": " + msg);
    }

    return msg.length();
  }

  @MJI
  public boolean isLoggable(MJIEnv env, int clsObjRef, int rString0, int v1) {
    boolean v = true;
    return v;
  }

}