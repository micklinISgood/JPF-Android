package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ClinitRequired;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.util.ObjectConverter;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;

public class JPF_android_content_pm_PackageManager {

  // private static ElementInfo packageInfoEI;
  private static AndroidManifestParser parser;
  private static PackageInfo packageInfo;
  private static Map<PackageItemInfo, List<IntentFilter>> filterMap;

  public static void $init____V(MJIEnv env, int robj) {
    ThreadInfo ti = env.getThreadInfo();

    // so that the method is not called twice on return from making direct call
    // if (!ti.hasReturnedFromDirectCall("[UI]")) {
    parser = AndroidManifestParser.getInstance();

    // build path to SUT's AndroidManifest.xml file
    Config conf = env.getConfig();
    String path = conf.getString("path");
    if (path == null || path.length() == 0) {
      // throw error
    } else if (path.endsWith("/")) {
      path += "AndroidManifest.xml";
      parser.parseFile(path);
    } else {
      path += "/AndroidManifest.xml";
      parser.parseFile(path);
    }

    packageInfo = parser.getPackageInfo();
    // if we have reached this point the package has been parsed and we need to populate the packageInfo
    int packageRef = ObjectConverter.JPFObjectFromJavaObject(env, packageInfo);
    env.setReferenceField(robj, "packageInfo", packageRef);
  }

  public static void $init__Ljava_lang_String_2__V(MJIEnv env, int robj, int ref) {
    ThreadInfo ti = env.getThreadInfo();
    if (!ti.hasReturnedFromDirectCall("[clinit]")) {

      // so that the method is not called twice on return from making direct call
      // if (!ti.hasReturnedFromDirectCall("[UI]")) {
      parser = AndroidManifestParser.getInstance();

      // build path to SUT's AndroidManifest.xml file
      try {
        parser.parseStream(new ByteArrayInputStream(env.getStringObject(ref).getBytes("UTF-8")));
      } catch (UnsupportedEncodingException e) {
      }

      packageInfo = parser.getPackageInfo();
      // if we have reached this point the package has been parsed and we need to populate the packageInfo
    }
    int packageRef = ObjectConverter.JPFObjectFromJavaObject(env, packageInfo);
    env.setReferenceField(robj, "packageInfo", packageRef);
  }

  public static String getPackageName() {
    return packageInfo.packageName;

  }
}