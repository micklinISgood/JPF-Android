/*
 * Copyright (C) 2006 The Android Open Source Project
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

package android.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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

/**
 * Common implementation of Context API, which provides the base context object for Activity and other
 * application components.
 */
class ContextImpl extends Context {
  /* package */ActivityThread mMainThread;
  private final static String TAG = "ApplicationContext";
  private final static boolean DEBUG = false;
  private Context mOuterContext;
  private LoadedApk mPackageInfo;

  ContextImpl() {
    mOuterContext = this;
  }

  final void init(LoadedApk packageInfo, IBinder activityToken, ActivityThread mainThread) {
    init(packageInfo, activityToken, mainThread, null, null);
  }

  final void init(LoadedApk packageInfo, IBinder activityToken, ActivityThread mainThread,
                  Resources container, String basePackageName) {
    mPackageInfo = packageInfo;
    // mBasePackageName = basePackageName != null ? basePackageName : packageInfo.mPackageName;
    // mResources = mPackageInfo.getResources(mainThread);

    // if (mResources != null
    // && container != null
    // && container.getCompatibilityInfo().applicationScale !=
    // mResources.getCompatibilityInfo().applicationScale) {
    // if (DEBUG) {
    // Log.d(TAG, "loaded context has different scaling. Using container's" + " compatiblity info:"
    // + container.getDisplayMetrics());
    // }
    // mResources = mainThread
    // .getTopLevelResources(mPackageInfo.getResDir(), container.getCompatibilityInfo());
    // }
    mMainThread = mainThread;
    // mContentResolver = new ApplicationContentResolver(this, mainThread);

    //TODO setActivityToken(activityToken);
  }

  final void setOuterContext(Context context) {
    mOuterContext = context;
  }

  final Context getOuterContext() {
    return mOuterContext;
  }

  @Override
  public AssetManager getAssets() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Resources getResources() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PackageManager getPackageManager() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ContentResolver getContentResolver() {
    // TODO
    return null;
  }

  @Override
  public Looper getMainLooper() {
    return mMainThread.getLooper();
  }

  @Override
  public Context getApplicationContext() {
    return mMainThread.getApplication();
  }

  @Override
  public void setTheme(int resid) {

  }

  @Override
  public Theme getTheme() {
    return null;
  }

  @Override
  public ClassLoader getClassLoader() {
    return null;
  }

  @Override
  public String getPackageName() {
    if (mPackageInfo != null) {
      return mPackageInfo.getPackageName();
    }
    throw new RuntimeException("Not supported in system context");
  }

  @Override
  public ApplicationInfo getApplicationInfo() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getPackageResourcePath() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getPackageCodePath() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public File getSharedPrefsFile(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SharedPreferences getSharedPreferences(String name, int mode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FileInputStream openFileInput(String name) throws FileNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean deleteFile(String name) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public File getFileStreamPath(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public File getFilesDir() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public File getExternalFilesDir(String type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public File getObbDir() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public File getCacheDir() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public File getExternalCacheDir() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String[] fileList() {
    // TODO
    return null;
  }

  @Override
  public File getDir(String name, int mode) {
    // TODO
    return null;
  }

  @Override
  public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
    // TODO
    return null;
  }

  @Override
  public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
                                             DatabaseErrorHandler errorHandler) {
    // TODO
    return null;
  }

  @Override
  public boolean deleteDatabase(String name) {
    // TODO
    return false;
  }

  @Override
  public File getDatabasePath(String name) {
    // TODO
    return null;
  }

  @Override
  public String[] databaseList() {
    // TODO
    return null;
  }

  @Override
  @Deprecated
  public Drawable getWallpaper() {
    return null;
  }

  @Override
  @Deprecated
  public Drawable peekWallpaper() {
    return null;
  }

  @Override
  @Deprecated
  public int getWallpaperDesiredMinimumWidth() {
    return 0;
  }

  @Override
  @Deprecated
  public int getWallpaperDesiredMinimumHeight() {
    return 0;
  }

  @Override
  @Deprecated
  public void setWallpaper(Bitmap bitmap) throws IOException {

  }

  @Override
  @Deprecated
  public void setWallpaper(InputStream data) throws IOException {

  }

  @Override
  @Deprecated
  public void clearWallpaper() throws IOException {

  }

  @Override
  public void startActivity(Intent intent) {
    // if ((intent.getFlags()&Intent.FLAG_ACTIVITY_NEW_TASK) == 0) {
    // throw new AndroidRuntimeException(
    // "Calling startActivity() from outside of an Activity "
    // + " context requires the FLAG_ACTIVITY_NEW_TASK flag."
    // + " Is this really what you want?");
    // }
    mMainThread.getInstrumentation().execStartActivity(getOuterContext(), null, null, (Activity) null,
        intent, -1);

  }

  @Override
  public void startActivities(Intent[] intents) {
    // TODO
  }

  @Override
  public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues,
                                int extraFlags) throws IntentSender.SendIntentException {
    // try {
    // String resolvedType = null;
    // if (fillInIntent != null) {
    // fillInIntent.setAllowFds(false);
    // resolvedType = fillInIntent.resolveTypeIfNeeded(getContentResolver());
    // }
    // int result = ActivityManagerNative.getDefault().startActivityIntentSender(
    // mMainThread.getApplicationThread(), intent, fillInIntent, resolvedType, null, null, 0, flagsMask,
    // flagsValues);
    // if (result == IActivityManager.START_CANCELED) {
    // throw new IntentSender.SendIntentException();
    // }
    // Instrumentation.checkStartActivityResult(result, null);
    // } catch (RemoteException e) {
    // }
  }

  @Override
  public void sendBroadcast(Intent intent) {
    // String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
    // try {
    // intent.setAllowFds(false);
    // ActivityManagerNative.getDefault().broadcastIntent(mMainThread.getApplicationThread(), intent,
    // resolvedType, null, Activity.RESULT_OK, null, null, null, false, false);
    // } catch (RemoteException e) {
    // }
  }

  @Override
  public void sendBroadcast(Intent intent, String receiverPermission) {
    // String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
    // try {
    // intent.setAllowFds(false);
    // ActivityManagerNative.getDefault().broadcastIntent(mMainThread.getApplicationThread(), intent,
    // resolvedType, null, Activity.RESULT_OK, null, null, receiverPermission, false, false);
    // } catch (RemoteException e) {
    // }

  }

  @Override
  public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
    // TODO

  }

  @Override
  public void sendOrderedBroadcast(Intent intent, String receiverPermission,
                                   BroadcastReceiver resultReceiver, Handler scheduler, int initialCode,
                                   String initialData, Bundle initialExtras) {
    // TODO

  }

  @Override
  public void sendStickyBroadcast(Intent intent) {
    // TODO

  }

  @Override
  public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler,
                                         int initialCode, String initialData, Bundle initialExtras) {
    // TODO

  }

  @Override
  public void removeStickyBroadcast(Intent intent) {
    // TODO

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
    // IIntentReceiver rd = null;
    // if (receiver != null) {
    // if (mPackageInfo != null && context != null) {
    // if (scheduler == null) {
    // scheduler = mMainThread.getHandler();
    // }
    // rd = mPackageInfo.getReceiverDispatcher(receiver, context, scheduler,
    // mMainThread.getInstrumentation(), true);
    // } else {
    // if (scheduler == null) {
    // scheduler = mMainThread.getHandler();
    // }
    // rd = new LoadedApk.ReceiverDispatcher(receiver, context, scheduler, null, true).getIIntentReceiver();
    // }
    // }
    // try {
    // return ActivityManagerNative.getDefault().registerReceiver(mMainThread.getApplicationThread(),
    // mBasePackageName, rd, filter, broadcastPermission);
    // } catch (RemoteException e) {
    // return null;
    // }
    return null;
  }

  @Override
  public void unregisterReceiver(BroadcastReceiver receiver) {
    // if (mPackageInfo != null) {
    // IIntentReceiver rd = mPackageInfo.forgetReceiverDispatcher(getOuterContext(), receiver);
    // try {
    // ActivityManagerNative.getDefault().unregisterReceiver(rd);
    // } catch (RemoteException e) {
    // }
    // } else {
    // throw new RuntimeException("Not supported in system context");
    // }
  }

  @Override
  public ComponentName startService(Intent service) {
    try {
      service.setAllowFds(false);
      ComponentName cn = ActivityManagerNative.getDefault().startService(mMainThread.getApplicationThread(),
          service, service.resolveTypeIfNeeded(getContentResolver()));
      if (cn != null && cn.getPackageName().equals("!")) {
        throw new SecurityException("Not allowed to start service " + service + " without permission "
            + cn.getClassName());
      }
      return cn;
    } catch (RemoteException e) {
      return null;
    }
  }

  @Override
  public boolean stopService(Intent service) {
    try {
      service.setAllowFds(false);
      int res = ActivityManagerNative.getDefault().stopService(mMainThread.getApplicationThread(), service,
          service.resolveTypeIfNeeded(getContentResolver()));
      if (res < 0) {
        throw new SecurityException("Not allowed to stop service " + service);
      }
      return res != 0;
    } catch (RemoteException e) {
      return false;
    }

  }

  final IBinder getActivityToken() {
    return null;// TODO mActivityToken;
  }

  @Override
  public boolean bindService(Intent service, ServiceConnection conn, int flags) {
    ServiceConnection sd;
    // if (mPackageInfo != null) {
    // sd = mPackageInfo.getServiceDisPapatcher(conn, getOuterContext(), mMainThread.getHandler(), flags);
    // } else {
    // throw new RuntimeException("Not supported in system context");
    // }
    try {
      IBinder token = getActivityToken();
      // if (token == null
      // && (flags & BIND_AUTO_CREATE) == 0
      // && mPackageInfo != null
      // && mPackageInfo.getApplicationInfo().targetSdkVersion <
      // android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      // flags |= BIND_WAIVE_PRIORITY;
      // }
      service.setAllowFds(false);
      int res = ActivityManagerNative.getDefault().bindService(mMainThread.getApplicationThread(),
          getActivityToken(), service, service.resolveTypeIfNeeded(getContentResolver()), conn, flags);
      if (res < 0) {
        throw new SecurityException("Not allowed to bind to service " + service);
      }
      return res != 0;
    } catch (RemoteException e) {
      return false;
    }
  }

  @Override
  public void unbindService(ServiceConnection conn) {
    if (mPackageInfo != null) {
      // IServiceConnection sd = mPackageInfo.forgetServiceDispatcher(
      // getOuterContext(), conn);
      try {
        ActivityManagerNative.getDefault().unbindService(conn);
      } catch (RemoteException e) {
      }
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
    // TODO
    return null;
  }

  @Override
  public int checkPermission(String permission, int pid, int uid) {
    return 0;
  }

  @Override
  public int checkCallingPermission(String permission) {
    return 0;
  }

  @Override
  public int checkCallingOrSelfPermission(String permission) {
    return 0;
  }

  @Override
  public void enforcePermission(String permission, int pid, int uid, String message) {

  }

  @Override
  public void enforceCallingPermission(String permission, String message) {

  }

  @Override
  public void enforceCallingOrSelfPermission(String permission, String message) {

  }

  @Override
  public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {

  }

  @Override
  public void revokeUriPermission(Uri uri, int modeFlags) {

  }

  @Override
  public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
    return 0;
  }

  @Override
  public int checkCallingUriPermission(Uri uri, int modeFlags) {
    return 0;
  }

  @Override
  public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
    return 0;
  }

  @Override
  public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid,
                                int modeFlags) {
    return 0;
  }

  @Override
  public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {

  }

  @Override
  public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {

  }

  @Override
  public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {

  }

  @Override
  public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid,
                                   int modeFlags, String message) {

  }

  @Override
  public Context createPackageContext(String packageName, int flags) throws NameNotFoundException {
    // TODO
    return null;
  }

  public void scheduleFinalCleanup(String who, String string) {
    // TODO Auto-generated method stub

  }

}
