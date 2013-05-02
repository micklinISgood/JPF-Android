package android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;

/**
 * This is a model of the View class. Currently it only stores the listeners of
 * the View and executes these listeners when the onClick method is called.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class View {
  /**
   * This view is visible. Use with {@link #setVisibility} and <a
   * href="#attr_android:visibility">{@code android:visibility}.
   */
  public static final int VISIBLE = 0x00000000;

  /**
   * This view is invisible, but it still takes up space for layout purposes.
   * Use with {@link #setVisibility} and <a href="#attr_android:visibility">
   * {@code android:visibility}.
   */
  public static final int INVISIBLE = 0x00000004;

  /**
   * This view is invisible, and it doesn't take any space for layout purposes.
   * Use with {@link #setVisibility} and <a href="#attr_android:visibility">
   * {@code android:visibility}.
   */
  public static final int GONE = 0x00000008;

  /**
   * Mask for use with setFlags indicating bits used for visibility. {@hide
   * }
   */
  static final int VISIBILITY_MASK = 0x0000000C;

  private static final int[] VISIBILITY_FLAGS = { VISIBLE, INVISIBLE, GONE };

  /**
   * Used to mark a View that has no ID.
   */
  public static final int NO_ID = -1;

  /** ID defined in R file */
  int mID = -1;
  String name = null;

  protected ViewGroup mParent = null;

  public ViewGroup getmParent() {
    return mParent;
  }

  public void setParent(ViewGroup mParent) {
    this.mParent = mParent;
  }

  private boolean enabled = true;

  private boolean visible = true;

  static class ListenerInfo {
    /**
     * Listener used to dispatch focus change events. This field should be made
     * private, so it is hidden from the SDK. {@hide}
     */
    protected OnFocusChangeListener mOnFocusChangeListener;

    /**
     * Listener used to dispatch click events. This field should be made
     * private, so it is hidden from the SDK. {@hide}
     */
    public OnClickListener mOnClickListener;

    /**
     * Listener used to dispatch long click events. This field should be made
     * private, so it is hidden from the SDK. {@hide}
     */
    protected OnLongClickListener mOnLongClickListener;

    private OnKeyListener mOnKeyListener;

    private OnTouchListener mOnTouchListener;

    private OnHoverListener mOnHoverListener;

    private OnGenericMotionListener mOnGenericMotionListener;

    private OnDragListener mOnDragListener;

  }

  ListenerInfo mListenerInfo;

  /**
   * The application environment this view lives in. This field should be made
   * private, so it is hidden from the SDK. {@hide}
   */
  protected Context mContext;

  public View(Context context) {
    mContext = context;
  }

  /**
   * Constructor that is called when inflating a view from XML. This is called
   * when a view is being constructed from an XML file, supplying attributes
   * that were specified in the XML file. This version uses a default style of
   * 0, so the only attribute values applied are those in the Context's Theme
   * and the given AttributeSet.
   * 
   * <p>
   * The method onFinishInflate() will be called after all children have been
   * added.
   * 
   * @param context
   *          The Context the view is running in, through which it can access
   *          the current theme, resources, etc.
   * @param attrs
   *          The attributes of the XML tag that is inflating the view.
   * @see #View(Context, AttributeSet, int)
   */
  public View(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  /**
   * Perform inflation from XML and apply a class-specific base style. This
   * constructor of View allows subclasses to use their own base style when they
   * are inflating. For example, a Button class's constructor would call this
   * version of the super class constructor and supply
   * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows the
   * theme's button style to modify all of the base view attributes (in
   * particular its background) as well as the Button class's attributes.
   * 
   * @param context
   *          The Context the view is running in, through which it can access
   *          the current theme, resources, etc.
   * @param attrs
   *          The attributes of the XML tag that is inflating the view.
   * @param defStyle
   *          The default style to apply to this view. If 0, no style will be
   *          applied (beyond what is included in the theme). This may either be
   *          an attribute resource, whose value will be retrieved from the
   *          current theme, or an explicit style resource.
   * @see #View(Context, AttributeSet)
   */
  public View(Context context, AttributeSet attrs, int defStyle) {
    this(context);

  }

  /**
   * Look for a child view with the given id. If this view has the given id,
   * return this view.
   * 
   * @param id
   *          The id to search for.
   * @return The view that has the given id in the hierarchy or null
   */
  public View findViewById(int id) {
    if (id < 0) {
      return null;
    }
    return findViewTraversal(id);
  }

  public int getId() {
    return mID;
  }

  public void setId(int mID) {
    this.mID = mID;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean isVisible) {
    this.visible = isVisible;
  }

  public ListenerInfo getListenerInfo() {
    if (mListenerInfo == null)
      mListenerInfo = new ListenerInfo();
    return mListenerInfo;
  }

  public void setListenerInfo(ListenerInfo mListenerInfo) {
    this.mListenerInfo = mListenerInfo;
  }

  /**
   * {@hide}
   * 
   * @param id
   *          the id of the view to be found
   * @return the view of the specified id, null if cannot be found
   */
  protected View findViewTraversal(int id) {
    if (id == mID) {
      return this;
    }
    return null;
  }

  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Set the enabled state of this view. The interpretation of the enabled state
   * varies by subclass.
   * 
   * @param enabled
   *          True if this view is enabled, false otherwise.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Implement this to do your drawing.
   * 
   * @param canvas
   *          the canvas on which the background will be drawn
   */
  protected void onDraw(Canvas canvas) {
  }

  public void draw(Canvas canvas) {
    onDraw(canvas);

  }

  /**
   * Store this view hierarchy's frozen state into the given container.
   * 
   * @param container
   *          The SparseArray in which to save the view's state.
   * 
   * @see #restoreHierarchyState(android.util.SparseArray)
   * @see #dispatchSaveInstanceState(android.util.SparseArray)
   * @see #onSaveInstanceState()
   */
  public void saveHierarchyState(SparseArray<Parcelable> container) {
    dispatchSaveInstanceState(container);
  }

  /**
   * Called by {@link #saveHierarchyState(android.util.SparseArray)} to store
   * the state for this view and its children. May be overridden to modify how
   * freezing happens to a view's children; for example, some views may want to
   * not store state for their children.
   * 
   * @param container
   *          The SparseArray in which to save the view's state.
   * 
   * @see #dispatchRestoreInstanceState(android.util.SparseArray)
   * @see #saveHierarchyState(android.util.SparseArray)
   * @see #onSaveInstanceState()
   */
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
    // if (mID != NO_ID ) {
    // Parcelable state = onSaveInstanceState();
    // if ((mPrivateFlags & SAVE_STATE_CALLED) == 0) {
    // throw new
    // IllegalStateException("Derived class did not call super.onSaveInstanceState()");
    // }
    // if (state != null) {
    // // Log.i("View", "Freezing #" + Integer.toHexString(mID)
    // // + ": " + state);
    // container.put(mID, state);
    // }
    // }
  }

  /**
   * Hook allowing a view to generate a representation of its internal state
   * that can later be used to create a new instance with that same state. This
   * state should only contain information that is not persistent or can not be
   * reconstructed later. For example, you will never store your current
   * position on screen because that will be computed again when a new instance
   * of the view is placed in its view hierarchy.
   * <p>
   * Some examples of things you may store here: the current cursor position in
   * a text view (but usually not the text itself since that is stored in a
   * content provider or other persistent storage), the currently selected item
   * in a list view.
   * 
   * @return Returns a Parcelable object containing the view's current dynamic
   *         state, or null if there is nothing interesting to save. The default
   *         implementation returns null.
   * @see #onRestoreInstanceState(android.os.Parcelable)
   * @see #saveHierarchyState(android.util.SparseArray)
   * @see #dispatchSaveInstanceState(android.util.SparseArray)
   * @see #setSaveEnabled(boolean)
   */
  protected Parcelable onSaveInstanceState() {
    // mPrivateFlags |= SAVE_STATE_CALLED;
    // return BaseSavedState.EMPTY_STATE;
    return null;
  }

  /**
   * Restore this view hierarchy's frozen state from the given container.
   * 
   * @param container
   *          The SparseArray which holds previously frozen states.
   * 
   * @see #saveHierarchyState(android.util.SparseArray)
   * @see #dispatchRestoreInstanceState(android.util.SparseArray)
   * @see #onRestoreInstanceState(android.os.Parcelable)
   */
  public void restoreHierarchyState(SparseArray<Parcelable> container) {
    dispatchRestoreInstanceState(container);
  }

  /**
   * Called by {@link #restoreHierarchyState(android.util.SparseArray)} to
   * retrieve the state for this view and its children. May be overridden to
   * modify how restoring happens to a view's children; for example, some views
   * may want to not store state for their children.
   * 
   * @param container
   *          The SparseArray which holds previously saved state.
   * 
   * @see #dispatchSaveInstanceState(android.util.SparseArray)
   * @see #restoreHierarchyState(android.util.SparseArray)
   * @see #onRestoreInstanceState(android.os.Parcelable)
   */
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
    // if (mID != NO_ID) {
    // Parcelable state = container.get(mID);
    // if (state != null) {
    // // Log.i("View", "Restoreing #" + Integer.toHexString(mID)
    // // + ": " + state);
    // mPrivateFlags &= ~SAVE_STATE_CALLED;
    // onRestoreInstanceState(state);
    // if ((mPrivateFlags & SAVE_STATE_CALLED) == 0) {
    // throw new
    // IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
    // }
    // }
    // }
  }

  /**
   * Hook allowing a view to re-apply a representation of its internal state
   * that had previously been generated by {@link #onSaveInstanceState}. This
   * function will never be called with a null state.
   * 
   * @param state
   *          The frozen state that had previously been returned by
   *          {@link #onSaveInstanceState}.
   * 
   * @see #onSaveInstanceState()
   * @see #restoreHierarchyState(android.util.SparseArray)
   * @see #dispatchRestoreInstanceState(android.util.SparseArray)
   */
  protected void onRestoreInstanceState(Parcelable state) {
    // mPrivateFlags |= SAVE_STATE_CALLED;
    // if (state != BaseSavedState.EMPTY_STATE && state != null) {
    // throw new
    // IllegalArgumentException("Wrong state class, expecting View State but " +
    // "received "
    // + state.getClass().toString() + " instead. This usually happens "
    // +
    // "when two views of different type have the same id in the same hierarchy. "
    // + "This view's id is " + ViewDebug.resolveId(mContext, getId()) +
    // ". Make sure "
    // + "other views do not use the same id.");
    // }
  }

  /**
   * Register a callback to be invoked when a key is pressed in this view.
   * 
   * @param l
   *          the key listener to attach to this view
   */
  public void setOnKeyListener(OnKeyListener l) {
    getListenerInfo().mOnKeyListener = l;
  }

  /**
   * Register a callback to be invoked when a touch event is sent to this view.
   * 
   * @param l
   *          the touch listener to attach to this view
   */
  public void setOnTouchListener(OnTouchListener l) {
    getListenerInfo().mOnTouchListener = l;
  }

  /**
   * Register a callback to be invoked when this view is clicked. If this view
   * is not clickable, it becomes clickable.
   * 
   * @param l
   *          The callback that will run
   * 
   * @see #setClickable(boolean)
   */
  public void setOnClickListener(OnClickListener l) {
    getListenerInfo().mOnClickListener = l;
  }

  public OnClickListener getOnClickListener() {
    ListenerInfo li = getListenerInfo();

    return mListenerInfo.mOnClickListener;
  }

  /**
   * Return whether this view has an attached OnClickListener. Returns true if
   * there is a listener, false if there is none.
   */
  public boolean hasOnClickListeners() {
    ListenerInfo li = getListenerInfo();
    return (li != null && li.mOnClickListener != null);
  }

  /**
   * Register a callback to be invoked when this view is clicked and held. If
   * this view is not long clickable, it becomes long clickable.
   * 
   * @param l
   *          The callback that will run
   * 
   * @see #setLongClickable(boolean)
   */
  public void setOnLongClickListener(OnLongClickListener l) {
    getListenerInfo().mOnLongClickListener = l;
  }

  /**
   * Call this view's OnClickListener, if it is defined. Performs all normal
   * actions associated with clicking: reporting accessibility event, playing a
   * sound, etc.
   * 
   * @return True there was an assigned OnClickListener that was called, false
   *         otherwise is returned.
   */
  public void onClick() {
    OnClickListener li = getOnClickListener();
    if (li != null) {
      li.onClick(this);
    }

  }

  /**
   * Directly call any attached OnClickListener. Unlike {@link #performClick()},
   * this only calls the listener, and does not do any associated clicking
   * actions like reporting an accessibility event.
   * 
   * @return True there was an assigned OnClickListener that was called, false
   *         otherwise is returned.
   */
  public boolean callOnClick() {
    ListenerInfo li = getListenerInfo();
    if (li != null && li.mOnClickListener != null) {
      li.mOnClickListener.onClick(this);
      return true;
    } else if (li != null && li.mOnClickListener == null) {
      System.out.println("No listener attached");
      return true;
    }

    return false;
  }

  /**
   * Pass the touch screen motion event down to the target view, or this view if
   * it is the target.
   * 
   * @param event
   *          The motion event to be dispatched.
   * @return True if the event was handled by the view, false otherwise.
   */
  public boolean dispatchTouchEvent(MotionEvent event) {
    ListenerInfo li = mListenerInfo;
    if (li != null && li.mOnTouchListener != null && isEnabled() && li.mOnTouchListener.onTouch(this, event)) {
      return true;
    }

    if (onTouchEvent(event)) {
      return true;
    }

    return false;
  }

  /**
   * Implement this method to handle touch screen motion events.
   * 
   * @param event
   *          The motion event.
   * @return True if the event was handled, false otherwise.
   */
  public boolean onTouchEvent(MotionEvent event) {
    onClick();
    return true;
  }

  public boolean performClick() {
    ListenerInfo li = mListenerInfo;
    if (li != null && li.mOnClickListener != null) {
      li.mOnClickListener.onClick(this);
      return true;
    }

    return false;
  }

  /**
   * Call this view's OnLongClickListener, if it is defined. Invokes the context
   * menu if the OnLongClickListener did not consume the event.
   * 
   * @return True if one of the above receivers consumed the event, false
   *         otherwise.
   */
  public boolean performLongClick() {
    boolean handled = false;
    ListenerInfo li = mListenerInfo;
    if (li != null && li.mOnLongClickListener != null) {
      handled = li.mOnLongClickListener.onLongClick(View.this);
    }
    return handled;
  }

  public int getVisibility() {
    return 0;// TODO mViewFlags & VISIBILITY_MASK;
  }

  /**
   * Set the enabled state of this view.
   * 
   * @param visibility
   *          One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
   * @attr ref android.R.styleable#View_visibility
   */
  @RemotableViewMethod
  public void setVisibility(int visibility) {
    // TODO setFlags(visibility, VISIBILITY_MASK);
  }

  /**
   * Interface definition for a callback to be invoked when a view is clicked.
   */
  public interface OnClickListener {
    /**
     * Called when a view has been clicked.
     * 
     * @param v
     *          The view that was clicked.
     */
    void onClick(View v);
  }

  /**
   * Interface definition for a callback to be invoked when a key event is
   * dispatched to this view. The callback will be invoked before the key event
   * is given to the view.
   */
  public interface OnKeyListener {
    /**
     * Called when a key is dispatched to a view. This allows listeners to get a
     * chance to respond before the target view.
     * 
     * @param v
     *          The view the key has been dispatched to.
     * @param keyCode
     *          The code for the physical key that was pressed
     * @param event
     *          The KeyEvent object containing full information about the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    boolean onKey(View v, int keyCode, KeyEvent event);
  }

  /**
   * Interface definition for a callback to be invoked when a touch event is
   * dispatched to this view. The callback will be invoked before the touch
   * event is given to the view.
   */
  public interface OnTouchListener {
    /**
     * Called when a touch event is dispatched to a view. This allows listeners
     * to get a chance to respond before the target view.
     * 
     * @param v
     *          The view the touch event has been dispatched to.
     * @param event
     *          The MotionEvent object containing full information about the
     *          event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    boolean onTouch(View v, MotionEvent event);
  }

  /**
   * Interface definition for a callback to be invoked when a hover event is
   * dispatched to this view. The callback will be invoked before the hover
   * event is given to the view.
   */
  public interface OnHoverListener {
    /**
     * Called when a hover event is dispatched to a view. This allows listeners
     * to get a chance to respond before the target view.
     * 
     * @param v
     *          The view the hover event has been dispatched to.
     * @param event
     *          The MotionEvent object containing full information about the
     *          event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    boolean onHover(View v, MotionEvent event);
  }

  /**
   * Interface definition for a callback to be invoked when a generic motion
   * event is dispatched to this view. The callback will be invoked before the
   * generic motion event is given to the view.
   */
  public interface OnGenericMotionListener {
    /**
     * Called when a generic motion event is dispatched to a view. This allows
     * listeners to get a chance to respond before the target view.
     * 
     * @param v
     *          The view the generic motion event has been dispatched to.
     * @param event
     *          The MotionEvent object containing full information about the
     *          event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    boolean onGenericMotion(View v, MotionEvent event);
  }

  /**
   * Interface definition for a callback to be invoked when a view has been
   * clicked and held.
   */
  public interface OnLongClickListener {
    /**
     * Called when a view has been clicked and held.
     * 
     * @param v
     *          The view that was clicked and held.
     * 
     * @return true if the callback consumed the long click, false otherwise.
     */
    boolean onLongClick(View v);
  }

  /**
   * Interface definition for a callback to be invoked when a drag is being
   * dispatched to this view. The callback will be invoked before the hosting
   * view's own onDrag(event) method. If the listener wants to fall back to the
   * hosting view's onDrag(event) behavior, it should return 'false' from this
   * callback.
   * 
   * <div class="special reference"> <h3>Developer Guides</h3>
   * <p>
   * For a guide to implementing drag and drop features, read the <a
   * href="{@docRoot} guide/topics/ui/drag-drop.html">Drag and Drop</a>
   * developer guide.
   * </p>
   * </div>
   */
  public interface OnDragListener {
    /**
     * Called when a drag event is dispatched to a view. This allows listeners
     * to get a chance to override base View behavior.
     * 
     * @param v
     *          The View that received the drag event.
     * @param event
     *          The {@link android.view.DragEvent} object for the drag event.
     * @return {@code true} if the drag event was handled successfully, or
     *         {@code false} if the drag event was not handled. Note that
     *         {@code false} will trigger the View to call its
     *         {@link #onDragEvent(DragEvent) onDragEvent()} handler.
     */
    boolean onDrag(View v, DragEvent event);
  }

  /**
   * Interface definition for a callback to be invoked when the focus state of a
   * view changed.
   */
  public interface OnFocusChangeListener {
    /**
     * Called when the focus state of a view has changed.
     * 
     * @param v
     *          The view whose state has changed.
     * @param hasFocus
     *          The new focus state of v.
     */
    void onFocusChange(View v, boolean hasFocus);
  }

  /**
   * Interface definition for a callback to be invoked when the status bar
   * changes visibility. This reports <strong>global</strong> changes to the
   * system UI state, not just what the application is requesting.
   * 
   * @see View#setOnSystemUiVisibilityChangeListener(android.view.View.OnSystemUiVisibilityChangeListener)
   */
  public interface OnSystemUiVisibilityChangeListener {
    /**
     * Called when the status bar changes visibility because of a call to
     * {@link View#setSystemUiVisibility(int)}.
     * 
     * @param visibility
     *          Bitwise-or of flags {@link #SYSTEM_UI_FLAG_LOW_PROFILE} or
     *          {@link #SYSTEM_UI_FLAG_HIDE_NAVIGATION}. This tells you the
     *          <strong>global</strong> state of the UI visibility flags, not
     *          what your app is currently applying.
     */
    public void onSystemUiVisibilityChange(int visibility);
  }

  /**
   * Interface definition for a callback to be invoked when this view is
   * attached or detached from its window.
   */
  public interface OnAttachStateChangeListener {
    /**
     * Called when the view is attached to a window.
     * 
     * @param v
     *          The view that was attached
     */
    public void onViewAttachedToWindow(View v);

    /**
     * Called when the view is detached from a window.
     * 
     * @param v
     *          The view that was detached
     */
    public void onViewDetachedFromWindow(View v);
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "View [ type=" + getClass().getSimpleName() + " mID=" + mID + ", name=" + name + "]";
  }

  protected View findViewByName(String name) {
    if (name == null || name.length() < 1) {
      return null;
    }
    return findViewTraversal(name);
  }

  /**
   * {@hide}
   * 
   * @param id
   *          the id of the view to be found
   * @return the view of the specified id, null if cannot be found
   */
  protected View findViewTraversal(String name) {
    if (this.name.equals(name)) {
      return this;
    }
    return null;
  }

}