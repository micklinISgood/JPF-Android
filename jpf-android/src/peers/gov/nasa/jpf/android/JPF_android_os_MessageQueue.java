package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.util.script.ESParser;
import gov.nasa.jpf.util.script.Event;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JPF_android_os_MessageQueue {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	// do we want to process all UIActionCGs regardless of state matching
	static boolean forceActionStates = false;
	static int counter; // the number of UIActionCGs generated so far

	// static UIActionGeneratorFactory cgFactory;
	static UIScriptEnvironment scriptEnv;

	static MethodInfo getMethodInfo(ClassInfo ci, UIAction action) {
		// <2do> this does not work - we can't deduce the exact method signature
		// from
		// the UI action because of the missing return type and the lossy
		// parameter
		// parsing (numbers are all stored as Double). We have to reverse match
		String methodName = action.getMethodName();
		System.out.println("Getting method: " + methodName);
		MethodInfo mi = ci.getMethod(methodName, true);
		System.out.println(mi);

		return mi;
	}

	/**
	 * this is called from the EventDispatchThread run, i.e. before each
	 * application run
	 */
	public static void init____V(MJIEnv env, int objref) {
		Config conf = env.getConfig();

		counter = 0;
		forceActionStates = conf.getBoolean("awt.force_states", true);

		String scriptName = conf.getString("awt.script");
		if (scriptName == null) {
			scriptName = conf.getString("inspect.script");

			if (scriptName == null) {
				log.severe("no \"awt.script\" or \"inspect.script\" property");
			}
		}
		try {
			scriptEnv = new UIScriptEnvironment(scriptName);
			scriptEnv.registerListener(env.getJPF());
			scriptEnv.parseScript();
		} catch (FileNotFoundException fnfx) {
			log.severe("script file not found: " + scriptName);
		} catch (ESParser.Exception e) {
			log.severe(e.toString());
		}
	}

	static MethodInfo miTransferFocus;

	static MethodInfo getTransferFocusMethod(MJIEnv env) {
		if (miTransferFocus == null) {
			ClassInfo ci = ClassInfo
					.tryGetResolvedClassInfo("android.view.View");
			assert ci != null : "android.view.View not loaded yet"; // should
																	// have
																	// been
																	// initialized

			MethodInfo mi = ci.getMethod("transferFocus()V", true);
			assert mi != null : "no android.view.View.transferFocus() method found (check model class)";

			miTransferFocus = mi.createDirectCallStub("transferFocus");
		}

		return miTransferFocus;
	}

	static final String UIACTION = "[UIAction]";

	/**
	 * this is called from within the EventDispatcher loop. If we return false,
	 * it means there is nothing else to check and we are done
	 */
	public static boolean processScriptAction(MJIEnv env, int objref) {

		ThreadInfo ti = env.getThreadInfo();
		SystemState ss = env.getSystemState();
		Instruction insn = ti.getPC();

		if (scriptEnv == null) { // that should have caused a warning during
									// initialization
			log.warning("no UIScriptEnvironment, terminating");
			return false;
		}

		if (!ti.hasReturnedFromDirectCall(UIACTION)) {
			if (!ti.isFirstStepInsn()) {
				UIActionGenerator cg = scriptEnv.getNext("processScriptAction");
				if (cg != null) {
					counter++;
					if (forceActionStates) {
						env.setIntField(objref, "forceNewState", counter);
					}

					ss.setNextChoiceGenerator(cg);
					// ti.skipInstructionLogging();
					log.info("ProcessAction");
					env.repeatInvocation();
					return true; // doesn't really matter
				} else {
					return false;
				}

			} else { // we should already have a cg (with at least one choice
						// left), retrieve it
				UIActionGenerator cg = ss.getCurrentChoiceGenerator(
						"processScriptAction", UIActionGenerator.class);
				assert (cg != null) : "no UIActionGenerator";

				if (log.isLoggable(Level.INFO)) {
					log.info("processing UIAction: " + cg);
				}

				runAction(env, cg.getNextChoice());
				env.repeatInvocation();
			}
		}

		return true;
	}

	private static void runAction(MJIEnv env, UIAction action) {
		log.info("ProcessAction: " + action.action + " on " + action.target);
		if (!action.isNone()) {
			if (action.target == null) {
				JPF_android_app_ActivityManagerProxy.handleComponentAction(env,
						action);
			} else if (action.target.startsWith("$")) {
				handleViewAction(env, action);
			} else if (action.target.startsWith("@")) {
				JPF_android_app_ActivityManagerProxy.setIntent(env, action);
			}
		}
	}

	private static void handleViewAction(MJIEnv env, UIAction action) {

		int tgtRef = JPF_android_view_Window.getViewRef(action.getTarget());

		if (tgtRef == MJIEnv.NULL) {
			log.warning("no view found for UIAction: " + action);
		} else {

			ElementInfo ei = env.getElementInfo(tgtRef);
			ClassInfo ci = ei.getClassInfo();
			if (!ci.isInstanceOf("android.view.View")) {
				log.warning("UIAction target reference for : " + action
						+ " is not a android.view.View: " + ei);
			} else {

				MethodInfo mi = getMethodInfo(ci, action);

				if (mi == null) {
					log.warning("UIAction " + action
							+ " refers to unknown method " + action
							+ "() in class " + ci.getName());

				} else {
					if (log.isLoggable(Level.FINER)) {
						log.finer("calling UIAction: " + action + " : " + ei
								+ "." + mi.getUniqueName());
					}
					// Ok, now we can finally make the (direct) call
					MethodInfo stub = mi.createDirectCallStub(UIACTION);
					DirectCallStackFrame frame = new DirectCallStackFrame(stub);

					// if (!mi.isStatic()) {
					frame.push(tgtRef, true);
					// }

					Object[] args = action.getArguments();
					if (args != null) {
						byte[] argTypes = mi.getArgumentTypes();
						for (int i = 0; i < args.length; i++) {
							pushArg(env, args[i], argTypes[i], frame);
						}
					}

					ThreadInfo ti = env.getThreadInfo();
					ti.pushFrame(frame);
				}
			}
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

	private static UIActionGenerator createRequestFocusCG(UIAction a) {
		String requestFocusActionId = a.getTarget() + ".requestFocus";
		Event requestFocusEvent = new Event(null, requestFocusActionId, null,
				a.getLine());
		UIAction requestFocusAction = new UIAction(requestFocusEvent);
		return new UIActionSingleChoice("requestFocusScriptAction",
				requestFocusAction);
	}

}