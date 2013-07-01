package android.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;

import com.android.internal.policy.PolicyManager;

/**
 * Models an Activity.
 * 
 * @author Heila van der Merwe
 * @version 1.3
 *          <div>
 *          Updated 11 June 2013 - updating ui event methods.
 *          </div>
 * 
 *          <div>
 *          Not supported yet:
 *          <ol>
 *          <li>Fragments
 *          <li>Managed Cursor
 *          <li>Action Bar/ Context menu
 *          <li>LoaderManager
 *          <li>SearchManager
 *          <li>Dialogs have to be revised
 *          </ol>
 *          </div>
 * 
 * 
 */
public class Activity extends ContextThemeWrapper implements Window.Callback, KeyEvent.Callback,
    ComponentCallbacks2 {
  private static int uniqueID = 0;
  private static final String TAG = "Activity";
  private static final boolean DEBUG_ACTIVITY = true;

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
  private ComponentName mComponent;
  /* package */ActivityInfo mActivityInfo; // Contains info of this activity from AndroidManifest.xml
  /* package */ActivityThread mMainThread; // The main thread of this application
  Activity mParent; // Used by ActivityGroups, stores this Activity's parent Activity
  boolean mCalled; // Used to make sure super lifecycle methods are called
  boolean mCheckedForLoaderManager; //TODO not modelled
  boolean mLoadersStarted;  //TODO not modelled

  // State of the Activity
  boolean mResumed; // true if this activity is in a resumed state
  private boolean mStopped; // true if this activity is in a stopped state
  boolean mFinished; // An Activity is finished when its finish method is called. Used by pause to determine
                     // if the activity is finishing or just temporarily pausing
  boolean mStartedActivity; // has this activity started another

  /** true if the activity is going through a transient pause */
  /* package */boolean mTemporaryPause = false; //used by deliver results and deliver intents in activitythread

  /**
   * true if the activity is being destroyed in order to recreate it with a new
   * configuration
   */
  /* package */boolean mChangingConfigurations = false; // used in activity to know when to stop loader manager, in case of relaunch, do no stop
  /* package */int mConfigChangeFlags;
  /* package */Configuration mCurrentConfig;

  // private SearchManager mSearchManager; //TODO
  // private MenuInflater mMenuInflater; //TODO

  static final class NonConfigurationInstances {
    Object activity;
    HashMap<String, Object> children;
    // ArrayList<Fragment> fragments;//TODO
    // android.util.SparseArray<LoaderManagerImpl> loaders;
    public Object fragments;
    public Object loaders;
  }

  /* package */NonConfigurationInstances mLastNonConfigurationInstances;

  protected Window mWindow;
  private WindowManager mWindowManager;
  /* package */View mDecor = null; //TODO not used yet
  /* package */boolean mWindowAdded = false;
  /* package */boolean mVisibleFromServer = false; //TODO not used yet
  /* package */boolean mVisibleFromClient = true; //TODO not used yet
  // /* package */ActionBarImpl mActionBar = null; //TODO

  private CharSequence mTitle; //TODO not used yet
  private int mTitleColor = 0; //TODO not used

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

  public Activity() {
    Log.i(TAG, "Constucting Activity " + this.getClass().getName());
  }

  /** Return the intent that started this activity. */
  public Intent getIntent() {
    return mIntent;
  }

  /**
   * Change the intent returned by {@link #getIntent}. This holds a
   * reference to the given intent; it does not copy it. Often used in
   * conjunction with {@link #onNewIntent}.
   * 
   * @param newIntent
   *          The new Intent object to return from getIntent
   * 
   * @see #getIntent
   * @see #onNewIntent
   */
  public void setIntent(Intent newIntent) {
    mIntent = newIntent;
  }

  /** Return the application that owns this activity. */
  public final Application getApplication() {
    return mApplication;
  }

  /** Is this activity embedded inside of another activity? */
  public final boolean isChild() {
    return mParent != null;
  }

  /** Return the parent activity if this view is an embedded child. */
  public final Activity getParent() {
    return mParent;
  }

  /** Retrieve the window manager for showing custom windows. */
  public WindowManager getWindowManager() {
    return mWindowManager;
  }

  /**
   * Retrieve the current {@link android.view.Window} for the activity.
   * This can be used to directly access parts of the Window API that
   * are not available through Activity/Screen.
   * 
   * @return Window The current window, or null if the activity is not
   *         visual.
   */
  public Window getWindow() {
    return mWindow;
  }

  /**
   * Return the LoaderManager for this fragment, creating it if needed.
   */
  public LoaderManager getLoaderManager() {
    throw new UnsupportedOperationException();
  }

  /**
   * Calls {@link android.view.Window#getCurrentFocus} on the
   * Window of this Activity to return the currently focused view.
   * 
   * @return View The current View with focus or null.
   * 
   * @see #getWindow
   * @see android.view.Window#getCurrentFocus
   */
  public View getCurrentFocus() {
    return mWindow != null ? mWindow.getCurrentFocus() : null;
  }

  protected void onCreate(Bundle savedInstanceState) {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onCreate()");

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
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".performRestoreInstanceState()");

    onRestoreInstanceState(savedInstanceState);
    restoreManagedDialogs(savedInstanceState);
  }

  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onRestoreInstanceState()");

    if (mWindow != null) {
      Bundle windowState = savedInstanceState.getBundle(WINDOW_HIERARCHY_TAG);
      if (windowState != null) {
        mWindow.restoreHierarchyState(windowState);
      }
    }
  }

  private void restoreManagedDialogs(Bundle savedInstanceState) {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".restoreManagedDialogs()");

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
          // TODO md.mDialog.onRestoreInstanceState(dialogState);
        }
      }
    }

  }

  private Dialog createDialog(Integer dialogId, Bundle state, Bundle args) {
    final Dialog dialog = onCreateDialog(dialogId, args);
    if (dialog == null) {
      return null;
    }
    dialog.dispatchOnCreate(state);
    return dialog;
  }

  private static String savedDialogKeyFor(int key) {
    return SAVED_DIALOG_KEY_PREFIX + key;
  }

  private static String savedDialogArgsKeyFor(int key) {
    return SAVED_DIALOG_ARGS_KEY_PREFIX + key;
  }

  protected void onPostCreate(Bundle savedInstanceState) {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onPostCreate()");

    if (!isChild()) {
      mTitleReady = true;
      onTitleChanged(getTitle(), getTitleColor());
    }
    mCalled = true;
  }

  protected void onStart() {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onStart()");

    mCalled = true;
    //TODO
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
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onRestart()");

    mCalled = true;
  }

  protected void onResume() {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onResume()");

    getApplication().dispatchActivityResumed(this);
    mCalled = true;
  }

  protected void onPostResume() {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onPostResume()");

    final Window win = getWindow();
    if (win != null)
      win.makeActive();
    //  TODO   if (mActionBar != null) mActionBar.setShowHideAnimationEnabled(true);
    mCalled = true;
  }

  protected void onNewIntent(Intent intent) {
  }

  final void performSaveInstanceState(Bundle outState) {
    onSaveInstanceState(outState);
    saveManagedDialogs(outState);
  }

  protected void onSaveInstanceState(Bundle outState) {
    outState.putBundle(WINDOW_HIERARCHY_TAG, mWindow.saveHierarchyState());
    //TODO    Parcelable p = mFragments.saveAllState();
    //    if (p != null) {
    //        outState.putParcelable(FRAGMENTS_TAG, p);
    //    }
    getApplication().dispatchActivitySaveInstanceState(this, outState);
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

  protected void onPause() {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onPause()");

    getApplication().dispatchActivityPaused(this);
    mCalled = true;
  }

  protected void onUserLeaveHint() {
  }

  public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
    return false;
  }

  public CharSequence onCreateDescription() {
    return null;
  }

  protected void onStop() {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onStop()");

    // TODO if (mActionBar != null)
    // mActionBar.setShowHideAnimationEnabled(false);
    getApplication().dispatchActivityStopped(this);
    mCalled = true;
  }

  protected void onDestroy() {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onDestroy()");

    mCalled = true;
    // dismiss any dialogs we are managing.
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

    //  close any cursors we are managing.
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

  public void onConfigurationChanged(Configuration newConfig) {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onConfigurationChanged()");

    mCalled = true;

    //TODO    mFragments.dispatchConfigurationChanged(newConfig);

    if (mWindow != null) {
      // Pass the configuration changed event to the window
      mWindow.onConfigurationChanged(newConfig);
    }

    //if (mActionBar != null) {
    // Do this last; the action bar will need to access
    // view changes from above.
    //  mActionBar.onConfigurationChanged(newConfig);
    //}
  }

  public int getChangingConfigurations() {
    return mConfigChangeFlags;
  }

  @Deprecated
  public Object getLastNonConfigurationInstance() {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".getLastNonConfigurationInstance()");

    return mLastNonConfigurationInstances != null ? mLastNonConfigurationInstances.activity : null;
  }

  public Object onRetainNonConfigurationInstance() {
    return null;
  }

  HashMap<String, Object> getLastNonConfigurationChildInstances() {
    return mLastNonConfigurationInstances != null ? mLastNonConfigurationInstances.children : null;
  }

  HashMap<String, Object> onRetainNonConfigurationChildInstances() {
    return null;
  }

  NonConfigurationInstances retainNonConfigurationInstances() {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".retainNonConfigurationInstances()");

    Object activity = onRetainNonConfigurationInstance();
    HashMap<String, Object> children = onRetainNonConfigurationChildInstances();
    // ArrayList<Fragment> fragments = mFragments.retainNonConfig();
    //    boolean retainLoaders = false;
    //    if (mAllLoaderManagers != null) {
    //        // prune out any loader managers that were already stopped and so
    //        // have nothing useful to retain.
    //        for (int i=mAllLoaderManagers.size()-1; i>=0; i--) {
    //            LoaderManagerImpl lm = mAllLoaderManagers.valueAt(i);
    //            if (lm.mRetaining) {
    //                retainLoaders = true;
    //            } else {
    //                lm.doDestroy();
    //                mAllLoaderManagers.removeAt(i);
    //            }
    //        }
    //    }
    //    if (activity == null && children == null && fragments == null && !retainLoaders) {
    //        return null;
    //    }

    NonConfigurationInstances nci = new NonConfigurationInstances();
    nci.activity = activity;
    nci.children = children;
    nci.fragments = null;//fragments;
    nci.loaders = null;//mAllLoaderManagers;
    return nci;
  }

  public void onLowMemory() {
    Log.v(TAG, this.getClass().getName() + "onLowMemory()");

    mCalled = true;
    // TODO   mFragments.dispatchLowMemory();
  }

  public void onTrimMemory(int level) {
    Log.v(TAG, this.getClass().getName() + "onTrimMemory()");

    mCalled = true;
    //TODO    mFragments.dispatchTrimMemory(level);
  }

  /**
   * Return the FragmentManager for interacting with fragments associated
   * with this activity.
   */
  public FragmentManager getFragmentManager() {
    throw new UnsupportedOperationException();
    // TODO   return null;//mFragments;
  }

  void invalidateFragmentIndex(int index) {
    throw new UnsupportedOperationException();

    //  TODO  Log.v(TAG, "invalidateFragmentIndex: index=" + index);
    //    if (mAllLoaderManagers != null) {
    //        LoaderManagerImpl lm = mAllLoaderManagers.get(index);
    //        if (lm != null && !lm.mRetaining) {
    //            lm.doDestroy();
    //            mAllLoaderManagers.remove(index);
    //        }
    //    }
  }

  public void onAttachFragment(Fragment fragment) {
    throw new UnsupportedOperationException();

  }

  public final Cursor managedQuery(Uri uri, String[] projection, String selection, String sortOrder) {
    throw new UnsupportedOperationException();

  }

  @Deprecated
  public final Cursor managedQuery(Uri uri, String[] projection, String selection, String[] selectionArgs,
                                   String sortOrder) {
    throw new UnsupportedOperationException();

  }

  @Deprecated
  public void startManagingCursor(Cursor c) {
    throw new UnsupportedOperationException();

  }

  @Deprecated
  public void stopManagingCursor(Cursor c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public void setPersistent(boolean isPersistent) {
  }

  public View findViewById(int id) {
    return getWindow().findViewById(id);
  }

  /**
   * Retrieve a reference to this activity's ActionBar.
   * 
   * @return The Activity's ActionBar, or null if it does not have one.
   */
  public ActionBar getActionBar() {
    throw new UnsupportedOperationException();
    //initActionBar();
    //return mActionBar;
  }

  /**
   * Creates a new ActionBar, locates the inflated ActionBarView,
   * initializes the ActionBar with the view, and sets mActionBar.
   */
  private void initActionBar() {
    throw new UnsupportedOperationException();
    //      Window window = getWindow();
    //
    //      // Initializing the window decor can change window feature flags.
    //      // Make sure that we have the correct set before performing the test below.
    //      window.getDecorView();
    //
    //      if (isChild() || !window.hasFeature(Window.FEATURE_ACTION_BAR) || mActionBar != null) {
    //          return;
    //      }
    //      
    //      mActionBar = new ActionBarImpl(this);
  }

  public void setContentView(int layoutResID) {
    getWindow().setContentView(layoutResID);
    // TODO initActionBar();
  }

  public void setContentView(ViewGroup v) {
    getWindow().setContentView(v);
    // TODO initActionBar();
  }

  public void setContentView(View view, ViewGroup.LayoutParams params) {
    getWindow().setContentView(view);// , params);
    //    initActionBar();
  }

  /**
   * Add an additional content view to the activity. Added after any existing
   * ones in the activity -- existing views are NOT removed.
   * 
   * @param view
   *          The desired content to display.
   * @param params
   *          Layout parameters for the view.
   */
  public void addContentView(View view, ViewGroup.LayoutParams params) {
    getWindow().addContentView(view);//, params);
    //    initActionBar();
  }

  /**
   * Sets whether this activity is finished when touched outside its window's
   * bounds.
   */
  public void setFinishOnTouchOutside(boolean finish) {
    mWindow.setCloseOnTouchOutside(finish);
  }

  /**
   * Use with {@link #setDefaultKeyMode} to turn off default handling of
   * keys.
   * 
   * @see #setDefaultKeyMode
   */
  static public final int DEFAULT_KEYS_DISABLE = 0;
  /**
   * Use with {@link #setDefaultKeyMode} to launch the dialer during default
   * key handling.
   * 
   * @see #setDefaultKeyMode
   */
  static public final int DEFAULT_KEYS_DIALER = 1;
  /**
   * Use with {@link #setDefaultKeyMode} to execute a menu shortcut in
   * default key handling.
   * 
   * <p>
   * That is, the user does not need to hold down the menu key to execute menu
   * shortcuts.
   * 
   * @see #setDefaultKeyMode
   */
  static public final int DEFAULT_KEYS_SHORTCUT = 2;
  /**
   * Use with {@link #setDefaultKeyMode} to specify that unhandled keystrokes
   * will start an application-defined search. (If the application or activity
   * does not
   * actually define a search, the the keys will be ignored.)
   * 
   * <p>
   * See {@link android.app.SearchManager android.app.SearchManager} for more
   * details.
   * 
   * @see #setDefaultKeyMode
   */
  static public final int DEFAULT_KEYS_SEARCH_LOCAL = 3;

  /**
   * Use with {@link #setDefaultKeyMode} to specify that unhandled keystrokes
   * will start a global search (typically web search, but some platforms may
   * define alternate
   * methods for global search)
   * 
   * <p>
   * See {@link android.app.SearchManager android.app.SearchManager} for more
   * details.
   * 
   * @see #setDefaultKeyMode
   */
  static public final int DEFAULT_KEYS_SEARCH_GLOBAL = 4;

  /**
   * Select the default key handling for this activity. This controls what
   * will happen to key events that are not otherwise handled. The default
   * mode ({@link #DEFAULT_KEYS_DISABLE}) will simply drop them on the
   * floor. Other modes allow you to launch the dialer
   * ({@link #DEFAULT_KEYS_DIALER}), execute a shortcut in your options
   * menu without requiring the menu key be held down
   * ({@link #DEFAULT_KEYS_SHORTCUT}), or launch a search (
   * {@link #DEFAULT_KEYS_SEARCH_LOCAL} and {@link #DEFAULT_KEYS_SEARCH_GLOBAL}
   * ).
   * 
   * <p>
   * Note that the mode selected here does not impact the default handling of
   * system keys, such as the "back" and "menu" keys, and your activity and its
   * views always get a first chance to receive and handle all application keys.
   * 
   * @param mode
   *          The desired default key mode constant.
   * 
   * @see #DEFAULT_KEYS_DISABLE
   * @see #DEFAULT_KEYS_DIALER
   * @see #DEFAULT_KEYS_SHORTCUT
   * @see #DEFAULT_KEYS_SEARCH_LOCAL
   * @see #DEFAULT_KEYS_SEARCH_GLOBAL
   * @see #onKeyDown
   */
  public final void setDefaultKeyMode(int mode) {
    //TODO      mDefaultKeyMode = mode;
    //      
    //      // Some modes use a SpannableStringBuilder to track & dispatch input events
    //      // This list must remain in sync with the switch in onKeyDown()
    //      switch (mode) {
    //      case DEFAULT_KEYS_DISABLE:
    //      case DEFAULT_KEYS_SHORTCUT:
    //          mDefaultKeySsb = null;      // not used in these modes
    //          break;
    //      case DEFAULT_KEYS_DIALER:
    //      case DEFAULT_KEYS_SEARCH_LOCAL:
    //      case DEFAULT_KEYS_SEARCH_GLOBAL:
    //          mDefaultKeySsb = new SpannableStringBuilder();
    //          Selection.setSelection(mDefaultKeySsb,0);
    //          break;
    //      default:
    //          throw new IllegalArgumentException();
    //      }
  }

  //////////////////////////////////////////
  //    UIEvent CallBacks                 //
  //////////////////////////////////////////

  /**
   * Called when a key was pressed down and not handled by any of the views
   * inside of the activity. So, for example, key presses while the cursor
   * is inside a TextView will not trigger the event (unless it is a navigation
   * to another object) because TextView handles its own key presses.
   * 
   * <p>
   * If the focused view didn't want this event, this method is called.
   * 
   * <p>
   * The default implementation takes care of {@link KeyEvent#KEYCODE_BACK} by
   * calling {@link #onBackPressed()}, though the behavior varies based on the
   * application compatibility mode: for
   * {@link android.os.Build.VERSION_CODES#ECLAIR} or later applications, it
   * will set up the dispatch to call {@link #onKeyUp} where the action will be
   * performed; for earlier applications, it will perform the action immediately
   * in on-down, as those versions of the platform behaved.
   * 
   * <p>
   * Other additional default key handling may be performed if configured with
   * {@link #setDefaultKeyMode}.
   * 
   * @return Return <code>true</code> to prevent this event from being
   *         propagated
   *         further, or <code>false</code> to indicate that you have not
   *         handled
   *         this event and it should continue to be propagated.
   * @see #onKeyUp
   * @see android.view.KeyEvent
   */
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (DEBUG_ACTIVITY)
      Log.i(TAG,
          this.getClass().getName() + ".onKeyDown(Event=" + event.getKeyCode() + "." + event.getAction()
              + ")");

    if (keyCode == KeyEvent.KEYCODE_BACK) {
      onBackPressed();
      return true;
    }
    return false;
    //    if (mDefaultKeyMode == DEFAULT_KEYS_DISABLE) {
    //      return false;
    //    } else if (mDefaultKeyMode == DEFAULT_KEYS_SHORTCUT) {
    //      if (getWindow().performPanelShortcut(Window.FEATURE_OPTIONS_PANEL, keyCode, event,
    //          Menu.FLAG_ALWAYS_PERFORM_CLOSE)) {
    //        return true;
    //      }
    //      return false;
    //    } else {
    //      // Common code for DEFAULT_KEYS_DIALER & DEFAULT_KEYS_SEARCH_*
    //      boolean clearSpannable = false;
    //      boolean handled;
    //      if ((event.getRepeatCount() != 0) || event.isSystem()) {
    //        clearSpannable = true;
    //        handled = false;
    //      } else {
    //        handled = TextKeyListener.getInstance().onKeyDown(null, mDefaultKeySsb, keyCode, event);
    //        if (handled && mDefaultKeySsb.length() > 0) {
    //          // something useable has been typed - dispatch it now.
    //
    //          final String str = mDefaultKeySsb.toString();
    //          clearSpannable = true;
    //
    //          switch (mDefaultKeyMode) {
    //          case DEFAULT_KEYS_DIALER:
    //            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + str));
    //            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //            startActivity(intent);
    //            break;
    //          case DEFAULT_KEYS_SEARCH_LOCAL:
    //            startSearch(str, false, null, false);
    //            break;
    //          case DEFAULT_KEYS_SEARCH_GLOBAL:
    //            startSearch(str, false, null, true);
    //            break;
    //          }
    //        }
    //      }
    //      if (clearSpannable) {
    //        mDefaultKeySsb.clear();
    //        mDefaultKeySsb.clearSpans();
    //        Selection.setSelection(mDefaultKeySsb, 0);
    //      }
    //      return handled;
    //    }
  }

  /**
   * Default implementation of
   * {@link KeyEvent.Callback#onKeyLongPress(int, KeyEvent)
   * KeyEvent.Callback.onKeyLongPress()}: always returns false (doesn't handle
   * the event).
   */
  public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    return false;
  }

  /**
   * Called when a key was released and not handled by any of the views
   * inside of the activity. So, for example, key presses while the cursor
   * is inside a TextView will not trigger the event (unless it is a navigation
   * to another object) because TextView handles its own key presses.
   * 
   * <p>
   * The default implementation handles KEYCODE_BACK to stop the activity and go
   * back.
   * 
   * @return Return <code>true</code> to prevent this event from being
   *         propagated
   *         further, or <code>false</code> to indicate that you have not
   *         handled
   *         this event and it should continue to be propagated.
   * @see #onKeyDown
   * @see KeyEvent
   */
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()) {
      onBackPressed();
      return true;
    }
    return false;
  }

  /**
   * Default implementation of
   * {@link KeyEvent.Callback#onKeyMultiple(int, int, KeyEvent)
   * KeyEvent.Callback.onKeyMultiple()}: always returns false (doesn't handle
   * the event).
   */
  public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
    return false;
  }

  /**
   * Called when the activity has detected the user's press of the back
   * key. The default implementation simply finishes the current activity,
   * but you can override this to do whatever you want.
   */
  public void onBackPressed() {
    // if (!mFragments.popBackStackImmediate()) {
    finish();
    // }
  }

  /**
   * Called when a key shortcut event is not handled by any of the views in the
   * Activity.
   * Override this method to implement global key shortcuts for the Activity.
   * Key shortcuts can also be implemented by setting the
   * {@link MenuItem#setShortcut(char, char) shortcut} property of menu items.
   * 
   * @param keyCode
   *          The value in event.getKeyCode().
   * @param event
   *          Description of the key event.
   * @return True if the key shortcut was handled.
   */
  public boolean onKeyShortcut(int keyCode, KeyEvent event) {
    return false;
  }

  /**
   * Called when a touch screen event was not handled by any of the views
   * under it. This is most useful to process touch events that happen
   * outside of your window bounds, where there is no view to receive it.
   * 
   * @param event
   *          The touch screen event being processed.
   * 
   * @return Return true if you have consumed the event, false if you haven't.
   *         The default implementation always returns false.
   */
  public boolean onTouchEvent(MotionEvent event) {
    if (mWindow.shouldCloseOnTouch(this, event)) {
      finish();
      return true;
    }

    return false;
  }

  /**
   * Called when the trackball was moved and not handled by any of the
   * views inside of the activity. So, for example, if the trackball moves
   * while focus is on a button, you will receive a call here because
   * buttons do not normally do anything with trackball events. The call
   * here happens <em>before</em> trackball movements are converted to
   * DPAD key events, which then get sent back to the view hierarchy, and
   * will be processed at the point for things like focus navigation.
   * 
   * @param event
   *          The trackball event being processed.
   * 
   * @return Return true if you have consumed the event, false if you haven't.
   *         The default implementation always returns false.
   */
  public boolean onTrackballEvent(MotionEvent event) {
    return false;
  }

  /**
   * Called when a generic motion event was not handled by any of the
   * views inside of the activity.
   * <p>
   * Generic motion events describe joystick movements, mouse hovers, track pad
   * touches, scroll wheel movements and other input events. The
   * {@link MotionEvent#getSource() source} of the motion event specifies the
   * class of input that was received. Implementations of this method must
   * examine the bits in the source before processing the event. The following
   * code example shows how this is done.
   * </p>
   * <p>
   * Generic motion events with source class
   * {@link android.view.InputDevice#SOURCE_CLASS_POINTER} are delivered to the
   * view under the pointer. All other generic motion events are delivered to
   * the focused view.
   * </p>
   * <p>
   * See {@link View#onGenericMotionEvent(MotionEvent)} for an example of how to
   * handle this event.
   * </p>
   * 
   * @param event
   *          The generic motion event being processed.
   * 
   * @return Return true if you have consumed the event, false if you haven't.
   *         The default implementation always returns false.
   */
  public boolean onGenericMotionEvent(MotionEvent event) {
    return false;
  }

  /**
   * Called whenever a key, touch, or trackball event is dispatched to the
   * activity. Implement this method if you wish to know that the user has
   * interacted with the device in some way while your activity is running.
   * This callback and {@link #onUserLeaveHint} are intended to help
   * activities manage status bar notifications intelligently; specifically,
   * for helping activities determine the proper time to cancel a notfication.
   * 
   * <p>
   * All calls to your activity's {@link #onUserLeaveHint} callback will be
   * accompanied by calls to {@link #onUserInteraction}. This ensures that your
   * activity will be told of relevant user activity such as pulling down the
   * notification pane and touching an item there.
   * 
   * <p>
   * Note that this callback will be invoked for the touch down action that
   * begins a touch gesture, but may not be invoked for the touch-moved and
   * touch-up actions that follow.
   * 
   * @see #onUserLeaveHint()
   */
  public void onUserInteraction() {
  }

  //  public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
  //    // Update window manager if: we have a view, that view is
  //    // attached to its parent (which will be a RootView), and
  //    // this activity is not embedded.
  //    if (mParent == null) {
  //      View decor = mDecor;
  //      if (decor != null && decor.getParent() != null) {
  //        getWindowManager().updateViewLayout(decor, params);
  //      }
  //    }
  //  }

  public void onContentChanged() {
  }

  /**
   * Called when the current {@link Window} of the activity gains or loses
   * focus. This is the best indicator of whether this activity is visible
   * to the user. The default implementation clears the key tracking
   * state, so should always be called.
   * 
   * <p>
   * Note that this provides information about global focus state, which is
   * managed independently of activity lifecycles. As such, while focus changes
   * will generally have some relation to lifecycle changes (an activity that is
   * stopped will not generally get window focus), you should not rely on any
   * particular order between the callbacks here and those in the other
   * lifecycle methods such as {@link #onResume}.
   * 
   * <p>
   * As a general rule, however, a resumed activity will have window focus...
   * unless it has displayed other dialogs or popups that take input focus, in
   * which case the activity itself will not have focus when the other windows
   * have it. Likewise, the system may display system-level windows (such as the
   * status bar notification panel or a system alert) which will temporarily
   * take window input focus without pausing the foreground activity.
   * 
   * @param hasFocus
   *          Whether the window of this activity has focus.
   * 
   * @see #hasWindowFocus()
   * @see #onResume
   * @see View#onWindowFocusChanged(boolean)
   */
  public void onWindowFocusChanged(boolean hasFocus) {
  }

  /**
   * Called when the main window associated with the activity has been
   * attached to the window manager.
   * See {@link View#onAttachedToWindow() View.onAttachedToWindow()} for more
   * information.
   * 
   * @see View#onAttachedToWindow
   */
  public void onAttachedToWindow() {
  }

  /**
   * Called when the main window associated with the activity has been
   * detached from the window manager.
   * See {@link View#onDetachedFromWindow() View.onDetachedFromWindow()} for
   * more information.
   * 
   * @see View#onDetachedFromWindow
   */
  public void onDetachedFromWindow() {
  }

  /**
   * Returns true if this activity's <em>main</em> window currently has window
   * focus.
   * Note that this is not the same as the view itself having focus.
   * 
   * @return True if this activity's main window currently has window focus.
   * 
   * @see #onWindowAttributesChanged(android.view.WindowManager.LayoutParams)
   */
  public boolean hasWindowFocus() {
    //  TODO  Window w = getWindow();
    //    if (w != null) {
    //      View d = w.getDecorView();
    //      if (d != null) {
    //        return d.hasWindowFocus();
    //      }
    //    }
    //    return false;
    throw new UnsupportedOperationException();

  }

  /**
   * Called to process key events. You can override this to intercept all
   * key events before they are dispatched to the window. Be sure to call
   * this implementation for key events that should be handled normally.
   * 
   * @param event
   *          The key event.
   * 
   * @return boolean Return true if this event was consumed.
   */
  public boolean dispatchKeyEvent(KeyEvent event) {
    //    onUserInteraction();
    //    Window win = getWindow();
    //    if (win.superDispatchKeyEvent(event)) {
    //      return true;
    //    }
    //    View decor = mDecor;
    //    if (decor == null)
    //      decor = win.getDecorView();
    //    return event.dispatch(this, decor != null ? decor.getKeyDispatcherState() : null, this);
    throw new UnsupportedOperationException();

  }

  /**
   * Called to process a key shortcut event.
   * You can override this to intercept all key shortcut events before they are
   * dispatched to the window. Be sure to call this implementation for key
   * shortcut
   * events that should be handled normally.
   * 
   * @param event
   *          The key shortcut event.
   * @return True if this event was consumed.
   */
  public boolean dispatchKeyShortcutEvent(KeyEvent event) {
    //    onUserInteraction();
    //    if (getWindow().superDispatchKeyShortcutEvent(event)) {
    //      return true;
    //    }
    //    return onKeyShortcut(event.getKeyCode(), event);
    throw new UnsupportedOperationException();

  }

  /**
   * Called to process touch screen events. You can override this to
   * intercept all touch screen events before they are dispatched to the
   * window. Be sure to call this implementation for touch screen events
   * that should be handled normally.
   * 
   * @param ev
   *          The touch screen event.
   * 
   * @return boolean Return true if this event was consumed.
   */
  public boolean dispatchTouchEvent(MotionEvent ev) {
    //    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
    //      onUserInteraction();
    //    }
    //    if (getWindow().superDispatchTouchEvent(ev)) {
    //      return true;
    //    }
    //    return onTouchEvent(ev);
    throw new UnsupportedOperationException();

  }

  /**
   * Called to process trackball events. You can override this to
   * intercept all trackball events before they are dispatched to the
   * window. Be sure to call this implementation for trackball events
   * that should be handled normally.
   * 
   * @param ev
   *          The trackball event.
   * 
   * @return boolean Return true if this event was consumed.
   */
  public boolean dispatchTrackballEvent(MotionEvent ev) {
    //    onUserInteraction();
    //    if (getWindow().superDispatchTrackballEvent(ev)) {
    //      return true;
    //    }
    //    return onTrackballEvent(ev);
    throw new UnsupportedOperationException();

  }

  /**
   * Called to process generic motion events. You can override this to
   * intercept all generic motion events before they are dispatched to the
   * window. Be sure to call this implementation for generic motion events
   * that should be handled normally.
   * 
   * @param ev
   *          The generic motion event.
   * 
   * @return boolean Return true if this event was consumed.
   */
  public boolean dispatchGenericMotionEvent(MotionEvent ev) {
    //    onUserInteraction();
    //    if (getWindow().superDispatchGenericMotionEvent(ev)) {
    //      return true;
    //    }
    //    return onGenericMotionEvent(ev);
    throw new UnsupportedOperationException();

  }

  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
    //    event.setClassName(getClass().getName());
    //    event.setPackageName(getPackageName());
    //
    //    LayoutParams params = getWindow().getAttributes();
    //    boolean isFullScreen = (params.width == LayoutParams.MATCH_PARENT)
    //        && (params.height == LayoutParams.MATCH_PARENT);
    //    event.setFullScreen(isFullScreen);
    //
    //    CharSequence title = getTitle();
    //    if (!TextUtils.isEmpty(title)) {
    //      event.getText().add(title);
    //    }
    throw new UnsupportedOperationException();
    //    return true;
  }

  /**
   * Default implementation of
   * {@link android.view.Window.Callback#onCreatePanelView} for activities. This
   * simply returns null so that all panel sub-windows will have the default
   * menu behavior.
   */
  public View onCreatePanelView(int featureId) {
    return null;
  }

  /**
   * Default implementation of
   * {@link android.view.Window.Callback#onCreatePanelMenu} for activities. This
   * calls through to the new {@link #onCreateOptionsMenu} method for the
   * {@link android.view.Window#FEATURE_OPTIONS_PANEL} panel,
   * so that subclasses of Activity don't need to deal with feature codes.
   */
  public boolean onCreatePanelMenu(int featureId, Menu menu) {
    //    if (featureId == Window.FEATURE_OPTIONS_PANEL) {
    //      boolean show = onCreateOptionsMenu(menu);
    //      show |= mFragments.dispatchCreateOptionsMenu(menu, getMenuInflater());
    //      return show;
    //    }
    //    return false;
    throw new UnsupportedOperationException();

  }

  /**
   * Default implementation of
   * {@link android.view.Window.Callback#onPreparePanel} for activities. This
   * calls through to the new {@link #onPrepareOptionsMenu} method for the
   * {@link android.view.Window#FEATURE_OPTIONS_PANEL} panel, so that subclasses
   * of
   * Activity don't need to deal with feature codes.
   */
  public boolean onPreparePanel(int featureId, View view, Menu menu) {
    //    if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null) {
    //      boolean goforit = onPrepareOptionsMenu(menu);
    //      goforit |= mFragments.dispatchPrepareOptionsMenu(menu);
    //      return goforit && menu.hasVisibleItems();
    //    }
    //    return true;
    throw new UnsupportedOperationException();

  }

  /**
   * {@inheritDoc}
   * 
   * @return The default implementation returns true.
   */
  public boolean onMenuOpened(int featureId, Menu menu) {
    //    if (featureId == Window.FEATURE_ACTION_BAR) {
    //      initActionBar();
    //      if (mActionBar != null) {
    //        mActionBar.dispatchMenuVisibilityChanged(true);
    //      } else {
    //        Log.e(TAG, "Tried to open action bar menu with no action bar");
    //      }
    //    }
    //    return true;
    throw new UnsupportedOperationException();

  }

  /**
   * Default implementation of
   * {@link android.view.Window.Callback#onMenuItemSelected} for activities.
   * This calls through to the new {@link #onOptionsItemSelected} method for the
   * {@link android.view.Window#FEATURE_OPTIONS_PANEL} panel, so that subclasses
   * of
   * Activity don't need to deal with feature codes.
   */
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    throw new UnsupportedOperationException();

    //    switch (featureId) {
    //    case Window.FEATURE_OPTIONS_PANEL:
    //      // Put event logging here so it gets called even if subclass
    //      // doesn't call through to superclass's implmeentation of each
    //      // of these methods below
    //      EventLog.writeEvent(50000, 0, item.getTitleCondensed());
    //      if (onOptionsItemSelected(item)) {
    //        return true;
    //      }
    //      return mFragments.dispatchOptionsItemSelected(item);
    //
    //    case Window.FEATURE_CONTEXT_MENU:
    //      EventLog.writeEvent(50000, 1, item.getTitleCondensed());
    //      if (onContextItemSelected(item)) {
    //        return true;
    //      }
    //      return mFragments.dispatchContextItemSelected(item);
    //
    //    default:
    //      return false;
    //    }
  }

  /**
   * Default implementation of
   * {@link android.view.Window.Callback#onPanelClosed(int, Menu)} for
   * activities. This calls through to {@link #onOptionsMenuClosed(Menu)} method
   * for the {@link android.view.Window#FEATURE_OPTIONS_PANEL} panel,
   * so that subclasses of Activity don't need to deal with feature codes.
   * For context menus ({@link Window#FEATURE_CONTEXT_MENU}), the
   * {@link #onContextMenuClosed(Menu)} will be called.
   */
  public void onPanelClosed(int featureId, Menu menu) {
    //    switch (featureId) {
    //    case Window.FEATURE_OPTIONS_PANEL:
    //      mFragments.dispatchOptionsMenuClosed(menu);
    //      onOptionsMenuClosed(menu);
    //      break;
    //
    //    case Window.FEATURE_CONTEXT_MENU:
    //      onContextMenuClosed(menu);
    //      break;
    //
    //    case Window.FEATURE_ACTION_BAR:
    //      initActionBar();
    //      mActionBar.dispatchMenuVisibilityChanged(false);
    //      break;
    //    }
    throw new UnsupportedOperationException();

  }

  /**
   * Declare that the options menu has changed, so should be recreated.
   * The {@link #onCreateOptionsMenu(Menu)} method will be called the next
   * time it needs to be displayed.
   */
  public void invalidateOptionsMenu() {
    //    mWindow.invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
    throw new UnsupportedOperationException();

  }

  /**
   * Initialize the contents of the Activity's standard options menu. You
   * should place your menu items in to <var>menu</var>.
   * 
   * <p>
   * This is only called once, the first time the options menu is displayed. To
   * update the menu every time it is displayed, see
   * {@link #onPrepareOptionsMenu}.
   * 
   * <p>
   * The default implementation populates the menu with standard system menu
   * items. These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
   * they will be correctly ordered with application-defined menu items.
   * Deriving classes should always call through to the base implementation.
   * 
   * <p>
   * You can safely hold on to <var>menu</var> (and any items created from it),
   * making modifications to it as desired, until the next time
   * onCreateOptionsMenu() is called.
   * 
   * <p>
   * When you add items to the menu, you can implement the Activity's
   * {@link #onOptionsItemSelected} method to handle them there.
   * 
   * @param menu
   *          The options menu in which you place your items.
   * 
   * @return You must return true for the menu to be displayed;
   *         if you return false it will not be shown.
   * 
   * @see #onPrepareOptionsMenu
   * @see #onOptionsItemSelected
   */
  public boolean onCreateOptionsMenu(Menu menu) {
    //    if (mParent != null) {
    //      return mParent.onCreateOptionsMenu(menu);
    //    }
    //    return true;
    throw new UnsupportedOperationException();

  }

  /**
   * Prepare the Screen's standard options menu to be displayed. This is
   * called right before the menu is shown, every time it is shown. You can
   * use this method to efficiently enable/disable items or otherwise
   * dynamically modify the contents.
   * 
   * <p>
   * The default implementation updates the system menu items based on the
   * activity's state. Deriving classes should always call through to the base
   * class implementation.
   * 
   * @param menu
   *          The options menu as last shown or first initialized by
   *          onCreateOptionsMenu().
   * 
   * @return You must return true for the menu to be displayed;
   *         if you return false it will not be shown.
   * 
   * @see #onCreateOptionsMenu
   */
  public boolean onPrepareOptionsMenu(Menu menu) {
    //    if (mParent != null) {
    //      return mParent.onPrepareOptionsMenu(menu);
    //    }
    //    return true;
    throw new UnsupportedOperationException();

  }

  /**
   * This hook is called whenever an item in your options menu is selected.
   * The default implementation simply returns false to have the normal
   * processing happen (calling the item's Runnable or sending a message to
   * its Handler as appropriate). You can use this method for any items
   * for which you would like to do processing without those other
   * facilities.
   * 
   * <p>
   * Derived classes should call through to the base class for it to perform the
   * default menu handling.
   * 
   * @param item
   *          The menu item that was selected.
   * 
   * @return boolean Return false to allow normal menu processing to
   *         proceed, true to consume it here.
   * 
   * @see #onCreateOptionsMenu
   */
  public boolean onOptionsItemSelected(MenuItem item) {
    //    if (mParent != null) {
    //      return mParent.onOptionsItemSelected(item);
    //    }
    //    return false;
    throw new UnsupportedOperationException();

  }

  /**
   * This hook is called whenever the options menu is being closed (either by
   * the user canceling
   * the menu with the back/menu button, or when an item is selected).
   * 
   * @param menu
   *          The options menu as last shown or first initialized by
   *          onCreateOptionsMenu().
   */
  public void onOptionsMenuClosed(Menu menu) {
    //    if (mParent != null) {
    //      mParent.onOptionsMenuClosed(menu);
    //    }
    throw new UnsupportedOperationException();

  }

  /**
   * Programmatically opens the options menu. If the options menu is already
   * open, this method does nothing.
   */
  public void openOptionsMenu() {
    //    mWindow.openPanel(Window.FEATURE_OPTIONS_PANEL, null);
    throw new UnsupportedOperationException();

  }

  /**
   * Progammatically closes the options menu. If the options menu is already
   * closed, this method does nothing.
   */
  public void closeOptionsMenu() {
    //    mWindow.closePanel(Window.FEATURE_OPTIONS_PANEL);
    throw new UnsupportedOperationException();

  }

  /**
   * Called when a context menu for the {@code view} is about to be shown.
   * Unlike {@link #onCreateOptionsMenu(Menu)}, this will be called every
   * time the context menu is about to be shown and should be populated for
   * the view (or item inside the view for {@link AdapterView} subclasses,
   * this can be found in the {@code menuInfo})).
   * <p>
   * Use {@link #onContextItemSelected(android.view.MenuItem)} to know when an
   * item has been selected.
   * <p>
   * It is not safe to hold onto the context menu after this method returns.
   * {@inheritDoc}
   */
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
  }

  /**
   * Registers a context menu to be shown for the given view (multiple views
   * can show the context menu). This method will set the
   * {@link OnCreateContextMenuListener} on the view to this activity, so
   * {@link #onCreateContextMenu(ContextMenu, View, ContextMenuInfo)} will be
   * called when it is time to show the context menu.
   * 
   * @see #unregisterForContextMenu(View)
   * @param view
   *          The view that should show a context menu.
   */
  public void registerForContextMenu(View view) {
    //    view.setOnCreateContextMenuListener(this);
    throw new UnsupportedOperationException();

  }

  /**
   * Prevents a context menu to be shown for the given view. This method will
   * remove the {@link OnCreateContextMenuListener} on the view.
   * 
   * @see #registerForContextMenu(View)
   * @param view
   *          The view that should stop showing a context menu.
   */
  public void unregisterForContextMenu(View view) {
    //    view.setOnCreateContextMenuListener(null);
    throw new UnsupportedOperationException();

  }

  /**
   * Programmatically opens the context menu for a particular {@code view}.
   * The {@code view} should have been added via
   * {@link #registerForContextMenu(View)}.
   * 
   * @param view
   *          The view to show the context menu for.
   */
  public void openContextMenu(View view) {
    //    view.showContextMenu();
    throw new UnsupportedOperationException();

  }

  /**
   * Programmatically closes the most recently opened context menu, if showing.
   */
  public void closeContextMenu() {
    //    mWindow.closePanel(Window.FEATURE_CONTEXT_MENU);
    throw new UnsupportedOperationException();

  }

  /**
   * This hook is called whenever an item in a context menu is selected. The
   * default implementation simply returns false to have the normal processing
   * happen (calling the item's Runnable or sending a message to its Handler
   * as appropriate). You can use this method for any items for which you
   * would like to do processing without those other facilities.
   * <p>
   * Use {@link MenuItem#getMenuInfo()} to get extra information set by the View
   * that added this menu item.
   * <p>
   * Derived classes should call through to the base class for it to perform the
   * default menu handling.
   * 
   * @param item
   *          The context menu item that was selected.
   * @return boolean Return false to allow normal context menu processing to
   *         proceed, true to consume it here.
   */
  public boolean onContextItemSelected(MenuItem item) {
    //    if (mParent != null) {
    //      return mParent.onContextItemSelected(item);
    //    }
    //    return false;
    throw new UnsupportedOperationException();

  }

  /**
   * This hook is called whenever the context menu is being closed (either by
   * the user canceling the menu with the back/menu button, or when an item is
   * selected).
   * 
   * @param menu
   *          The context menu that is being closed.
   */
  public void onContextMenuClosed(Menu menu) {
    //      if (mParent != null) {
    //          mParent.onContextMenuClosed(menu);
    //      }
    throw new UnsupportedOperationException();
  }

  void makeVisible() {
    //    if (!mWindowAdded) {
    //      ViewManager wm = getWindowManager();
    //      wm.addView(mDecor, getWindow().getAttributes());
    //      mWindowAdded = true;
    //    }
    //    mDecor.setVisibility(View.VISIBLE);
    mWindow.setVisible();
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
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".startActivityForResult(intent=" + intent + " requestCode="
          + requestCode + ")");
    // if (mParent == null) {
    // Instrumentation.ActivityResult ar =
    onPause();
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

  public boolean isChangingConfigurations() {
    return mChangingConfigurations;
  }

  /**
   * Runs the specified action on the UI thread. If the current thread is the UI
   * thread, then the action is
   * executed immediately. If the current thread is not the UI thread, the
   * action is posted to the event queue
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
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".attach()");

    attachBaseContext(context);

    // mFragments.attachActivity(this);

    mWindow = PolicyManager.makeNewWindow(this);
    mWindow.setCallback(this);

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

    mWindow.setWindowManager(null, mToken, mComponent.getClassName(), true);
    // (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
    // if (mParent != null) {
    // mWindow.setContainer(mParent.getWindow());
    // }
    mWindowManager = mWindow.getWindowManager();
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
      throw new SuperNotCalledException("Activity " + mComponent.getClassName()
          + " did not call through to super.onStart()");
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

    mLastNonConfigurationInstances = null;

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
    if (!mCalled) {
      throw new SuperNotCalledException("Activity " + mComponent.toShortString()
          + " did not call through to super.onPause()");
    }
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
    mWindow.destroy();
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
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".onActivityResult()");
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

  /**
   * Check to see whether this activity is in the process of finishing, either
   * because you called {@link #finish} on it or someone else has requested that
   * it finished. This is often used in {@link #onPause} to determine whether
   * the activity is simply pausing or completely finishing.
   * 
   * @return If the activity is finishing, returns true; else returns false.
   * 
   * @see #finish
   */
  public boolean isFinishing() {
    return mFinished;
  }

  /**
   * Call this when your activity is done and should be closed. The
   * ActivityResult is propagated back to
   * whoever launched you via onActivityResult().
   */
  public void finish() {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".finish()");
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
    ActivityManagerNative.getDefault().finishActivity(mToken, resultCode, resultData);
  }

  public final void setResult(int resultCode) {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".setResult(resultCode=" + resultCode + ")");
    synchronized (this) {
      mResultCode = resultCode;
      mResultData = null;
    }
  }

  public final void setResult(int resultCode, Intent data) {
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".setResult(resultCode=" + resultCode + " data=" + data + ")");
    synchronized (this) {
      mResultCode = resultCode;
      mResultData = data;
    }
  }

  final void performUserLeaving() {
    onUserInteraction();
    onUserLeaveHint();
  }

  @Deprecated
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
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".showDialog(id=" + id + ")");

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
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".dismissDialog(id=" + id + ")");
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
    if (DEBUG_ACTIVITY)
      Log.i(TAG, this.getClass().getName() + ".removeDialog(id=" + id + ")");
    if (mManagedDialogs != null) {
      final ManagedDialog md = mManagedDialogs.get(id);
      if (md != null) {
        md.mDialog.dismiss();
        mManagedDialogs.remove(id);
      }
    }
  }

  /**
   * Change the title associated with this activity. If this is a
   * top-level activity, the title for its window will change. If it
   * is an embedded activity, the parent can do whatever it wants
   * with it.
   */
  public void setTitle(CharSequence title) {
    mTitle = title;
    onTitleChanged(title, mTitleColor);

    if (mParent != null) {
      mParent.onChildTitleChanged(this, title);
    }
  }

  /**
   * Change the title associated with this activity. If this is a
   * top-level activity, the title for its window will change. If it
   * is an embedded activity, the parent can do whatever it wants
   * with it.
   */
  public void setTitle(int titleId) {
    setTitle(getText(titleId));
  }

  public void setTitleColor(int textColor) {
    mTitleColor = textColor;
    onTitleChanged(mTitle, textColor);
  }

  public final CharSequence getTitle() {
    return mTitle;
  }

  public final int getTitleColor() {
    return mTitleColor;
  }

  protected void onTitleChanged(CharSequence title, int color) {
    //      if (mTitleReady) {
    //          final Window win = getWindow();
    //          if (win != null) {
    //              win.setTitle(title);
    //              if (color != 0) {
    //                  win.setTitleColor(color);
    //              }
    //          }
    //      }
  }

  protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
  }

}
