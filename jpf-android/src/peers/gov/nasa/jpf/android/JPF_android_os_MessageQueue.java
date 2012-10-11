package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.util.script.ESParser;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

/**
 * Native counterpart of the MessageQueue class.
 * 
 * @author Heila van der Merwe
 * 
 */
public class JPF_android_os_MessageQueue {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");
  static final String UIACTION = "[UIAction]";

  // do we want to process all UIActionCGs regardless of state matching
  static boolean forceActionStates = false;

  static int counter; // the number of UIActionCGs generated so far

  public static UIActionGenerator c;
  // static UIActionGeneratorFactory cgFactory;
  static UIScriptEnvironment scriptEnv;

  /**
   * Called from the MesaageQueue Constructor, i.e. before each application run. It opens and parses the input
   * script.
   */
  public static void init____V(MJIEnv env, int objref) {
    Config conf = env.getConfig();

    counter = 0;
    forceActionStates = conf.getBoolean("awt.force_states", true);

    String scriptName = conf.getString("awt.script");
    System.out.println("scriptName");

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

  /**
   * Called from within the message queue to retrieve new message when queue is empty. If we return false, it
   * means there is nothing else to check and we are done
   */
  public static boolean processScriptAction(MJIEnv env, int objref) {
    ThreadInfo ti = env.getThreadInfo();
    SystemState ss = env.getSystemState();
    Instruction insn = ti.getPC();

    if (scriptEnv == null) {
      log.warning("no UIScriptEnvironment, terminating");
      return false;
    }

    if (!ti.hasReturnedFromDirectCall(UIACTION)) { // before direct call to
      // handle action
      if (!ti.isFirstStepInsn()) {
        String currentActivity = JPF_android_app_ActivityThread.getCurrentActivity(env);
        log.fine("Current Activity: " + currentActivity);
        // get next action to process of this activity
        UIActionGenerator cg = scriptEnv.getNext("processScriptAction", currentActivity);

        if (cg != null) {
          counter++;
          if (forceActionStates) {
            env.setIntField(objref, "forceNewState", counter);
          }
          log.fine("setting next cg: " + cg);
          ss.setNextChoiceGenerator(cg);
          // ti.skipInstructionLogging();
          env.repeatInvocation(); // will execute else where first
          // option of the cg will be
          // retrieved etc.
          return true; // doesn't really matter
        } else {
          log.fine("cg == null"); // no more events in this event
          // sequence - either backtrack or
          // end
          return false;
        }

      } else { // we should already have a cg (with at least one choice
        // left), retrieve it
        c = ss.getCurrentChoiceGenerator("processScriptAction", UIActionGenerator.class);

        assert (c != null) : "no UIActionGenerator";
        log.fine("processing UIAction: " + c);
        UIAction ac = c.getNextChoice();
        log.fine("Next choice : " + ac);
        runAction(env, ac);
        env.repeatInvocation(); // this will execute until no more
        // choices in this cg.
      }
    }
    return true;
  }

  /**
   * Sends the action to the appropriate native peer to handle
   * 
   * @param env
   * @param action
   */
  private static void runAction(MJIEnv env, UIAction action) {
    System.out.println("*******************************");
    System.out.println("ProcessAction: " + action.action + " on " + action.target);

    if (!action.isNone()) {
      if (action.target == null) { // componentAction
        JPF_android_app_ActivityManagerProxy.handleComponentAction(env, action);
      } else if (action.target.startsWith("$")) { // viewAction
        JPF_android_view_Window.handleViewAction(env, action);
      } else if (action.target.startsWith("@")) { // intentAction
        JPF_android_app_ActivityManagerProxy.setIntent(env, action);
      }
    }
  }

}