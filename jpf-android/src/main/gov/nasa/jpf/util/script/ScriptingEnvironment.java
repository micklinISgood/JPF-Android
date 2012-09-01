package gov.nasa.jpf.util.script;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.util.StateExtensionClient;
import gov.nasa.jpf.util.StateExtensionListener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * class representing a statemachine environment that produces SCEventGenerators from scripts
 */
public abstract class ScriptingEnvironment<CG extends ChoiceGenerator<?>> implements
    StateExtensionClient<ScriptingEnvironment<CG>.SectionsSnapshot> {

  static final String DEFAULT = "default";

  static class SectionState implements Cloneable {
    String sectionName; // the name of the section
    Section section; // pointer to the section object
    SequenceInterpreter intrp; // keeps track of the current position of the

    // section events

    public SectionState(String sectionName, Section section, SequenceInterpreter intrp) {
      this.sectionName = sectionName;
      this.section = section;
      this.intrp = intrp;
    }

    public Object clone() {
      try {
        SectionState as = (SectionState) super.clone();
        as.intrp = (SequenceInterpreter) intrp.clone();
        return as;
      } catch (CloneNotSupportedException nonsense) {
        return null; // we are a Cloneable, so we don't get here
      }
    }

    public boolean isDone() {
      return intrp.isDone();
    }
  }

  // --- our state extension - we need this mostly for cloning (deep copy)
  class SectionsSnapshot implements Cloneable {
    ArrayList<SectionState> sectionsState; // current state of all Active

    SectionsSnapshot() {
      sectionsState = new ArrayList<SectionState>();
    }

    SectionsSnapshot(ArrayList<SectionState> as) {
      sectionsState = as;
    }

    public SectionState get(String sectionName) {
      for (SectionState as : sectionsState) {
        if (as.sectionName.equals(sectionName)) {
          return as;
        }
      }
      return null;
    }

    public Object clone() {
      try {
        SectionsSnapshot ss = (SectionsSnapshot) super.clone();
        for (SectionState as : this.sectionsState) {
          ss.sectionsState.add((SectionState) as.clone());
        }
        return ss;
      } catch (CloneNotSupportedException nonsense) {
        return null; // we are a Cloneable, so we don't get here
      }
    }

    /**
     * 
     * 
     * @param activeStates
     *          name of section to advance
     * @param isReEntered
     * @return
     */
    SectionsSnapshot advance(String activeState, BitSet isReEntered) {
      ArrayList<SectionState> newActives = new ArrayList<SectionState>(); // new

      // --- carry over the persisting entries
      for (SectionState as : sectionsState) {
        // we could use isReEntered to determine if we want to
        // restart sequences
        // <2do> how do we factor this out as policy?
        newActives.add((SectionState) as.clone());
      }

      // get the script section
      Section sec = getSection(activeState);
      if (sec != null) {

        // check if that section is already processed by another
        // active state, in which case we skip
        for (SectionState as : newActives) { // *********************
          if (as != null && as.section == sec) {
            return new SectionsSnapshot(newActives);
          }
        }

        // it's a new one
        SectionState as = new SectionState(activeState, sec, new SequenceInterpreter(sec));
        newActives.add(as);

      } else { // sec == null : we didn't find any sequence for
        // this state
      }

      return new SectionsSnapshot(newActives);
    }
  }

  // --- start of ScriptEnvronment

  String scriptName;
  Reader scriptReader;
  Script script;
  SectionsSnapshot cur;

  HashMap<String, Section> sections = new HashMap<String, Section>();
  Section defaultSection;

  // --- initialization
  public ScriptingEnvironment(String fname) throws FileNotFoundException {
    this(fname, new FileReader(fname));
  }

  public ScriptingEnvironment(String name, Reader r) {
    this.scriptName = name;
    this.scriptReader = r;
  }

  public void parseScript() throws ESParser.Exception {
    ESParser parser = new ESParser(scriptName, scriptReader);
    script = parser.parse();

    initSections();

    cur = new SectionsSnapshot();
  }

  void initSections() {
    Section defSec = new Section(script, DEFAULT);

    for (ScriptElement e : script) {

      if (e instanceof Section) {
        Section sec = (Section) e;
        List<String> secIds = sec.getIds();
        if (secIds.size() > 0) {
          for (String id : secIds) {
            sections.put(id, (Section) sec.clone()); // clone to
            // guarantee
            // different
            // identities
          }
        } else {
          sections.put(secIds.get(0), sec);
        }
      } else { // add copy to default sequence
        defSec.add(e.clone());
      }
    }

    if (defSec.getNumberOfChildren() > 0) {
      defaultSection = defSec;
    }
  }

  Section getSection(String id) {
    Section sec = null;

    while (id != null) {
      sec = sections.get(id);
      if (sec != null) {
        return sec;
      }

      int idx = id.lastIndexOf('.');
      if (idx > 0) {
        id = id.substring(0, idx); // ?? do we really want this
        // recursive? that's policy
      } else {
        id = null;
      }
    }

    return defaultSection;
  }

  void addExpandedEvent(ArrayList<Event> events, Event se) {
    for (Event e : se.expand()) {
      if (!events.contains(e)) {
        events.add(e);
      }
    }
  }

  static final String ACTIVE_DEFAULT = DEFAULT;

  public CG getNext(String id) {
    return getNext(id, ACTIVE_DEFAULT, null);
  }

  /**
   * gets the new action from the section activestate
   * 
   * @param id
   * @param activeStates
   * @return
   */
  public CG getNext(String id, String activeStates) {
    return getNext(id, activeStates, null);
  }

  /**
   * this is our main purpose in life, but there is some policy in here
   * 
   * 
   * @param id
   *          "processScriptAction"
   * @param activeStates
   *          - list of section names to return events from
   * @param isReEntered
   * @return
   */
  public CG getNext(String id, String activeState, BitSet isReEntered) {

    cur = cur.advance(activeState, isReEntered);

    ArrayList<Event> events = new ArrayList<Event>(1); // space
    for (SectionState as : cur.sectionsState) { // for all sections
      if (activeState.equals(as.sectionName)) {
        ScriptElement se = as.intrp.getNext();
        if (se != null) {
          if (se instanceof Event) {
            addExpandedEvent(events, (Event) se);
            break;
          } else if (se instanceof Alternative) {
            for (ScriptElement ase : (Alternative) se) {
              if (ase instanceof Event) {
                addExpandedEvent(events, (Event) ase);
              }
            }
            break;
          } else {
            // get next event
          }
        } else {
          break; // process next active sequence
        }
      }
    }
    return createCGFromEvents(id, events);
  }

  protected abstract CG createCGFromEvents(String id, List<Event> events);

  // --- StateExtension interface
  public SectionsSnapshot getStateExtension() {
    return cur;
  }

  public void restore(SectionsSnapshot stateExtension) {
    cur = stateExtension;
  }

  public void registerListener(JPF jpf) {
    StateExtensionListener<SectionsSnapshot> sel = new StateExtensionListener<SectionsSnapshot>(this);
    jpf.addSearchListener(sel);
  }

}
