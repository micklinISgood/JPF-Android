package android.view;

import android.util.Log;

public class WindowManager {
  private final static String TAG = "WindowManager";

  public static Window currentWindow = null;

  public WindowManager() {
    init0();
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
  private void handleViewAction(String name, String action) {
    if (name.equals("$backButton")) {
      System.out.println("Back button handled by window manager");
      currentWindow.dispatchEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    } else {
      currentWindow.handleViewAction(name, action);
    }
  }

}