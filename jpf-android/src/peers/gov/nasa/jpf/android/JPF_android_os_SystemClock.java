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

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

/**
 * This has to be modeled better. But does it make sense as time can be taken
 * into account due to non-determinism? TODO
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class JPF_android_os_SystemClock  extends NativePeer {

  @MJI
  public long uptimeMillis(MJIEnv env, int clsObjRef) {
    long v = (long) 0;
    return v;
  }

  @MJI
  public boolean setCurrentTimeMillis(MJIEnv env, int clsObjRef, long v0) {
    boolean v = true;
    return v;
  }

  @MJI
  public long elapsedRealtime(MJIEnv env, int clsObjRef) {
    long v = (long) 0;
    return v;
  }

  @MJI
  public long currentThreadTimeMillis(MJIEnv env, int clsObjRef) {
    long v = (long) 0;
    return v;
  }

  @MJI
  public long currentThreadTimeMicro(MJIEnv env, int clsObjRef) {
    long v = (long) 0;
    return v;
  }

  @MJI
  public long currentTimeMicro(MJIEnv env, int clsObjRef) {
    long v = (long) 0;
    return v;
  }
}