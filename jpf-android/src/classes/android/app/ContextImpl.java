/*
 * Copyright (C) 2006 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ReceiverCallNotAllowedException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.LayoutInflater;

/**
 * Restricted Context given to BroadcastReceivers to make sure they can't bind
 * to a service or register a receiver usind a handler
 * 
 * 
 */
class ReceiverRestrictedContext extends ContextWrapper {
  ReceiverRestrictedContext(Context base) {
    super(base);
  }

  @Override
  public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    return registerReceiver(receiver, filter, null, null);
  }

  @Override
  public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission,
                                 Handler scheduler) {
    throw new ReceiverCallNotAllowedException(
        "IntentReceiver components are not allowed to register to receive intents");
  }

  @Override
  public boolean bindService(Intent service, ServiceConnection conn, int flags) {
    throw new ReceiverCallNotAllowedException("IntentReceiver components are not allowed to bind to services");
  }
}

/**
 * Common implementation of Context API, which provides the base context object
 * for Activity and other
 * application components.
 */
public class ContextImpl extends Context {
  private final static String TAG = "Context";
  private final static boolean DEBUG_CONTEXT = true;

  private static final HashMap<String, SharedPreferencesImpl> sSharedPrefs = new HashMap<String, SharedPreferencesImpl>();

  /* package */LoadedApk mLoadedPackageInfo;
  private String mBasePackageName;
  private Resources mResources;
  /* package */ActivityThread mMainThread;
  private Context mOuterContext;
  private IBinder mActivityToken = null;
  // private int mThemeResource = 0;
  // private Resources.Theme mTheme = null;
  private PackageManager mPackageManager;
  private Context mReceiverRestrictedContext = null;

  // private boolean mRestricted;

  //lock for shared prefs, files and database
  private final Object mSync = new Object();

  // private File mDatabasesDir;
  // private File mPreferencesDir;
  // private File mFilesDir;
  // private File mCacheDir;
  // private File mObbDir;
  // private File mExternalFilesDir;
  // private File mExternalCacheDir;
  //
  // private static final String[] EMPTY_FILE_LIST = {};

  ContextImpl() {
    mOuterContext = this;

    if (DEBUG_CONTEXT)
      Log.i(TAG, "Creating new Context.");
  }

  final void init(LoadedApk packageInfo, IBinder activityToken, ActivityThread mainThread) {
    init(packageInfo, activityToken, mainThread, null, null);
  }

  final void init(LoadedApk loadedPackageInfo, IBinder activityToken, ActivityThread mainThread,
                  Resources container, String basePackageName) {

    mLoadedPackageInfo = loadedPackageInfo;
    mBasePackageName = basePackageName != null ? basePackageName : loadedPackageInfo.mPackageName;
    mResources = mLoadedPackageInfo.getResources(mainThread);

    //    if (mResources != null
    //        && container != null
    //        && container.getCompatibilityInfo().applicationScale != mResources.getCompatibilityInfo().applicationScale) {
    //      if (DEBUG) {
    //        Log.d(TAG, "loaded context has different scaling. Using container's" + " compatiblity info:"
    //            + container.getDisplayMetrics());
    //      }
    //      // mResources = mainThread
    //      // .getTopLevelResources(mPackageInfo.getResDir(), container.getCompatibilityInfo());
    //    }
    mMainThread = mainThread;
    // mContentResolver = new ApplicationContentResolver(this, mainThread);

    setActivityToken(activityToken);
    if (DEBUG_CONTEXT)
      Log.i(TAG, "Setting up Context with Resources=" + mResources);

  }

  final void init(Resources resources, ActivityThread mainThread) {
    mLoadedPackageInfo = null;
    mBasePackageName = null;
    mResources = resources;
    mMainThread = mainThread;
    // mContentResolver = new ApplicationContentResolver(this, mainThread);
  }

  final void scheduleFinalCleanup(String who, String what) {
    mMainThread.scheduleContextCleanup(this, who, what);
  }

  final void performFinalCleanup(String who, String what) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "Cleanup up context: " + this);
    mLoadedPackageInfo.removeContextRegistrations(getOuterContext(), who, what);
  }

  final Context getReceiverRestrictedContext() {
    if (mReceiverRestrictedContext != null) {
      return mReceiverRestrictedContext;
    }
    return mReceiverRestrictedContext = new ReceiverRestrictedContext(getOuterContext());
  }

  final void setActivityToken(IBinder token) {
    mActivityToken = token;
  }

  final void setOuterContext(Context context) {
    mOuterContext = context;
  }

  final Context getOuterContext() {
    return mOuterContext;
  }

  final IBinder getActivityToken() {
    return mActivityToken;
  }

  @Override
  public AssetManager getAssets() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public Resources getResources() {
    return mResources;
  }

  @Override
  public PackageManager getPackageManager() {
    if (mPackageManager == null) {
      mPackageManager = ActivityThread.getPackageManager();
    }
    return mPackageManager;

  }

  @Override
  public ContentResolver getContentResolver() {
    // TODO
    throw new UnsupportedOperationException();
  }

  @Override
  public Looper getMainLooper() {
    return mMainThread.getLooper();
  }

  @Override
  public Context getApplicationContext() {
    return (mLoadedPackageInfo != null) ? mLoadedPackageInfo.getApplication() : mMainThread.getApplication();
  }

  @Override
  public void setTheme(int resid) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Theme getTheme() {
    throw new UnsupportedOperationException();
  }

  // TODO @Override
  // public Resources.Theme getTheme() {
  // if (mTheme == null) {
  // mThemeResource = Resources.selectDefaultTheme(mThemeResource,
  // getOuterContext().getApplicationInfo().targetSdkVersion);
  // mTheme = mResources.newTheme();
  // mTheme.applyStyle(mThemeResource, true);
  // }
  // return mTheme;
  // }

  @Override
  public ClassLoader getClassLoader() {
    return mLoadedPackageInfo != null ? mLoadedPackageInfo.getClassLoader() : ClassLoader
        .getSystemClassLoader();
  }

  @Override
  public String getPackageName() {
    if (mLoadedPackageInfo != null) {
      return mLoadedPackageInfo.getPackageName();
    }
    throw new RuntimeException("Not supported in system context");
  }

  @Override
  public ApplicationInfo getApplicationInfo() {
    if (mLoadedPackageInfo != null) {
      return mLoadedPackageInfo.getApplicationInfo();
    }
    throw new RuntimeException("Not supported in system context");
  }

  @Override
  public String getPackageResourcePath() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPackageCodePath() {
    // if (mPackageInfo != null) {
    // return mPackageInfo.getAppDir();
    // }
    throw new UnsupportedOperationException();
  }

  public File getSharedPrefsFile(String name) {
    return null;// TODO makeFilename(getPreferencesDir(), name + ".xml");
  }

  @Override
  public SharedPreferences getSharedPreferences(String name, int mode) {
    SharedPreferencesImpl sp;
    synchronized (sSharedPrefs) {
      sp = sSharedPrefs.get(name);
      if (sp == null) {
        File prefsFile = getSharedPrefsFile(name);
        sp = new SharedPreferencesImpl(prefsFile, mode);
        sSharedPrefs.put(name, sp);
        if (DEBUG_CONTEXT)
          Log.i(TAG, "Returning Shared Prefs (name=" + name + " found=" + (sp != null) + ")");
        return sp;
      }
    }
    if ((mode & Context.MODE_MULTI_PROCESS) != 0
        || getApplicationInfo().targetSdkVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
      // If somebody else (some other process) changed the prefs
      // file behind our back, we reload it. This has been the
      // historical (if undocumented) behavior.
      sp.startReloadIfChangedUnexpectedly();
    }
    if (DEBUG_CONTEXT)
      Log.i(TAG, "Returning Shared Prefs (name=" + name + " found=" + (sp != null) + ")");
    return sp;
  }

  private File getPreferencesDir() {
    throw new UnsupportedOperationException();
    // TODO
  }

  @Override
  public FileInputStream openFileInput(String name) throws FileNotFoundException {
    throw new UnsupportedOperationException();
  }

  @Override
  public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean deleteFile(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public File getFileStreamPath(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public File getFilesDir() {
    throw new UnsupportedOperationException();
  }

  @Override
  public File getExternalFilesDir(String type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public File getObbDir() {
    throw new UnsupportedOperationException();
  }

  @Override
  public File getCacheDir() {
    throw new UnsupportedOperationException();
  }

  @Override
  public File getExternalCacheDir() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String[] fileList() {
    throw new UnsupportedOperationException();
  }

  @Override
  public File getDir(String name, int mode) {
    throw new UnsupportedOperationException();
  }

  @Override
  public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
    throw new UnsupportedOperationException();
    // TODO
  }

  @Override
  public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
                                             DatabaseErrorHandler errorHandler) {
    // TODO
    throw new UnsupportedOperationException();

  }

  @Override
  public boolean deleteDatabase(String name) {
    // TODO
    throw new UnsupportedOperationException();

  }

  @Override
  public File getDatabasePath(String name) {
    // TODO
    throw new UnsupportedOperationException();

  }

  @Override
  public String[] databaseList() {
    // TODO
    throw new UnsupportedOperationException();

  }

  @Override
  @Deprecated
  public Drawable getWallpaper() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public Drawable peekWallpaper() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public int getWallpaperDesiredMinimumWidth() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public int getWallpaperDesiredMinimumHeight() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setWallpaper(Bitmap bitmap) throws IOException {
    throw new UnsupportedOperationException();

  }

  @Override
  @Deprecated
  public void setWallpaper(InputStream data) throws IOException {
    throw new UnsupportedOperationException();

  }

  @Override
  public void startActivity(Intent intent) {
    if (true) { // TODO (intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) == 0
      throw new AndroidRuntimeException("Calling startActivity() from outside of an Activity "
          + " context requires the FLAG_ACTIVITY_NEW_TASK flag."
          + " Is this really what you want? This is not supported in jpf-android");
    }
    //    mMainThread.getInstrumentation().execStartActivity(getOuterContext(), null, null, (Activity) null,
    //        intent, -1);

  }

  @Override
  public void startActivities(Intent[] intents) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues,
                                int extraFlags) throws IntentSender.SendIntentException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void sendBroadcast(Intent intent) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "sendBroadcast(intent=" + intent + ")");
    //TODO String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
    try {
      intent.setAllowFds(false);
      ActivityManagerNative.getDefault().broadcastIntent(intent, null, null, Activity.RESULT_OK, null, null,
          null, false, false);
    } catch (RemoteException e) {
    }
  }

  @Override
  public void sendBroadcast(Intent intent, String receiverPermission) {
    //    String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
    if (DEBUG_CONTEXT)
      Log.i(TAG, "sendBroadcast(intent=" + intent + " permission=" + receiverPermission + ")");
    try {
      intent.setAllowFds(false);
      ActivityManagerNative.getDefault().broadcastIntent(intent, null, null, Activity.RESULT_OK, null, null,
          receiverPermission, false, false);
    } catch (RemoteException e) {
    }
  }

  @Override
  public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "sendOrderedBroadcast(intent=" + intent + " permission=" + receiverPermission + ")");
    //    String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
    try {
      intent.setAllowFds(false);
      ActivityManagerNative.getDefault().broadcastIntent(intent, null, null, Activity.RESULT_OK, null, null,
          receiverPermission, true, false);
    } catch (RemoteException e) {
    }
  }

  @Override
  public void sendOrderedBroadcast(Intent intent, String receiverPermission,
                                   BroadcastReceiver resultReceiver, Handler scheduler, int initialCode,
                                   String initialData, Bundle initialExtras) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "sendOrderedBroadcast(intent=" + intent + " permission=" + receiverPermission + ")");
    IIntentReceiver rd = null;
    if (resultReceiver != null) {
      if (mLoadedPackageInfo != null) {
        if (scheduler == null) {
          scheduler = mMainThread.getHandler();
        }
        rd = mLoadedPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler,
            mMainThread.getInstrumentation(), false);
      } else {
        if (scheduler == null) {
          scheduler = mMainThread.getHandler();
        }
        rd = new LoadedApk.ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, false)
            .getIIntentReceiver();
      }
    }
    String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
    try {
      intent.setAllowFds(false);
      ActivityManagerNative.getDefault().broadcastIntent(intent, resolvedType, rd, initialCode, initialData,
          initialExtras, receiverPermission, true, false);
    } catch (RemoteException e) {
    }
  }

  @Override
  public void sendStickyBroadcast(Intent intent) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "sendStickyBroadcast(intent=" + intent + ")");
    try {
      intent.setAllowFds(false);
      ActivityManagerNative.getDefault().broadcastIntent(intent, null, null, Activity.RESULT_OK, null, null,
          null, false, true);
    } catch (RemoteException e) {
    }
  }

  @Override
  public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler,
                                         int initialCode, String initialData, Bundle initialExtras) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "sendStickyOrderedBroadcast(intent=" + intent + ")");

    IIntentReceiver rd = null;
    if (resultReceiver != null) {
      if (mLoadedPackageInfo != null) {
        if (scheduler == null) {
          scheduler = mMainThread.getHandler();
        }
        rd = mLoadedPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler,
            mMainThread.getInstrumentation(), false);
      } else {
        if (scheduler == null) {
          scheduler = mMainThread.getHandler();
        }
        rd = new LoadedApk.ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, false)
            .getIIntentReceiver();
      }
    }
    String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
    try {
      intent.setAllowFds(false);
      ActivityManagerNative.getDefault().broadcastIntent(intent, resolvedType, rd, initialCode, initialData,
          initialExtras, null, true, true);
    } catch (RemoteException e) {
    }
  }

  @Override
  public void removeStickyBroadcast(Intent intent) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "removeStickyBroadcast(intent=" + intent + ")");

    String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
    if (resolvedType != null) {
      intent = new Intent(intent);
      intent.setDataAndType(intent.getData(), resolvedType);
    }
    try {
      intent.setAllowFds(false);
      ActivityManagerNative.getDefault().unbroadcastIntent(mMainThread.getApplicationThread(), intent);
    } catch (RemoteException e) {
    }
  }

  @Override
  public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    return registerReceiver(receiver, filter, null, null);
  }

  @Override
  public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission,
                                 Handler scheduler) {
    return registerReceiverInternal(receiver, filter, broadcastPermission, scheduler, getOuterContext());
  }

  private Intent registerReceiverInternal(BroadcastReceiver receiver, IntentFilter filter,
                                          String broadcastPermission, Handler scheduler, Context context) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "registerReceiver(receiver=" + receiver + " filter=" + filter + " permission="
          + broadcastPermission + ")");

    IIntentReceiver rd = null;
    if (receiver != null) {
      if (mLoadedPackageInfo != null && context != null) {
        if (scheduler == null) {
          scheduler = mMainThread.getHandler();
        }
        rd = mLoadedPackageInfo.getReceiverDispatcher(receiver, context, scheduler,
            mMainThread.getInstrumentation(), true);
      } else {
        if (scheduler == null) {
          scheduler = mMainThread.getHandler();
        }
        rd = new LoadedApk.ReceiverDispatcher(receiver, context, scheduler, null, true).getIIntentReceiver();
      }
    }
    try {
      return ActivityManagerNative.getDefault().registerReceiver(mMainThread.getApplicationThread(),
          mBasePackageName, rd, filter, broadcastPermission);
    } catch (RemoteException e) {
      return null;
    }
  }

  @Override
  public void unregisterReceiver(BroadcastReceiver receiver) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "unregisterReceiver(receiver=" + receiver + ")");

    if (mLoadedPackageInfo != null) {
      IIntentReceiver rd = mLoadedPackageInfo.forgetReceiverDispatcher(getOuterContext(), receiver);
      try {
        ActivityManagerNative.getDefault().unregisterReceiver(rd);
      } catch (RemoteException e) {
      }
    } else {
      throw new RuntimeException("Not supported in system context");
    }
  }

  @Override
  public ComponentName startService(Intent service) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "startService(Intent=" + service + ")");

    service.setAllowFds(false);
    ComponentName cn = ActivityManagerNative.getDefault().startService(service, "");
    if (cn != null && cn.getPackageName().equals("!")) {
      throw new SecurityException("Not allowed to start service " + service + " without permission "
          + cn.getClassName());
    }
    return cn;
  }

  @Override
  public boolean stopService(Intent service) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "stopService(Intent=" + service + ")");

    service.setAllowFds(false);
    int res = ActivityManagerNative.getDefault().stopService(service, "");
    if (res < 0) {
      throw new SecurityException("Not allowed to stop service " + service);
    }
    return res != 0;

  }

  @Override
  public boolean bindService(Intent service, ServiceConnection conn, int flags) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "bindService(Intent=" + service + ")");

    ServiceConnection sd;
    // if (mPackageInfo != null) {
    // sd = mPackageInfo.getServiceDisPapatcher(conn, getOuterContext(), mMainThread.getHandler(), flags);
    // } else {
    // throw new RuntimeException("Not supported in system context");
    // }
    IBinder token = getActivityToken();
    // if (token == null
    // && (flags & BIND_AUTO_CREATE) == 0
    // && mPackageInfo != null
    // && mPackageInfo.getApplicationInfo().targetSdkVersion <
    // android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
    // flags |= BIND_WAIVE_PRIORITY;
    // }
    //    service.setAllowFds(false);
    //    int res = ActivityManagerNative.getDefault().bindService(getActivityToken(), service,
    //        service.resolveTypeIfNeeded(getContentResolver()), conn, flags);
    //    if (res < 0) {
    //      throw new SecurityException("Not allowed to bind to service " + service);
    //    }
    //    return res != 0;
    return true;
  }

  @Override
  public void unbindService(ServiceConnection conn) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "unbindService(ServiceConnection=" + conn + ")");

    if (mLoadedPackageInfo != null) {
      // IServiceConnection sd = mPackageInfo.forgetServiceDispatcher(
      // getOuterContext(), conn);
      ActivityManagerNative.getDefault().unbindService(conn);
    } else {
      throw new RuntimeException("Not supported in system context");
    }
  }

  @Override
  public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
    // TODO
    return false;
  }

  @Override
  public Object getSystemService(String name) {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "getSystemService(name=" + name + ")");
    if (name.equals(Context.LAYOUT_INFLATER_SERVICE))
      return new LayoutInflater(this);
    return ServiceManager.getSystemService(name);
  }

  @Override
  public int checkPermission(String permission, int pid, int uid) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int checkCallingPermission(String permission) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int checkCallingOrSelfPermission(String permission) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void enforcePermission(String permission, int pid, int uid, String message) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void enforceCallingPermission(String permission, String message) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void enforceCallingOrSelfPermission(String permission, String message) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void revokeUriPermission(Uri uri, int modeFlags) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int checkCallingUriPermission(Uri uri, int modeFlags) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid,
                                int modeFlags) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid,
                                   int modeFlags, String message) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Context createPackageContext(String packageName, int flags)
      throws PackageManager.NameNotFoundException {
    if (DEBUG_CONTEXT)
      Log.i(TAG, "createPackageContext(packageName=" + packageName + " flags=" + flags + ")");

    LoadedApk pi = mMainThread.getPackageInfo(packageName);
    if (pi != null) {
      ContextImpl c = new ContextImpl();
      // c.mRestricted = (flags & CONTEXT_RESTRICTED) == CONTEXT_RESTRICTED;
      c.init(pi, null, mMainThread, mResources, mBasePackageName);
      if (c.mResources != null) {
        return c;
      }
    }

    // Should be a better exception.
    throw new PackageManager.NameNotFoundException("Application package " + packageName + " not found");
  }

  /**
   * Creates a context for the Android OS. The main thead is the main thread of
   * the system to schedule messages in etc.
   * 
   * @param mainThread
   * @return
   */
  public static ContextImpl createSystemContext(ActivityThread thread) {
    ContextImpl context = new ContextImpl();
    context.init(Resources.getSystem(), thread);
    return context;
  }

  @Override
  public void clearWallpaper() throws IOException {
    // TODO Auto-generated method stub
    
  }

}
