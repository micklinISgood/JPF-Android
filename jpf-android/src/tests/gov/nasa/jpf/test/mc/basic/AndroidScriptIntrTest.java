package gov.nasa.jpf.test.mc.basic;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

/**
 * Tests for the Script Interpreter responcible for expanding the script into
 * nondeterministic event sequences.
 * 
 * @author heila
 * 
 */
public class AndroidScriptIntrTest extends TestJPF {

  public static class Sequencer extends ListenerAdapter {

    public void choiceGeneratorRegistered(JVM vm) {
      ChoiceGenerator<?> cg = vm.getLastChoiceGenerator();
      System.out.println("# CG registered: " + cg);

    }

    public void choiceGeneratorSet(JVM vm) {
      ChoiceGenerator<?> cg = vm.getLastChoiceGenerator();
      System.out.println("# CG set:        " + cg);

    }

    public void choiceGeneratorAdvanced(JVM vm) {
      ChoiceGenerator<?> cg = vm.getLastChoiceGenerator();
      System.out.println("#   CG advanced: " + cg);
    }

    public void choiceGeneratorProcessed(JVM vm) {
      ChoiceGenerator<?> cg = vm.getLastChoiceGenerator();
      System.out.println("# CG processed:  " + cg);

    }

    // public void instructionExecuted(JVM vm) {
    // Instruction insn = vm.getLastInstruction();
    // ThreadInfo ti = vm.getLastThreadInfo();
    // SystemState ss = vm.getSystemState();
    //
    // if (insn instanceof EXECUTENATIVE) { // break on native method exec
    // EXECUTENATIVE exec = (EXECUTENATIVE) insn;
    //
    // // this insn did create a CG
    // if (exec.getExecutedMethodName().equals("getNextScriptElement")) {
    // if (ti.isFirstStepInsn()) {
    // try {
    // System.out.println("value: " + ti.getIntReturnValue());
    // } catch (Exception e) {
    //
    // - }
    //
    // }
    // }
    // }
    //
    // }
  }

  @Test
  public void testAny() {

    String script = "SECTION default {ANY{GROUP{a},GROUP{b}, GROUP{c}}}";
    if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.test.mc.basic.AndroidScriptIntrTest$Sequencer",
        "+log.level=info")) {

      ScriptParser s = new ScriptParser(script);
      script = s.parseScript();
      System.out.println("Sequence:");
      System.out.println(script);

    }
  }

  @Test
  public void testAnyAny() {
    if (verifyNoPropertyViolation()) {
      String script = "SECTION default {ANY{GROUP{a},GROUP{b}},ANY{GROUP{c},GROUP{d}}}";
      ScriptParser s = new ScriptParser(script);
      script = s.parseScript();
      System.out.println(script);

    }
  }

  @Test
  public void testAnyInAnyInAny() {
    if (verifyNoPropertyViolation()) {
      String script = "SECTION default {" + "ANY{" + "GROUP{a,ANY{ GROUP{b}, GROUP{c} } },"
          + "GROUP{a,ANY{ GROUP{b}, GROUP{c} } }," +
          // "GROUP{  ANY{ GROUP{d}, GROUP{ e, ANY{ GROUP{e}, GROUP{fgh} }} } },"
          // +
          "GROUP{i}" + "}, k" + "}";
      ScriptParser s = new ScriptParser(script);
      script = s.parseScript();
      System.out.println(script);

    }
  }

  @Test
  public void testShortAny() {
    if (verifyNoPropertyViolation()) {
      if (verifyNoPropertyViolation()) {
        String script = "SECTION default {a[1-3],b[1-3]}";
        ScriptParser s = new ScriptParser(script);
        script = s.parseScript();
        System.out.println(script);

      }

    }
  }

  public static void main(String[] testMethods) {
    runTestsOfThisClass(testMethods);
  }

}
