package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

import java.util.logging.Logger;

public class JPF_android_os_MessageQueue {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	public static boolean processScriptAction(MJIEnv env, int objRef) {
		boolean v = false;
		log.info("Native is cool!");
		return v;
	}
	
	

}