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
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.util.script.UIAction;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the native methods of the WindowManager class.
 * 
 * One of the main functions of this class is to keep a list of all the View-objects of the application
 * registered in R.java. Their unique id's and names are resolved from the R.java file and stored as a map
 * <code>componentMap</code>. This is used during layout inflation of the views to make sure the views
 * registered in the R.java file gets assigned the right id. This is is used in the application's code to
 * identify the views.
 * 
 * The componentMap contains ViewEntry objects. They keep a reference to the actual View -objects in memory.
 * 
 */
public class JPF_android_view_WindowManager extends NativePeer {
  static Logger log = JPF.getLogger("JPF_android_view_WindowManager");

  static int classRef = -1;

  /**
   * Intercept default constructor and initialize package information.
   * 
   * @param env
   * @param robj
   */
  @MJI
  public void init0(MJIEnv env, int robj) {
    classRef = robj;
  }

  /**
   * Calls the handleViewAction(String target, String action) method on the JPF model of WindowManager to
   * handle the event.
   * 
   * @param env
   * @param uiAction
   */
  static void handleViewAction(MJIEnv env, UIAction uiAction) {
    String action = uiAction.getAction();
    String target = uiAction.getTarget();
    Object[] arguments = uiAction.getArguments();
    int aref = MJIEnv.NULL;
    if (arguments != null && arguments.length > 0) {
      aref = env.newObjectArray("java.lang.String", arguments.length);
      for (int i = 0; i < arguments.length; i++) {
        env.setReferenceArrayElement(aref, i, env.newString(arguments[i].toString()));
      }
    }
    ClassInfo classInfo = env.getReferredClassInfo(classRef);
    // Ok, now we make the (direct) call
    String methodName = "handleViewAction(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V";

    if (!AndroidUtil.callMethod(env, classInfo, methodName,
        new int[] { env.newString(target), env.newString(action), aref })) {
      log.log(Level.SEVERE, "Error calling handleViewAction for " + target + "." + action);
    }

  }

  /**
   * Returns the name of the current Window that is showing on the screen. It is used to determine which part
   * of the script has to be executed.
   * 
   * @param env
   * @return the name of the section of the script to execute or default if no window is currently on the
   *         screen
   */
  public static String getCurrentWindow(MJIEnv env) {
    int windowRef = env.getStaticReferenceField(classRef, "currentWindow");
    if (windowRef <= 0) // no window has been set yet
      return "default";

    String windowName = env.getStringField(windowRef, "name");
    if (windowName == null || windowName.length() == 0)
      return "default";
    else
      return windowName;
  }

}
