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

import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.choice.IntIntervalGenerator;
import gov.nasa.jpf.util.script.ScriptElementContainer.SECIterator;

import java.util.List;

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
      return handleScriptElement(env, topIt, e);
    } else {
      return null;
    }
  }

  private ScriptElement handleScriptElement(MJIEnv env, SECIterator topIt, ScriptElement e) {
    if (e != null) {
      if (e instanceof ScriptElementContainer && !(e instanceof Alternative)) {
        push(((ScriptElementContainer) e).iterator());
        return getNext(env);
      } else if (e instanceof Alternative) {
        return handleAlternative(env, e, topIt);
      } else {
        return addExpandedEvent(env, e, topIt);
      }
    } else {
      pop();
      return (topIt != null) ? getNext(env) : null;
    }
  }

  ScriptElement addExpandedEvent(MJIEnv env, ScriptElement se, SECIterator topIt) {
    List<Event> eventList = ((Event) se).expand();
    if (eventList.size() > 1) {
      ScriptElementContainer s = new Alternative(se.getParent(), se.getLine());
      for (Event e : eventList) {
        Group g = new Group(s, se.getLine());
        g.add((ScriptElement) e);
        s.add(g);
      }
      return handleAlternative(env, s, topIt);
    } else
      return se;
  }

  // for (ScriptElement ase : (Alternative) e) {
  // if (ase instanceof Event) {
  // List<Event> ls = ((Event) ase).expand();
  // ase.
  // System.out.println();
  // }
  // }

  public ScriptElement handleAlternative(MJIEnv env, ScriptElement e, SECIterator topIt) {
    ThreadInfo ti = env.getThreadInfo();
    SystemState ss = env.getSystemState();
    Instruction insn = env.getInstruction();

    if (!ti.isFirstStepInsn()) { // top half - first execution
      IntIntervalGenerator cg = new IntIntervalGenerator("outerNext", 1,
          ((ScriptElementContainer) e).getNumberOfChildren());
      ss.setNextChoiceGenerator(cg);
      topIt.previous(e);
      env.repeatInvocation();
    } else { // bottom half - re-execution at the beginning of the next transition
      if (!(e.getParent() instanceof Section)) {
        if (((Alternative) e).used == false) {
          IntIntervalGenerator[] list = ss.getChoiceGeneratorsOfType(IntIntervalGenerator.class);

          IntIntervalGenerator cg = ss.getCurrentChoiceGenerator(String.valueOf(e.hashCode()),
              IntIntervalGenerator.class);
          if (cg == null) {
            cg = new IntIntervalGenerator(String.valueOf(e.hashCode()), 1,
                ((ScriptElementContainer) e).getNumberOfChildren());
            ss.setForced(true);
            ss.setNextChoiceGenerator(cg);
            topIt.previous(e);
            // System.out.println(cg);
            // System.out.println(ss.getCurrentChoiceGenerator(String.valueOf(-1243537750),
            // IntIntervalGenerator.class));
            env.repeatInvocation();
            return null;
          } else {
            int myChoice = cg.getNextChoice();
            push(((Alternative) e).iterator(myChoice));
            // System.out.println(cg);
            // System.out.println(ss.getCurrentChoiceGenerator(String.valueOf(-1243537750),
            // IntIntervalGenerator.class));
            if (cg.getTotalNumberOfChoices() == cg.getProcessedNumberOfChoices())
              ((Alternative) e).used = true;
            return getNext(env);
          }
        } else {
          if (e.parent instanceof Group)
            e = e.parent.parent;
          else
            e = e.parent;
          return handleScriptElement(env, topIt, e);

        }

      }
      IntIntervalGenerator cg = ss.getCurrentChoiceGenerator("outerNext", IntIntervalGenerator.class);
      assert cg != null : "no 'getNext' IntIntervalGenerator found";
      int myChoice = cg.getNextChoice();
      push(((Alternative) e).iterator(myChoice));
      // System.out.println(cg);
      return getNext(env);
    }
    return null;
  }
  // public IntIntervalGenerator getCurrentInnerCG(SystemState ss) {
  // IntIntervalGenerator[] cgs = ss.getChoiceGeneratorsOfType(IntIntervalGenerator.class);
  // int max = -1;
  // IntIntervalGenerator maxCG = null;
  //
  // for (IntIntervalGenerator cg : cgs) {
  // String[] name = cg.getId().split(":");
  // if (name[0].equals("innerNext")) {
  // int size = Integer.parseInt(name[1]);
  // if (size > max) {
  // max = size;
  // maxCG = cg;
  // }
  // }
  //
  // }
  // return maxCG;
  // }

}
