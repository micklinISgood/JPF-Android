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
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import nhandler.conversion.ConversionException;
import nhandler.conversion.jvm2jpf.JVM2JPFConverter;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;

/**
 * Responsible for parsing and setting up Package information that is used by PackageManager.
 * 
 * TODO: - Bring Filters over as well or provide call back for resolving components natively.
 * 
 * @author Heila van der Merwe
 */
public class JPF_android_content_pm_PackageManager extends NativePeer {
  private static final JPFLogger logger = JPF.getLogger("JPF_android_content_pm_PackageManager");

  private static AndroidManifestParser parser;
  private static PackageInfo packageInfo;
  private static HashMap<String, ArrayList<IntentFilter>> filters;

  /**
   * Intercept default constructor and initialize package information.
   * 
   * @param env
   * @param robj
   */
  @MJI
  public void init0(MJIEnv env, int robj) {
    ThreadInfo ti = env.getThreadInfo();

    if (!ti.hasReturnedFromDirectCall("[<clinit>]")) { // Make sure that when we repeat the code during
      // class initialization in ObjectConverter, this is not
      // executed again.
      // Determine the path to the manifest file
      String manifestPath = AndroidPathManager.getManifestPath();
      if (manifestPath == null || manifestPath.length() == 0) {
        logger.severe("Could not determine the path of the AndroidManifest.xml file.");
        return;
      }

      // Parse the AndroidManifest.xml file
      parser = AndroidManifestParser.getInstance();
      try {
        parser.parseFile(manifestPath);
        packageInfo = parser.getPackageInfo();
        filters =  parser.getFilters();

      } catch (Exception e) {
        logger.severe("Could not parse AndroidManifest.xml file:" + e.getMessage());
        packageInfo = null;
      }
    }
    // If we have reached this point the package has been parsed and we need to populate the PackageManager
    // model object
    if (packageInfo != null) {

      int packageRef = MJIEnv.NULL;
      int filtersRef = MJIEnv.NULL;
      try {
        packageRef = JVM2JPFConverter.obtainJPFObj(packageInfo, env);
        env.setReferenceField(robj, "packageInfo", packageRef);
        
        filtersRef = JVM2JPFConverter.obtainJPFObj(filters, env);
        env.setReferenceField(robj, "filters", filtersRef);


      } catch (ConversionException e) {
        e.printStackTrace();
      }

    }

  }

  /**
   * Intercept constructor used during testing. The constructor is provided with an XML string that contains
   * the contents of the AndroidManifestFile.
   * 
   * @param env
   * @param robj
   * @param ref
   *          a String containing the AndroidManifest contents as a XML string.
   * @throws Exception
   */
  @MJI
  public void init1(MJIEnv env, int robj, int ref) {
    ThreadInfo ti = env.getThreadInfo();
    logger.info("Testing init__Ljava_lang_String_2");
    if (!ti.hasReturnedFromDirectCall("[clinit]")) {
      parser = AndroidManifestParser.getInstance();
      try {
        parser.parseStream(new ByteArrayInputStream(env.getStringObject(ref).getBytes("UTF-8")));
        packageInfo = parser.getPackageInfo();
        filters =  parser.getFilters();
      } catch (Exception e) {
        logger.severe("Could not parse AndroidManifest.xml file:" + e.getMessage());
        packageInfo = null;
      }
    }
    // If we have reached this point the package has been parsed and we need to populate the PackagManager on
    // the JPF side
    if (packageInfo != null) {
      int packageRef = MJIEnv.NULL;
      int filtersRef = MJIEnv.NULL;
      try {
        packageRef = JVM2JPFConverter.obtainJPFObj(packageInfo, env);
        env.setReferenceField(robj, "packageInfo", packageRef);
        
        filtersRef = JVM2JPFConverter.obtainJPFObj(filters, env);
        env.setReferenceField(robj, "filters", filtersRef);


      } catch (ConversionException e) {
        e.printStackTrace();
      }

    }
  }

  public static String getPackageName() {
    return packageInfo.packageName;
  }

  /**
   * Returns the reference to an array of Intent Filters that was registered for the component in the
   * AndroidManifest.
   * 
   * @param componentName
   *          The string name of the component
   * @return the array of IntentFilters.
   * @throws ConversionException
   */
  @MJI
  public int getFiltersNative(MJIEnv env, int objRef, int componentNameRef) {
    ThreadInfo ti = env.getThreadInfo();

    int filtersRef = MJIEnv.NULL;

    ArrayList<IntentFilter> filters = null;

    String componentName = env.getStringObject(componentNameRef);
    for (Entry<String, ArrayList<IntentFilter>> l : parser.getFilters().entrySet())
      System.out.println("Entry: #" + componentName + "#:#" + l.getKey() + "# "
          + l.getValue().get(0).getAction(0));
    filters = parser.getFilters().get(componentName.trim());
    System.out.println("filters for : #" + componentName + "# " + filters.size());

    if (filters != null) {
      try {
        filtersRef = JVM2JPFConverter.obtainJPFObj(filters.get(0), env);
      } catch (Exception e) {
        System.out.println("FILTERS COULD NOT BE CONVERTED");
        e.printStackTrace();

      }

      System.out.println("filters:" + filtersRef);
      if (filtersRef == MJIEnv.NULL) {
        System.out.println("FILTERS COULD NOT BE CONVERTED");

      }
    }
    return filters.size();

  }

}