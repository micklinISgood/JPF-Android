package android.widget;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.view.ViewDebug;

public class TextView extends View {
  private CharSequence mText;
  private BufferType mBufferType;
  private Editable.Factory mEditableFactory = Editable.Factory.getInstance();

  public enum BufferType {
    NORMAL, SPANNABLE, EDITABLE,
  }

  public TextView(Context context) {
    this(context, false);
  }
  
  public TextView(Context context, boolean editable){
    super(context);
    if (editable) {
      setText("", BufferType.EDITABLE);
      mBufferType = BufferType.EDITABLE;
    }
    else{
      setText("", BufferType.NORMAL);
      mBufferType = BufferType.EDITABLE;
    }
      
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

  /**
   * Convenience for {@link Selection#getSelectionStart}.
   */
  @ViewDebug.ExportedProperty(category = "text")
  public int getSelectionStart() {
    return Selection.getSelectionStart(getText());
  }

  /**
   * Convenience for {@link Selection#getSelectionEnd}.
   */
  @ViewDebug.ExportedProperty(category = "text")
  public int getSelectionEnd() {
    return Selection.getSelectionEnd(getText());
  }

  /**
   * Return true iff there is a selection inside this text view.
   */
  public boolean hasSelection() {
    final int selectionStart = getSelectionStart();
    final int selectionEnd = getSelectionEnd();

    return selectionStart >= 0 && selectionStart != selectionEnd;
  }

  private void setText(CharSequence text, BufferType type) {
    if (text == null) {
      text = "";
    }

    if (type == BufferType.EDITABLE) {
      text = mEditableFactory.newEditable(text);
    }

    mBufferType = type;
    mText = text;
  }

}