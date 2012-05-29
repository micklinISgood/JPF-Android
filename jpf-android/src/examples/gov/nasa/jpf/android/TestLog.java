package gov.nasa.jpf.android;

import android.util.Log;

public class TestLog {

	public static void main(String[] args) {
		String message = "This is a test Message";
		int i = Log.v("", message);
		assert (i == message.length());

	}

}
