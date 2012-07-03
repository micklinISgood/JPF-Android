package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

import java.util.logging.Logger;

/**
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class JPF_android_app_ActivityThread {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	static int applicationRef = -1;
	private static ApplicationInfo appInfo;

	public static void init0(MJIEnv env, int objectRef) {
		appInfo = new ApplicationInfo();
		appInfo.init(env);
	}

	public static void setApplicationRef(MJIEnv env, int objectRef, int appRef) {
		applicationRef = appRef;
	}

	public static int getApplicationRef() {
		return applicationRef;
	}

	public static String getPackageName() {
		return appInfo.getPackageName();

	}

}
