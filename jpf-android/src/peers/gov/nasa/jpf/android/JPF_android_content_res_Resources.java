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
import gov.nasa.jpf.android.JPF_android_view_WindowManager.ViewEntry;
import gov.nasa.jpf.jvm.MJIEnv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.view.Window;

public class JPF_android_content_res_Resources {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  private String ID_HEADER = "public static final class id {";
  private String LAYOUT_HEADER = "public static final class layout {";
  private String STRING_HEADER = "public static final class string {";
  private String MENU_HEADER = "public static final class menu {";
  private String STYLE_HEADER = "public static final class style {";
  private String BOOL_HEADER = "public static final class bool {";
  private String COLOR_HEADER = "public static final class color {";
  private String ARRAY_HEADER = "public static final class array {";
  private String ANIM_HEADER = "public static final class anim {";
  private String INTEGER_HEADER = "public static final class int {";

  private String FOOTER = "}";

  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

  private HashMap<Integer, String> stringsMap = new HashMap<Integer, String>();

  private HashMap<Integer, String> drawableMap = new HashMap<Integer, String>();

  private HashMap<Integer, String> menuMap = new HashMap<Integer, String>();
  String rPath = "";

  /**
   * Sets up the environment for the {@link Window} class. Creates componentMap and layoutMap.
   * 
   * @param env
   * @param cref
   */
  public void init0____V(MJIEnv env, int cref) {
    // Lookup the path to the R.java file
    rPath = AndroidFileUtil.getRPath(JPF_android_content_pm_PackageManager.getPackageName().replace('.', '/'));
    if (rPath == null || rPath.length() <= 0) {
      log.severe("Could not find R.java file.");
      return;
    }

    try {
      parseRFile(new FileInputStream(rPath));
    } catch (FileNotFoundException e) {
      log.severe("R.java file not found.");
    }
  }

  /**
   * Parse the R.java file and builds the componentMap and layoutMap.
   * 
   * 
   * @param rPath
   *          the path to the R.java file on disk
   */
  public void parseRFile(InputStream is) {
    String nextLine;
    Scanner scanner = new Scanner(is);
    while (scanner != null && scanner.hasNextLine()) {
      nextLine = scanner.nextLine().trim();
      if (nextLine.equals(STRING_HEADER)) {
        parseStrings(scanner);
      }
    }
  }

  private void parseStrings(Scanner scanner) {
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
      stringsMap.put(c.getId(), c.getName());
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

  private String getNodeValue(InputStream is, String type, String name) {
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(is);
      Element e = doc.getDocumentElement();
      NodeList nodeList = doc.getElementsByTagName(type);

      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) node;
          NodeList nodelist = element.getElementsByTagName("firstname");
          Element element1 = (Element) nodelist.item(0);
          NodeList fstNm = element1.getChildNodes();
          System.out.print("First Name : " + (fstNm.item(0)).getNodeValue());
        }
      }

    } catch (Exception e) {

    }
    return new String();
  }

  // /////////////////
  // Public methods //
  // /////////////////

  /*
   * public int getAnimation(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getComponent(MJIEnv env, int objref, int id) {
   * 
   * return 0; }
   * 
   * public int getColorStateList(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath
   * + "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   */
  /**
   * Reads the value of the language in the config and returns the corresponding value of a String object.
   * 
   * @param env
   * @param objref
   * @param id
   * @return "" if no such string entry exists
   */
  public int getString(MJIEnv env, int objref, int id) {
    // TODO add config
    String filename = "";
    if (rPath.endsWith("/"))
      filename = rPath + "res/values/strings.xml";
    else
      filename = rPath + "/res/values/strings.xml";

    String s = "";
    try {
      s = getNodeValue(new FileInputStream(filename), "String", stringsMap.get(id));
    } catch (FileNotFoundException e) {
    }
    return env.newString(s);
  }

  /*
   * public int getDrawable(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   */
  public int getLayout(MJIEnv env, int objref, int id) {
    // TODO add config
    String filename = rPath + "/res/values/strings.xml";
    String s = null;
    // try {
    // // s = getNodeValue(new FileInputStream(filename), "String", valueMap.get(id));
    // } catch (FileNotFoundException e) {
    // }
    return env.newString(s);
  }

  /*
   * public int getMenu(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getStringArray(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getPlurals(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getBool(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getColor(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public float getDim(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getStyle(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getID(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getInteger(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getIntegerArray(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
   * public int getTypedArray(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   */

  // public static void main(String[] args) {
  // String s =
  // "<resources><string name=\"app_name\">TestCalculator</string><string name=\"menu_settings\">Settings</string> <string name=\title_activity\">Calculator</string></resources>";
  // // getNodeValue(new StringReader(s));
  //
  // }
}
