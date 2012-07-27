package dov.nasa.jpf.test.android.view;

import org.junit.Test;

import android.view.Window;

import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.test.basic.MJITest;
import gov.nasa.jpf.util.test.TestJPF;

public class WindowTest extends TestJPF {
	public static void main(String[] args) {
		runTestsOfThisClass(args);
	}

	@Test
	public void testWindow() {
		if (verifyNoPropertyViolation()) {
			Window w = new Window(null);
			
		}

	}
}
