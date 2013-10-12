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

/**
 * Overall information about the contents of a package. This corresponds to all of the information collected
 * from AndroidManifest.xml.
 */
public class PackageInfo {
  /**
   * The name of this package. From the &lt;manifest&gt; tag's "name" attribute.
   */
  public String packageName = "";

  /**
   * The version number of this package, as specified by the &lt;manifest&gt; tag's
   * {@link android.R.styleable#AndroidManifest_versionCode versionCode} attribute.
   */
  public int versionCode =0;

  /**
   * The version name of this package, as specified by the &lt;manifest&gt; tag's
   * {@link android.R.styleable#AndroidManifest_versionName versionName} attribute.
   */
  public String versionName="teset";

  /**
   * The shared user ID name of this package, as specified by the &lt;manifest&gt; tag's
   * {@link android.R.styleable#AndroidManifest_sharedUserId sharedUserId} attribute.
   */
  public String sharedUserId = "";

  /**
   * The shared user ID label of this package, as specified by the &lt;manifest&gt; tag's
   * {@link android.R.styleable#AndroidManifest_sharedUserLabel sharedUserLabel} attribute.
   */
  public int sharedUserLabel =0;

  /**
   * Information collected from the &lt;application&gt; tag, or null if there was none.
   */
  public ApplicationInfo applicationInfo = new ApplicationInfo();

  /**
   * The time at which the app was first installed. Units are as per {@link System#currentTimeMillis()}.
   */
  public long firstInstallTime =0;

  /**
   * The time at which the app was last updated. Units are as per {@link System#currentTimeMillis()}.
   */
  public long lastUpdateTime = 0;

  /**
   * All kernel group-IDs that have been assigned to this package. This is only filled in if the flag
   * {@link PackageManager#GET_GIDS} was set.
   */
  public int[] gids = new int[0];

  /**
   * Array of all {@link android.R.styleable#AndroidManifestActivity &lt;activity&gt;} tags included under
   * &lt;application&gt;, or null if there were none. This is only filled in if the flag
   * {@link PackageManager#GET_ACTIVITIES} was set.
   */
  public ActivityInfo[] activities = new ActivityInfo[0];

  /**
   * Array of all {@link android.R.styleable#AndroidManifestReceiver &lt;receiver&gt;} tags included under
   * &lt;application&gt;, or null if there were none. This is only filled in if the flag
   * {@link PackageManager#GET_RECEIVERS} was set.
   */
  public ActivityInfo[] receivers = new ActivityInfo[0];

  /**
   * Array of all {@link android.R.styleable#AndroidManifestService &lt;service&gt;} tags included under
   * &lt;application&gt;, or null if there were none. This is only filled in if the flag
   * {@link PackageManager#GET_SERVICES} was set.
   */
  public ServiceInfo[] services = new ServiceInfo[0];

  /**
   * Array of all {@link android.R.styleable#AndroidManifestProvider &lt;provider&gt;} tags included under
   * &lt;application&gt;, or null if there were none. This is only filled in if the flag
   * {@link PackageManager#GET_PROVIDERS} was set.
   */
  public ProviderInfo[] providers = new ProviderInfo[0];

  /**
   * Array of all {@link android.R.styleable#AndroidManifestInstrumentation &lt;instrumentation&gt;} tags
   * included under &lt;manifest&gt;, or null if there were none. This is only filled in if the flag
   * {@link PackageManager#GET_INSTRUMENTATION} was set.
   */
  public InstrumentationInfo[] instrumentation = new InstrumentationInfo[0];

  /**
   * Array of all {@link android.R.styleable#AndroidManifestPermission &lt;permission&gt;} tags included under
   * &lt;manifest&gt;, or null if there were none. This is only filled in if the flag
   * {@link PackageManager#GET_PERMISSIONS} was set.
   */
  public PermissionInfo[] permissions = new PermissionInfo[0];

  /**
   * Array of all {@link android.R.styleable#AndroidManifestUsesPermission &lt;uses-permission&gt;} tags
   * included under &lt;manifest&gt;, or null if there were none. This is only filled in if the flag
   * {@link PackageManager#GET_PERMISSIONS} was set. This list includes all permissions requested, even those
   * that were not granted or known by the system at install time.
   */
  public String[] requestedPermissions = new String[0];

  /**
   * Array of all signatures read from the package file. This is only filled in if the flag
   * {@link PackageManager#GET_SIGNATURES} was set.
   */
  public Signature[] signatures = new Signature[0];

  /**
   * Application specified preferred configuration
   * {@link android.R.styleable#AndroidManifestUsesConfiguration &lt;uses-configuration&gt;} tags included
   * under &lt;manifest&gt;, or null if there were none. This is only filled in if the flag
   * {@link PackageManager#GET_CONFIGURATIONS} was set.
   */
  public ConfigurationInfo[] configPreferences = new ConfigurationInfo[0];

  /**
   * The features that this application has said it requires.
   */
  public FeatureInfo[] reqFeatures = new FeatureInfo[0];

  public PackageInfo() {
    System.out.println(versionName);
  }

  public String toString() {
    return "PackageInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + packageName + "}";
  }

  public int describeContents() {
    return 0;
  }

}
