package gov.nasa.jpf.android;

import java.util.logging.Logger;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

public class JPF_android_util_Log {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	public static int println_native(MJIEnv env, int clsObjRef, int v0, int v1,
			int rString2, int rString3) {

		String msg = env.getStringObject(rString3);
		log.info(msg);
		return msg.length();
	}

	public static boolean isLoggable(MJIEnv env, int clsObjRef, int rString0,
			int v1) {
		boolean v = true;
		return v;
	}

}