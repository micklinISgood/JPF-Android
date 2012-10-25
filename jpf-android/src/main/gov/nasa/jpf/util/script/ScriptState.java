package gov.nasa.jpf.util.script;

import gov.nasa.jpf.android.UIAction;

import java.util.ArrayList;
import java.util.Stack;

public class ScriptState implements Cloneable {
  protected ArrayList<SectionState> sectionsState; // current state of all sections
  protected Stack<UIAction> actions;

  ScriptState() {
    sectionsState = new ArrayList<SectionState>();
    actions = new Stack<UIAction>();
  }

  ScriptState(ArrayList<SectionState> as, Stack<UIAction> actions) {
    sectionsState = as;
    this.actions = actions;
  }

  public SectionState get(String sectionName) {
    for (SectionState as : sectionsState) {
      if (as.sectionName.equals(sectionName)) {
        return as;
      }
    }
    return null;
  }

  public ArrayList<SectionState> getSectionsState() {
    return sectionsState;
  }

  /**
   * @param activeStates
   *          name of section to advance
   * @param isReEntered
   * @return
   */
  ScriptState advance(String activeState, Section sec) {
    ArrayList<SectionState> newActives = new ArrayList<SectionState>(); // new

    // --- carry over the persisting entries
    for (SectionState as : sectionsState) {
      // we could use isReEntered to determine if we want to
      // restart sequences
      // <2do> how do we factor this out as policy?
      newActives.add((SectionState) as.clone());
    }

    // get the script section
    if (sec != null) {

      // check if that section is already processed by another
      // active state, in which case we skip
      for (SectionState as : newActives) { // *********************
        if (as != null && as.section == sec) {
          ScriptState s = new ScriptState(newActives, (Stack<UIAction>) actions.clone());
          return s;
        }
      }

      // it's a new section that has not been used before in this branch
      SectionState as = new SectionState(activeState, sec, new AndroidSequenceIntpr(sec));
      newActives.add(as);

    } else { // sec == null : we didn't find any sequence for
      // this state
    }

    ScriptState s = new ScriptState(newActives, actions);
    return s;
  }

  public Object clone() {
    try {
      ScriptState ss = (ScriptState) super.clone();
      for (SectionState as : this.sectionsState) {
        ss.sectionsState.add((SectionState) as.clone());
      }
      ss.actions = (Stack<UIAction>) actions.clone();
      return ss;
    } catch (CloneNotSupportedException nonsense) {
      return null; // we are a Cloneable, so we don't get here
    }
  }

}
