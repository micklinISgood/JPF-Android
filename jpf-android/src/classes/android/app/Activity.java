package android.app;

import android.os.Bundle;
import android.os.Looper;
import android.view.ContextThemeWrapper;

public class Activity extends ContextThemeWrapper {

	Looper mLooper;

	public Activity() {
		// If this is the main Activity the looper needs to be started.
		if (mLooper == null) {
			ActivityThread.start(this);

		}

	}

	public void onCreate(Bundle savedInstanceState) {

	}

	/**
	 * Creates the view three
	 * 
	 * @param id
	 */
	public void setContentView(int id) {
		System.out.println("Generate view structure + input events");

		// This can be a problem if setContentView is never called - the
		// activity has no view?
		mLooper.loop();
	}

}
