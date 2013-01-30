package android.view;

public class WindowManager {
  private final static String TAG = "WindowManager";

  static Window currentWindow = null;

  public native static void init0();

  public static void setWindow(Window window) {
    currentWindow = window;
  }

  private void handleViewAction(String name, String action) {
    currentWindow.handleViewAction(name, action);
  }
}