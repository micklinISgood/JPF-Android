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
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.view.Window;

/**
 * Implements the native methods of the WindowManager class.
 * 
 * One of the main functions of this class is to keep a list of all the
 * View-objects of the application
 * registered in R.java. Their unique id's and names are resolved from the
 * R.java file and stored as a map <code>componentMap</code>. This is used
 * during layout inflation of the views to make sure the views
 * registered in the R.java file gets assigned the right id. This is is used in
 * the application's code to
 * identify the views.
 * 
 * The componentMap contains ViewEntry objects. They keep a reference to the
 * actual View -objects in memory.
 * 
 */
public class JPF_android_view_WindowManager {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");
  static final String UIACTION = "[UIAction]";

  private static String ID_HEADER = "public static final class id {";
  private static String LAYOUT_HEADER = "public static final class layout {";
  private static String FOOTER = "}";

  private static final char NAME_PREFIX = '$'; // put is front of view
  // object's names
  /**
   * Maps script actions to View objects. The key is the name of the View-object
   * as defined in the R.java
   * file. The value keeps a reference to the actual View-object. This map
   * contains all the components of the
   * application. The R.java file requires that these names be unique.
   */
  private static HashMap<String, ViewEntry> componentMap = new HashMap<String, ViewEntry>();

  /**
   * Maps the resource ID of a layout to the name of the layout file. This
   * information is read from the R.java
   * class and stored as a map fir quick lookup by the LayoutInflater.
   */
  static HashMap<Integer, String> layoutMap = new HashMap<Integer, String>();

  static String rPath = null;

  static int classRef = -1;

  /**
   * Intercept default constructor and initialize package information.
   * 
   * @param env
   * @param robj
   */
  public static void init0(MJIEnv env, int robj) {
    if (rPath == null) {
      classRef = robj;
      // Lookup the path to the R.java file
      rPath = AndroidFileUtil.getRPath(JPF_android_content_pm_PackageManager.getPackageName().replace('.',
          '/'));
      if (rPath == null || rPath.length() <= 0) {
        log.severe("Could not find R.java file.");
        return;
      }

      parseRFile(rPath);
    }

  }

  /**
   * Parse the R.java file and builds the componentMap and layoutMap.
   * 
   * 
   * @param rPath
   *          the path to the R.java file on disk
   */
  public static void parseRFile(String rPath) {

    Scanner scanner = null;
    String nextLine;
    try {
      scanner = new Scanner(new FileInputStream(rPath));
      while (scanner != null && scanner.hasNextLine()) {
        nextLine = scanner.nextLine().trim();
        if (nextLine.equals(ID_HEADER)) {
          parseViews(scanner);
        }
        if (nextLine.equals(LAYOUT_HEADER)) {
          parseLayouts(scanner);
        }
      }
    } catch (FileNotFoundException e) {
      log.severe("R.java file not found.");
    } finally {
      scanner.close();
    }

  }

  /**
   * Parse the name and ID's of the View objects in the R.java file.
   * 
   * @param scanner
   */
  private static void parseViews(Scanner scanner) {
    String next = "";
    String[] list;
    ViewEntry c;
    while (scanner.hasNextLine()) {
      next = scanner.nextLine().trim();
      if (next.equals(FOOTER))
        break;
      list = getFields(next);
      c = new ViewEntry();

      c.setId(Integer.parseInt(list[1].substring(2), 16));
      c.setName(list[0]);
      componentMap.put("$" + list[0], c);
    }
    // TODO Add window to the componentMap to catch window events
    c = new ViewEntry();
    c.setId(0);
    c.setName("window");
    componentMap.put("$" + c.getName(), c);

  }

  /**
   * Parse the name and ID's of the layout file in the R.java file.
   * 
   * @param scanner
   */
  private static void parseLayouts(Scanner scanner) {
    String next = "";
    String[] list;
    ViewEntry c;
    while (scanner.hasNextLine()) {
      next = scanner.nextLine().trim();
      if (next.equals(FOOTER))
        break;
      list = getFields(next);
      c = new ViewEntry();
      c.setId(Integer.parseInt(list[1].substring(2), 16));
      c.setName(list[0]);
      layoutMap.put(c.getId(), c.getName());

    }

  }

  /**
   * Returns the NAME and ID of an object given its declaration.
   * 
   * @param line
   * @return
   */
  private static String[] getFields(String line) {
    String[] list;
    line = line.substring(0, line.length() - 1); // throw ';' away
    list = line.split(" ");
    line = list[list.length - 1]; // last item in list
    list = line.split("=");
    log.fine("Parsed from R.java NAME: " + list[0] + " ID: " + list[1]);
    return list;

  }

  /**
   * Used by setContentView(int resId) method in {@link Window} to resolve the
   * resource id of a layout to the
   * name of the layout file.
   * 
   * @param id
   *          the resource id of the layout as defined in the file R.java
   * @return
   */
  static String getLayoutFileName(int id) {
    String name = layoutMap.get(id);
    log.fine("Getting the name of Layout " + id + ": " + name);
    return AndroidFileUtil.getLayoutPath() + name + ".xml";
  }

  /**
   * Returns the name field of a View . If the name field is null, a unique name
   * is generated.
   * 
   * @param env
   * @param objref
   * @return
   */
  static int getID(String name) {
    ViewEntry c = JPF_android_view_WindowManager.componentMap.get(name);
    if (c == null)
      return -1;
    else
      return c.getId();
  }

  /**
   * Calls the handleViewAction(String target, String action) method on the JPF
   * model of WindowManager to
   * handle the event.
   * 
   * @param env
   * @param uiAction
   */
  static void handleViewAction(MJIEnv env, UIAction uiAction) {
    String action = uiAction.getAction();
    String target = uiAction.getTarget();
    Object[] arguments = uiAction.getArguments();
    int aref = -1;
    if (arguments != null && arguments.length > 0) {
      aref = env.newObjectArray("java.lang.String", arguments.length);
      for (int i = 0; i < arguments.length; i++) {
        env.setReferenceArrayElement(aref, i, env.newString(arguments[i].toString()));
      }
    }
    env.getElementInfo(classRef);

    ClassInfo ei = env.getReferredClassInfo(classRef);

    // Ok, now we make the (direct) call
    MethodInfo mi = ei.getMethod(
        "handleViewAction(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V", false);

    if (mi != null) {
      MethodInfo stub = mi.createDirectCallStub(UIACTION);
      DirectCallStackFrame frame = new DirectCallStackFrame(stub);

      frame.push(classRef, false);
      frame.push(env.newString(target), true);
      frame.push(env.newString(action), true);
      frame.push(aref, true);

      ThreadInfo ti = env.getThreadInfo();
      ti.pushFrame(frame);

    } else {
      log.log(Level.SEVERE, "Error calling handleViewAction for " + target + "." + action);
    }
  }

  /**
   * Returns the name of the current Window that is showing on the screen. It is
   * used to determine which part
   * of the script has to be executed.
   * 
   * @param env
   * @return the name of the section of the script to execute or default if no
   *         window is currently on the
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

  /**
   * Keeps a reference to a {@link View} object.
   * 
   * @author "Heila van der Merwe"
   * 
   */
  public static class ViewEntry {
    /**
     * The unique id of a {@link View} as defined in the R.java file else
     * generated in native Window class.
     */
    private int id;

    /**
     * The unique name of the {@link View} as defined in the R.java file else
     * generated in native Window
     * class.
     */
    private String name;

    public ViewEntry() {
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

  }

}
