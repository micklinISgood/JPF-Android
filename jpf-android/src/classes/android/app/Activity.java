package android.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.internal.policy.PolicyManager;

public class Activity extends ContextThemeWrapper {

	private Context mBase;
	// private Application mApplication;
	Intent mIntent; // Reference to the intent that started this Activity
	ActivityThread mMainThread; // Reference to the applciation's ActivityThread
	Activity mParent; // Reference to the activity that started this activity

	// Activity Locks
	boolean mResumed;
	private boolean mStopped;
	boolean mFinished;
	boolean mStartedActivity;

	/** true if the activity is going through a transient pause */
	boolean mTemporaryPause = false;

	private Window mWindow;
	// private WindowManager mWindowManager;

	boolean mWindowAdded = false;

	private CharSequence mTitle;
	private int mTitleColor = 0;

	Intent mResultData = null;

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

	final void attach(ActivityThread aThread,  Application application, Activity parent) {// ,
		// Instrumentation instr, IBinder token, int ident,
		//, Intent intent, ActivityInfo info,
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

	public void startActivity(Intent intent) {
		startActivityForResult(intent, -1);
	}

	public void startActivityForResult(Intent intent, int requestCode) {
		// if (mParent == null) {
		// Instrumentation.ActivityResult ar =
		System.out.println("Activity for result");
		ActivityManagerProxy.startActivityProxy(intent);
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

}
