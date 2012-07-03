package gov.nasa.jpf.test.android.os;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

/**
 * 
 * @author Heila van der Merwe
 * 
 */
public class LooperTest extends TestJPF {

	@Test
	public void testLooper() {
//		if (verifyNoPropertyViolation()) {
//			Looper.prepare();
//			Handler H = new Handler(new Callback() {
//				public boolean handleMessage(Message msg) {
//					System.out.println("Message " + msg.arg1);
//					return false;
//				}
//			});
//
//			Message m1 = new Message();
//			m1.arg1 = 1;
//			H.sendMessage(m1);
//			Message m2 = new Message();
//			m2.arg2 = 2;
//			H.sendMessage(m2);
//			// H.sendStopMessage(new Message());
//			Looper.loop();
//		}

	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testPrepare() {

	}

}