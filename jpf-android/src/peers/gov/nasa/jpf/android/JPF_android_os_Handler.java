//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA). All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3. The NOSA has been approved by the Open Source
// Initiative. See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.android.checkpoint.ChecklistManager;
import gov.nasa.jpf.android.checkpoint.Path;
import gov.nasa.jpf.android.checkpoint.ThreadManagerListener;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.util.logging.Logger;

/**
 * Native counterpart of the Handler class.
 * 
 * @author Heila van der Merwe
 * 
 */
public class JPF_android_os_Handler {
  private static final String TAG = "JPF_Handler";
  static Logger log = JPF.getLogger(TAG);

  static ChecklistManager checkpointManager;

  /** Returns the eventId and new Path id of this thread */
  public static int getPathInfo(MJIEnv env, int objref) {
    ThreadInfo ti = env.getThreadInfo();
    int eventID = -1;
    int pathID = -1;

    /// if we are currently in the main thread
    if (ti.getName().equals("main")) {

      //adopt the main thead's event id from message queue 
      StaticElementInfo mq = env.getVM().getClassReference("android.os.MessageQueue");
      eventID = mq.getIntField("eventID");
      pathID = mq.getIntField("pathID");

    } else {

      //get thread's eventID
      Path p = ThreadManagerListener.getPathInfo(ti.getId());
      assert (p != null);
      eventID = p.getEventID();
      int oldPathID = p.getPathID();
      
      pathID = oldPathID + 1;

      //new path & clone checklists
      checkpointManager.splitPath(p, new Path(pathID, eventID));
    }

    int[] retValues = { eventID, pathID };
    return env.newIntArray(retValues);
  }
}