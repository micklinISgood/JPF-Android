package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

import java.util.HashMap;
import java.util.logging.Logger;

import android.app.Activity;

public class JPF_android_app_ActivityThread {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	// this is what we use to map script actions to Components
	static HashMap<String, ComponentEntry> activityMap = new HashMap<String, ComponentEntry>();

	//parseManifest();

	public static void parseApplicationStructure(MJIEnv env, int objRef,
			int mapRef) {

		HashMap<String, Activity> map = (HashMap<String, Activity>) env
				.getObjectAttr(mapRef);
		map.put("DeadlockActivity", new Activity());

	}

}
