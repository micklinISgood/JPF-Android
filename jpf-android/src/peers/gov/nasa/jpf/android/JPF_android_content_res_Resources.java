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
import gov.nasa.jpf.android.AndroidProjectInfo.ProjectParseException;
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

public class JPF_android_content_res_Resources extends NativePeer {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

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
   * @throws ProjectParseException
   */
  @MJI
  public int getString__I__Ljava_lang_String_2(MJIEnv env, int objref, int id) throws ProjectParseException {
    return env.newString(AndroidProjectInfo.get().getStringValue(id));
  }

  /*
   * public int getDrawable(MJIEnv env, int objref, int id) { // TODO add config String filename = rPath +
   * "/res/values/strings.xml"; String s = null; try { s = getNodeValue(new FileInputStream(filename),
   * "String", valueMap.get(id)); } catch (FileNotFoundException e) { } return env.newString(s); }
   * 
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
