package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JPF_android_app_ActivityManagerProxy {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");
	static final String UIACTION = "[UIAction]";

	// stack

	private static HashMap<String, Integer> intentMap = new HashMap<String, Integer>();

	/**
	 * Retrieves the reference to an intent in the intentMap using its name
	 * specified by action.target. If no such intent exists, an Intent object is
	 * created and stored in the intentMap. It then sets the field of the intent
	 * (specified by action.getAction()) with value action.arguments[0].
	 * 
	 * @param env
	 * @param action
	 */
	public static void setIntent(MJIEnv env, UIAction action) {
		log.warning("setIntent " + action.toString());

		Integer intentRef = intentMap.get(action.getTarget());
		if (intentRef == null) {
			intentRef = env.newObject("android.content.Intent");
			intentMap.put(action.getTarget(), intentRef);
		}

		// int[] arg = { env.newString((String) action.getArguments()[0]) };
		// callApplicationMethod(env, intentRef, action.getAction()
		// + "(Ljava/lang/String;)V", arg);
		int arg = env.newString((String) action.getArguments()[0]);
		env.setReferenceField(intentRef, "m" + action.getAction().substring(3),
				arg);

	}

	/**
	 * Handles actions related to application component such as starting and
	 * stopping activities and sending broadcasts etc.
	 * 
	 * @param env
	 * @param action
	 */
	static void handleComponentAction(MJIEnv env, UIAction action) {
		System.out.println("handle component: " + action.toString());
		String methodName = "";

		if (action.action.equals("startActivity")) {
			String intentName = (String) action.arguments[0];
			int intRef = intentMap.get(intentName);
			ElementInfo ei = env.getElementInfo(intRef);
			// ClassInfo ci = ei.getClassInfo();

			startActivity(env, 0, intRef);

		} else if (action.action.equals("destroy")) {
			methodName = "scheduleDestroyActivity(Ljava/lang/String;)V";
		}

	}

	/**
	 * This is always called to start an Activity
	 * 
	 * @param env
	 * @param intentRef
	 *            the reference to the intent starting the activity
	 */
	public static void startActivity(MJIEnv env, int clsRef, int intentRef) {

		// Lookup the name of the activity to launch -- TODO lookup other field
		// through filters
		String activityName = env.getStringField(intentRef, "mComponent");
		log.warning("***********************Starting activity " + activityName);
		int activityNameRef = env.newString(activityName);

		// schedule launch of activity
		String methodName = "scheduleLaunchActivity(Ljava/lang/String;Landroid/content/Intent;)V";
		int[] args = { activityNameRef, intentRef };
		int appRef = JPF_android_app_ActivityThread.getApplicationRef();

		callMethod(env, appRef, methodName, args);
	}

	/**
	 * Link to the ApplicationThread.
	 * 
	 * @param env
	 * @param methodName
	 * @param args
	 */
	private static void callMethod(MJIEnv env, int classRef, String methodName,
			int[] argsRefs) {
		ThreadInfo ti = env.getThreadInfo();
		StackFrame frame = ti.getReturnedDirectCall();
		if (frame == null) { // first time
			MethodInfo mi = env.getClassInfo(classRef).getMethod(methodName,
					true);
			MethodInfo stub = mi.createDirectCallStub("[SCHEDULE_MESSAGE]"
					+ mi.getName());
			frame = new DirectCallStackFrame(stub);
			if (!mi.isStatic()) {
				frame.push(classRef, true);
			}
			if (argsRefs != null) {
				for (int i = 0; i < argsRefs.length; i++) {
					frame.push(argsRefs[i], true);
				}
			}
			ti.pushFrame(frame);
		} else { // direct call returned
			// this shows how to get information back from the JPF roundtrip
			// into
			// the native method
			int r = frame.pop(); // the return value of the direct call above
		}
	}

	// <2do> very simplistic argument handling for now
	static void pushArg(MJIEnv env, Object arg, byte typeCode, StackFrame frame) {
		if (arg == null) {
			frame.push(MJIEnv.NULL, false);
		} else if (arg instanceof String) {
			int sRef = env.newString((String) arg);
			frame.push(sRef, true);
		} else if (arg instanceof Double) {
			frame.doublePush(((Double) arg).doubleValue());
		} else if (arg instanceof Integer) {
			frame.push(((Integer) arg).intValue(), false);
		} else if (arg instanceof Boolean) {
			frame.push(((Boolean) arg).booleanValue() ? 1 : 0, false);
		} else {
			throw new UnsupportedOperationException(
					"argument type not supported: " + arg);
		}
	}

}
