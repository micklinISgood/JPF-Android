package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

import java.util.HashMap;
import java.util.logging.Logger;

import android.app.Activity;

public class JPF_android_app_ActivityThread {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	public static void parseApplicationStructure(MJIEnv env, int objRef,
			int mapRef) {

		HashMap<String, Activity> map = (HashMap<String, Activity>) env
				.getObjectAttr(mapRef);
		map.put("DeadlockActivity", new Activity());

	}

}
