package gov.nasa.jpf.test.android.util;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

import android.util.Log;

public class LogTest extends TestJPF {

	@Test
	public void testPrintLn_Native() {
		String message = "This is a test Message";
		if (verifyNoPropertyViolation()) {
			int i = Log.v("", message);
			assert (i == message.length());
		}

	}

}
