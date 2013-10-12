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
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JPF_android_content_res_Resources  extends NativePeer {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  private static class ResourceInfo {
    int id;
    String name;
    String value;
  }

  private static String LAYOUT_HEADER = "public static final class layout {";
  private static String STRING_HEADER = "public static final class string {";
  private static String MENU_HEADER = "public static final class menu {";
  private static String STYLE_HEADER = "public static final class style {";
  private static String BOOL_HEADER = "public static final class bool {";
  private static String COLOR_HEADER = "public static final class color {";
  private static String ARRAY_HEADER = "public static final class array {";
  private static String ANIM_HEADER = "public static final class anim {";
  private static String INTEGER_HEADER = "public static final class int {";

  private static String FOOTER = "}";

  static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

  private static HashMap<Integer, ResourceInfo> stringsMap = new HashMap<Integer, ResourceInfo>();

  private static HashMap<Integer, String> drawableMap = new HashMap<Integer, String>();

  private static HashMap<Integer, String> menuMap = new HashMap<Integer, String>();
  private static String rPath = null;

  /**
   * Sets up the environment for the {@link Window} class. Creates componentMap
   * and layoutMap.
   * 
   * @param env
   * @param cref
   */
  @MJI
  public void init0(MJIEnv env, int robj) {

    // Lookup the path to the R.java file
    if (rPath == null) {
      // Lookup the path to the R.java file
      rPath = AndroidPathManager.getRPath(JPF_android_content_pm_PackageManager.getPackageName().replace('.',
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
        if (nextLine.equals(STRING_HEADER)) {
          parseStrings(scanner);
        }
      }
    } catch (FileNotFoundException e) {
      log.severe("R.java file not found.");
    } finally {
      scanner.close();
    }

  }

  private static void parseStrings(Scanner scanner) {
    String next = "";
    String[] list;
    ResourceInfo c;
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(AndroidPathManager.getStringsPath());
      NodeList nodeList = doc.getElementsByTagName("string");

      while (scanner.hasNextLine()) {
        next = scanner.nextLine().trim();
        if (next.equals(FOOTER))
          break;
        list = getFields(next);
        c = new ResourceInfo();
        c.id = Integer.parseInt(list[1].substring(2), 16);
        c.name = list[0];
        c.value = getNodeValueByName(nodeList, c.name);
        stringsMap.put(c.id, c);
      }
    } catch (Exception e) {
      log.severe("string.xml file could not be parsed.");
    }    
  }

  private static String getNodeValueByName(NodeList list, String name) {
    Node node = null;

    for (int i = 0; i < list.getLength(); i++) {
      node = list.item(i);
      if (node.getAttributes().getNamedItem("name").toString().equals("name=\"" + name + "\"")) {
        return node.getTextContent();
      }
    }
    return null;
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

  // /////////////////
  // Public methods //
  // /////////////////

  /*
   * public int getAnimation(MJIEnv env, int objref, int id) { // TODO add
   * config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getComponent(MJIEnv env, int objref, int id) {
   * 
   * return 0; }
   * 
   * public int getColorStateList(MJIEnv env, int objref, int id) { // TODO add
   * config String filename = rPath
   * + "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   */
  /**
   * Reads the value of the language in the config and returns the corresponding
   * value of a String object.
   * 
   * @param env
   * @param objref
   * @param id
   * @return "" if no such string entry exists
   */
  @MJI
  public int getString__I__Ljava_lang_String_2(MJIEnv env, int objref, int id) {
    return env.newString(stringsMap.get(id).value);
  }
  
  public static String getString(String name){
    for(Entry<Integer, ResourceInfo> info : stringsMap.entrySet()){
      if(info.getValue().name.equals(name)){
        return info.getValue().value;
      }
    }
    return null;
  }

  /*
   * public int getDrawable(MJIEnv env, int objref, int id) { // TODO add config
   * String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
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
   * public int getMenu(MJIEnv env, int objref, int id) { // TODO add config
   * String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getStringArray(MJIEnv env, int objref, int id) { // TODO add
   * config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getPlurals(MJIEnv env, int objref, int id) { // TODO add config
   * String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getBool(MJIEnv env, int objref, int id) { // TODO add config
   * String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getColor(MJIEnv env, int objref, int id) { // TODO add config
   * String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public float getDim(MJIEnv env, int objref, int id) { // TODO add config
   * String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getStyle(MJIEnv env, int objref, int id) { // TODO add config
   * String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getID(MJIEnv env, int objref, int id) { // TODO add config
   * String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getInteger(MJIEnv env, int objref, int id) { // TODO add config
   * String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getIntegerArray(MJIEnv env, int objref, int id) { // TODO add
   * config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   * 
   * public int getTypedArray(MJIEnv env, int objref, int id) { // TODO add
   * config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new
   * FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return
   * env.newString(s); }
   */

  // public static void main(String[] args) {
  // String s =
  // "<resources><string name=\"app_name\">TestCalculator</string><string name=\"menu_settings\">Settings</string> <string name=\title_activity\">Calculator</string></resources>";
  // // getNodeValue(new StringReader(s));
  //
  // }
}
