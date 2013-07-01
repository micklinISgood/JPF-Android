package android.widget;

import android.content.Context;
import android.view.View;

public class TextView extends View {
  private CharSequence mText;

  public TextView(Context context) {
    super(context);
  }

  public final void setText(CharSequence text) {
    mText = text;
    if (name == null || name.length() == 0) {
      name = text.toString();
    }
  }

  public CharSequence getText() {
    return mText;
  }

}