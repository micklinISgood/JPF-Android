package android.widget;

import android.content.Context;
import android.content.res.Resources;

public class Toast {
  public static Toast makeText(Context context, CharSequence text, int duration) {
    return new Toast();
  }

  public static Toast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
    return new Toast();
  }

  /**
   * Show the view for the specified duration.
   */
  public void show() {
  }
}
