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
import gov.nasa.jpf.util.JPFLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

/**
 * AndroidManifest.xml parser. Implemented as a singleton class. Creates a PackageInfo object containing all
 * the package information. It also stores all filters, permissions etc. defined in the AndroidManifest.xml
 * file of the SUT.
 * 
 * @author Heila van der Merwe
 * 
 * 
 *         TODO:
 * 
 *         - parse permissions - check filters - add further properties as needed for Activity, Service and
 *         Content Provider
 */
public class AndroidManifestParser extends DefaultHandler {
  private static final JPFLogger logger = JPF.getLogger("gov.nasa.jpf.android.AndroidManifestParser");

  private static AndroidManifestParser parser = null;

  /** Contains the base packageInfo of the application */
  private PackageInfo packageInfo;
  /** Temporary stores the current component to populate */
  private PackageItemInfo componentTemp;
  /** Temporary stores the current IntentFilter to populate */
  private IntentFilter filterTemp;

  /**
   * Temporary stores the list of Activities. As we do not know how many Activities are defined in the
   * Manifest we store them in an expandable ArrayList and afterwards copy them into PackageInfo's activities
   * field.
   */
  private ArrayList<ActivityInfo> activities;
  private ArrayList<ServiceInfo> services;
  private ArrayList<ProviderInfo> providers;
  private ArrayList<ActivityInfo> receivers;

  private ArrayList<PermissionInfo> permissions;

  /** List of Filters by component */
  private Map<PackageItemInfo, List<IntentFilter>> filterMap;

  private AndroidManifestParser() {
    // this is a singleton class
  }

  public static AndroidManifestParser getInstance() {
    if (parser == null)
      parser = new AndroidManifestParser();
    return parser;
  }

  protected void parseFile(String filename) throws SAXException, ParserConfigurationException, IOException {
    packageInfo = new PackageInfo();
    componentTemp = new PackageItemInfo();
    filterTemp = new IntentFilter();

    activities = new ArrayList<ActivityInfo>();
    services = new ArrayList<ServiceInfo>();
    providers = new ArrayList<ProviderInfo>();
    receivers = new ArrayList<ActivityInfo>();
    filterMap = new HashMap<PackageItemInfo, List<IntentFilter>>();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    parser.parse(filename, this);
    parseSuccessful();

  }

  protected void parseStream(InputStream is) throws SAXException, ParserConfigurationException, IOException {
    packageInfo = new PackageInfo();
    componentTemp = new PackageItemInfo();
    filterTemp = new IntentFilter();

    activities = new ArrayList<ActivityInfo>();
    services = new ArrayList<ServiceInfo>();
    providers = new ArrayList<ProviderInfo>();
    receivers = new ArrayList<ActivityInfo>();
    filterMap = new HashMap<PackageItemInfo, List<IntentFilter>>();

    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    parser.parse(is, this);
    parseSuccessful();
  }

  /**
   * Builds up the packageInfo's component array fields.
   * 
   */
  private void parseSuccessful() {
    logger.info("AndroidManifestParser: Manifest parsed successfully");
    packageInfo.activities = new ActivityInfo[activities.size()];
    activities.toArray(packageInfo.activities);

    packageInfo.services = new ServiceInfo[services.size()];
    services.toArray(packageInfo.services);

    packageInfo.providers = new ProviderInfo[providers.size()];
    providers.toArray(packageInfo.providers);

    packageInfo.receivers = new ActivityInfo[receivers.size()];
    receivers.toArray(packageInfo.receivers);

  }

  @Override
  public void startElement(String s, String s1, String elementName, Attributes attributes)
      throws SAXException {

    if (elementName.equalsIgnoreCase("manifest")) {
      packageInfo.packageName = parseString(attributes.getValue("package"), "", true);
    }
    if (elementName.equalsIgnoreCase("application")) {
      parseApplication(attributes);
    }
    if (elementName.equalsIgnoreCase("activity")) {
      parseActivity(attributes);
    }
    if (elementName.equalsIgnoreCase("service")) {
      parseService(attributes);
    }
    if (elementName.equalsIgnoreCase("receiver")) {
      parseReceiver(attributes);
    }
    if (elementName.equalsIgnoreCase("provider")) {
      parseProvider(attributes);
    }
    if (elementName.equalsIgnoreCase("intent-filter")) {
      parseIntentFilter(attributes);
    }
    if (elementName.equalsIgnoreCase("action")) {
      parseAction(attributes);
    }
    if (elementName.equalsIgnoreCase("category")) {
      parseCategory(attributes);
    }
    if (elementName.equalsIgnoreCase("data")) {
      parseData(attributes);
    }

  }

  @Override
  public void endElement(String s, String s1, String element) throws SAXException {
    if (element.equals("activity")) {
      ((ActivityInfo) componentTemp).applicationInfo = packageInfo.applicationInfo;
      activities.add((ActivityInfo) componentTemp);
    }
    if (element.equalsIgnoreCase("service")) {
      ((ServiceInfo) componentTemp).applicationInfo = packageInfo.applicationInfo;
      services.add((ServiceInfo) componentTemp);
    }
    if (element.equalsIgnoreCase("provider")) {
      providers.add((ProviderInfo) componentTemp);
    }
    if (element.equalsIgnoreCase("receiver")) {
      ((ActivityInfo) componentTemp).applicationInfo = packageInfo.applicationInfo;
      receivers.add((ActivityInfo) componentTemp);
    }
    if (element.equalsIgnoreCase("application")) {
      packageInfo.applicationInfo.packageName = packageInfo.packageName;
    }
    if (element.equalsIgnoreCase("intent-filter")) {
      List<IntentFilter> filters = filterMap.get(componentTemp);
      if (filters == null) {
        filters = new ArrayList<IntentFilter>();
      }
      filters.add(filterTemp);
      ((ComponentInfo) componentTemp).exported = true;
    }
  }

  protected void parseApplication(Attributes attributes) throws InvalidManifestException {
    packageInfo.applicationInfo = new ApplicationInfo();
    String name = parseString(attributes.getValue("android:name"), "", false);
    // String packageName = "";
    // if (name != null && name.length() > 0) {
    // if (name.startsWith(".")) {
    // name = name.substring(1);
    // }
    // String[] nameArr = name.split("\\.");
    // if (nameArr.length > 1) { // packageName included in name
    // packageName = name.substring(0, name.lastIndexOf("."));
    // } else {
    // packageName = packageInfo.packageName;
    // }
    // packageInfo.applicationInfo.name = packageName + nameArr[nameArr.length - 1]; // get the string after
    // // the last "."
    // } else
    packageInfo.applicationInfo.name = name;
    packageInfo.applicationInfo.packageName = packageInfo.packageName;
  }

  /**
   * Parses an Activity Tag:
   * 
   * <pre class="prettyprint">
   * &ltactivity android:allowTaskReparenting=["true" | "false"]
   *           android:alwaysRetainTaskState=["true" | "false"]
   *           android:clearTaskOnLaunch=["true" | "false"]
   *           android:configChanges=["mcc", "mnc", "locale",
   *                                  "touchscreen", "keyboard", "keyboardHidden",
   *                                  "navigation", "screenLayout", "fontScale", "uiMode",
   *                                  "orientation", "screenSize", "smallestScreenSize"]
   *           android:enabled=["true" | "false"]
   *           android:excludeFromRecents=["true" | "false"]
   *           android:exported=["true" | "false"]
   *           android:finishOnTaskLaunch=["true" | "false"]
   *           android:hardwareAccelerated=["true" | "false"]
   *           android:icon="drawable resource"
   *           android:label="string resource"
   *           android:launchMode=["multiple" | "singleTop" |
   *                               "singleTask" | "singleInstance"]
   *           android:multiprocess=["true" | "false"]
   *           android:name="string"
   *           android:noHistory=["true" | "false"]  
   *           android:parentActivityName="string" 
   *           android:permission="string"
   *           android:process="string"
   *           android:screenOrientation=["unspecified" | "user" | "behind" |
   *                                      "landscape" | "portrait" |
   *                                      "reverseLandscape" | "reversePortrait" |
   *                                      "sensorLandscape" | "sensorPortrait" |
   *                                      "sensor" | "fullSensor" | "nosensor"]
   *           android:stateNotNeeded=["true" | "false"]
   *           android:taskAffinity="string"
   *           android:theme="resource or theme"
   *           android:uiOptions=["none" | "splitActionBarWhenNarrow"]
   *           android:windowSoftInputMode=["stateUnspecified",
   *                                        "stateUnchanged", "stateHidden",
   *                                        "stateAlwaysHidden", "stateVisible",
   *                                        "stateAlwaysVisible", "adjustUnspecified",
   *                                        "adjustResize", "adjustPan"] &gt
   * </pre>
   * 
   * contained in:
   * 
   * <pre class="prettyprint">
   * &ltapplication&gt
   * </pre>
   * 
   * can contain:
   * 
   * <pre class="prettyprint">
   * &ltintent-filter&gt
   * &ltmeta-data&gt
   * </pre>
   * 
   * @param attributes
   * @throws InvalidManifestException
   */
  protected void parseActivity(Attributes attributes) throws InvalidManifestException {
    componentTemp = new ActivityInfo();
    parseName(attributes);
  }

  /**
   * Parses an Service Tag:
   * 
   * <pre class="prettyprint">
   * &lt;service android:enabled=["true" | "false"] 
   *          android:exported=["true" | "false"]
   *          android:icon="drawable resource"
   *          android:isolatedProcess=["true" | "false"]
   *          android:label="string resource"
   *          android:name="string"
   *          android:permission="string"
   *          android:process="string" &gt
   * </pre>
   * 
   * contained in:
   * 
   * <pre class="prettyprint">
   * &ltapplication&gt
   * </pre>
   * 
   * can contain:
   * 
   * <pre class="prettyprint">
   * &ltintent-filter&gt
   * &ltmeta-data&gt
   * </pre>
   * 
   * @param attributes
   * @throws InvalidManifestException
   */
  protected void parseService(Attributes attributes) throws InvalidManifestException {
    componentTemp = new ServiceInfo();
    parseName(attributes);
  }

  protected void parseProvider(Attributes attributes) throws InvalidManifestException {
    componentTemp = new ProviderInfo();
    parseName(attributes);
  }

  /**
   * Parses a Broadcast Receiver Tag:
   * 
   * <pre class="prettyprint">
   *     &ltreceiver android:enabled=["true" | "false"] //can be instantiated by system 
   *             android:exported=["true" | "false"] // can receive messages outside of application
   *             android:icon="drawable resource" // icon representing the broadcast receiver
   *             android:label="string resource" //user-readable label for the broadcast receiver
   *             android:name="string" //name of the class that implements the broadcast receiver
   *             android:permission="string" // permission that broadcasters must have to send a message
   *             android:process="string"&gt //name of the process in which the broadcast receiver should run
   *         . . .
   *    &ltreceiver&gt
   * </pre>
   * 
   * contained in:
   * 
   * <pre class="prettyprint">
   * &ltapplication&gt
   * </pre>
   * 
   * can contain:
   * 
   * <pre class="prettyprint">
   * &ltintent-filter&gt
   * &ltmeta-data&gt
   * </pre>
   * 
   * @param attributes
   * @throws InvalidManifestException
   */
  protected void parseReceiver(Attributes attributes) throws InvalidManifestException {
    componentTemp = new ActivityInfo();
    parseName(attributes);
    ((ActivityInfo) componentTemp).enabled = parseBoolean(attributes.getValue("android:enabled"), true, false);
    ((ActivityInfo) componentTemp).exported = parseBoolean(attributes.getValue("android:exported"), true,
        false);
    ((ActivityInfo) componentTemp).permission = parseString(attributes.getValue("android:permission"), "",
        false);
    ((ActivityInfo) componentTemp).processName = parseString(attributes.getValue("android:process"),
        packageInfo.packageName, false);

    ((ActivityInfo) componentTemp).labelRes = parseStringResource(attributes.getValue("android:label"),
        packageInfo.applicationInfo.labelRes, false);
    ((ActivityInfo) componentTemp).icon = parseDrawableResource(attributes.getValue("android:icon"),
        packageInfo.applicationInfo.labelRes, false);

  }

  /**
   * Parses an IntentFilter Tag:
   * 
   * <pre class="prettyprint">
   *     &ltintent-filter android:icon="drawable resource"
   *                    android:label="string resource"
   *                    android:priority="integer"&gt 
   *         . . .
   *     &lt/intent-filter&gt
   * </pre>
   * 
   * contained in:
   * 
   * <pre class="prettyprint">
   *     &ltactivity&gt
   *     &ltactivity-alias&gt
   *     &ltservice&gt
   *     &ltreceiver&gt
   * </pre>
   * 
   * must contain:
   * 
   * <pre class="prettyprint">
   *     &ltaction&gt
   * </pre>
   * 
   * can contain:
   * 
   * <pre class="prettyprint">
   *     &ltcategory&gt
   *     &ltdata&gt
   * </pre>
   * 
   * 
   * @param attributes
   * @throws InvalidManifestException
   */
  protected void parseIntentFilter(Attributes attributes) throws InvalidManifestException {
    filterTemp = new IntentFilter();
    filterTemp.setPriority(parseInt(attributes.getValue("android:priority"), 0, false));

  }

  protected void parseAction(Attributes attributes) throws InvalidManifestException {
    filterTemp.addAction(parseString(attributes.getValue("android:name"), "", true));
  }

  protected void parseCategory(Attributes attributes) throws InvalidManifestException {
    filterTemp.addCategory(parseString(attributes.getValue("android:name"), "", true));
  }

  protected void parseData(Attributes attributes) throws InvalidManifestException {
    filterTemp.addDataAuthority(parseString(attributes.getValue("android:host"), "", false),
        parseString(attributes.getValue("android:port"), "", true));
    filterTemp.addDataPath(parseString(attributes.getValue("android:path"), "", false), 0); // TODO not sure
                                                                                            // about this
    filterTemp.addDataScheme(parseString(attributes.getValue("android:scheme"), "", false));
    try {
      filterTemp.addDataType(parseString(attributes.getValue("android:mimeType"), "", false));
    } catch (MalformedMimeTypeException e) {
      throw new InvalidManifestException("Malformed MimeType");
    }
  }

  /**
   * Parses a component name attribute in the Manifest.
   * 
   * @param attributes
   * @throws InvalidManifestException
   */
  protected void parseName(Attributes attributes) throws InvalidManifestException {
    String tempName = parseString(attributes.getValue("android:name"), "", true);

    // if (tempName.startsWith(".")) {
    // tempName = tempName.substring(1);
    // }
    // String[] name = tempName.split("\\.");
    // if (name.length > 1) { // packageName included in name
    // componentTemp.packageName = tempName.substring(0, tempName.lastIndexOf("."));
    // } else {
    // componentTemp.packageName = packageInfo.packageName;
    // }
    // componentTemp.name = name[name.length - 1]; // get the string after the last "."
    componentTemp.name = tempName;
    componentTemp.packageName = packageInfo.packageName;
  }

  /**
   * Parses and returns the boolean value of the attribute <code>value</code>. The default value is returned
   * if the attribute is not set and not required to be set by in the Manifest.
   * 
   * @param value
   *          the attribute value to parse
   * @param defaultValue
   *          the default value to return if the attribute is not required and is null/length < 1
   * @param required
   *          true if this attribute is required
   * @return the boolean value of the attribute
   * 
   * @throws InvalidManifestException
   *           - thrown when the boolean attribute is required and not set or when the attribute could not be
   *           parsed to a boolean value
   * 
   * 
   */
  protected boolean parseBoolean(String value, boolean defaultValue, boolean required)
      throws InvalidManifestException {
    boolean rValue = false;
    if (value == null || value.length() < 1) {
      if (required) {
        throw new InvalidManifestException("Required boolean attribute not set");
      } else {
        rValue = defaultValue;
      }
    } else {
      try {
        rValue = Boolean.parseBoolean(value);
      } catch (NumberFormatException e) {
        throw new InvalidManifestException("Boolean attribute could not be parsed correctly");
      }
    }
    return rValue;
  }

  /**
   * Parses and returns the integer value of the attribute <code>value</code>. The default value is returned
   * if the attribute is not set and not required to be set by the Manifest.
   * 
   * @param value
   *          the attribute value to parse
   * @param defaultValue
   *          the default value to return if the attribute is not required and is null/length < 1
   * @param required
   *          true if this attribute is required
   * @return the integer value of the attribute
   * 
   * @throws InvalidManifestException
   *           - thrown when the int attribute is required and not set or when the attribute could not be
   *           parsed to an integer
   * 
   * 
   */
  protected int parseInt(String value, int defaultValue, boolean required) throws InvalidManifestException {
    int rValue = -1;
    if (value == null || value.length() < 1) {
      if (required) {
        throw new InvalidManifestException("Required integer attribute not set");
      } else
        rValue = defaultValue;
    } else {
      try {
        rValue = Integer.parseInt(value);
      } catch (NumberFormatException e) {
        throw new InvalidManifestException("Integer attribute could not be parsed correctly");
      }
    }
    return rValue;
  }

  /**
   * Parses and returns the String representation of the attribute <code>value</code>. The default value is
   * returned if the attribute is not set and not required to be set by the Manifest.
   * 
   * @param value
   *          the attribute value to parse
   * @param defaultValue
   *          the default value to return if the attribute is not required and is null/length < 1
   * @param required
   *          true if this attribute is required
   * @return the String value of the attribute
   * 
   * @throws InvalidManifestException
   *           - thrown when the String attribute is required and not set
   * 
   */
  protected String parseString(String value, String defaultValue, boolean required)
      throws InvalidManifestException {
    if (value == null || value.length() < 1) {
      if (required) {
        throw new InvalidManifestException("Required String attribute not set");
      } else {
        value = defaultValue;
      }
    }
    return value;
  }

  private int parseDrawableResource(String value, int labelRes, boolean b) {
    // TODO Auto-generated method stub
    return 0;
  }

  private int parseStringResource(String value, int labelRes, boolean b) {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * For testing purposes only. We want to be able to test that the component is correctly populated.
   * 
   * @return The component that is currently being parsed.
   */
  protected PackageItemInfo getComponent() {
    return componentTemp;
  }

  /**
   * Returns the PackageInfo object containing the information in the AndroidManifest.xml file
   * 
   * @return a PackageInfo object
   */
  public PackageInfo getPackageInfo() {
    return packageInfo;
  }

  /**
   * Returns a map of filters as defined in the Android ManifestFile.
   * 
   * @return
   */
  protected Map<PackageItemInfo, List<IntentFilter>> getFilters() {
    return filterMap;
  }

  /**
   * Thrown when the manifest contains errors
   * 
   * @author Heila van der Merwe
   * 
   */
  public class InvalidManifestException extends SAXException {
    private static final long serialVersionUID = 7988578359042835572L;

    public InvalidManifestException(String message) {
      super(message);
    }

  }
}