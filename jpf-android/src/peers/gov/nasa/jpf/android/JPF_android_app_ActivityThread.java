//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
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
import gov.nasa.jpf.jvm.MJIEnv;

import java.util.logging.Logger;

/**
 * Native counterpart of the ActivityThread model. This is only used to lookup the current Activity from the
 * ActivityThread model class. The current Activity could also have been stored here, but to make sure we keep
 * the correct state during backtracking, we stored it in the ActivityThread class and then we lookup this
 * value when necessary.
 * 
 * As we can lookup the currentActivity from any native class, we need to store a reference to the model to
 * get a reference on its "currentActivity" field.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class JPF_android_app_ActivityThread {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  static int activityThreadRef = -1;

  public static void init0(MJIEnv env, int objectRef) {
    activityThreadRef = objectRef;
  }
}
