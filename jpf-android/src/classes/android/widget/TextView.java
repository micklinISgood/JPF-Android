package android.widget;

import android.content.Context;
import android.view.View;

public class TextView extends View {
  private CharSequence mText;

  public TextView(Context context) {
    super(context);
    mText = "";
  }

  public final void setText(CharSequence text) {
    mText = text;
  }

  public CharSequence getText() {
    return mText;
  }

}