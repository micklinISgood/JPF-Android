package gov.nasa.jpf.android;

import gov.nasa.jpf.jvm.MJIEnv;

/**
 * This has to be modelled better. But does it make sense as time can be taken
 * into account due to non-determinism? TODO
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class JPF_android_os_SystemClock {

	public static long uptimeMillis(MJIEnv env, int clsObjRef) {
		long v = (long) 0;
		return v;
	}

	public static boolean setCurrentTimeMillis(MJIEnv env, int clsObjRef,
			long v0) {
		boolean v = true;
		return v;
	}

	public static long elapsedRealtime(MJIEnv env, int clsObjRef) {
		long v = (long) 0;
		return v;
	}

	public static long currentThreadTimeMillis(MJIEnv env, int clsObjRef) {
		long v = (long) 0;
		return v;
	}

	public static long currentThreadTimeMicro(MJIEnv env, int clsObjRef) {
		long v = (long) 0;
		return v;
	}

	public static long currentTimeMicro(MJIEnv env, int clsObjRef) {
		long v = (long) 0;
		return v;
	}
}