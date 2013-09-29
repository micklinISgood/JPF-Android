package gov.nasa.jpf.test.mc.basic;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.util.test.TestJPF;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
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
    int sequenceCount = 0;

    static ArrayList<String> sequence;

    public void instructionExecuted(JVM vm) {
      ThreadInfo ti = vm.getLastThreadInfo();
      Instruction insn = vm.getLastInstruction();
      if (insn instanceof gov.nasa.jpf.jvm.bytecode.ARETURN) {
        MethodInfo mi = insn.getMethodInfo();

        if (mi.getUniqueName().equals("parseScript()Ljava/lang/String;")) {
          String a = ti.getStringReturnValue();

          if (!a.equals("")) {
            sequenceCount++;
            sequence.add("Sequence " + sequenceCount + ": ");
            sequence.add(a);
          }
        }
      }
    }
  }

  @Before
  public void setUp() {
    if (!isJPFRun()) {
      Sequencer.sequence = new ArrayList<String>();
    }
  }

  String[] expected;

  @Test
  public void testAny() {
    if (!isJPFRun()) {
      String[] e = { "Sequence 1: ", "#1[a] ", "Sequence 2: ", "#1[b] ", "Sequence 3: ", "#1[c] " };
      expected = e;
    }
    String script = "SECTION default {ANY{GROUP{a},GROUP{b}, GROUP{c}}}";
    if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.test.mc.basic.AndroidScriptIntrTest$Sequencer")) {
      ScriptParser s = new ScriptParser(script);
      script = s.parseScript();
    }
  }

  @Test
  public void testAnyAny() {
    if (!isJPFRun()) {
      String[] e = { "Sequence 1: ", "#1[a] #2[c] ", "Sequence 2: ", "#1[a] #2[d] ", "Sequence 3: ",
          "#1[b] #2[c] ", "Sequence 4: ", "#1[b] #2[d] " };
      expected = e;
    }
    if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.test.mc.basic.AndroidScriptIntrTest$Sequencer")) {
      String script = "SECTION default {ANY{GROUP{a},GROUP{b}},ANY{GROUP{c},GROUP{d}}}";
      ScriptParser s = new ScriptParser(script);
      script = s.parseScript();

    }

  }

  
  
  @Test
  public void testAnyAnyANYBFS() {
    if (!isJPFRun()) {
      String[] e = { "Sequence 1: ", "#1[a] #2[c] ", "Sequence 2: ", "#1[a] #2[d] ", "Sequence 3: ",
          "#1[b] #2[c] ", "Sequence 4: ", "#1[b] #2[d] " };
      expected = e;
    }
    if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.test.mc.basic.AndroidScriptIntrTest$Sequencer", "+search.class=gov.nasa.jpf.search.heuristic.BFSHeuristic")) {
      String script = "SECTION default {a, z[0-1], <a|b>}";
      ScriptParser s = new ScriptParser(script);
      script = s.parseScript();

    }

  }
  
  
  @Test
  public void testAnyInAnyInAny() {
    if (!isJPFRun()) {
      String[] e = { "Sequence 1: ", "#1[a] #2[b] #3[k] ", "Sequence 2: ", "#1[a] #2[c] #3[k] ",
          "Sequence 3: ", "#1[a] #2[b] #3[k] ", "Sequence 4: ", "#1[a] #2[c] #3[k] ", "Sequence 5: ",
          "#1[i] #2[k] " };
      expected = e;
    }
    if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.test.mc.basic.AndroidScriptIntrTest$Sequencer")) {
      String script = "SECTION default {" + "ANY{" + "GROUP{a,ANY{ GROUP{b}, GROUP{c} } },"
          + "GROUP{a,ANY{ GROUP{b}, GROUP{c} } }," +
          // "GROUP{  ANY{ GROUP{d}, GROUP{ e, ANY{ GROUP{e}, GROUP{fgh} }} } },"
          // +
          "GROUP{i}" + "}, k" + "}";
      ScriptParser s = new ScriptParser(script);
      script = s.parseScript();
    }
  }

  @Test
  public void testShortAny() {
    if (!isJPFRun()) {
      String[] e = { "Sequence 1: ", "#1[a1] #2[b1] ", "Sequence 2: ", "#1[a1] #2[b2] ", "Sequence 3: ",
          "#1[a1] #2[b3] ", "Sequence 4: ", "#1[a2] #2[b1] ", "Sequence 5: ", "#1[a2] #2[b2] ",
          "Sequence 6: ", "#1[a2] #2[b3] ", "Sequence 7: ", "#1[a3] #2[b1] ", "Sequence 8: ",
          "#1[a3] #2[b2] ", "Sequence 9: ", "#1[a3] #2[b3] " };
      expected = e;
    }
    if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.test.mc.basic.AndroidScriptIntrTest$Sequencer")) {
      String script = "SECTION default {a[1-3],<a|b>}";
      ScriptParser s = new ScriptParser(script);
      script = s.parseScript();
    }
  }

  @Test
  public void testLongShortAny() {
    if (!isJPFRun()) {
      String[] e = {
          "Sequence 1: ",
          "#1[$buttonMinus.onClick] #2[$button0.onClick] #3[$buttonPlus.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 2: ",
          "#1[$buttonMinus.onClick] #2[$button0.onClick] #3[$buttonPlus.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 3: ",
          "#1[$buttonMinus.onClick] #2[$button0.onClick] #3[$buttonPlus.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 4: ",
          "#1[$buttonMinus.onClick] #2[$button0.onClick] #3[$buttonMul.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 5: ",
          "#1[$buttonMinus.onClick] #2[$button0.onClick] #3[$buttonMul.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 6: ",
          "#1[$buttonMinus.onClick] #2[$button0.onClick] #3[$buttonMul.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 7: ",
          "#1[$buttonMinus.onClick] #2[$button0.onClick] #3[$buttonDiv.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 8: ",
          "#1[$buttonMinus.onClick] #2[$button0.onClick] #3[$buttonDiv.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 9: ",
          "#1[$buttonMinus.onClick] #2[$button0.onClick] #3[$buttonDiv.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 10: ",
          "#1[$buttonMinus.onClick] #2[$button1.onClick] #3[$buttonPlus.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 11: ",
          "#1[$buttonMinus.onClick] #2[$button1.onClick] #3[$buttonPlus.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 12: ",
          "#1[$buttonMinus.onClick] #2[$button1.onClick] #3[$buttonPlus.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 13: ",
          "#1[$buttonMinus.onClick] #2[$button1.onClick] #3[$buttonMul.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 14: ",
          "#1[$buttonMinus.onClick] #2[$button1.onClick] #3[$buttonMul.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 15: ",
          "#1[$buttonMinus.onClick] #2[$button1.onClick] #3[$buttonMul.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 16: ",
          "#1[$buttonMinus.onClick] #2[$button1.onClick] #3[$buttonDiv.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 17: ",
          "#1[$buttonMinus.onClick] #2[$button1.onClick] #3[$buttonDiv.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 18: ",
          "#1[$buttonMinus.onClick] #2[$button1.onClick] #3[$buttonDiv.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 19: ",
          "#1[$buttonMinus.onClick] #2[$button2.onClick] #3[$buttonPlus.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 20: ",
          "#1[$buttonMinus.onClick] #2[$button2.onClick] #3[$buttonPlus.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 21: ",
          "#1[$buttonMinus.onClick] #2[$button2.onClick] #3[$buttonPlus.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 22: ",
          "#1[$buttonMinus.onClick] #2[$button2.onClick] #3[$buttonMul.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 23: ",
          "#1[$buttonMinus.onClick] #2[$button2.onClick] #3[$buttonMul.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 24: ",
          "#1[$buttonMinus.onClick] #2[$button2.onClick] #3[$buttonMul.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 25: ",
          "#1[$buttonMinus.onClick] #2[$button2.onClick] #3[$buttonDiv.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 26: ",
          "#1[$buttonMinus.onClick] #2[$button2.onClick] #3[$buttonDiv.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 27: ",
          "#1[$buttonMinus.onClick] #2[$button2.onClick] #3[$buttonDiv.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 28: ",
          "#1[$buttonMinus.onClick] #2[$button3.onClick] #3[$buttonPlus.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 29: ",
          "#1[$buttonMinus.onClick] #2[$button3.onClick] #3[$buttonPlus.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 30: ",
          "#1[$buttonMinus.onClick] #2[$button3.onClick] #3[$buttonPlus.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 31: ",
          "#1[$buttonMinus.onClick] #2[$button3.onClick] #3[$buttonMul.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 32: ",
          "#1[$buttonMinus.onClick] #2[$button3.onClick] #3[$buttonMul.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 33: ",
          "#1[$buttonMinus.onClick] #2[$button3.onClick] #3[$buttonMul.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 34: ",
          "#1[$buttonMinus.onClick] #2[$button3.onClick] #3[$buttonDiv.onClick] #4[$button1.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 35: ",
          "#1[$buttonMinus.onClick] #2[$button3.onClick] #3[$buttonDiv.onClick] #4[$button2.onClick] #5[$buttonEquals.onClick] ",
          "Sequence 36: ",
          "#1[$buttonMinus.onClick] #2[$button3.onClick] #3[$buttonDiv.onClick] #4[$button3.onClick] #5[$buttonEquals.onClick] " };
      expected = e;
    }
    if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.test.mc.basic.AndroidScriptIntrTest$Sequencer")) {
      String script = "SECTION default  {$buttonMinus.onClick()" + "$button[0-3].onClick()"
          + "$button<Plus|Mul|Div>.onClick()" + "$button[1-3].onClick()" + "$buttonEquals.onClick() }";
      ScriptParser s = new ScriptParser(script);
      script = s.parseScript();
    }

  }

  @After
  public void takeDown() {
    if (!isJPFRun()) {
    //  Assert.assertEquals(expected.length, Sequencer.sequence.size());

      int i = 0;
      for (String s : Sequencer.sequence) {
        //Assert.assertEquals("given: \"" + s + "\", expected: " + expected[i], expected[i], s);
         System.out.println("\"" + s + "\", ");
        i++;
      }
    }
  }

  public static void main(String[] testMethods) {
    runTestsOfThisClass(testMethods);
  }
}
