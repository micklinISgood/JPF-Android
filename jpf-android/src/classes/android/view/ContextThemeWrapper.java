package android.view;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * A ContextWrapper that allows you to modify the theme from what is in the
 * wrapped context.
 */
public class ContextThemeWrapper extends ContextWrapper {
	private Context mBase;
	private int mThemeResource;
	// private Resources.Theme mTheme;
	private LayoutInflater mInflater;

	public ContextThemeWrapper() {
		super(null);
	}

	public ContextThemeWrapper(Context base, int themeres) {
		super(base);
		setBaseContext(base);
		mThemeResource = themeres;
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
		setBaseContext(newBase);
	}

	// @Override
	// public void setTheme(int resid) {
	// mThemeResource = resid;
	// initializeTheme();
	// }

	/** @hide */
	@Override
	public int getThemeResId() {
		return mThemeResource;
	}

  public Context getBaseContext() {
    return mBase;
  }

  public void setBaseContext(Context mBase) {
    this.mBase = mBase;
  }

	// @Override public Resources.Theme getTheme() {
	// if (mTheme != null) {
	// return mTheme;
	// }
	//
	// mThemeResource = Resources.selectDefaultTheme(mThemeResource,
	// getApplicationInfo().targetSdkVersion);
	// initializeTheme();
	//
	// return mTheme;
	// }

	// @Override public Object getSystemService(String name) {
	// if (LAYOUT_INFLATER_SERVICE.equals(name)) {
	// if (mInflater == null) {
	// mInflater = LayoutInflater.from(mBase).cloneInContext(this);
	// }
	// return mInflater;
	// }
	// return mBase.getSystemService(name);
	// }

	/**
	 * Called by {@link #setTheme} and {@link #getTheme} to apply a theme
	 * resource to the current Theme object. Can override to change the default
	 * (simple) behavior. This method will not be called in multiple threads
	 * simultaneously.
	 * 
	 * @param theme
	 *            The Theme object being modified.
	 * @param resid
	 *            The theme style resource being applied to <var>theme</var>.
	 * @param first
	 *            Set to true if this is the first time a style is being applied
	 *            to <var>theme</var>.
	 */
	// protected void onApplyThemeResource(Resources.Theme theme, int resid,
	// boolean first) {
	// theme.applyStyle(resid, true);
	// }
	//
	// private void initializeTheme() {
	// final boolean first = mTheme == null;
	// if (first) {
	// mTheme = getResources().newTheme();
	// Resources.Theme theme = mBase.getTheme();
	// if (theme != null) {
	// mTheme.setTo(theme);
	// }
	// }
	// onApplyThemeResource(mTheme, mThemeResource, first);
	// }
}
