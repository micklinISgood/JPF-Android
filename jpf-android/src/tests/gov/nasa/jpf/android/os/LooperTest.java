//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import android.os.Handler;
//import android.os.Handler.Callback;
//import android.os.Looper;
//import android.os.Message;
//
///**
// * 
// * @author Heila van der Merwe
// * 
// */
//public class LooperTest extends TestJPF {
//
//	public LooperTest() {
//
//		Looper.prepare();
//		Handler H = new Handler(new Callback() {
//			public boolean handleMessage(Message msg) {
//				System.out.println("Message ");
//				return false;
//			}
//		});
//
//		Looper.loop();
//
//	}
//
//	@BeforeClass
//	public static void setUpClass() throws Exception {
//
//	}
//
//	@AfterClass
//	public static void tearDownClass() throws Exception {
//
//	}
//
//	@Before
//	public void setUp() {
//	}
//
//	@After
//	public void tearDown() {
//	}
//
//	@Test
//	public void testPrepare() {
//
//	}
//
//}