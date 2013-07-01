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

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;

import com.android.internal.app.ActionBarImpl;
import com.android.internal.policy.PolicyManager;

public class Dialog implements DialogInterface {// , Window.Callback, KeyEvent.Callback,
                                                // View.OnCreateContextMenuListener {

  private static final String TAG = "Dialog";
  private Activity mOwnerActivity;

  final Context mContext;
  // final WindowManager mWindowManager;
  Window mWindow;
  View mDecor;
  private ActionBarImpl mActionBar;

  /**
   * This field should be made private, so it is hidden from the SDK. {@hide}
   */
  protected boolean mCancelable = true;

  private String mCancelAndDismissTaken;
  private Message mCancelMessage;
  private Message mDismissMessage;
  private Message mShowMessage;

  private OnKeyListener mOnKeyListener;

  private boolean mCreated = false;
  private boolean mShowing = false;
  private boolean mCanceled = false;

  private final Thread mUiThread;
  private final Handler mHandler = new Handler();

  private static final int DISMISS = 0x43;
  private static final int CANCEL = 0x44;
  private static final int SHOW = 0x45;

  private Handler mListenersHandler;

  private ActionMode mActionMode;

  private final Runnable mDismissAction = new Runnable() {
    public void run() {
      dismissDialog();
    }
  };

  public Dialog(Context context) {

    Window w = PolicyManager.makeNewWindow(context);
    mWindow = w;
    mUiThread = Thread.currentThread();
    // mListenersHandler = new ListenersHandler(this);
    mContext = context;
  }

  public Dialog(Context context, int theme) {
    this(context);
  }

  protected Dialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
    this(context);
  }

  public final Context getContext() {
    return mContext;
  }

  public ActionBar getActionBar() {
    throw new RuntimeException("Stub!");
  }

  public final void setOwnerActivity(Activity activity) {
    mOwnerActivity = activity;
  }

  public final Activity getOwnerActivity() {
    return mOwnerActivity;
  }

  public boolean isShowing() {
    return mShowing;
  }

  public void show() {
    if (mShowing) {
      if (mDecor != null) {
        mDecor.setVisibility(View.VISIBLE);
      }
      return;
    }

    mCanceled = false;

    if (!mCreated) {
      dispatchOnCreate(null);
    }

    onStart();
    mDecor = mWindow.getContentView();
    mWindow.setVisible();

    // if (mActionBar == null && mWindow.hasFeature(Window.FEATURE_ACTION_BAR)) {
    // mActionBar = new ActionBarImpl(this);
    // }

    // WindowManager.LayoutParams l = mWindow.getAttributes();
    // if ((l.softInputMode
    // & WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION) == 0) {
    // WindowManager.LayoutParams nl = new WindowManager.LayoutParams();
    // nl.copyFrom(l);
    // nl.softInputMode |=
    // WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
    // l = nl;
    // }
    //
    // try {
    // mWindowManager.addView(mDecor, l);
    mShowing = true;

    // sendShowMessage();
    // } finally {
    // }
  }

  public void hide() {
    if (mDecor != null) {
      mDecor.setVisibility(View.GONE);
    }
  }

  public void dismiss() {
    mDismissAction.run();
  }

  void dismissDialog() {
    if (mDecor == null || !mShowing) {
      return;
    }

    // if (mWindow.isDestroyed()) {
    // Log.e(TAG, "Tried to dismissDialog() but the Dialog's window was already destroyed!");
    // return;
    // }

    // try {
    // mWindowManager.removeView(mDecor);
    // } finally {
    // if (mActionMode != null) {
    // mActionMode.finish();
    // }
    mDecor = null;
    // mWindow.closeAllPanels();
    onStop();
    mShowing = false;

    // sendDismissMessage();
    // }
  }

  void dispatchOnCreate(Bundle savedInstanceState) {
    if (!mCreated) {
      onCreate(savedInstanceState);
      mCreated = true;
    }
  }

  protected void onCreate(Bundle savedInstanceState) {
  }

  protected void onStart() {
  }

  protected void onStop() {
  }

  public Bundle onSaveInstanceState() {
    throw new RuntimeException("Stub!");
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    throw new RuntimeException("Stub!");
  }

  public Window getWindow() {
    return mWindow;
  }

  public View getCurrentFocus() {
    throw new RuntimeException("Stub!");
  }

  public View findViewById(int id) {
    return mWindow.findViewById(id);
  }

  public void setContentView(int layoutResID) {
    mWindow.setContentView(layoutResID);
  }

  public void setContentView(View view) {
    mWindow.setContentView(view);
  }

  // public void setContentView(View view, ViewGroup.LayoutParams params) {
  // throw new RuntimeException("Stub!");
  // }
  //
  // public void addContentView(View view, ViewGroup.LayoutParams params) {
  // throw new RuntimeException("Stub!");
  // }

  public void setTitle(CharSequence title) {
    mWindow.setTitle(title);
  }

  public void setTitle(int titleId) {
    throw new RuntimeException("Stub!");
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    throw new RuntimeException("Stub!");
  }

  public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    throw new RuntimeException("Stub!");
  }

  public boolean onKeyUp(int keyCode, KeyEvent event) {
    throw new RuntimeException("Stub!");
  }

  public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
    throw new RuntimeException("Stub!");
  }

  public void onBackPressed() {
    throw new RuntimeException("Stub!");
  }

  public boolean onKeyShortcut(int keyCode, KeyEvent event) {
    throw new RuntimeException("Stub!");
  }

  public boolean onTouchEvent(MotionEvent event) {
    throw new RuntimeException("Stub!");
  }

  public boolean onTrackballEvent(MotionEvent event) {
    throw new RuntimeException("Stub!");
  }

  public boolean onGenericMotionEvent(MotionEvent event) {
    throw new RuntimeException("Stub!");
  }

  // public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
  // throw new RuntimeException("Stub!");
  // }

  public void onContentChanged() {
    throw new RuntimeException("Stub!");
  }

  public void onWindowFocusChanged(boolean hasFocus) {
    throw new RuntimeException("Stub!");
  }

  public void onAttachedToWindow() {
    throw new RuntimeException("Stub!");
  }

  public void onDetachedFromWindow() {
    throw new RuntimeException("Stub!");
  }

  public boolean dispatchKeyEvent(KeyEvent event) {
    throw new RuntimeException("Stub!");
  }

  public boolean dispatchKeyShortcutEvent(KeyEvent event) {
    throw new RuntimeException("Stub!");
  }

  public boolean dispatchTouchEvent(MotionEvent ev) {
    throw new RuntimeException("Stub!");
  }

  public boolean dispatchTrackballEvent(MotionEvent ev) {
    throw new RuntimeException("Stub!");
  }

  public boolean dispatchGenericMotionEvent(MotionEvent ev) {
    throw new RuntimeException("Stub!");
  }

  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
    throw new RuntimeException("Stub!");
  }

  public View onCreatePanelView(int featureId) {
    throw new RuntimeException("Stub!");
  }

  public boolean onCreatePanelMenu(int featureId, Menu menu) {
    throw new RuntimeException("Stub!");
  }

  public boolean onPreparePanel(int featureId, View view, Menu menu) {
    throw new RuntimeException("Stub!");
  }

  public boolean onMenuOpened(int featureId, Menu menu) {
    throw new RuntimeException("Stub!");
  }

  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    throw new RuntimeException("Stub!");
  }

  public void onPanelClosed(int featureId, Menu menu) {
    throw new RuntimeException("Stub!");
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    throw new RuntimeException("Stub!");
  }

  public boolean onPrepareOptionsMenu(Menu menu) {
    throw new RuntimeException("Stub!");
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    throw new RuntimeException("Stub!");
  }

  public void onOptionsMenuClosed(Menu menu) {
    throw new RuntimeException("Stub!");
  }

  public void openOptionsMenu() {
    throw new RuntimeException("Stub!");
  }

  public void closeOptionsMenu() {
    throw new RuntimeException("Stub!");
  }

  public void invalidateOptionsMenu() {
    throw new RuntimeException("Stub!");
  }

  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    throw new RuntimeException("Stub!");
  }

  public void registerForContextMenu(View view) {
    throw new RuntimeException("Stub!");
  }

  public void unregisterForContextMenu(View view) {
    throw new RuntimeException("Stub!");
  }

  public void openContextMenu(View view) {
    throw new RuntimeException("Stub!");
  }

  public boolean onContextItemSelected(MenuItem item) {
    throw new RuntimeException("Stub!");
  }

  public void onContextMenuClosed(Menu menu) {
    throw new RuntimeException("Stub!");
  }

  public boolean onSearchRequested() {
    throw new RuntimeException("Stub!");
  }

  public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
    throw new RuntimeException("Stub!");
  }

  public void onActionModeStarted(ActionMode mode) {
    throw new RuntimeException("Stub!");
  }

  public void onActionModeFinished(ActionMode mode) {
    throw new RuntimeException("Stub!");
  }

  public void takeKeyEvents(boolean get) {
    throw new RuntimeException("Stub!");
  }

  public final boolean requestWindowFeature(int featureId) {
    throw new RuntimeException("Stub!");
  }

  public final void setFeatureDrawableResource(int featureId, int resId) {
    throw new RuntimeException("Stub!");
  }

  public final void setFeatureDrawableUri(int featureId, Uri uri) {
    throw new RuntimeException("Stub!");
  }

  public final void setFeatureDrawable(int featureId, Drawable drawable) {
    throw new RuntimeException("Stub!");
  }

  public final void setFeatureDrawableAlpha(int featureId, int alpha) {
    throw new RuntimeException("Stub!");
  }

  public LayoutInflater getLayoutInflater() {
    return mWindow.getLayoutInflater();
  }

  public void setCancelable(boolean flag) {
    mCancelable = flag;
  }

  public void setCanceledOnTouchOutside(boolean cancel) {
    throw new RuntimeException("Stub!");
  }

  public void cancel() {
    if (!mCanceled && mCancelMessage != null) {
      mCanceled = true;
      // Obtain a new message so this dialog can be re-used
    }
    dismiss();
  }

  public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
    throw new RuntimeException("Stub!");
  }

  public void setCancelMessage(Message msg) {
    throw new RuntimeException("Stub!");
  }

  public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
    throw new RuntimeException("Stub!");
  }

  public void setOnShowListener(DialogInterface.OnShowListener listener) {
    throw new RuntimeException("Stub!");
  }

  public void setDismissMessage(Message msg) {
    throw new RuntimeException("Stub!");
  }

  public final void setVolumeControlStream(int streamType) {
    throw new RuntimeException("Stub!");
  }

  public final int getVolumeControlStream() {
    throw new RuntimeException("Stub!");
  }

  public void setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
    throw new RuntimeException("Stub!");
  }

}
