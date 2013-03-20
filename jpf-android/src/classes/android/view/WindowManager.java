package android.view;

public class WindowManager {
  private final static String TAG = "WindowManager";

  public static Window currentWindow = null;

  public native static void init0();

  public static void setWindow(Window window) {
    System.out.println("Changing from Window " + currentWindow + " to  " + window);
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