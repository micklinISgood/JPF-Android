package android.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class Activity {

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

	//private SearchManager mSearchManager;
	// private MenuInflater mMenuInflater;

	private Window mWindow;

	// private WindowManager mWindowManager;
	View mDecor = null;
	boolean mWindowAdded = false;
	// ActionBarImpl mActionBar = null;

	private CharSequence mTitle;
	private int mTitleColor = 0;

	Intent mResultData = null;

	private boolean mTitleReady = false;

	private Thread mUiThread;
	final Handler mHandler = new Handler();

	public Activity() {
	}

	public void onCreate(Bundle savedInstanceState) {

	}

	/**
	 * Retrieve the current {@link android.view.Window} for the activity. This
	 * can be used to directly access parts of the Window API that are not
	 * available through Activity/Screen.
	 * 
	 * @return Window The current window, or null if the activity is not visual.
	 */
	public Window getWindow() {
		return mWindow;
	}

	/**
	 * Finds a view that was identified by the id attribute from the XML that
	 * was processed in {@link #onCreate}.
	 * 
	 * @return The view if found or null otherwise.
	 */
	public View findViewById(int id) {
		return getWindow().findViewById(id);
	}

	/**
	 * Set the activity content from a layout resource. The resource will be
	 * inflated, adding all top-level views to the activity.
	 * 
	 * @param layoutResID
	 *            Resource ID to be inflated.
	 * 
	 * @see #setContentView(android.view.View)
	 * @see #setContentView(android.view.View,
	 *      android.view.ViewGroup.LayoutParams)
	 */
	public void setContentView(int layoutResID) {
		getWindow().setContentView(layoutResID);
		// initActionBar();
	}

	/**
	 * Set the activity content to an explicit view. This view is placed
	 * directly into the activity's view hierarchy. It can itself be a complex
	 * view hierarchy. When calling this method, the layout parameters of the
	 * specified view are ignored. Both the width and the height of the view are
	 * set by default to {@link ViewGroup.LayoutParams#MATCH_PARENT}. To use
	 * your own layout parameters, invoke
	 * {@link #setContentView(android.view.View, android.view.ViewGroup.LayoutParams)}
	 * instead.
	 * 
	 * @param view
	 *            The desired content to display.
	 * 
	 * @see #setContentView(int)
	 * @see #setContentView(android.view.View,
	 *      android.view.ViewGroup.LayoutParams)
	 */
	public void setContentView(View view) {
		getWindow().setContentView(view);
	//	initActionBar();
	}

	void makeVisible() {
//		if (!mWindowAdded) {
//			ViewManager wm = getWindowManager();
//			wm.addView(mDecor, getWindow().getAttributes());
//			mWindowAdded = true;
//		}
//		mDecor.setVisibility(View.VISIBLE);
	}

	
//	final void attach(Context context, ActivityThread aThread,
//            Instrumentation instr, IBinder token, int ident,
//            Application application, Intent intent, ActivityInfo info,
//            CharSequence title, Activity parent, String id,
//            NonConfigurationInstances lastNonConfigurationInstances,
//            Configuration config) {
//        attachBaseContext(context);
//
//        mWindow = new PhoneWindo
//        mWindow.setCallback(this);
//        mWindow.getLayoutInflater().setPrivateFactory(this);
//        if (info.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
//            mWindow.setSoftInputMode(info.softInputMode);
//        }
//        if (info.uiOptions != 0) {
//            mWindow.setUiOptions(info.uiOptions);
//        }
//        mUiThread = Thread.currentThread();
//        
//        mMainThread = aThread;
//        mInstrumentation = instr;
//        mToken = token;
//        mIdent = ident;
//        mApplication = application;
//        mIntent = intent;
//        mComponent = intent.getComponent();
//        mActivityInfo = info;
//        mTitle = title;
//        mParent = parent;
//        mEmbeddedID = id;
//        mLastNonConfigurationInstances = lastNonConfigurationInstances;
//
//        mWindow.setWindowManager(null, mToken, mComponent.flattenToString(),
//                (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
//        if (mParent != null) {
//            mWindow.setContainer(mParent.getWindow());
//        }
//        mWindowManager = mWindow.getWindowManager();
//        mCurrentConfig = config;
//    }
}
