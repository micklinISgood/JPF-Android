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
import nhandler.conversion.ConversionException;
import nhandler.conversion.jvm2jpf.JVM2JPFConverter;

/**
 * Responsible for parsing and setting up Package information that is used by PackageManager.
 * 
 * TODO: - Bring Filters over as well or provide call back for resolving components natively.
 * 
 * @author Heila van der Merwe
 */
public class JPF_android_content_pm_PackageManager extends NativePeer {
  private static final JPFLogger logger = JPF.getLogger("JPF_android_content_pm_PackageManager");

  /**
   * Intercept default constructor and initialize package information.
   * 
   * @param env
   * @param robj
   */
  @MJI
  public void init0(MJIEnv env, int robj) {
    try {
      int packageRef = JVM2JPFConverter.obtainJPFObj(AndroidProjectInfo.get().getAndroidManifest()
          .getPackageInfo(), env);
      env.setReferenceField(robj, "packageInfo", packageRef);

      int filtersRef = JVM2JPFConverter.obtainJPFObj(AndroidProjectInfo.get().getAndroidManifest()
          .getFilters(), env);
      env.setReferenceField(robj, "filters", filtersRef);

    } catch (ConversionException e) {
      e.printStackTrace();
      logger.severe("Could not initialize PackageManager:" + e.getMessage());

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
//    ThreadInfo ti = env.getThreadInfo();
//    logger.info("Testing init__Ljava_lang_String_2");
//    AndroidManifestParser parser = AndroidManifestParser.getInstance();
//    try {
//      AndroidManifest androidManifest = parser.parse(new ByteArrayInputStream(env.getStringObject(ref)
//          .getBytes("UTF-8")));
//    } catch (Exception e) {
//      logger.severe("Could not parse AndroidManifest.xml file:" + e.getMessage());
//    }
//
//    // If we have reached this point the package has been parsed and we need to populate the PackagManager on
//    // the JPF side
//    if (packageInfo != null) {
//      int packageRef = MJIEnv.NULL;
//      int filtersRef = MJIEnv.NULL;
//      try {
//        packageRef = JVM2JPFConverter.obtainJPFObj(packageInfo, env);
//        env.setReferenceField(robj, "packageInfo", packageRef);
//
//        filtersRef = JVM2JPFConverter.obtainJPFObj(filters, env);
//        env.setReferenceField(robj, "filters", filtersRef);
//
//      } catch (ConversionException e) {
//        e.printStackTrace();
//      }
//
//    }
  }
  

}