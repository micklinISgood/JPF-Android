package gov.nasa.jpf.os;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

import java.util.logging.Logger;

public class JPF_android_os_MessageQueue {
	static Logger log = JPF.getLogger("gov.nasa.jpf.os");

	static Thread looperThread;

	public static void nativeInit____V(MJIEnv env, int objref) {
		looperThread = Thread.currentThread();
	}

	public static void nativeDestroy____V(MJIEnv env, int objref) {

	}

	public static void nativePollOnce__II__V(MJIEnv env, int objref) {
		int time = env.getIntField(objref, "timeoutMillis");
		try {
			looperThread.wait(time);
		} catch (InterruptedException e) {
		}

	}

	public static void nativeWake__I__V(MJIEnv env, int objref) {
		looperThread.notify();
		
		
	}
}
