package android.view;

import android.util.Log;

public class WindowManager {
  private final static String TAG = "WindowManager";

  private static WindowManager windowManager = null;

  public static Window currentWindow = null;

  public WindowManager() {
    init0();
    Log.i(TAG, "Started WindowManager");

  }

  public static native void init0();

  public static void setWindow(Window window) {
    Log.i(TAG, currentWindow + " ----> " + window);
    currentWindow = window;
  }

  /**
   * Dispatch event to current Window
   * 
   * @param name
   * @param action
   */
  private void handleViewAction(String name, String action, String[] arguments) {
    //TODO
    if (name.equals("$backButton")) {

      Log.i(TAG, "Back button handled by window manager");
      //currentWindow.dispatchEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
      currentWindow.dispatchEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    } else if (name.equals("$homeButton")) {

    } else if (name.equals("$volumeButton")) {

    } else if (name.equals("$homeButton")) {
      currentWindow.dispatchEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME));
      currentWindow.dispatchEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME));

    } else if (name.equals("$menuButton")) {
      currentWindow.dispatchEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MENU));
      currentWindow.dispatchEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU));
    } else {
      currentWindow.handleViewAction(name, action, arguments);
    }
  }

  public static WindowManager getInstance() {
    if (windowManager == null)
      windowManager = new WindowManager();

    return windowManager;
  }
}