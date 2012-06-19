package android.view;

import android.content.Context;

public class ViewGroup extends View {

	private static final int ARRAY_INITIAL_CAPACITY = 12;
	private static final int ARRAY_CAPACITY_INCREMENT = 12;
	private int mLastTouchDownIndex = -1;

	public ViewGroup(Context context) {
		super(context);
	}

	// The view contained within this ViewGroup that has or contains focus.
	private View mFocused;
	private int mChildrenCount = 0;
	// Child views of this ViewGroup
	private View[] mChildren = new View[ARRAY_INITIAL_CAPACITY];

	
	
	public View[] getChildren() {
		return mChildren;
	}

	@Override
	protected View findViewTraversal(int id) {
		if (id == mID) {
			return this;
		}

		final View[] where = mChildren;
		final int len = mChildrenCount;

		for (int i = 0; i < len; i++) {
			View v = where[i];

			// if ((v.mPrivateFlags & IS_ROOT_NAMESPACE) == 0) {
			v = v.findViewById(id);

			if (v != null) {
				return v;
			}
			// }
		}

		return null;
	}

	/**
	 * Adds a child view. If no layout parameters are already set on the child,
	 * the default parameters for this ViewGroup are set on the child.
	 * 
	 * @param child
	 *            the child view to add
	 * 
	 * @see #generateDefaultLayoutParams()
	 */
	public void addView(View child) {
		addView(child, -1);
	}

	/**
	 * Adds a child view with the specified layout parameters.
	 * 
	 * @param child
	 *            the child view to add
	 * @param index
	 *            the position at which to add the child
	 * @param params
	 *            the layout parameters to set on the child
	 */
	public void addView(View child, int index) {
		addViewInner(child, index, false);
	}

	private void addViewInner(View child, int index,
			boolean preventRequestLayout) {

		if (child.mParent != null) {
			throw new IllegalStateException(
					"The specified child already has a parent. "
							+ "You must call removeView() on the child's parent first.");
		}

		if (index < 0) {
			index = mChildrenCount;
		}

		addInArray(child, index);
		child.mParent = this;
		//
		// if (child.hasFocus()) {
		// requestChildFocus(child, child.findFocus());
		// }

		// onViewAdded(child);

	}

	private void addInArray(View child, int index) {
		View[] children = mChildren;
		final int count = mChildrenCount;
		final int size = children.length;
		if (index == count) {
			if (size == count) {
				mChildren = new View[size + ARRAY_CAPACITY_INCREMENT];
				System.arraycopy(children, 0, mChildren, 0, size);
				children = mChildren;
			}
			children[mChildrenCount++] = child;
		} else if (index < count) {
			if (size == count) {
				mChildren = new View[size + ARRAY_CAPACITY_INCREMENT];
				System.arraycopy(children, 0, mChildren, 0, index);
				System.arraycopy(children, index, mChildren, index + 1, count
						- index);
				children = mChildren;
			} else {
				System.arraycopy(children, index, children, index + 1, count
						- index);
			}
			children[index] = child;
			mChildrenCount++;
			if (mLastTouchDownIndex >= index) {
				mLastTouchDownIndex++;
			}
		} else {
			throw new IndexOutOfBoundsException("index=" + index + " count="
					+ count);
		}
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

	public ViewGroup findViewGroupByName(String name) {
		if (name == this.name) {
			return this;
		}

		final View[] where = mChildren;
		final int len = mChildrenCount;

		for (int i = 0; i < len; i++) {
			View v = where[i];
			if (v instanceof ViewGroup)
				v = ((ViewGroup) v).findViewGroupByName(name);

			if (v != null) {
				return (ViewGroup) v;
			}
		}

		return null;
	}
}
