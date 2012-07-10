package android.view;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

/**
 * Models a Window
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class Window extends ViewGroup {
	private final static String TAG = "Window";

	/** Application context */
	Context mContext; // The Activity's context to which this window belong

	/** Used to inflate layout */
	private static LayoutInflater mLayoutInflater; // must be the same for the
													// entire application
													// context

	/**
	 * For now this is the root of the ViewTree (it is suppose to be the
	 * DecorView but we are not using DecorView yet)
	 */
	private ViewGroup mContentParent;

	/**Maps the id of the layout to the inflated view hierarchy */
	static private HashMap<Integer, View> layoutMap = new HashMap<Integer, View>(); 
																					
	public Window(Context context) {
		super(context);
		Log.i(TAG, "Creating new Window");
		
		super.mID = 0; // temp solution the Window must not be a view If you
						// look at view hierarchy the frame view is the base
		mContext = context;
		
		if (mLayoutInflater == null) {
			Log.i(TAG, "Creating new LayouInflator");
			mLayoutInflater = new LayoutInflater(mContext);
			init();
		}
	}

	/**
	 * inflates the layouts
	 */
	private void init() {
		init0(); // build component map
		// build layout map and + component map
		int[] layouts = getLayouts();
		for (int i = 0; i < layouts.length; i++) {
			layoutMap
					.put(layouts[i], mLayoutInflater.inflate(layouts[i], null));
		}

	}

	/**
	 * Returns a list of the int id's of the layout resources
	 * 
	 * @return
	 */
	static native int[] getLayouts();

	/**
	 * Return the Context this window policy is running in, for retrieving
	 * resources and other information.
	 * 
	 * @return Context The Context that was supplied to the constructor.
	 */
	public final Context getContext() {
		return mContext;
	}

	public View findViewById(int id) {
		return mContentParent.findViewById(id);
	}

	/**
	 * Sets the parent view of this Window
	 * @param v
	 */
	public void setContentView(int layoutResID) {
		View layout = layoutMap.get(layoutResID); // inflate
		layout.mParent = null;
		mContentParent = (ViewGroup) layout;
		addView(mContentParent);
		// final Callback cb = getCallback();
		// //if (cb != null && !isDestroyed()) {
		// cb.onContentChanged();
		// }
		setVisible0("title", true); //TODO add the actual title
	}


	public void setContentView(View v) {
		mContentParent = (ViewGroup) v;
		addView(v);
		setVisible0("title", true);
	}

	native void setVisible0(String title, boolean isVisible);

	native void setTitle0(String oldTitle, String newTitle);

	native void dispose0(String title);

	native static void init0();

}
