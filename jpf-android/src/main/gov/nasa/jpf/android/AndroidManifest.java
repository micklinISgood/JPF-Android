package gov.nasa.jpf.android;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

/**
 * Stores all info obtained by parsing the AndroidManifest file.
 * 
 * @author Heila
 *
 */
public class AndroidManifest {

  /** Contains the base packageInfo of the application */
  PackageInfo packageInfo;

  /**
   * Temporary stores the list of Activities. As we do not know how many Activities are defined in the
   * Manifest we store them in an expandable ArrayList and afterwards copy them into PackageInfo's activities
   * field.
   */
  ArrayList<ActivityInfo> activities;
  ArrayList<ServiceInfo> services;
  ArrayList<ProviderInfo> providers;
  ArrayList<ActivityInfo> receivers;

  ArrayList<PermissionInfo> permissions;

  /** List of Filters by componentname */
  HashMap<String, ArrayList<IntentFilter>> filterMap;

  String packageName;

  ApplicationInfo applicationInfo;

  public AndroidManifest() {
    packageInfo = new PackageInfo();

    activities = new ArrayList<ActivityInfo>();
    services = new ArrayList<ServiceInfo>();
    providers = new ArrayList<ProviderInfo>();
    receivers = new ArrayList<ActivityInfo>();
    filterMap = new HashMap<String, ArrayList<IntentFilter>>();
    packageName = "";
    applicationInfo = new ApplicationInfo();
  }

  public String getPackageNameAsDirectoryPath() {
    return packageName.replace('.', File.separatorChar);
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
  protected HashMap<String, ArrayList<IntentFilter>> getFilters() {
    return filterMap;
  }
}
