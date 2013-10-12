//
// Copyright (C) 2008 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA). All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3. The NOSA has been approved by the Open Source
// Initiative. See the file NOSA-1.3-JPF at the top of the distribution
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

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.List;

/**
 * @author Heila van der Merwe
 * Created date: August 2012
 * 
 * Updates:
 * 3 July 2013 - Fix BFS bug
 * 
 */
public class AndroidSequenceIntpr extends SequenceInterpreter {
  private static final JPFLogger logger = JPF.getLogger("AndroidSequenceIntpr");
  boolean DEBUG = false;

  private MJIEnv env = null;

  public AndroidSequenceIntpr(ScriptElementContainer seq) {
    super(seq);
  }

  public ScriptElement getNext(MJIEnv env) {
    if (DEBUG)
      logger.info("TOP = " + top);
    this.env = env;
    if (!isDone()) {
      ScriptElement e = top.next();
      if (e != null) {
        return handleScriptElement(e);
      } else {
        pop();
        return (top != null) ? getNext(env) : null;
      }
    } else {
      return null;
    }
  }

  private ScriptElement handleScriptElement(ScriptElement e) {
    if (e instanceof Alternative) {
      return handleAlternative((AlternativeE) e);
    } else if (e instanceof ScriptElementContainer) {
      push(((ScriptElementContainer) e).iterator());
      return getNext(env);
    } else {
      return handleEvent(e);
    }

  }

  /**
   * Expand the Event and add either return the Event or push the new Container
   * on the stack
   * 
   * @param se
   * @return
   */
  ScriptElement handleEvent(ScriptElement se) {
    List<Event> eventList = ((Event) se).expand();
    if (eventList.size() > 1) {
      AlternativeE s = new AlternativeE(se.getParent(), se.getLine());
      for (Event e : eventList) {
        Group g = new Group(s, se.getLine());
        g.add((ScriptElement) e);
        s.add(g);
      }
      return handleAlternative(s);
    } else
      return se;
  }

  /**
   * Returns the next element determined by processing the current Alternative
   * ScriptElement
   * 
   * @param e
   *          the Alternative ScriptElement
   * @return the
   * @throws Exception
   */
  public ScriptElement handleAlternative(AlternativeE e) {
    ThreadInfo ti = env.getThreadInfo();
    SystemState ss = env.getSystemState();

    // get the choice generator for this script element
    AlternativeChoiceGenerator cg = ss.getCurrentChoiceGenerator(String.valueOf(e.hashCode()),
        AlternativeChoiceGenerator.class);

    // top half (first execution of this element)
    if (cg == null) {
      // create new ChoiceGenerator
      cg = createCG(e);
      ss.setForced(true); // force new state
      ss.setNextChoiceGenerator(cg);

      // reschedule this ScriptElement so that we try this again 
      top.rescheduleCurrent();
      env.repeatInvocation(); // re-executes the current instruction to make
                              // sure that we set the first choice
                              // of the cg now.
      return null;
    } else {
      // bottom-half (re -execution)
        int myChoice = cg.getNextChoice();
        push(e.iterator(myChoice)); // push the iterator of the next GROUP in the ANY
        env.repeatInvocation();
        return null;
    }
  }

  private AlternativeChoiceGenerator createCG(AlternativeE e) {
    // push new ChoiceGenerator
    return new AlternativeChoiceGenerator((String.valueOf(e.hashCode())), e.getNumberOfChildren());
  }

}
