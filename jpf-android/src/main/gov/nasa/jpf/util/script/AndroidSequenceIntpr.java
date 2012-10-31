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
import gov.nasa.jpf.util.script.ScriptElementContainer.SECIterator;

import java.util.List;

/**
 * Interpreter that walks a ScriptElementContainer hierarchy, returning Events, and expanding loops any
 * alternatives. It contains a stack of SECIterators. Each of these iterators iterates through a list of
 * events. When an iterator has no more script elements to return it is popped from the top of the stack.
 * 
 * For an REPEAT structure the a REPEAT iterator is pushed. This loops through the events in the loop the
 * required number of times before ending.
 * 
 * An ANY structure makes use of a ChoiceGenerators to create it's iterator. It fist pushes an iterator for
 * the first group in the ANY. Thanks to the choice generator this is backtracked and then the next group of
 * the ANY is pushed.
 * 
 * A single event can either be a single event or an alternative that has to be expanded. So it expands the
 * event and it only one event is returned, getNext returns the event, otherwise the expanded event list is
 * sent to the handleAlternative method to be handled as an ANY.
 * 
 */
public class AndroidSequenceIntpr extends SequenceInterpreter {

  public AndroidSequenceIntpr(ScriptElementContainer seq) {
    super(seq);
  }

  /**
   * Return the next ScriptElement from the script.
   * 
   * @param env
   * @return
   */
  public ScriptElement getNext(MJIEnv env) {
    SECIterator topIt = getTop();
    if (topIt != null) {
      ScriptElement e = topIt.next();
      return handleScriptElement(env, topIt, e);
    } else {
      return null;
    }
  }

  /**
   * 
   * @param env
   * @param topIt
   * @param e
   * @return
   */
  private ScriptElement handleScriptElement(MJIEnv env, SECIterator topIt, ScriptElement e) {
    if (e != null) {
      if (e instanceof ScriptElementContainer && !(e instanceof Alternative)) { // REPEAT
        push(((ScriptElementContainer) e).iterator());
        return getNext(env);
      } else if (e instanceof Alternative) { // ALTERNATIVE
        return handleAlternative(env, e, topIt);
      } else { // EVENT
        return getExpandedEvent(env, e, topIt);
      }
    } else {
      pop();
      return (topIt != null) ? getNext(env) : null;
    }
  }

  /**
   * 
   * @param env
   * @param se
   * @param topIt
   * @return
   */
  ScriptElement getExpandedEvent(MJIEnv env, ScriptElement se, SECIterator topIt) {
    ThreadInfo ti = env.getThreadInfo();
    SystemState ss = env.getSystemState();
    List<Event> eventList = ((Event) se).expand();
    if (eventList.size() > 1) {
      ScriptElementContainer s = new Alternative(se.getParent(), se.getLine());
      for (Event e : eventList) {
        Group g = new Group(s, se.getLine());
        g.add((ScriptElement) e);
        s.add(g);
      }
      return handleAlternative(env, s, topIt);
    } else {
      // IntIntervalGenerator cg = ss.getCurrentChoiceGenerator(String.valueOf(se.hashCode()).toString(),
      // IntIntervalGenerator.class);
      // if (cg == null) {
      // cg = new IntIntervalGenerator(String.valueOf(se.hashCode()).toString(), 1, 1);
      // ss.setForced(true);
      // ss.setNextChoiceGenerator(cg);
      // topIt.previous(se);
      // env.repeatInvocation();
      // return null;
      // } else {
      // int myChoice = cg.getNextChoice();
      return se;
      // }
    }
  }

  public ScriptElement handleAlternative(MJIEnv env, ScriptElement e, SECIterator topIt) {
    SystemState ss = env.getSystemState();
    ss.setForced(true);
    String cgID = String.valueOf(e.hashCode()); // id unique to each script element
    UIEventChoiceGenerator cg = ss.getCurrentChoiceGenerator(cgID, UIEventChoiceGenerator.class);
    if (cg == null) {
      cg = new UIEventChoiceGenerator(cgID, e);
      ss.setNextChoiceGenerator(cg);
      topIt.previous(e);
      env.repeatInvocation();
      return null;
    } else {
      // if (cg.isDone()) {
      // if (e.getParent() instanceof Group)
      // e = e.getParent().getParent();
      // else if (e.getParent() instanceof Section)
      // e = null;
      // else
      // e = e.getParent();
      // return handleScriptElement(env, topIt, e);
      push(cg.getNextChoice());
      return getNext(env);
    }
  }
}
