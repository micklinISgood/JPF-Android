package android.app;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.internal.policy.PolicyManager;

public class Activity extends ContextThemeWrapper {
  private static int uniqueID = 0;
  private static final String TAG = "Activity";

  /** Standard activity result: operation cancelled. */
  public static final int RESULT_CANCELED = 0;
  /** Standard activity result: operation succeeded. */
  public static final int RESULT_OK = -1;
  /** Start of user-defined activity results. */
  public static final int RESULT_FIRST_USER = 1;

  private static final String WINDOW_HIERARCHY_TAG = "android:viewHierarchyState";
  private static final String FRAGMENTS_TAG = "android:fragments";
  private static final String SAVED_DIALOG_IDS_KEY = "android:savedDialogIds";
  private static final String SAVED_DIALOGS_TAG = "android:savedDialogs";
  private static final String SAVED_DIALOG_KEY_PREFIX = "android:dialog_";
  private static final String SAVED_DIALOG_ARGS_KEY_PREFIX = "android:dialog_args_";

  private Context mBase;
  private Application mApplication;
  int mIdent; // unique identifier

  Intent mIntent; // Reference to the intent that started this Activity
  private String mComponent;
  ActivityThread mMainThread; // Reference to the applciation's ActivityThread
  ActivityInfo mActivityInfo;

  Activity mParent; // Reference to the activity that started this activity
  boolean mCalled;
  boolean mCheckedForLoaderManager;
  boolean mLoadersStarted;
  /* package */boolean mResumed;
  private boolean mStopped;
  boolean mFinished;
  boolean mStartedActivity;
  /** true if the activity is going through a transient pause */
  /* package */boolean mTemporaryPause = false;
  /** true if the activity is being destroyed in order to recreate it with a new configuration */
  /* package */boolean mChangingConfigurations = false;
  /* package */int mConfigChangeFlags;
  /* package */Configuration mCurrentConfig;

  static final class NonConfigurationInstances {
    Object activity;
    HashMap<String, Object> children;
    // ArrayList<Fragment> fragments;
    // android.util.SparseArray<LoaderManagerImpl> loaders;
  }

  /* package */NonConfigurationInstances mLastNonConfigurationInstances;

  private Window mWindow;
  // private WindowManager mWindowManager;

  boolean mWindowAdded = false;

  private CharSequence mTitle;
  private int mTitleColor = 0;

  private Thread mUiThread;
  final Handler mHandler = new Handler();

  // protected by synchronised (this)
  int mResultCode = RESULT_CANCELED;
  Intent mResultData = null;
  int mRequestCode;

  public void onCreate(Bundle savedInstanceState) {
    // if (mLastNonConfigurationInstances != null) { -- comes from attach
    // mAllLoaderManagers = mLastNonConfigurationInstances.loaders;
    // }

    // if (savedInstanceState != null) {
    // Parcelable p = savedInstanceState.getParcelable(FRAGMENTS_TAG);
    // mFragments.restoreAllState(p,
    // mLastNonConfigurationInstances != null ? mLastNonConfigurationInstances.fragments : null);
    // }
    // mFragments.dispatchCreate();

    getApplication().dispatchActivityCreated(this, savedInstanceState);
    mCalled = true;

  }

  /**
   * The hook for {@link ActivityThread} to restore the state of this activity.
   * 
   * Calls {@link #onSaveInstanceState(android.os.Bundle)} and
   * {@link #restoreManagedDialogs(android.os.Bundle)}.
   * 
   * @param savedInstanceState
   *          contains the saved state
   */
  final void performRestoreInstanceState(Bundle savedInstanceState) {
    onRestoreInstanceState(savedInstanceState);
    // restoreManagedDialogs(savedInstanceState);
  }

  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    if (mWindow != null) {
      Bundle windowState = savedInstanceState.getBundle(WINDOW_HIERARCHY_TAG);
      if (windowState != null) {
        // mWindow.restoreHierarchyState(windowState);
      }
    }
  }

  protected void onPostCreate(Bundle savedInstanceState) {
    if (!isChild()) {
      // mTitleReady = true;
      // onTitleChanged(getTitle(), getTitleColor());
    }
    mCalled = true;
  }

  /** Is this activity embedded inside of another activity? */
  public final boolean isChild() {
    return mParent != null;
  }

  protected void onStart() {
    mCalled = true;
    //
    // if (!mLoadersStarted) {
    // mLoadersStarted = true;
    // if (mLoaderManager != null) {
    // mLoaderManager.doStart();
    // } else if (!mCheckedForLoaderManager) {
    // mLoaderManager = getLoaderManager(-1, mLoadersStarted, false);
    // }
    // mCheckedForLoaderManager = true;
    // }
    //
    getApplication().dispatchActivityStarted(this);
  }

  protected void onRestart() {
    mCalled = true;
  }

  protected void onResume() {
    getApplication().dispatchActivityResumed(this);
    mCalled = true;
  }

  protected void onPostResume() {
    // final Window win = getWindow();
    // if (win != null) win.makeActive();
    // if (mActionBar != null) mActionBar.setShowHideAnimationEnabled(true);
    mCalled = true;
  }

  protected void onPause() {
    getApplication().dispatchActivityPaused(this);
    mCalled = true;
  }

  protected void onStop() {
    // if (mActionBar != null)
    // mActionBar.setShowHideAnimationEnabled(false);
    getApplication().dispatchActivityStopped(this);
    mCalled = true;
  }

  protected void onDestroy() {
    mCalled = true;
    //
    // // dismiss any dialogs we are managing.
    // if (mManagedDialogs != null) {
    // final int numDialogs = mManagedDialogs.size();
    // for (int i = 0; i < numDialogs; i++) {
    // final ManagedDialog md = mManagedDialogs.valueAt(i);
    // if (md.mDialog.isShowing()) {
    // md.mDialog.dismiss();
    // }
    // }
    // mManagedDialogs = null;
    // }
    //
    // // close any cursors we are managing.
    // synchronized (mManagedCursors) {
    // int numCursors = mManagedCursors.size();
    // for (int i = 0; i < numCursors; i++) {
    // ManagedCursor c = mManagedCursors.get(i);
    // if (c != null) {
    // c.mCursor.close();
    // }
    // }
    // mManagedCursors.clear();
    // }
    //
    // // Close any open search dialog
    // if (mSearchManager != null) {
    // mSearchManager.stopSearch();
    // }
    //
    getApplication().dispatchActivityDestroyed(this);
  }

  public Window getWindow() {
    return mWindow;
  }

  /** Return the application that owns this activity. */
  public final Application getApplication() {
    return mApplication;
  }

  public View findViewById(int id) {
    return getWindow().findViewById(id);
  }

  public void setContentView(int layoutResID) {
    getWindow().setContentView(layoutResID);
    // initActionBar();
  }

  public void setContentView(ViewGroup v) {
    getWindow().setContentView(v);
  }

  void makeVisible() {
    // if (!mWindowAdded) {
    // ViewManager wm = getWindowManager();
    // wm.addView(mDecor, getWindow().getAttributes());
    // mWindowAdded = true;
    // }
    // mDecor.setVisibility(View.VISIBLE);
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(newBase);
    mBase = newBase;
  }

  public void startActivity(Intent intent) {
    startActivityForResult(intent, -1);
  }

  public final void startActivityForResult(Intent intent, int requestCode) {
    // if (mParent == null) {
    // Instrumentation.ActivityResult ar =
    intent.setParent(this);
    // onPause();
    // System.out.println("Activity for result");
    mRequestCode = requestCode;
    ActivityManagerProxy.startActivityProxy(intent, requestCode);
    // if (ar != null) {
    // mMainThread.sendActivityResult(
    // mToken, mEmbeddedID, requestCode, ar.getResultCode(),
    // ar.getResultData());
    // }
    // if (requestCode >= 0) {
    // If this start is requesting a result, we can avoid making
    // the activity visible until the result is received. Setting
    // this code during onCreate(Bundle savedInstanceState) or onResume()
    // will keep the
    // activity hidden during this time, to avoid flickering.
    // This can only be done when a result is requested because
    // that guarantees we will get information back when the
    // activity is finished, no matter what happens to it.
    // / mStartedActivity = true;
    // }
    // } else {
    // mParent.startActivityFromChild(this, intent, requestCode);
    // }
  }

  public Intent getIntent() {
    return mIntent;
  }

  /**
   * Runs the specified action on the UI thread. If the current thread is the UI thread, then the action is
   * executed immediately. If the current thread is not the UI thread, the action is posted to the event queue
   * of the UI thread.
   * 
   * @param action
   *          the action to run on the UI thread
   */
  public final void runOnUiThread(Runnable action) {
    if (Thread.currentThread() != mUiThread) {
      mHandler.post(action);
    } else {
      action.run();
    }
  }

  // ------------------ Internal API ------------------

  final void setParent(Activity parent) {
    mParent = parent;
  }

  final void attach(Context context, ActivityThread aThread, Application application, Intent intent,
                    ActivityInfo info, CharSequence title, Activity parent) {

    // final void attach(Context context, ActivityThread aThread,
    // Instrumentation instr, IBinder token, int ident,
    // Application application, Intent intent, ActivityInfo info,
    // CharSequence title, Activity parent, String id,
    // NonConfigurationInstances lastNonConfigurationInstances,
    // Configuration config) {

    // attachBaseContext(context);

    // mFragments.attachActivity(this);

    mWindow = PolicyManager.makeNewWindow(this);
    // mWindow.getLayoutInflater().setPrivateFactory(this);
    // if (info.softInputMode !=
    // WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
    // mWindow.setSoftInputMode(info.softInputMode);
    // }
    // if (info.uiOptions != 0) {
    // mWindow.setUiOptions(info.uiOptions);
    // }
    mUiThread = Thread.currentThread();
    mMainThread = aThread;
    // mInstrumentation = instr;
    // mToken = token;
    mIdent = uniqueID;
    uniqueID++;
    mApplication = application;
    mIntent = intent;
    mComponent = intent.getComponent();
    mActivityInfo = info;
    mTitle = title;
    mParent = parent;
    // mEmbeddedID = id;
    // mLastNonConfigurationInstances = lastNonConfigurationInstances;

    // mWindow.setWindowManager(null, mToken, mComponent.flattenToString(),
    // (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
    // if (mParent != null) {
    // mWindow.setContainer(mParent.getWindow());
    // }
    // mWindowManager = mWindow.getWindowManager();
    // mCurrentConfig = config;
  }

  final void performCreate(Bundle icicle) {
    onCreate(icicle);
    // mVisibleFromClient = !mWindow.getWindowStyle().getBoolean(
    // com.android.internal.R.styleable.Window_windowNoDisplay, false);
    // mFragments.dispatchActivityCreated();
  }

  final void performStart() {
    // mFragments.noteStateNotSaved();
    mCalled = false;
    // mFragments.execPendingActions();
    onStart();
    if (!mCalled) {
      throw new SuperNotCalledException("Activity " + mComponent + " did not call through to super.onStart()");
    }
    // mFragments.dispatchStart();
    // if (mAllLoaderManagers != null) {
    // for (int i = mAllLoaderManagers.size() - 1; i >= 0; i--) {
    // LoaderManagerImpl lm = mAllLoaderManagers.valueAt(i);
    // lm.finishRetain();
    // lm.doReportStart();
    // }
    // }
  }

  final void performRestart() {
    // mFragments.noteStateNotSaved();

    if (mStopped) {
      mStopped = false;
      // if (mToken != null && mParent == null) {
      // WindowManagerImpl.getDefault().setStoppedState(mToken, false);
      // }

      // synchronized (mManagedCursors) {
      // final int N = mManagedCursors.size();
      // for (int i = 0; i < N; i++) {
      // ManagedCursor mc = mManagedCursors.get(i);
      // if (mc.mReleased || mc.mUpdated) {
      // if (!mc.mCursor.requery()) {
      // if (getApplicationInfo().targetSdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      // throw new IllegalStateException("trying to requery an already closed cursor  " + mc.mCursor);
      // }
      // }
      // mc.mReleased = false;
      // mc.mUpdated = false;
      // }
      // }
      // }
      //
      mCalled = false;
      onRestart();
      if (!mCalled) {
        throw new SuperNotCalledException("Activity " + mComponent
            + " did not call through to super.onRestart()");
      }
      performStart();
    }
  }

  final void performResume() {
    performRestart();

    // mFragments.execPendingActions();

    // mLastNonConfigurationInstances = null;

    mCalled = false;
    // mResumed is set by the instrumentation
    onResume();
    if (!mCalled) {
      throw new SuperNotCalledException("Activity " + mComponent
          + " did not call through to super.onResume()");
    }

    // Now really resume, and install the current status bar and menu.
    mCalled = false;
    //
    // mFragments.dispatchResume();
    // mFragments.execPendingActions();

    onPostResume();
    if (!mCalled) {
      throw new SuperNotCalledException("Activity " + mComponent
          + " did not call through to super.onPostResume()");
    }
  }

  final void performPause() {
    // mFragments.dispatchPause();
    mCalled = false;
    onPause();
    mResumed = false;
    // if (!mCalled && getApplicationInfo().targetSdkVersion >= android.os.Build.VERSION_CODES.GINGERBREAD) {
    // throw new SuperNotCalledException("Activity " + mComponent.toShortString()
    // + " did not call through to super.onPause()");
    // }
    mResumed = false;
  }

  final void performStop() {
    // if (mLoadersStarted) {
    // mLoadersStarted = false;
    // if (mLoaderManager != null) {
    // if (!mChangingConfigurations) {
    // mLoaderManager.doStop();
    // } else {
    // mLoaderManager.doRetain();
    // }
    // }
    // }

    // if (!mStopped) {
    // if (mWindow != null) {
    // mWindow.closeAllPanels();
    // }
    //
    // if (mToken != null && mParent == null) {
    // WindowManagerImpl.getDefault().setStoppedState(mToken, true);
    // }
    //
    // mFragments.dispatchStop();
    //
    mCalled = false;
    this.onStop();
    if (!mCalled) {
      throw new SuperNotCalledException("Activity " + mComponent + " did not call through to super.onStop()");
    }

    // synchronized (mManagedCursors) {
    // final int N = mManagedCursors.size();
    // for (int i = 0; i < N; i++) {
    // ManagedCursor mc = mManagedCursors.get(i);
    // if (!mc.mReleased) {
    // mc.mCursor.deactivate();
    // mc.mReleased = true;
    // }
    // }
    // }

    mStopped = true;
    // }
    mResumed = false;
  }

  final void performDestroy() {
    // mWindow.destroy();
    // mFragments.dispatchDestroy();
    onDestroy();
    // if (mLoaderManager != null) {
    // mLoaderManager.doDestroy();
    // }
  }

  /**
   * @hide
   */
  public final boolean isResumed() {
    return mResumed;
  }

  void dispatchActivityResult(String who, int requestCode, int resultCode, Intent data) {
    // if (false) Log.v(
    // TAG, "Dispatching result: who=" + who + ", reqCode=" + requestCode
    // + ", resCode=" + resultCode + ", data=" + data);
    // mFragments.noteStateNotSaved();
    // if (who == null) {
    // onActivityResult(requestCode, resultCode, data);
    // } else {
    // Fragment frag = mFragments.findFragmentByWho(who);
    // if (frag != null) {
    // frag.onActivityResult(requestCode, resultCode, data);
    // }
    // }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Activity other = (Activity) obj;
    if (mIdent != other.mIdent)
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + mIdent;
    return result;
  }

  protected void onSaveInstanceState(Bundle outState) {
    // outState.putBundle(WINDOW_HIERARCHY_TAG, mWindow.saveHierarchyState());
    // Parcelable p = mFragments.saveAllState();
    // if (p != null) {
    // outState.putParcelable(FRAGMENTS_TAG, p);
    // }
    getApplication().dispatchActivitySaveInstanceState(this, outState);
  }

  /**
   * Check to see whether this activity is in the process of finishing, either because you called
   * {@link #finish} on it or someone else has requested that it finished. This is often used in
   * {@link #onPause} to determine whether the activity is simply pausing or completely finishing.
   * 
   * @return If the activity is finishing, returns true; else returns false.
   * 
   * @see #finish
   */
  public boolean isFinishing() {
    return mFinished;
  }

  /**
   * Call this when your activity is done and should be closed. The ActivityResult is propagated back to
   * whoever launched you via onActivityResult().
   */
  public void finish() {
    // if (mParent == null) {
    int resultCode;
    Intent resultData;
    synchronized (this) {
      resultCode = mResultCode;
      resultData = mResultData;
    }
    if (resultData != null) {
      // resultData.setAllowFds(false);
    }
    // if (ActivityManagerNative.getDefault().finishActivity(ident, resultCode, resultData)) {
    mFinished = true;
    // }
  }

  /**
   * Called when the activity has detected the user's press of the back key. The default implementation simply
   * finishes the current activity, but you can override this to do whatever you want.
   */
  public void onBackPressed() {
    // if (!mFragments.popBackStackImmediate()) {
    finish();
    // }
  }
}
