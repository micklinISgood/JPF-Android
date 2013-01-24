package android.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

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
  // protected by synchronised (this)
  int mResultCode = RESULT_CANCELED;
  Intent mResultData = null;

  private static final String WINDOW_HIERARCHY_TAG = "android:viewHierarchyState";
  private static final String FRAGMENTS_TAG = "android:fragments";
  private static final String SAVED_DIALOG_IDS_KEY = "android:savedDialogIds";
  private static final String SAVED_DIALOGS_TAG = "android:savedDialogs";
  private static final String SAVED_DIALOG_KEY_PREFIX = "android:dialog_";
  private static final String SAVED_DIALOG_ARGS_KEY_PREFIX = "android:dialog_args_";

  private static class ManagedDialog {
    Dialog mDialog;
    Bundle mArgs;
  }

  //
  private SparseArray<ManagedDialog> mManagedDialogs;

  // set by the thread after the constructor and before onCreate(Bundle savedInstanceState) is called.
  private Instrumentation mInstrumentation;
  private IBinder mToken;
  int mIdent; // unique identifier
  /* package */String mEmbeddedID; // Used for ActivityGroups
  private Application mApplication; // Main application context

  Intent mIntent; // Reference to the intent that started this Activity
  private String mComponent; // TODO
  /* package */ActivityInfo mActivityInfo; // Contains info of this activity from AndroidManifest.xml
  /* package */ActivityThread mMainThread; // The main thread of this application
  Activity mParent; // Used by ActivityGroups, stores this Activity's parent Activity
  boolean mCalled; // Used to make sure super lifecycle methods are called
  boolean mCheckedForLoaderManager;
  boolean mLoadersStarted;

  // State of the Activity
  boolean mResumed; // true if this activity is in a resumed state
  private boolean mStopped; // true if this activity is in a stopped state
  boolean mFinished; // An Activity is finished when its finish method is called. Used by pause to determine
                     // if the activity is finishing or just temporarily pausing
  boolean mStartedActivity; // has this activity started another

  /** true if the activity is going through a transient pause */
  /* package */boolean mTemporaryPause = false;

  /** true if the activity is being destroyed in order to recreate it with a new configuration */
  /* package */boolean mChangingConfigurations = false;
  /* package */int mConfigChangeFlags;
  /* package */Configuration mCurrentConfig;

  // private SearchManager mSearchManager; //TODO
  // private MenuInflater mMenuInflater; //TODO

  static final class NonConfigurationInstances {
    Object activity;
    HashMap<String, Object> children;
    // ArrayList<Fragment> fragments;//TODO
    // android.util.SparseArray<LoaderManagerImpl> loaders;
  }

  /* package */NonConfigurationInstances mLastNonConfigurationInstances;

  private Window mWindow;
  private WindowManager mWindowManager;
  /* package */View mDecor = null;
  /* package */boolean mWindowAdded = false;
  /* package */boolean mVisibleFromServer = false;
  /* package */boolean mVisibleFromClient = true;
  // /* package */ActionBarImpl mActionBar = null; //TODO

  private CharSequence mTitle;
  private int mTitleColor = 0;

  // final FragmentManagerImpl mFragments = new FragmentManagerImpl(); TODO
  //
  // SparseArray<LoaderManagerImpl> mAllLoaderManagers; //TODO
  // LoaderManagerImpl mLoaderManager;

  private static final class ManagedCursor {
    ManagedCursor(Cursor cursor) {
      mCursor = cursor;
      mReleased = false;
      mUpdated = false;
    }

    private final Cursor mCursor;
    private boolean mReleased;
    private boolean mUpdated;
  }

  private final ArrayList<ManagedCursor> mManagedCursors = new ArrayList<ManagedCursor>();

  private boolean mTitleReady = false;

  // private int mDefaultKeyMode = DEFAULT_KEYS_DISABLE;TODO
  // private SpannableStringBuilder mDefaultKeySsb = null;

  // protected static final int[] FOCUSED_STATE_SET = { com.android.internal.R.attr.state_focused };

  // private final Object mInstanceTracker = StrictMode.trackActivity(this);

  private Thread mUiThread;
  final Handler mHandler = new Handler();

  protected void onCreate(Bundle savedInstanceState) {
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
    restoreManagedDialogs(savedInstanceState);
  }

  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    if (mWindow != null) {
      Bundle windowState = savedInstanceState.getBundle(WINDOW_HIERARCHY_TAG);
      if (windowState != null) {
        // mWindow.restoreHierarchyState(windowState);
      }
    }
  }

  private void restoreManagedDialogs(Bundle savedInstanceState) {
    final Bundle b = savedInstanceState.getBundle(SAVED_DIALOGS_TAG);
    if (b == null) {
      return;
    }

    final int[] ids = b.getIntArray(SAVED_DIALOG_IDS_KEY);
    final int numDialogs = ids.length;
    mManagedDialogs = new SparseArray<ManagedDialog>(numDialogs);
    for (int i = 0; i < numDialogs; i++) {
      final Integer dialogId = ids[i];
      Bundle dialogState = b.getBundle(savedDialogKeyFor(dialogId));
      if (dialogState != null) {
        // Calling onRestoreInstanceState() below will invoke dispatchOnCreate
        // so tell createDialog() not to do it, otherwise we get an exception
        final ManagedDialog md = new ManagedDialog();
        md.mArgs = b.getBundle(savedDialogArgsKeyFor(dialogId));
        md.mDialog = createDialog(dialogId, dialogState, md.mArgs);
        if (md.mDialog != null) {
          mManagedDialogs.put(dialogId, md);
          onPrepareDialog(dialogId, md.mDialog, md.mArgs);
          md.mDialog.onRestoreInstanceState(dialogState);
        }
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

  /** Return the parent activity if this view is an embedded child. */
  public final Activity getParent() {
    return mParent;
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
    if (mManagedDialogs != null) {
      final int numDialogs = mManagedDialogs.size();
      for (int i = 0; i < numDialogs; i++) {
        final ManagedDialog md = mManagedDialogs.valueAt(i);
        if (md.mDialog.isShowing()) {
          md.mDialog.dismiss();
        }
      }
      mManagedDialogs = null;
    }
    //
    // // close any cursors we are managing.
    synchronized (mManagedCursors) {
      int numCursors = mManagedCursors.size();
      for (int i = 0; i < numCursors; i++) {
        ManagedCursor c = mManagedCursors.get(i);
        if (c != null) {
          c.mCursor.close();
        }
      }
      mManagedCursors.clear();
    }
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

  /** Retrieve the window manager for showing custom windows. */
  public WindowManager getWindowManager() {
    return mWindowManager;
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
    setBaseContext(newBase);
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
    ActivityManagerNative.getDefault().startActivity(intent, requestCode);
    // if (ar != null) {
    // mMainThread.sendActivityResult(
    // mToken, mEmbeddedID, requestCode, ar.getResultCode(),
    // ar.getResultData());
    // }
    if (requestCode >= 0) {
      // If this start is requesting a result, we can avoid making
      // the activity visible until the result is received. Setting
      // this code during onCreate(Bundle savedInstanceState) or onResume()
      // will keep the
      // activity hidden during this time, to avoid flickering.
      // This can only be done when a result is requested because
      // that guarantees we will get information back when the
      // activity is finished, no matter what happens to it.
      mStartedActivity = true;
    }

  }

  public Intent getIntent() {
    return mIntent;
  }

  public void setIntent(Intent newIntent) {
    mIntent = newIntent;
  }

  public boolean isChangingConfigurations() {
    return mChangingConfigurations;
  }

  public int getChangingConfigurations() {
    return mConfigChangeFlags;
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

  final void attach(Context context, ActivityThread aThread, Instrumentation instr, IBinder token,
                    Application application, Intent intent, ActivityInfo info, CharSequence title,
                    Activity parent, String id, NonConfigurationInstances lastNonConfigurationInstances,
                    Configuration config) {
    attach(context, aThread, instr, token, 0, application, intent, info, title, parent, id,
        lastNonConfigurationInstances, config);
  }

  final void attach(Context context, ActivityThread aThread, Instrumentation instr, IBinder token, int ident,
                    Application application, Intent intent, ActivityInfo info, CharSequence title,
                    Activity parent, String id, NonConfigurationInstances lastNonConfigurationInstances,
                    Configuration config) {

    attachBaseContext(context);

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
    mInstrumentation = instr;
    mToken = token;
    mIdent = uniqueID;
    uniqueID++;
    mApplication = application;
    mIntent = intent;
    mComponent = intent.getComponent();
    mActivityInfo = info;
    mTitle = title;
    mParent = parent;
    mEmbeddedID = id;
    mLastNonConfigurationInstances = lastNonConfigurationInstances;

    // mWindow.setWindowManager(null, mToken, mComponent.flattenToString(),
    // (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
    // if (mParent != null) {
    // mWindow.setContainer(mParent.getWindow());
    // }
    // mWindowManager = mWindow.getWindowManager();
    mCurrentConfig = config;
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
    if (!mChangingConfigurations) {
      // mLoaderManager.doStop();
    } else {
      // mLoaderManager.doRetain();
    }
    // }
    // }

    if (!mStopped) {
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
        throw new SuperNotCalledException("Activity " + mComponent
            + " did not call through to super.onStop()");
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
    }
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
    onActivityResult(requestCode, resultCode, data);
    // } else {
    // Fragment frag = mFragments.findFragmentByWho(who);
    // if (frag != null) {
    // frag.onActivityResult(requestCode, resultCode, data);
    // }
    // }
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    mFinished = true;
    ActivityManagerNative.getDefault().finishActivity(resultCode, resultData);
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

  public final void setResult(int resultCode) {
    synchronized (this) {
      mResultCode = resultCode;
      mResultData = null;
    }
  }

  public final void setResult(int resultCode, Intent data) {
    synchronized (this) {
      mResultCode = resultCode;
      mResultData = data;
    }
  }

  public Object onRetainNonConfigurationInstance() {
    return null;
  }

  public Object getLastNonConfigurationInstance() {
    return mLastNonConfigurationInstances != null ? mLastNonConfigurationInstances.activity : null;
  }

  NonConfigurationInstances retainNonConfigurationInstances() {
    Object activity = onRetainNonConfigurationInstance();
    // HashMap<String, Object> children = onRetainNonConfigurationChildInstances();
    // ArrayList<Fragment> fragments = mFragments.retainNonConfig();
    // boolean retainLoaders = false;
    // if (mAllLoaderManagers != null) {
    // // prune out any loader managers that were already stopped and so
    // // have nothing useful to retain.
    // for (int i = mAllLoaderManagers.size() - 1; i >= 0; i--) {
    // LoaderManagerImpl lm = mAllLoaderManagers.valueAt(i);
    // if (lm.mRetaining) {
    // retainLoaders = true;
    // } else {
    // lm.doDestroy();
    // mAllLoaderManagers.removeAt(i);
    // }
    // }
    // }
    // if (activity == null && children == null && fragments == null && !retainLoaders) {
    // return null;
    // }

    NonConfigurationInstances nci = new NonConfigurationInstances();
    nci.activity = activity;
    // nci.children = children;
    // nci.fragments = fragments;
    // nci.loaders = mAllLoaderManagers;
    return nci;
  }

  protected void onNewIntent(Intent intent) {
  }

  final void performSaveInstanceState(Bundle outState) {
    onSaveInstanceState(outState);
    saveManagedDialogs(outState);
  }

  final void performUserLeaving() {
    // onUserInteraction();
    // onUserLeaveHint();
    // TODO
  }

  private void saveManagedDialogs(Bundle outState) {
    if (mManagedDialogs == null) {
      return;
    }

    final int numDialogs = mManagedDialogs.size();
    if (numDialogs == 0) {
      return;
    }

    Bundle dialogState = new Bundle();

    int[] ids = new int[mManagedDialogs.size()];

    // save each dialog's bundle, gather the ids
    for (int i = 0; i < numDialogs; i++) {
      final int key = mManagedDialogs.keyAt(i);
      ids[i] = key;
      final ManagedDialog md = mManagedDialogs.valueAt(i);
      dialogState.putBundle(savedDialogKeyFor(key), md.mDialog.onSaveInstanceState());
      if (md.mArgs != null) {
        dialogState.putBundle(savedDialogArgsKeyFor(key), md.mArgs);
      }
    }

    dialogState.putIntArray(SAVED_DIALOG_IDS_KEY, ids);
    outState.putBundle(SAVED_DIALOGS_TAG, dialogState);
  }

  private static String savedDialogKeyFor(int key) {
    return SAVED_DIALOG_KEY_PREFIX + key;
  }

  private static String savedDialogArgsKeyFor(int key) {
    return SAVED_DIALOG_ARGS_KEY_PREFIX + key;
  }

  private Dialog createDialog(Integer dialogId, Bundle state, Bundle args) {
    final Dialog dialog = onCreateDialog(dialogId, args);
    if (dialog == null) {
      return null;
    }
    dialog.dispatchOnCreate(state);
    return dialog;
  }

  protected Dialog onCreateDialog(int id) {
    return null;
  }

  protected Dialog onCreateDialog(int id, Bundle args) {
    return onCreateDialog(id);
  }

  protected void onPrepareDialog(int id, Dialog dialog) {
    dialog.setOwnerActivity(this);
  }

  protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
    onPrepareDialog(id, dialog);
  }

  public final void showDialog(int id) {
    showDialog(id, null);
  }

  public final boolean showDialog(int id, Bundle args) {
    if (mManagedDialogs == null) {
      mManagedDialogs = new SparseArray<ManagedDialog>();
    }
    ManagedDialog md = mManagedDialogs.get(id);
    if (md == null) {
      md = new ManagedDialog();
      md.mDialog = createDialog(id, null, args);
      if (md.mDialog == null) {
        return false;
      }
      mManagedDialogs.put(id, md);
    }

    md.mArgs = args;
    onPrepareDialog(id, md.mDialog, args);
    md.mDialog.show();
    return true;
  }

  public final void dismissDialog(int id) {
    if (mManagedDialogs == null) {
      throw missingDialog(id);
    }

    final ManagedDialog md = mManagedDialogs.get(id);
    if (md == null) {
      throw missingDialog(id);
    }
    md.mDialog.dismiss();
  }

  private IllegalArgumentException missingDialog(int id) {
    return new IllegalArgumentException("no dialog with id " + id + " was ever "
        + "shown via Activity#showDialog");
  }

  public final void removeDialog(int id) {
    if (mManagedDialogs != null) {
      final ManagedDialog md = mManagedDialogs.get(id);
      if (md != null) {
        md.mDialog.dismiss();
        mManagedDialogs.remove(id);
      }
    }
  }

}
