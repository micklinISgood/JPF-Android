package android.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.internal.policy.PolicyManager;

public class Activity extends ContextThemeWrapper implements Window.Callback {
	private Context mBase;
	private Application mApplication;
	Intent mIntent;

	ActivityThread mMainThread;
	Activity mParent;

	boolean mLoadersStarted;
	boolean mResumed;
	private boolean mStopped;
	boolean mFinished;
	boolean mStartedActivity;
	/** true if the activity is going through a transient pause */
	boolean mTemporaryPause = false;

	// private SearchManager mSearchManager;
	// private MenuInflater mMenuInflater;

	private Window mWindow;
	// private WindowManager mWindowManager;

	// View mDecor = null; - not using Decor jet
	boolean mWindowAdded = false;
	// ActionBarImpl mActionBar = null;

	private CharSequence mTitle;
	private int mTitleColor = 0;

	Intent mResultData = null;

	private boolean mTitleReady = false;

	private Thread mUiThread;
	final Handler mHandler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		// Loads activity from bundle

	}

	protected void onPostCreate(Bundle savedInstanceState) {
		// if (!isChild()) {
		// mTitleReady = true;
		// onTitleChanged(getTitle(), getTitleColor());
		// }
		// mCalled = true;
	}

	protected void onStart() {
		// mCalled = true;
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
		// getApplication().dispatchActivityStarted(this);
	}

	protected void onRestart() {
		// mCalled = true;
	}

	protected void onResume() {
		// getApplication().dispatchActivityResumed(this);
		// mCalled = true;
	}

	protected void onPostResume() {
		// final Window win = getWindow();
		// if (win != null) win.makeActive();
		// if (mActionBar != null) mActionBar.setShowHideAnimationEnabled(true);
		// mCalled = true;
	}

	protected void onPause() {
		// getApplication().dispatchActivityPaused(this);
		// mCalled = true;
	}

	protected void onStop() {
		// if (mActionBar != null)
		// mActionBar.setShowHideAnimationEnabled(false);
		// getApplication().dispatchActivityStopped(this);
		// mCalled = true;
	}

	protected void onDestroy() {
		// mCalled = true;
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
		// getApplication().dispatchActivityDestroyed(this);
	}

	public Window getWindow() {
		return mWindow;
	}

	public View findViewById(int id) {
		return getWindow().findViewById(id);
	}

	public void setContentView(int layoutResID) {
		getWindow().setContentView(layoutResID);
		// initActionBar();
	}
	
	public void setContentView(ViewGroup v){
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

	final void attach(ActivityThread aThread) {// ,
		// Instrumentation instr, IBinder token, int ident,
		// Application application, Intent intent, ActivityInfo info,
		// CharSequence title, Activity parent, String id,
		// NonConfigurationInstances lastNonConfigurationInstances,
		// Configuration config) {
		//attachBaseContext(context);

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
		// mInstrumentation = instr;
		// mToken = token;
		// mIdent = ident;
		// mApplication = application;
		// mIntent = intent;
		// mComponent = intent.getComponent();
		// mActivityInfo = info;
		// mTitle = title;
		// mParent = parent;
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

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchKeyShortcutEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchTrackballEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View onCreatePanelView(int featureId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onContentChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}
	
	
	
}
