/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * <p>
 * A button with two states, checked and unchecked. When the button is pressed or clicked, the state changes
 * automatically.
 * </p>
 * 
 * <p>
 * <strong>XML attributes</strong>
 * </p>
 * <p>
 * See {@link android.R.styleable#CompoundButton CompoundButton Attributes},
 * {@link android.R.styleable#Button Button Attributes}, {@link android.R.styleable#TextView TextView
 * Attributes}, {@link android.R.styleable#View View Attributes}
 * </p>
 */
public abstract class CompoundButton extends Button implements Checkable {
  private boolean mChecked;
  private boolean mBroadcasting;

  private OnCheckedChangeListener mOnCheckedChangeListener;
  private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

  public CompoundButton(Context context) {
    super(context);
  }

  public void toggle() {
    setChecked(!mChecked);
  }

  @Override
  public boolean performClick() {
    /*
     * XXX: These are tiny, need some surrounding 'expanded touch area', which will need to be implemented in
     * Button if we only override performClick()
     */

    /* When clicked, toggle the state */
    toggle();
    return super.performClick();
  }

  @ViewDebug.ExportedProperty
  public boolean isChecked() {
    return mChecked;
  }

  /**
   * <p>
   * Changes the checked state of this button.
   * </p>
   * 
   * @param checked
   *          true to check the button, false to uncheck it
   */
  public void setChecked(boolean checked) {
    if (mChecked != checked) {
      mChecked = checked;

      // Avoid infinite recursions if setChecked() is called from a listener
      if (mBroadcasting) {
        return;
      }

      mBroadcasting = true;
      if (mOnCheckedChangeListener != null) {
        mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
      }
      if (mOnCheckedChangeWidgetListener != null) {
        mOnCheckedChangeWidgetListener.onCheckedChanged(this, mChecked);
      }

      mBroadcasting = false;
    }
  }

  /**
   * Register a callback to be invoked when the checked state of this button changes.
   * 
   * @param listener
   *          the callback to call on checked state change
   */
  public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
    mOnCheckedChangeListener = listener;
  }

  /**
   * Register a callback to be invoked when the checked state of this button changes. This callback is used
   * for internal purpose only.
   * 
   * @param listener
   *          the callback to call on checked state change
   * @hide
   */
  void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
    mOnCheckedChangeWidgetListener = listener;
  }

  /**
   * Interface definition for a callback to be invoked when the checked state of a compound button changed.
   */
  public static interface OnCheckedChangeListener {
    /**
     * Called when the checked state of a compound button has changed.
     * 
     * @param buttonView
     *          The compound button view whose state has changed.
     * @param isChecked
     *          The new checked state of buttonView.
     */
    void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
  }

  /**
   * Set the background to a given Drawable, identified by its resource id.
   * 
   * @param resid
   *          the resource id of the drawable to use as the background
   */
  public void setButtonDrawable(int resid) {
  }

  /**
   * Set the background to a given Drawable
   * 
   * @param d
   *          The Drawable to use as the background
   */
  public void setButtonDrawable(Drawable d) {
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

  }

  static class SavedState extends BaseSavedState {
    boolean checked;

    /**
     * Constructor called from {@link CompoundButton#onSaveInstanceState()}
     */
    SavedState(Parcelable superState) {
      super(superState);
    }

    /**
     * Constructor called from {@link #CREATOR}
     */
    private SavedState(Parcel in) {
      super(in);
      checked = (Boolean) in.readValue(null);
    }

    // @Override
    // public void writeToParcel(Parcel out, int flags) {
    // super.writeToParcel(out, flags);
    // out.writeValue(checked);
    // }
    //
    // @Override
    // public String toString() {
    // return "CompoundButton.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked="
    // + checked + "}";
    // }
    //
    // public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
    // public SavedState createFromParcel(Parcel in) {
    // return new SavedState(in);
    // }
    //
    // public SavedState[] newArray(int size) {
    // return new SavedState[size];
    // }
    // };
    // }
    //
    // @Override
    // public Parcelable onSaveInstanceState() {
    // // Force our ancestor class to save its state
    // setFreezesText(true);
    // Parcelable superState = super.onSaveInstanceState();
    //
    // SavedState ss = new SavedState(superState);
    //
    // ss.checked = isChecked();
    // return ss;
    // }
    //
    // @Override
    // public void onRestoreInstanceState(Parcelable state) {
    // SavedState ss = (SavedState) state;
    //
    // super.onRestoreInstanceState(ss.getSuperState());
    // setChecked(ss.checked);
    // requestLayout();
    // }
  }
}