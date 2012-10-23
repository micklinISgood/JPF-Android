//
// Copyright  (C) 2008 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
//  (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
//  (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.util.script;

import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.choice.IntIntervalGenerator;
import gov.nasa.jpf.util.script.ScriptElementContainer.SECIterator;

/**
 * an interpreter that walks a ScriptElementContainer hierarchy, returning Events and Alternatives while
 * expanding loops
 */
public class AndroidSequenceIntpr extends SequenceInterpreter {

  public AndroidSequenceIntpr(ScriptElementContainer seq) {
    super(seq);
  }

  public ScriptElement getNext(MJIEnv env) {

    SECIterator topIt = getTop();
    if (topIt != null) {
      ScriptElement e = topIt.next();
      if (e != null) {
        if (e instanceof ScriptElementContainer && !(e instanceof Alternative)) {
          push(((ScriptElementContainer) e).iterator());
          return getNext(env);
        } else if (e instanceof Alternative) {
          return handleAlternative(env, e, topIt);
        } else {
          return e;
        }
      } else {
        pop();
        return (top != null) ? getNext(env) : null;
      }
    } else {
      return null;
    }
  }

  public ScriptElement handleAlternative(MJIEnv env, ScriptElement e, SECIterator topIt) {
    ThreadInfo ti = env.getThreadInfo();
    SystemState ss = env.getSystemState();
    Instruction insn = env.getInstruction();
    // for (ScriptElement ase : (Alternative) e) {
    // if (ase instanceof Event) {
    // List<Event> ls = ((Event) ase).expand();
    // ase.
    // System.out.println();
    // }
    // }
    if (!ti.isFirstStepInsn()) { // top half - first execution
      IntIntervalGenerator cg = new IntIntervalGenerator("outerNext", 1,
          ((ScriptElementContainer) e).getNumberOfChildren());
      ss.setNextChoiceGenerator(cg);
      topIt.previous(e);
      env.repeatInvocation();

    } else { // bottom half - re-execution at the beginning of the next transition
      if (!(e.getParent() instanceof Section)) {
        IntIntervalGenerator cg = ss.getCurrentChoiceGenerator("innerNext", IntIntervalGenerator.class);
        if (cg == null) {
          cg = new IntIntervalGenerator("innerNext", 1, ((ScriptElementContainer) e).getNumberOfChildren());
          cg.setCascaded();
          boolean b = ss.setNextChoiceGenerator(cg);
          topIt.previous(e);
          System.out.println(cg);
          env.repeatInvocation();
          return null;
        } else {
          int myChoice = cg.getNextChoice();
          push(((Alternative) e).iterator(myChoice));
          System.out.println(cg);
          return getNext(env);
        }
      }
      ChoiceGenerator[] cgs = ss.getChoiceGenerators();
      IntIntervalGenerator cg = ss.getCurrentChoiceGenerator("outerNext", IntIntervalGenerator.class);
      assert cg != null : "no 'getNext' IntIntervalGenerator found";
      int myChoice = cg.getNextChoice();
      push(((Alternative) e).iterator(myChoice));
      System.out.println(cg);
      return getNext(env);
    }
    return null;
  }
}
