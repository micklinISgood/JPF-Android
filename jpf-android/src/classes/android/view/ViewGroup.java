package android.view;

import android.content.Context;

public class ViewGroup extends View {

	public ViewGroup(Context context) {
		super(context);
	}

	// The view contained within this ViewGroup that has or contains focus.
	private View mFocused;
	private int mChildrenCount;
	// Child views of this ViewGroup
	private View[] mChildren;

	
	@Override
	protected View findViewTraversal(int id) {
		if (id == mID) {
			return this;
		}

		final View[] where = mChildren;
		final int len = mChildrenCount;

		for (int i = 0; i < len; i++) {
			View v = where[i];

			if ((v.mPrivateFlags & IS_ROOT_NAMESPACE) == 0) {
				v = v.findViewById(id);

				if (v != null) {
					return v;
				}
			}
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
	 * Adds a child view. If no layout parameters are already set on the child,
	 * the default parameters for this ViewGroup are set on the child.
	 * 
	 * @param child
	 *            the child view to add
	 * @param index
	 *            the position at which to add the child
	 * 
	 * @see #generateDefaultLayoutParams()
	 */
	public void addView(View child, int index) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = generateDefaultLayoutParams();
			if (params == null) {
				throw new IllegalArgumentException(
						"generateDefaultLayoutParams() cannot return null");
			}
		}
		addView(child, index, params);
	}

	/**
	 * Adds a child view with this ViewGroup's default layout parameters and the
	 * specified width and height.
	 * 
	 * @param child
	 *            the child view to add
	 */
	public void addView(View child, int width, int height) {
		final LayoutParams params = generateDefaultLayoutParams();
		params.width = width;
		params.height = height;
		addView(child, -1, params);
	}

	/**
	 * Adds a child view with the specified layout parameters.
	 * 
	 * @param child
	 *            the child view to add
	 * @param params
	 *            the layout parameters to set on the child
	 */
	public void addView(View child, LayoutParams params) {
		addView(child, -1, params);
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
	public void addView(View child, int index, LayoutParams params) {
		if (DBG) {
			System.out.println(this + " addView");
		}

		// addViewInner() will call child.requestLayout() when setting the new
		// LayoutParams
		// therefore, we call requestLayout() on ourselves before, so that the
		// child's request
		// will be blocked at our level
		requestLayout();
		invalidate(true);
		addViewInner(child, index, params, false);
	}
}
