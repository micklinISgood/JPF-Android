package gov.nasa.jpf.test.mc.basic;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.StateExtensionClient;
import gov.nasa.jpf.util.StateExtensionListener;
import gov.nasa.jpf.util.script.AndroidSequenceIntpr;
import gov.nasa.jpf.util.script.ESParserE;
import gov.nasa.jpf.util.script.Script;
import gov.nasa.jpf.util.script.ScriptElement;

import java.io.StringReader;

/**
 * Native Counterpart of the
 * 
 * @author heila
 * 
 */
public class JPF_gov_nasa_jpf_test_mc_basic_ScriptParser {
  private static final JPFLogger logger = JPF.getLogger("JPF_gov_nasa_jpf_test_mc_basic_ScriptParser");

  /**
   * Stores the state of the ScriptInterpreter in other words the current place
   * in the current path in the script.
   * 
   * @author Heila van der Merwe
   * 
   */
  public static class ScriptIntprExtension implements StateExtensionClient<AndroidSequenceIntpr> {

    @Override
    public AndroidSequenceIntpr getStateExtension() {
      logger.info("#Saving ScriptInterpreter state");
      return (AndroidSequenceIntpr) JPF_gov_nasa_jpf_test_mc_basic_ScriptParser.si.clone();
    }

    @Override
    public void restore(AndroidSequenceIntpr stateExtension) {
      JPF_gov_nasa_jpf_test_mc_basic_ScriptParser.si = (AndroidSequenceIntpr) stateExtension.clone();
      logger.info("#Restoring ScriptInterpreter state");
    }

    @Override
    public void registerListener(JPF jpf) {
      StateExtensionListener<AndroidSequenceIntpr> sel = new StateExtensionListener<AndroidSequenceIntpr>(
          ScriptIntprExtension.this);
      jpf.addSearchListener(sel);
    }

  }

  /**
   * Stores the current instance of the AndroidSequenceIntpr. This is saved and
   * restored by the ScriptIntprExtension while traversing the search space.
   */
  protected static AndroidSequenceIntpr si;

  /**
   * Returns the next ScriptElement for the current path through the script.
   * 
   * @param env
   * @param objectRef
   * @return
   */
  public static int getNextScriptElement(MJIEnv env, int objectRef) {
    ScriptElement e = si.getNext(env);
    if (e == null) {
      return env.newString("");
    } else
      return env.newString(e.toString());
  }

  public static void $init__Ljava_lang_String_2__V(MJIEnv env, int robj, int scriptRef) {

    // convert the scriptRef to a JVM string
    String scriptString = env.getStringObject(scriptRef);

    // create a reader to traverse the string
    StringReader r = new StringReader(scriptString);

    try {

      // create a parser to parse the string into ScriptElements
      ESParserE parser = new ESParserE("test", r);
      Script script = parser.parse();

      // create an interpreter to interpret the sequence of ScriptElements
      si = new AndroidSequenceIntpr(script);

      // register a state extension storing the state of the interpreter
      new ScriptIntprExtension().registerListener(env.getJPF());

    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
};
