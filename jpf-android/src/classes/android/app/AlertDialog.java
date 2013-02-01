package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Drawable;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AlertDialog extends Dialog implements DialogInterface {
  public static final int THEME_TRADITIONAL = 1;
  public static final int THEME_HOLO_DARK = 2;
  public static final int THEME_HOLO_LIGHT = 3;
  public static final int THEME_DEVICE_DEFAULT_DARK = 4;
  public static final int THEME_DEVICE_DEFAULT_LIGHT = 5;

  protected AlertDialog(Context context) {
    super((Context) null, false, (DialogInterface.OnCancelListener) null);
  }

  protected AlertDialog(Context context, int theme) {
    super((Context) null, false, (DialogInterface.OnCancelListener) null);
  }

  protected AlertDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
    super((Context) null, false, (DialogInterface.OnCancelListener) null);
  }

  public Button getButton(int whichButton) {
    return null;
  }

  public ListView getListView() {
    return null;
  }

  public void setTitle(CharSequence title) {
  }

  public void setCustomTitle(View customTitleView) {
  }

  public void setMessage(CharSequence message) {
  }

  public void setView(View view) {
  }

  public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
                      int viewSpacingBottom) {
  }

  public void setButton(int whichButton, CharSequence text, Message msg) {
  }

  public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener) {
  }

  @Deprecated
  public void setButton(CharSequence text, Message msg) {
  }

  @Deprecated
  public void setButton2(CharSequence text, Message msg) {
  }

  @Deprecated
  public void setButton3(CharSequence text, Message msg) {
  }

  @Deprecated
  public void setButton(CharSequence text, DialogInterface.OnClickListener listener) {
  }

  @Deprecated
  public void setButton2(CharSequence text, DialogInterface.OnClickListener listener) {
  }

  @Deprecated
  public void setButton3(CharSequence text, DialogInterface.OnClickListener listener) {
  }

  public void setIcon(int resId) {
  }

  public void setIcon(Drawable icon) {
  }

  public void setIconAttribute(int attrId) {
  }

  public void setInverseBackgroundForced(boolean forceInverseBackground) {
  }

  protected void onCreate(Bundle savedInstanceState) {
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return true;
  }

  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return true;
  }

  public static class Builder {
    Context context;
    
    public Builder(Context context) {
    }

    public Builder(Context context, int theme) {
    }

    public Context getContext() {
      return null;
    }

    public Builder setTitle(int titleId) {
      return this;
    }

    public Builder setTitle(CharSequence title) {
      return this;
    }

    public Builder setCustomTitle(View customTitleView) {
      return this;
    }

    public Builder setMessage(int messageId) {
      return this;
    }

    public Builder setMessage(CharSequence message) {
      return this;
    }

    public Builder setIcon(int iconId) {
      return this;
    }

    public Builder setIcon(Drawable icon) {
      return this;
    }

    public Builder setIconAttribute(int attrId) {
      return this;
    }

    public Builder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setCancelable(boolean cancelable) {
      return this;
    }

    public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
      return this;
    }

    public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
      return this;
    }

    public Builder setItems(int itemsId, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setAdapter(ListAdapter adapter, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setCursor(Cursor cursor, DialogInterface.OnClickListener listener, String labelColumn) {
      return this;
    }

    public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems,
                                       DialogInterface.OnMultiChoiceClickListener listener) {
      return this;
    }

    public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                                       DialogInterface.OnMultiChoiceClickListener listener) {
      return this;
    }

    public Builder setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn,
                                       DialogInterface.OnMultiChoiceClickListener listener) {
      return this;
    }

    public Builder setSingleChoiceItems(int itemsId, int checkedItem, DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn,
                                        DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem,
                                        DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem,
                                        DialogInterface.OnClickListener listener) {
      return this;
    }

    public Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
      return this;

    }

    public Builder setView(View view) {
      return this;
    }

    public Builder setInverseBackgroundForced(boolean useInverseBackground) {
      return this;

    }

    public AlertDialog create() {
      return new AlertDialog(context);

    }

    public AlertDialog show() {
      return new AlertDialog(context);
    }
  }
}