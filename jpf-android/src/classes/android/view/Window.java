package android.view;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

/**
 * Models a the Window and PhoneWindow classes. Each {@link Activity} has at least one Window. Each Dialog has
 * it's own window as well, so if an Activity has one or more Dialogs, it will have more than one window. The
 * window stores the view hierarchy and allows this hierarchy to be traversed and changed dynamically.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class Window extends ViewGroup {
  private final static String TAG = "Window";

  // used to store name of the/ activity dialog to which this window belongs
  String name = "";

  // From Android
  TextView mTitleView;
  String mTitle;

  private static WindowManager mWindowManager;

  /** Activity's context */
  Context mContext; // The Activity's context to which this window belongs

  /** Used to inflate layout */
  private static LayoutInflater mLayoutInflater;
  /**
   * For now this is the root of the ViewTree (it is suppose to be the DecorView but we are not using
   * DecorView yet)
   */
  private ViewGroup mContentParent;

  public Window(Context context) {
    super(context);
    Log.i(TAG, "Creating new Window for " + name);
    super.mID = 0; // temp solution the Window must not be a view If you
    // look at view hierarchy the frame view is the base
    mContext = context;

    Log.i(TAG, "Creating new LayouInflator");
    mLayoutInflater = new LayoutInflater(mContext);
  }

  /**
   * Return the Context this window policy is running in, for retrieving resources and other information.
   * 
   * @return Context The Context that was supplied to the constructor.
   */
  public final Context getContext() {
    return mContext;
  }

  public View getContentView() {
    return mContentParent;
  }

  public View findViewById(int id) {
    return mContentParent.findViewById(id);
  }

  protected View findViewByName(String name) {
    return mContentParent.findViewByName(name);
  }

  /**
   * Sets the parent view of this Window
   * 
   * @param v
   */
  public void setContentView(int layoutResID) {
    View layout = mLayoutInflater.inflate(layoutResID, null); // inflate
    layout.mParent = null;
    setContentView(layout);
    // final Callback cb = getCallback();
    // //if (cb != null && !isDestroyed()) {
    // cb.onContentChanged();
    // }
  }

  public void setContentView(View v) {
    mContentParent = (ViewGroup) v;
    addView(v);
  }

  public void setVisible() {
    WindowManager.setWindow(this);
  }

  public void setTitle(CharSequence title) {
    mTitle = title.toString();
  }

  private native void setVisible0();

  public void setWindowManager(WindowManager wm, IBinder appToken, String appName, boolean hardwareAccelerated) {
    if (wm == null) {
      wm = new WindowManager();
    }
    name = appName;
    mWindowManager = wm;
  }

  /**
   * Return the window manager allowing this Window to display its own windows.
   * 
   * @return WindowManager The ViewManager.
   */
  public static WindowManager getWindowManager() {
    return mWindowManager;
  }

  void handleViewAction(String name, String action) {

    Log.i(TAG, "Invoking " + action + " on view " + name);
    // find the view object
    View view = findViewByName(name.substring(1));
    if (view == null) {
      Log.e(TAG, "No view with name " + name + " exists for window " + this.name);
    } else {
      // invoke the action on this view object
      try {
        Class<? extends View> viewClass = (Class<? extends View>) view.getClass(); // get the class
        Method m = viewClass.getMethod(action, new Class[] {});
        m.invoke(view, (Object[]) null);

      } catch (NoSuchMethodException e) {
        Log.e(TAG, "View " + name + "has no method " + action);
      } catch (Exception e) {
        Log.e(TAG, "Could not execute action " + action + " on " + name + " for window " + this.name, e);

      }
    }
  }

  public LayoutInflater getLayoutInflater() {
    return mLayoutInflater;
  }

  @Override
  public String toString() {
    return "Window [name=" + name + ", mTitleView=" + mTitleView + ", mTitle=" + mTitle + "]";
  }

}
