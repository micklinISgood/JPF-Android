/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content.pm;

import android.util.Printer;

/**
 * Information you can retrieve about a particular application. This corresponds to information collected from
 * the AndroidManifest.xml's &lt;application&gt; tag.
 */
public class ApplicationInfo extends PackageItemInfo {

  /**
   * Default task affinity of all activities in this application. See {@link ActivityInfo#taskAffinity} for
   * more information. This comes from the "taskAffinity" attribute.
   */
  public String taskAffinity;

  /**
   * Optional name of a permission required to be able to access this application's components. From the
   * "permission" attribute.
   */
  public String permission;

  /**
   * The name of the process this application should run in. From the "process" attribute or, if not set, the
   * same as <var>packageName</var>.
   */
  public String processName;

  /**
   * Class implementing the Application object. From the "class" attribute.
   */
  public String className;

  /**
   * A style resource identifier (in the package's resources) of the description of an application. From the
   * "description" attribute or, if not set, 0.
   */
  public int descriptionRes;

  /**
   * A style resource identifier (in the package's resources) of the default visual theme of the application.
   * From the "theme" attribute or, if not set, 0.
   */
  public int theme;

  /**
   * Class implementing the Application's manage space functionality. From the "manageSpaceActivity"
   * attribute. This is an optional attribute and will be null if applications don't specify it in their
   * manifest
   */
  public String manageSpaceActivityName;

  /**
   * Class implementing the Application's backup functionality. From the "backupAgent" attribute. This is an
   * optional attribute and will be null if the application does not specify it in its manifest.
   * 
   * <p>
   * If android:allowBackup is set to false, this attribute is ignored.
   */
  public String backupAgentName;

  /**
   * The default extra UI options for activities in this application. Set from the
   * {@link android.R.attr#uiOptions} attribute in the activity's manifest.
   */
  public int uiOptions = 0;

  public int flags = 0;

  /**
   * The required smallest screen width the application can run on. If 0, nothing has been specified. Comes
   * from {@link android.R.styleable#AndroidManifestSupportsScreens_requiresSmallestWidthDp
   * android:requiresSmallestWidthDp} attribute of the &lt;supports-screens&gt; tag.
   */
  public int requiresSmallestWidthDp = 0;

  /**
   * The maximum smallest screen width the application is designed for. If 0, nothing has been specified.
   * Comes from {@link android.R.styleable#AndroidManifestSupportsScreens_compatibleWidthLimitDp
   * android:compatibleWidthLimitDp} attribute of the &lt;supports-screens&gt; tag.
   */
  public int compatibleWidthLimitDp = 0;

  /**
   * The maximum smallest screen width the application will work on. If 0, nothing has been specified. Comes
   * from {@link android.R.styleable#AndroidManifestSupportsScreens_largestWidthLimitDp
   * android:largestWidthLimitDp} attribute of the &lt;supports-screens&gt; tag.
   */
  public int largestWidthLimitDp = 0;

  /**
   * Full path to the location of this package.
   */
  public String sourceDir;

  /**
   * Full path to the location of the publicly available parts of this package (i.e. the primary resource
   * package and manifest). For non-forward-locked apps this will be the same as {@link #sourceDir).
   */
  public String publicSourceDir;

  /**
   * Full paths to the locations of extra resource packages this application uses. This field is only used if
   * there are extra resource packages, otherwise it is null.
   * 
   * {@hide}
   */
  public String[] resourceDirs;

  /**
   * Paths to all shared libraries this application is linked against. This field is only set if the
   * {@link PackageManager#GET_SHARED_LIBRARY_FILES PackageManager.GET_SHARED_LIBRARY_FILES} flag was used
   * when retrieving the structure.
   */
  public String[] sharedLibraryFiles;

  /**
   * Full path to a directory assigned to the package for its persistent data.
   */
  public String dataDir;

  /**
   * Full path to the directory where native JNI libraries are stored.
   */
  public String nativeLibraryDir;

  /**
   * The kernel user-ID that has been assigned to this application; currently this is not a unique ID
   * (multiple applications can have the same uid).
   */
  public int uid;

  /**
   * The minimum SDK version this application targets. It may run on earlier versions, but it knows how to
   * work with any new behavior added at this version. Will be
   * {@link android.os.Build.VERSION_CODES#CUR_DEVELOPMENT} if this is a development build and the app is
   * targeting that. You should compare that this number is >= the SDK version number at which your behavior
   * was introduced.
   */
  public int targetSdkVersion;

  /**
   * When false, indicates that all components within this application are considered disabled, regardless of
   * their individually set enabled status.
   */
  public boolean enabled = true;

  /**
   * For convenient access to the current enabled setting of this app.
   * 
   * @hide
   */

  /**
   * For convenient access to package's install location.
   * 
   * @hide
   */

  public void dump(Printer pw, String prefix) {
    super.dumpFront(pw, prefix);
    if (className != null) {
      pw.println(prefix + "className=" + className);
    }
    if (permission != null) {
      pw.println(prefix + "permission=" + permission);
    }
    pw.println(prefix + "processName=" + processName);
    pw.println(prefix + "taskAffinity=" + taskAffinity);
    pw.println(prefix + "uid=" + uid + " flags=0x" + Integer.toHexString(flags) + " theme=0x"
        + Integer.toHexString(theme));
    pw.println(prefix + "requiresSmallestWidthDp=" + requiresSmallestWidthDp + " compatibleWidthLimitDp="
        + compatibleWidthLimitDp + " largestWidthLimitDp=" + largestWidthLimitDp);
    pw.println(prefix + "sourceDir=" + sourceDir);
    if (sourceDir == null) {
      if (publicSourceDir != null) {
        pw.println(prefix + "publicSourceDir=" + publicSourceDir);
      }
    } else if (!sourceDir.equals(publicSourceDir)) {
      pw.println(prefix + "publicSourceDir=" + publicSourceDir);
    }
    if (resourceDirs != null) {
      pw.println(prefix + "resourceDirs=" + resourceDirs);
    }
    pw.println(prefix + "dataDir=" + dataDir);
    if (sharedLibraryFiles != null) {
      pw.println(prefix + "sharedLibraryFiles=" + sharedLibraryFiles);
    }
    pw.println(prefix + "enabled=" + enabled + " targetSdkVersion=" + targetSdkVersion);
    if (manageSpaceActivityName != null) {
      pw.println(prefix + "manageSpaceActivityName=" + manageSpaceActivityName);
    }
    if (descriptionRes != 0) {
      pw.println(prefix + "description=0x" + Integer.toHexString(descriptionRes));
    }
    if (uiOptions != 0) {
      pw.println(prefix + "uiOptions=0x" + Integer.toHexString(uiOptions));
    }
    super.dumpBack(pw, prefix);
  }

  public ApplicationInfo() {
  }

  public ApplicationInfo(ApplicationInfo orig) {
    super(orig);
    taskAffinity = orig.taskAffinity;
    permission = orig.permission;
    processName = orig.processName;
    className = orig.className;
    theme = orig.theme;
    flags = orig.flags;
    requiresSmallestWidthDp = orig.requiresSmallestWidthDp;
    compatibleWidthLimitDp = orig.compatibleWidthLimitDp;
    largestWidthLimitDp = orig.largestWidthLimitDp;
    sourceDir = orig.sourceDir;
    publicSourceDir = orig.publicSourceDir;
    nativeLibraryDir = orig.nativeLibraryDir;
    resourceDirs = orig.resourceDirs;
    sharedLibraryFiles = orig.sharedLibraryFiles;
    dataDir = orig.dataDir;
    uid = orig.uid;
    targetSdkVersion = orig.targetSdkVersion;
    enabled = orig.enabled;
    manageSpaceActivityName = orig.manageSpaceActivityName;
    descriptionRes = orig.descriptionRes;
    uiOptions = orig.uiOptions;
  }

  public String toString() {
    return "ApplicationInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + packageName + "}";
  }

  public int describeContents() {
    return 0;
  }

  /**
   * Retrieve the textual description of the application. This will call back on the given PackageManager to
   * load the description from the application.
   * 
   * @param pm
   *          A PackageManager from which the label can be loaded; usually the PackageManager from which you
   *          originally retrieved this item.
   * 
   * @return Returns a CharSequence containing the application's description. If there is no description, null
   *         is returned.
   */
  public CharSequence loadDescription(PackageManager pm) {
    if (descriptionRes != 0) {
      CharSequence label = pm.getText(packageName, descriptionRes, this);
      if (label != null) {
        return label;
      }
    }
    return null;
  }

  private boolean isPackageUnavailable(PackageManager pm) {
    return false;
  }

  /**
   * @hide
   */
  @Override
  protected ApplicationInfo getApplicationInfo() {
    return this;
  }
}
