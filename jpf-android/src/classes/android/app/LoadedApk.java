package android.app;

import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;

public class LoadedApk {

  private final ActivityThread mActivityThread;
  private final ApplicationInfo mApplicationInfo;
  final String mPackageName;
  // private final String mAppDir ="";
  // private final String mResDir ="";
  // private final String[] mSharedLibraries;
  // private final String mDataDir;
  // private final String mLibDir;
  // private final File mDataDirFile;
  // private final ClassLoader mBaseClassLoader;
  // private final boolean mSecurityViolation;
  // private final boolean mIncludeCode;
  // public final CompatibilityInfoHolder mCompatibilityInfo = new CompatibilityInfoHolder();
  // Resources mResources;
  // private ClassLoader mClassLoader;
  private Application mApplication;

  /**
   * Create information about a new .apk
   * 
   * NOTE: This constructor is called with ActivityThread's lock held, so MUST NOT call back out to the
   * activity manager.
   */
  public LoadedApk(ActivityThread activityThread, ApplicationInfo aInfo, CompatibilityInfo compatInfo,
      ActivityThread mainThread, ClassLoader baseLoader, boolean securityViolation, boolean includeCode) {
    mActivityThread = activityThread;
    mApplicationInfo = init();
    mPackageName = aInfo.packageName;
    // mAppDir = aInfo.sourceDir;
    // mResDir = aInfo.uid == Process.myUid() ? aInfo.sourceDir
    // : aInfo.publicSourceDir;
    // mSharedLibraries = aInfo.sharedLibraryFiles;
    // mDataDir = aInfo.dataDir;
    // mDataDirFile = mDataDir != null ? new File(mDataDir) : null;
    // mLibDir = aInfo.nativeLibraryDir;
    // mBaseClassLoader = baseLoader;
    // mSecurityViolation = securityViolation;
    // mIncludeCode = includeCode;
    // mCompatibilityInfo.set(compatInfo);
    //
    // if (mAppDir == null) {
    // if (ActivityThread.mSystemContext == null) {
    // ActivityThread.mSystemContext =
    // ContextImpl.createSystemContext(mainThread);
    // ActivityThread.mSystemContext.getResources().updateConfiguration(
    // mainThread.getConfiguration(),
    // mainThread.getDisplayMetricsLocked(compatInfo, false),
    // compatInfo);
    // //Slog.i(TAG, "Created system resources "
    // // + mSystemContext.getResources() + ": "
    // // + mSystemContext.getResources().getConfiguration());
    // }
    // mClassLoader = ActivityThread.mSystemContext.getClassLoader();
    // mResources = ActivityThread.mSystemContext.getResources();
  }

  private native ApplicationInfo init();
  
  

  public ClassLoader getClassLoader() {
    return ClassLoader.getSystemClassLoader();
  }

  public Application makeApplication(boolean b, Instrumentation mInstrumentation) {
    // TODO Auto-generated method stub
    return null;
  }

  public String getPackageName() {
    return mPackageName;
  }
  
  

}
