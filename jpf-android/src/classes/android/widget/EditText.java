package android.widget;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;

public class EditText extends TextView {

  public EditText(Context context) {
    super(context, true);
  }

  public EditText(Context context, AttributeSet attrs) {
    super(context);
  }

  public EditText(Context context, AttributeSet attrs, int defStyle) {
    super(context);
  }

  
  @Override
  public Editable getText() {
    CharSequence seq = super.getText();
    return  new SpannableStringBuilder(seq);
  }

  
  public void setSelection(int start, int stop) {
    // Selection.setSelection(getText(), start, stop);
  }

  public void setSelection(int index) {
    // Selection.setSelection(getText(), index);
  }

  public void selectAll() {
    // Selection.selectAll(getText());
  }

  public void extendSelection(int index) {
    // Selection.extendSelection(getText(), index);
  }

}
