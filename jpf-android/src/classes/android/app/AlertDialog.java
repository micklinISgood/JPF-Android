package android.app;

import com.android.internal.app.AlertController;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AlertDialog extends Dialog implements DialogInterface {

  public static class Builder {
    private final AlertController.AlertParams P;
    private int mTheme;

    /**
     * Constructor using a context for this builder and the {@link AlertDialog} it creates.
     */
    public Builder(Context context) {
      this(context, resolveDialogTheme(context, 0));
    }

    /**
     * Constructor using a context and theme for this builder and the {@link AlertDialog} it creates. The
     * actual theme that an AlertDialog uses is a private implementation, however you can here supply either
     * the name of an attribute in the theme from which to get the dialog's style (such as
     * {@link android.R.attr#alertDialogTheme} or one of the constants {@link AlertDialog#THEME_TRADITIONAL
     * AlertDialog.THEME_TRADITIONAL}, {@link AlertDialog#THEME_HOLO_DARK AlertDialog.THEME_HOLO_DARK}, or
     * {@link AlertDialog#THEME_HOLO_LIGHT AlertDialog.THEME_HOLO_LIGHT}.
     */
    public Builder(Context context, int theme) {
      P = new AlertController.AlertParams(
          new ContextThemeWrapper(context, resolveDialogTheme(context, theme)));
      mTheme = theme;
    }

    /**
     * Returns a {@link Context} with the appropriate theme for dialogs created by this Builder. Applications
     * should use this Context for obtaining LayoutInflaters for inflating views that will be used in the
     * resulting dialogs, as it will cause views to be inflated with the correct theme.
     * 
     * @return A Context for built Dialogs.
     */
    public Context getContext() {
      return P.mContext;
    }

    /**
     * Set the title using the given resource id.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setTitle(int titleId) {
      P.mTitle = P.mContext.getText(titleId);
      return this;
    }

    /**
     * Set the title displayed in the {@link Dialog}.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setTitle(CharSequence title) {
      P.mTitle = title;
      return this;
    }

    /**
     * Set the title using the custom view {@code customTitleView}. The methods {@link #setTitle(int)} and
     * {@link #setIcon(int)} should be sufficient for most titles, but this is provided if the title needs
     * more customization. Using this will replace the title and icon set via the other methods.
     * 
     * @param customTitleView
     *          The custom view to use as the title.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setCustomTitle(View customTitleView) {
      P.mCustomTitleView = customTitleView;
      return this;
    }

    /**
     * Set the message to display using the given resource id.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setMessage(int messageId) {
      P.mMessage = P.mContext.getText(messageId);
      return this;
    }

    /**
     * Set the message to display.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setMessage(CharSequence message) {
      P.mMessage = message;
      return this;
    }

    /**
     * Set the resource id of the {@link Drawable} to be used in the title.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setIcon(int iconId) {
      return this;
    }

    /**
     * Set the {@link Drawable} to be used in the title.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setIcon(Drawable icon) {
      return this;
    }

    /**
     * Set an icon as supplied by a theme attribute. e.g. android.R.attr.alertDialogIcon
     * 
     * @param attrId
     *          ID of a theme attribute that points to a drawable resource.
     */
    public Builder setIconAttribute(int attrId) {
      TypedValue out = new TypedValue();
      P.mContext.getTheme().resolveAttribute(attrId, out, true);
      P.mIconId = out.resourceId;
      return this;
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     * 
     * @param textId
     *          The resource id of the text to display in the positive button
     * @param listener
     *          The {@link DialogInterface.OnClickListener} to use.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setPositiveButton(int textId, final OnClickListener listener) {
      P.mPositiveButtonText = P.mContext.getText(textId);
      P.mPositiveButtonListener = listener;
      return this;
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     * 
     * @param text
     *          The text to display in the positive button
     * @param listener
     *          The {@link DialogInterface.OnClickListener} to use.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
      P.mPositiveButtonText = text;
      P.mPositiveButtonListener = listener;
      return this;
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     * 
     * @param textId
     *          The resource id of the text to display in the negative button
     * @param listener
     *          The {@link DialogInterface.OnClickListener} to use.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setNegativeButton(int textId, final OnClickListener listener) {
      P.mNegativeButtonText = P.mContext.getText(textId);
      P.mNegativeButtonListener = listener;
      return this;
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     * 
     * @param text
     *          The text to display in the negative button
     * @param listener
     *          The {@link DialogInterface.OnClickListener} to use.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
      P.mNegativeButtonText = text;
      P.mNegativeButtonListener = listener;
      return this;
    }

    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     * 
     * @param textId
     *          The resource id of the text to display in the neutral button
     * @param listener
     *          The {@link DialogInterface.OnClickListener} to use.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setNeutralButton(int textId, final OnClickListener listener) {
      P.mNeutralButtonText = P.mContext.getText(textId);
      P.mNeutralButtonListener = listener;
      return this;
    }

    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     * 
     * @param text
     *          The text to display in the neutral button
     * @param listener
     *          The {@link DialogInterface.OnClickListener} to use.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
      P.mNeutralButtonText = text;
      P.mNeutralButtonListener = listener;
      return this;
    }

    /**
     * Sets whether the dialog is cancelable or not. Default is true.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setCancelable(boolean cancelable) {
      P.mCancelable = cancelable;
      return this;
    }

    /**
     * Sets the callback that will be called if the dialog is canceled.
     * 
     * @see #setCancelable(boolean)
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setOnCancelListener(OnCancelListener onCancelListener) {
      P.mOnCancelListener = onCancelListener;
      return this;
    }

    /**
     * Sets the callback that will be called if a key is dispatched to the dialog.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setOnKeyListener(OnKeyListener onKeyListener) {
      P.mOnKeyListener = onKeyListener;
      return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the selected
     * item via the supplied listener. This should be an array type i.e. R.array.foo
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setItems(int itemsId, final OnClickListener listener) {
      P.mItems = P.mContext.getResources().getTextArray(itemsId);
      P.mOnClickListener = listener;
      return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the selected
     * item via the supplied listener.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setItems(CharSequence[] items, final OnClickListener listener) {
      P.mItems = items;
      P.mOnClickListener = listener;
      return this;
    }

    /**
     * Set a list of items, which are supplied by the given {@link ListAdapter}, to be displayed in the dialog
     * as the content, you will be notified of the selected item via the supplied listener.
     * 
     * @param adapter
     *          The {@link ListAdapter} to supply the list of items
     * @param listener
     *          The listener that will be called when an item is clicked.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setAdapter(final ListAdapter adapter, final OnClickListener listener) {
      P.mAdapter = adapter;
      P.mOnClickListener = listener;
      return this;
    }

    /**
     * Set a list of items, which are supplied by the given {@link Cursor}, to be displayed in the dialog as
     * the content, you will be notified of the selected item via the supplied listener.
     * 
     * @param cursor
     *          The {@link Cursor} to supply the list of items
     * @param listener
     *          The listener that will be called when an item is clicked.
     * @param labelColumn
     *          The column name on the cursor containing the string to display in the label.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setCursor(final Cursor cursor, final OnClickListener listener, String labelColumn) {
      P.mCursor = cursor;
      P.mLabelColumn = labelColumn;
      P.mOnClickListener = listener;
      return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the selected
     * item via the supplied listener. This should be an array type, e.g. R.array.foo. The list will have a
     * check mark displayed to the right of the text for each checked item. Clicking on an item in the list
     * will not dismiss the dialog. Clicking on a button will dismiss the dialog.
     * 
     * @param itemsId
     *          the resource id of an array i.e. R.array.foo
     * @param checkedItems
     *          specifies which items are checked. It should be null in which case no items are checked. If
     *          non null it must be exactly the same length as the array of items.
     * @param listener
     *          notified when an item on the list is clicked. The dialog will not be dismissed when an item is
     *          clicked. It will only be dismissed if clicked on a button, if no buttons are supplied it's up
     *          to the user to dismiss the dialog.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems,
                                       final OnMultiChoiceClickListener listener) {
      P.mItems = P.mContext.getResources().getTextArray(itemsId);
      P.mOnCheckboxClickListener = listener;
      P.mCheckedItems = checkedItems;
      P.mIsMultiChoice = true;
      return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the selected
     * item via the supplied listener. The list will have a check mark displayed to the right of the text for
     * each checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a button
     * will dismiss the dialog.
     * 
     * @param items
     *          the text of the items to be displayed in the list.
     * @param checkedItems
     *          specifies which items are checked. It should be null in which case no items are checked. If
     *          non null it must be exactly the same length as the array of items.
     * @param listener
     *          notified when an item on the list is clicked. The dialog will not be dismissed when an item is
     *          clicked. It will only be dismissed if clicked on a button, if no buttons are supplied it's up
     *          to the user to dismiss the dialog.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                                       final OnMultiChoiceClickListener listener) {
      P.mItems = items;
      P.mOnCheckboxClickListener = listener;
      P.mCheckedItems = checkedItems;
      P.mIsMultiChoice = true;
      return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the selected
     * item via the supplied listener. The list will have a check mark displayed to the right of the text for
     * each checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a button
     * will dismiss the dialog.
     * 
     * @param cursor
     *          the cursor used to provide the items.
     * @param isCheckedColumn
     *          specifies the column name on the cursor to use to determine whether a checkbox is checked or
     *          not. It must return an integer value where 1 means checked and 0 means unchecked.
     * @param labelColumn
     *          The column name on the cursor containing the string to display in the label.
     * @param listener
     *          notified when an item on the list is clicked. The dialog will not be dismissed when an item is
     *          clicked. It will only be dismissed if clicked on a button, if no buttons are supplied it's up
     *          to the user to dismiss the dialog.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn,
                                       final OnMultiChoiceClickListener listener) {
      P.mCursor = cursor;
      P.mOnCheckboxClickListener = listener;
      P.mIsCheckedColumn = isCheckedColumn;
      P.mLabelColumn = labelColumn;
      P.mIsMultiChoice = true;
      return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the selected
     * item via the supplied listener. This should be an array type i.e. R.array.foo The list will have a
     * check mark displayed to the right of the text for the checked item. Clicking on an item in the list
     * will not dismiss the dialog. Clicking on a button will dismiss the dialog.
     * 
     * @param itemsId
     *          the resource id of an array i.e. R.array.foo
     * @param checkedItem
     *          specifies which item is checked. If -1 no items are checked.
     * @param listener
     *          notified when an item on the list is clicked. The dialog will not be dismissed when an item is
     *          clicked. It will only be dismissed if clicked on a button, if no buttons are supplied it's up
     *          to the user to dismiss the dialog.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setSingleChoiceItems(int itemsId, int checkedItem, final OnClickListener listener) {
      P.mItems = P.mContext.getResources().getTextArray(itemsId);
      P.mOnClickListener = listener;
      P.mCheckedItem = checkedItem;
      P.mIsSingleChoice = true;
      return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the selected
     * item via the supplied listener. The list will have a check mark displayed to the right of the text for
     * the checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a button
     * will dismiss the dialog.
     * 
     * @param cursor
     *          the cursor to retrieve the items from.
     * @param checkedItem
     *          specifies which item is checked. If -1 no items are checked.
     * @param labelColumn
     *          The column name on the cursor containing the string to display in the label.
     * @param listener
     *          notified when an item on the list is clicked. The dialog will not be dismissed when an item is
     *          clicked. It will only be dismissed if clicked on a button, if no buttons are supplied it's up
     *          to the user to dismiss the dialog.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn,
                                        final OnClickListener listener) {
      P.mCursor = cursor;
      P.mOnClickListener = listener;
      P.mCheckedItem = checkedItem;
      P.mLabelColumn = labelColumn;
      P.mIsSingleChoice = true;
      return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the selected
     * item via the supplied listener. The list will have a check mark displayed to the right of the text for
     * the checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a button
     * will dismiss the dialog.
     * 
     * @param items
     *          the items to be displayed.
     * @param checkedItem
     *          specifies which item is checked. If -1 no items are checked.
     * @param listener
     *          notified when an item on the list is clicked. The dialog will not be dismissed when an item is
     *          clicked. It will only be dismissed if clicked on a button, if no buttons are supplied it's up
     *          to the user to dismiss the dialog.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, final OnClickListener listener) {
      P.mItems = items;
      P.mOnClickListener = listener;
      P.mCheckedItem = checkedItem;
      P.mIsSingleChoice = true;
      return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the selected
     * item via the supplied listener. The list will have a check mark displayed to the right of the text for
     * the checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a button
     * will dismiss the dialog.
     * 
     * @param adapter
     *          The {@link ListAdapter} to supply the list of items
     * @param checkedItem
     *          specifies which item is checked. If -1 no items are checked.
     * @param listener
     *          notified when an item on the list is clicked. The dialog will not be dismissed when an item is
     *          clicked. It will only be dismissed if clicked on a button, if no buttons are supplied it's up
     *          to the user to dismiss the dialog.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, final OnClickListener listener) {
      P.mAdapter = adapter;
      P.mOnClickListener = listener;
      P.mCheckedItem = checkedItem;
      P.mIsSingleChoice = true;
      return this;
    }

    /**
     * Sets a listener to be invoked when an item in the list is selected.
     * 
     * @param listener
     *          The listener to be invoked.
     * @see AdapterView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setOnItemSelectedListener(final AdapterView.OnItemSelectedListener listener) {
      P.mOnItemSelectedListener = listener;
      return this;
    }

    /**
     * Set a custom view to be the contents of the Dialog. If the supplied view is an instance of a
     * {@link ListView} the light background will be used.
     * 
     * @param view
     *          The view to use as the contents of the Dialog.
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setView(View view) {
      P.mView = view;
      return this;
    }

    /**
     * Set a custom view to be the contents of the Dialog, specifying the spacing to appear around that view.
     * If the supplied view is an instance of a {@link ListView} the light background will be used.
     * 
     * @param view
     *          The view to use as the contents of the Dialog.
     * @param viewSpacingLeft
     *          Spacing between the left edge of the view and the dialog frame
     * @param viewSpacingTop
     *          Spacing between the top edge of the view and the dialog frame
     * @param viewSpacingRight
     *          Spacing between the right edge of the view and the dialog frame
     * @param viewSpacingBottom
     *          Spacing between the bottom edge of the view and the dialog frame
     * @return This Builder object to allow for chaining of calls to set methods
     * 
     * 
     *         This is currently hidden because it seems like people should just be able to put padding around
     *         the view.
     * @hide
     */
    public Builder setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
                           int viewSpacingBottom) {
      P.mView = view;
      return this;
    }

    /**
     * Sets the Dialog to use the inverse background, regardless of what the contents is.
     * 
     * @param useInverseBackground
     *          Whether to use the inverse background
     * 
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder setInverseBackgroundForced(boolean useInverseBackground) {
      P.mForceInverseBackground = useInverseBackground;
      return this;
    }

    /**
     * @hide
     */
    public Builder setRecycleOnMeasureEnabled(boolean enabled) {
      P.mRecycleOnMeasure = enabled;
      return this;
    }

    /**
     * Creates a {@link AlertDialog} with the arguments supplied to this builder. It does not
     * {@link Dialog#show()} the dialog. This allows the user to do any extra processing before displaying the
     * dialog. Use {@link #show()} if you don't have any other processing to do and want this to be created
     * and displayed.
     */
    public AlertDialog create() {
      final AlertDialog dialog = new AlertDialog(P.mContext, mTheme, false);
      P.apply(dialog.mAlert);
      dialog.setCancelable(P.mCancelable);
      if (P.mCancelable) {
        dialog.setCanceledOnTouchOutside(true);
      }
      dialog.setOnCancelListener(P.mOnCancelListener);
      if (P.mOnKeyListener != null) {
        dialog.setOnKeyListener(P.mOnKeyListener);
      }
      return dialog;
    }

    /**
     * Creates a {@link AlertDialog} with the arguments supplied to this builder and {@link Dialog#show()}'s
     * the dialog.
     */
    public AlertDialog show() {
      AlertDialog dialog = create();
      dialog.show();
      return dialog;
    }
  }

}
