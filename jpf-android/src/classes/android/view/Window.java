package android.view;

import android.content.Context;

/**
 * Models a Window
 * @author "Heila van der Merwe"
 *
 */
public class Window {
	private final static String TAG = "Window";

	private Window mContainer;

	/** Application context */
	Context mContext;

	/** Used to inflate layout */
	private LayoutInflater mLayoutInflater;

	/**
	 * For now this is the root of the VIewTree (it is suppose to be the
	 * DecorView but we are not using DecorView yet)
	 */
	private ViewGroup mContentParent;

	
	public Window(Context context) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
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
		if (mContentParent != null) {
			mContentParent.removeAllViews();
		}
		
		mLayoutInflater.inflate(layoutResID, mContentParent);
		final Callback cb = getCallback();
		if (cb != null && !isDestroyed()) {
			cb.onContentChanged();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean dispatchGenericPointerEvent(MotionEvent event) {
		// Send the event to the child under the pointer.
		final int childrenCount = mChildrenCount;
		if (childrenCount != 0) {
			final View[] children = mChildren;
			final float x = event.getX();
			final float y = event.getY();

			for (int i = childrenCount - 1; i >= 0; i--) {
				final View child = children[i];
				if (!canViewReceivePointerEvents(child)
						|| !isTransformedTouchPointInView(x, y, child, null)) {
					continue;
				}

				if (dispatchTransformedGenericPointerEvent(event, child)) {
					return true;
				}
			}
		}

		// No child handled the event. Send it to this view group.
		return super.dispatchGenericPointerEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mInputEventConsistencyVerifier != null) {
			mInputEventConsistencyVerifier.onTouchEvent(ev, 1);
		}

		boolean handled = false;
		if (onFilterTouchEventForSecurity(ev)) {
			final int action = ev.getAction();
			final int actionMasked = action & MotionEvent.ACTION_MASK;

			// Handle an initial down.
			if (actionMasked == MotionEvent.ACTION_DOWN) {
				// Throw away all previous state when starting a new touch
				// gesture.
				// The framework may have dropped the up or cancel event for the
				// previous gesture
				// due to an app switch, ANR, or some other state change.
				cancelAndClearTouchTargets(ev);
				resetTouchState();
			}

			// Check for interception.
			final boolean intercepted;
			if (actionMasked == MotionEvent.ACTION_DOWN
					|| mFirstTouchTarget != null) {
				final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
				if (!disallowIntercept) {
					intercepted = onInterceptTouchEvent(ev);
					ev.setAction(action); // restore action in case it was
											// changed
				} else {
					intercepted = false;
				}
			} else {
				// There are no touch targets and this action is not an initial
				// down
				// so this view group continues to intercept touches.
				intercepted = true;
			}

			// Check for cancelation.
			final boolean canceled = resetCancelNextUpFlag(this)
					|| actionMasked == MotionEvent.ACTION_CANCEL;

			// Update list of touch targets for pointer down, if needed.
			final boolean split = (mGroupFlags & FLAG_SPLIT_MOTION_EVENTS) != 0;
			TouchTarget newTouchTarget = null;
			boolean alreadyDispatchedToNewTouchTarget = false;
			if (!canceled && !intercepted) {
				if (actionMasked == MotionEvent.ACTION_DOWN
						|| (split && actionMasked == MotionEvent.ACTION_POINTER_DOWN)
						|| actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
					final int actionIndex = ev.getActionIndex(); // always 0 for
																	// down
					final int idBitsToAssign = split ? 1 << ev
							.getPointerId(actionIndex)
							: TouchTarget.ALL_POINTER_IDS;

					// Clean up earlier touch targets for this pointer id in
					// case they
					// have become out of sync.
					removePointersFromTouchTargets(idBitsToAssign);

					final int childrenCount = mChildrenCount;
					if (childrenCount != 0) {
						// Find a child that can receive the event.
						// Scan children from front to back.
						final View[] children = mChildren;
						final float x = ev.getX(actionIndex);
						final float y = ev.getY(actionIndex);

						for (int i = childrenCount - 1; i >= 0; i--) {
							final View child = children[i];
							if (!canViewReceivePointerEvents(child)
									|| !isTransformedTouchPointInView(x, y,
											child, null)) {
								continue;
							}

							newTouchTarget = getTouchTarget(child);
							if (newTouchTarget != null) {
								// Child is already receiving touch within its
								// bounds.
								// Give it the new pointer in addition to the
								// ones it is handling.
								newTouchTarget.pointerIdBits |= idBitsToAssign;
								break;
							}

							resetCancelNextUpFlag(child);
							if (dispatchTransformedTouchEvent(ev, false, child,
									idBitsToAssign)) {
								// Child wants to receive touch within its
								// bounds.
								mLastTouchDownTime = ev.getDownTime();
								mLastTouchDownIndex = i;
								mLastTouchDownX = ev.getX();
								mLastTouchDownY = ev.getY();
								newTouchTarget = addTouchTarget(child,
										idBitsToAssign);
								alreadyDispatchedToNewTouchTarget = true;
								break;
							}
						}
					}

					if (newTouchTarget == null && mFirstTouchTarget != null) {
						// Did not find a child to receive the event.
						// Assign the pointer to the least recently added
						// target.
						newTouchTarget = mFirstTouchTarget;
						while (newTouchTarget.next != null) {
							newTouchTarget = newTouchTarget.next;
						}
						newTouchTarget.pointerIdBits |= idBitsToAssign;
					}
				}
			}

			// Dispatch to touch targets.
			if (mFirstTouchTarget == null) {
				// No touch targets so treat this as an ordinary view.
				handled = dispatchTransformedTouchEvent(ev, canceled, null,
						TouchTarget.ALL_POINTER_IDS);
			} else {
				// Dispatch to touch targets, excluding the new touch target if
				// we already
				// dispatched to it. Cancel touch targets if necessary.
				TouchTarget predecessor = null;
				TouchTarget target = mFirstTouchTarget;
				while (target != null) {
					final TouchTarget next = target.next;
					if (alreadyDispatchedToNewTouchTarget
							&& target == newTouchTarget) {
						handled = true;
					} else {
						final boolean cancelChild = resetCancelNextUpFlag(target.child)
								|| intercepted;
						if (dispatchTransformedTouchEvent(ev, cancelChild,
								target.child, target.pointerIdBits)) {
							handled = true;
						}
						if (cancelChild) {
							if (predecessor == null) {
								mFirstTouchTarget = next;
							} else {
								predecessor.next = next;
							}
							target.recycle();
							target = next;
							continue;
						}
					}
					predecessor = target;
					target = next;
				}
			}

			// Update list of touch targets for pointer up or cancel, if needed.
			if (canceled || actionMasked == MotionEvent.ACTION_UP
					|| actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
				resetTouchState();
			} else if (split && actionMasked == MotionEvent.ACTION_POINTER_UP) {
				final int actionIndex = ev.getActionIndex();
				final int idBitsToRemove = 1 << ev.getPointerId(actionIndex);
				removePointersFromTouchTargets(idBitsToRemove);
			}
		}

		if (!handled && mInputEventConsistencyVerifier != null) {
			mInputEventConsistencyVerifier.onUnhandledEvent(ev, 1);
		}
		return handled;
	}

	/**
	 * Returns the position in the group of the specified child view.
	 * 
	 * @param child
	 *            the view for which to get the position
	 * @return a positive integer representing the position of the view in the
	 *         group, or -1 if the view does not exist in the group
	 */
	public int indexOfChild(View child) {
		final int count = mChildrenCount;
		final View[] children = mChildren;
		for (int i = 0; i < count; i++) {
			if (children[i] == child) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the number of children in the group.
	 * 
	 * @return a positive integer representing the number of children in the
	 *         group
	 */
	public int getChildCount() {
		return mChildrenCount;
	}

	/**
	 * Returns the view at the specified position in the group.
	 * 
	 * @param index
	 *            the position at which to get the view from
	 * @return the view at the specified position or null if the position does
	 *         not exist within the group
	 */
	public View getChildAt(int index) {
		if (index < 0 || index >= mChildrenCount) {
			return null;
		}
		return mChildren[index];
	}

}
