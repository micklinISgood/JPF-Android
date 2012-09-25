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

package android.content;

//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageManager;
//import android.content.res.AssetManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Looper;
//import android.database.DatabaseErrorHandler;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase.CursorFactory;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;

/**
 * Proxying implementation of Context that simply delegates all of its calls to another Context. Can be
 * subclassed to modify behavior without changing the original Context.
 */
public class ContextWrapper extends Context {
  Context mBase;

  public ContextWrapper(Context base) {
    mBase = base;
  }

  /**
   * Set the base context for this ContextWrapper. All calls will then be delegated to the base context.
   * Throws IllegalStateException if a base context has already been set.
   * 
   * @param base
   *          The new base context for this wrapper.
   */
  protected void attachBaseContext(Context base) {
    if (mBase != null) {
      throw new IllegalStateException("Base context already set");
    }
    mBase = base;
  }

  /**
   * @return the base context as set by the constructor or setBaseContext
   */
  public Context getBaseContext() {
    return mBase;
  }

  @Override
  public Looper getMainLooper() {
    return mBase.getMainLooper();
  }

  @Override
  public Context getApplicationContext() {
    return mBase.getApplicationContext();
  }

  @Override
  public void setTheme(int resid) {
    mBase.setTheme(resid);
  }

  /** @hide */
  @Override
  public int getThemeResId() {
    return mBase.getThemeResId();
  }

  @Override
  public String getPackageResourcePath() {
    return mBase.getPackageResourcePath();
  }

  @Override
  public String getPackageCodePath() {
    return mBase.getPackageCodePath();
  }

  /** @hide */
  @Override
  public File getSharedPrefsFile(String name) {
    return mBase.getSharedPrefsFile(name);
  }

  @Override
  public FileInputStream openFileInput(String name) throws FileNotFoundException {
    return mBase.openFileInput(name);
  }

  @Override
  public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
    return mBase.openFileOutput(name, mode);
  }

  @Override
  public boolean deleteFile(String name) {
    return mBase.deleteFile(name);
  }

  @Override
  public File getFileStreamPath(String name) {
    return mBase.getFileStreamPath(name);
  }

  @Override
  public String[] fileList() {
    return mBase.fileList();
  }

  @Override
  public File getFilesDir() {
    return mBase.getFilesDir();
  }

  @Override
  public File getExternalFilesDir(String type) {
    return mBase.getExternalFilesDir(type);
  }

  @Override
  public File getObbDir() {
    return mBase.getObbDir();
  }

  @Override
  public File getCacheDir() {
    return mBase.getCacheDir();
  }

  @Override
  public File getExternalCacheDir() {
    return mBase.getExternalCacheDir();
  }

  @Override
  public File getDir(String name, int mode) {
    return mBase.getDir(name, mode);
  }

  @Override
  public boolean deleteDatabase(String name) {
    return mBase.deleteDatabase(name);
  }

  @Override
  public File getDatabasePath(String name) {
    return mBase.getDatabasePath(name);
  }

  @Override
  public String[] databaseList() {
    return mBase.databaseList();
  }

  @Override
  public int getWallpaperDesiredMinimumWidth() {
    return mBase.getWallpaperDesiredMinimumWidth();
  }

  @Override
  public int getWallpaperDesiredMinimumHeight() {
    return mBase.getWallpaperDesiredMinimumHeight();
  }

  @Override
  public void setWallpaper(InputStream data) throws IOException {
    mBase.setWallpaper(data);
  }

  @Override
  public void clearWallpaper() throws IOException {
    mBase.clearWallpaper();
  }

  @Override
  public void startActivity(Intent intent) {
    mBase.startActivity(intent);
  }

  @Override
  public void startActivities(Intent[] intents) {
    mBase.startActivities(intents);
  }

  @Override
  public void sendBroadcast(Intent intent) {
    mBase.sendBroadcast(intent);
  }

  @Override
  public void sendBroadcast(Intent intent, String receiverPermission) {
    mBase.sendBroadcast(intent, receiverPermission);
  }

  @Override
  public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
    mBase.sendOrderedBroadcast(intent, receiverPermission);
  }

  @Override
  public void sendStickyBroadcast(Intent intent) {
    mBase.sendStickyBroadcast(intent);
  }

  @Override
  public void removeStickyBroadcast(Intent intent) {
    mBase.removeStickyBroadcast(intent);
  }

  @Override
  public boolean stopService(Intent name) {
    return mBase.stopService(name);
  }

  @Override
  public Object getSystemService(String name) {
    return mBase.getSystemService(name);
  }

  @Override
  public int checkPermission(String permission, int pid, int uid) {
    return mBase.checkPermission(permission, pid, uid);
  }

  @Override
  public int checkCallingPermission(String permission) {
    return mBase.checkCallingPermission(permission);
  }

  @Override
  public int checkCallingOrSelfPermission(String permission) {
    return mBase.checkCallingOrSelfPermission(permission);
  }

  @Override
  public void enforcePermission(String permission, int pid, int uid, String message) {
    mBase.enforcePermission(permission, pid, uid, message);
  }

  @Override
  public void enforceCallingPermission(String permission, String message) {
    mBase.enforceCallingPermission(permission, message);
  }

  @Override
  public void enforceCallingOrSelfPermission(String permission, String message) {
    mBase.enforceCallingOrSelfPermission(permission, message);
  }

  @Override
  public boolean isRestricted() {
    return mBase.isRestricted();
  }
}
