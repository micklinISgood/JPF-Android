package android.view;

import android.content.Context;

/**
 * Models a Window
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class Window extends ViewGroup {
	private final static String TAG = "Window";

	private Window mContainer;

	/** Application context */
	Context mContext;

	/** Used to inflate layout */
	private LayoutInflater mLayoutInflater;

	private Callback mCallback;

	/**
	 * For now this is the root of the VIewTree (it is suppose to be the
	 * DecorView but we are not using DecorView yet)
	 */
	private ViewGroup mContentParent;

	public Window(Context context) {
		super(context);
		super.mID = 0;
		mContext = context;
		// mLayoutInflater = LayoutInflater.from(context);
		init();

	}

	/**
	 * Set the Callback interface for this window, used to intercept key events
	 * and other dynamic operations in the window.
	 * 
	 * @param callback
	 *            The desired Callback interface.
	 */
	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	/**
	 * Return the current Callback interface for this window.
	 */
	public final Callback getCallback() {
		return mCallback;
	}

	/**
	 * Return the Context this window policy is running in, for retrieving
	 * resources and other information.
	 * 
	 * @return Context The Context that was supplied to the constructor.
	 */
	public final Context getContext() {
		return mContext;
	}

	public View findViewById(int id) {
		return mContentParent.findViewById(id);
	}

	public void setContentView(int layoutResID) {
		// if (mContentParent != null) {
		// mContentParent.removeAllViews();
		// }
		//
		// setContentView0(layoutResID, mContentParent);
		//
		// mLayoutInflater.inflate(layoutResID, mContentParent);
		// final Callback cb = getCallback();
		// if (cb != null && !isDestroyed()) {
		// cb.onContentChanged();
		// }
	}

	public void setContentView(View v) {
		mContentParent = (ViewGroup) v;
		addView(v);
		setVisible0("title", true);
	}

	/**
	 * protected boolean dispatchGenericPointerEvent(MotionEvent event) { //
	 * Send the event to the child under the pointer. final int childrenCount =
	 * mChildrenCount; if (childrenCount != 0) { final View[] children =
	 * mChildren; final float x = event.getX(); final float y = event.getY();
	 * 
	 * for (int i = childrenCount - 1; i >= 0; i--) { final View child =
	 * children[i]; if (!canViewReceivePointerEvents(child) ||
	 * !isTransformedTouchPointInView(x, y, child, null)) { continue; }
	 * 
	 * if (dispatchTransformedGenericPointerEvent(event, child)) { return true;
	 * } } }
	 * 
	 * // No child handled the event. Send it to this view group. return
	 * super.dispatchGenericPointerEvent(event); }
	 * 
	 * public boolean dispatchTouchEvent(MotionEvent ev) { if
	 * (mInputEventConsistencyVerifier != null) {
	 * mInputEventConsistencyVerifier.onTouchEvent(ev, 1); }
	 * 
	 * boolean handled = false; if (onFilterTouchEventForSecurity(ev)) { final
	 * int action = ev.getAction(); final int actionMasked = action &
	 * MotionEvent.ACTION_MASK;
	 * 
	 * // Handle an initial down. if (actionMasked == MotionEvent.ACTION_DOWN) {
	 * // Throw away all previous state when starting a new touch // gesture. //
	 * The framework may have dropped the up or cancel event for the // previous
	 * gesture // due to an app switch, ANR, or some other state change.
	 * cancelAndClearTouchTargets(ev); resetTouchState(); }
	 * 
	 * // Check for interception. final boolean intercepted; if (actionMasked ==
	 * MotionEvent.ACTION_DOWN || mFirstTouchTarget != null) { final boolean
	 * disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0; if
	 * (!disallowIntercept) { intercepted = onInterceptTouchEvent(ev);
	 * ev.setAction(action); // restore action in case it was // changed } else
	 * { intercepted = false; } } else { // There are no touch targets and this
	 * action is not an initial // down // so this view group continues to
	 * intercept touches. intercepted = true; }
	 * 
	 * // Check for cancelation. final boolean canceled =
	 * resetCancelNextUpFlag(this) || actionMasked == MotionEvent.ACTION_CANCEL;
	 * 
	 * // Update list of touch targets for pointer down, if needed. final
	 * boolean split = (mGroupFlags & FLAG_SPLIT_MOTION_EVENTS) != 0;
	 * TouchTarget newTouchTarget = null; boolean
	 * alreadyDispatchedToNewTouchTarget = false; if (!canceled && !intercepted)
	 * { if (actionMasked == MotionEvent.ACTION_DOWN || (split && actionMasked
	 * == MotionEvent.ACTION_POINTER_DOWN) || actionMasked ==
	 * MotionEvent.ACTION_HOVER_MOVE) { final int actionIndex =
	 * ev.getActionIndex(); // always 0 for // down final int idBitsToAssign =
	 * split ? 1 << ev .getPointerId(actionIndex) : TouchTarget.ALL_POINTER_IDS;
	 * 
	 * // Clean up earlier touch targets for this pointer id in // case they //
	 * have become out of sync. removePointersFromTouchTargets(idBitsToAssign);
	 * 
	 * final int childrenCount = mChildrenCount; if (childrenCount != 0) { //
	 * Find a child that can receive the event. // Scan children from front to
	 * back. final View[] children = mChildren; final float x =
	 * ev.getX(actionIndex); final float y = ev.getY(actionIndex);
	 * 
	 * for (int i = childrenCount - 1; i >= 0; i--) { final View child =
	 * children[i]; if (!canViewReceivePointerEvents(child) ||
	 * !isTransformedTouchPointInView(x, y, child, null)) { continue; }
	 * 
	 * newTouchTarget = getTouchTarget(child); if (newTouchTarget != null) { //
	 * Child is already receiving touch within its // bounds. // Give it the new
	 * pointer in addition to the // ones it is handling.
	 * newTouchTarget.pointerIdBits |= idBitsToAssign; break; }
	 * 
	 * resetCancelNextUpFlag(child); if (dispatchTransformedTouchEvent(ev,
	 * false, child, idBitsToAssign)) { // Child wants to receive touch within
	 * its // bounds. mLastTouchDownTime = ev.getDownTime(); mLastTouchDownIndex
	 * = i; mLastTouchDownX = ev.getX(); mLastTouchDownY = ev.getY();
	 * newTouchTarget = addTouchTarget(child, idBitsToAssign);
	 * alreadyDispatchedToNewTouchTarget = true; break; } } }
	 * 
	 * if (newTouchTarget == null && mFirstTouchTarget != null) { // Did not
	 * find a child to receive the event. // Assign the pointer to the least
	 * recently added // target. newTouchTarget = mFirstTouchTarget; while
	 * (newTouchTarget.next != null) { newTouchTarget = newTouchTarget.next; }
	 * newTouchTarget.pointerIdBits |= idBitsToAssign; } } }
	 * 
	 * // Dispatch to touch targets. if (mFirstTouchTarget == null) { // No
	 * touch targets so treat this as an ordinary view. handled =
	 * dispatchTransformedTouchEvent(ev, canceled, null,
	 * TouchTarget.ALL_POINTER_IDS); } else { // Dispatch to touch targets,
	 * excluding the new touch target if // we already // dispatched to it.
	 * Cancel touch targets if necessary. TouchTarget predecessor = null;
	 * TouchTarget target = mFirstTouchTarget; while (target != null) { final
	 * TouchTarget next = target.next; if (alreadyDispatchedToNewTouchTarget &&
	 * target == newTouchTarget) { handled = true; } else { final boolean
	 * cancelChild = resetCancelNextUpFlag(target.child) || intercepted; if
	 * (dispatchTransformedTouchEvent(ev, cancelChild, target.child,
	 * target.pointerIdBits)) { handled = true; } if (cancelChild) { if
	 * (predecessor == null) { mFirstTouchTarget = next; } else {
	 * predecessor.next = next; } target.recycle(); target = next; continue; } }
	 * predecessor = target; target = next; } }
	 * 
	 * // Update list of touch targets for pointer up or cancel, if needed. if
	 * (canceled || actionMasked == MotionEvent.ACTION_UP || actionMasked ==
	 * MotionEvent.ACTION_HOVER_MOVE) { resetTouchState(); } else if (split &&
	 * actionMasked == MotionEvent.ACTION_POINTER_UP) { final int actionIndex =
	 * ev.getActionIndex(); final int idBitsToRemove = 1 <<
	 * ev.getPointerId(actionIndex);
	 * removePointersFromTouchTargets(idBitsToRemove); } }
	 * 
	 * if (!handled && mInputEventConsistencyVerifier != null) {
	 * mInputEventConsistencyVerifier.onUnhandledEvent(ev, 1); } return handled;
	 * }
	 **/

	/**
	 * API from a Window back to its caller. This allows the client to intercept
	 * key dispatching, panels and menus, etc.
	 */
	public interface Callback {
		/**
		 * Called to process key events. At the very least your implementation
		 * must call {@link android.view.Window#superDispatchKeyEvent} to do the
		 * standard key processing.
		 * 
		 * @param event
		 *            The key event.
		 * 
		 * @return boolean Return true if this event was consumed.
		 */
		public boolean dispatchKeyEvent(KeyEvent event);

		/**
		 * Called to process a key shortcut event. At the very least your
		 * implementation must call
		 * {@link android.view.Window#superDispatchKeyShortcutEvent} to do the
		 * standard key shortcut processing.
		 * 
		 * @param event
		 *            The key shortcut event.
		 * @return True if this event was consumed.
		 */
		public boolean dispatchKeyShortcutEvent(KeyEvent event);

		/**
		 * Called to process touch screen events. At the very least your
		 * implementation must call
		 * {@link android.view.Window#superDispatchTouchEvent} to do the
		 * standard touch screen processing.
		 * 
		 * @param event
		 *            The touch screen event.
		 * 
		 * @return boolean Return true if this event was consumed.
		 */
		public boolean dispatchTouchEvent(MotionEvent event);

		/**
		 * Called to process trackball events. At the very least your
		 * implementation must call
		 * {@link android.view.Window#superDispatchTrackballEvent} to do the
		 * standard trackball processing.
		 * 
		 * @param event
		 *            The trackball event.
		 * 
		 * @return boolean Return true if this event was consumed.
		 */
		public boolean dispatchTrackballEvent(MotionEvent event);

		/**
		 * Called to process generic motion events. At the very least your
		 * implementation must call
		 * {@link android.view.Window#superDispatchGenericMotionEvent} to do the
		 * standard processing.
		 * 
		 * @param event
		 *            The generic motion event.
		 * 
		 * @return boolean Return true if this event was consumed.
		 */
		public boolean dispatchGenericMotionEvent(MotionEvent event);

		// /**
		// * Called to process population of {@link AccessibilityEvent}s.
		// *
		// * @param event The event.
		// *
		// * @return boolean Return true if event population was completed.
		// */
		// public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent
		// event);

		/**
		 * Instantiate the view to display in the panel for 'featureId'. You can
		 * return null, in which case the default content (typically a menu)
		 * will be created for you.
		 * 
		 * @param featureId
		 *            Which panel is being created.
		 * 
		 * @return view The top-level view to place in the panel.
		 * 
		 * @see #onPreparePanel
		 */
		public View onCreatePanelView(int featureId);

		/**
		 * Initialize the contents of the menu for panel 'featureId'. This is
		 * called if onCreatePanelView() returns null, giving you a standard
		 * menu in which you can place your items. It is only called once for
		 * the panel, the first time it is shown.
		 * 
		 * <p>
		 * You can safely hold on to <var>menu</var> (and any items created from
		 * it), making modifications to it as desired, until the next time
		 * onCreatePanelMenu() is called for this feature.
		 * 
		 * @param featureId
		 *            The panel being created.
		 * @param menu
		 *            The menu inside the panel.
		 * 
		 * @return boolean You must return true for the panel to be displayed;
		 *         if you return false it will not be shown.
		 */
		// public boolean onCreatePanelMenu(int featureId, Menu menu);

		/**
		 * Prepare a panel to be displayed. This is called right before the
		 * panel window is shown, every time it is shown.
		 * 
		 * @param featureId
		 *            The panel that is being displayed.
		 * @param view
		 *            The View that was returned by onCreatePanelView().
		 * @param menu
		 *            If onCreatePanelView() returned null, this is the Menu
		 *            being displayed in the panel.
		 * 
		 * @return boolean You must return true for the panel to be displayed;
		 *         if you return false it will not be shown.
		 * 
		 * @see #onCreatePanelView
		 */
		// public boolean onPreparePanel(int featureId, View view, Menu menu);

		/**
		 * Called when a panel's menu is opened by the user. This may also be
		 * called when the menu is changing from one type to another (for
		 * example, from the icon menu to the expanded menu).
		 * 
		 * @param featureId
		 *            The panel that the menu is in.
		 * @param menu
		 *            The menu that is opened.
		 * @return Return true to allow the menu to open, or false to prevent
		 *         the menu from opening.
		 */
		// public boolean onMenuOpened(int featureId, Menu menu);

		/**
		 * Called when a panel's menu item has been selected by the user.
		 * 
		 * @param featureId
		 *            The panel that the menu is in.
		 * @param item
		 *            The menu item that was selected.
		 * 
		 * @return boolean Return true to finish processing of selection, or
		 *         false to perform the normal menu handling (calling its
		 *         Runnable or sending a Message to its target Handler).
		 */
		// public boolean onMenuItemSelected(int featureId, MenuItem item);

		/**
		 * This is called whenever the current window attributes change.
		 * 
		 */
		// public void onWindowAttributesChanged(WindowManager.LayoutParams
		// attrs);

		/**
		 * This hook is called whenever the content view of the screen changes
		 * (due to a call to
		 * {@link Window#setContentView(View, android.view.ViewGroup.LayoutParams)
		 * Window.setContentView} or
		 * {@link Window#addContentView(View, android.view.ViewGroup.LayoutParams)
		 * Window.addContentView}).
		 */
		public void onContentChanged();

		/**
		 * This hook is called whenever the window focus changes. See
		 * {@link View#onWindowFocusChanged(boolean)
		 * View.onWindowFocusChanged(boolean)} for more information.
		 * 
		 * @param hasFocus
		 *            Whether the window now has focus.
		 */
		public void onWindowFocusChanged(boolean hasFocus);

		/**
		 * Called when the window has been attached to the window manager. See
		 * {@link View#onAttachedToWindow() View.onAttachedToWindow()} for more
		 * information.
		 */
		public void onAttachedToWindow();

		/**
		 * Called when the window has been attached to the window manager. See
		 * {@link View#onDetachedFromWindow() View.onDetachedFromWindow()} for
		 * more information.
		 */
		public void onDetachedFromWindow();

		/**
		 * Called when a panel is being closed. If another logical subsequent
		 * panel is being opened (and this panel is being closed to make room
		 * for the subsequent panel), this method will NOT be called.
		 * 
		 * @param featureId
		 *            The panel that is being displayed.
		 * @param menu
		 *            If onCreatePanelView() returned null, this is the Menu
		 *            being displayed in the panel.
		 */
		// public void onPanelClosed(int featureId, Menu menu);

		/**
		 * Called when the user signals the desire to start a search.
		 * 
		 * @return true if search launched, false if activity refuses (blocks)
		 * 
		 * @see android.app.Activity#onSearchRequested()
		 */
		public boolean onSearchRequested();

		/**
		 * Called when an action mode is being started for this window. Gives
		 * the callback an opportunity to handle the action mode in its own
		 * unique and beautiful way. If this method returns null the system can
		 * choose a way to present the mode or choose not to start the mode at
		 * all.
		 * 
		 * @param callback
		 *            Callback to control the lifecycle of this action mode
		 * @return The ActionMode that was started, or null if the system should
		 *         present it
		 */
		// public ActionMode onWindowStartingActionMode(ActionMode.Callback
		// callback);

		/**
		 * Called when an action mode has been started. The appropriate mode
		 * callback method will have already been invoked.
		 * 
		 * @param mode
		 *            The new mode that has just been started.
		 */
		// public void onActionModeStarted(ActionMode mode);

		/**
		 * Called when an action mode has been finished. The appropriate mode
		 * callback method will have already been invoked.
		 * 
		 * @param mode
		 *            The mode that was just finished.
		 */
		// public void onActionModeFinished(ActionMode mode);
	}

	native void setVisible0(String title, boolean isVisible);

	native void setTitle0(String oldTitle, String newTitle);

	native void dispose0(String title);

	native void init();

}
