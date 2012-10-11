package gov.nasa.jpf.util.script;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.util.StateExtensionClient;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the script an its current state
 */
public abstract class ScriptingEnvironment<CG extends ChoiceGenerator<?>> implements
    StateExtensionClient<ScriptState> {

  static final String DEFAULT = "default";
  static final String ACTIVE_DEFAULT = DEFAULT;

  String scriptName;
  Reader scriptReader;
  Script script;
  ScriptState cur;

  static HashMap<String, Section> sections = new HashMap<String, Section>();
  static Section defaultSection;

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
    cur = new ScriptState();
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

  static Section getSection(String id) {
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

    cur = cur.advance(activeState, getSection(activeState));
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

    CG cg = createCGFromEvents(id, events);
    return cg;
  }

  protected abstract CG createCGFromEvents(String id, List<Event> events);

  // --- StateExtension interface
  public ScriptState getStateExtension() {
    return cur;
  }

  public void restore(ScriptState stateExtension) {
    cur = stateExtension;
  }

  public void registerListener(JPF jpf) {
    ScriptListener sel = new ScriptListener(this);

    jpf.addSearchListener(sel);
    jpf.addPublisherExtension(ConsolePublisher.class, sel);
    jpf.addVMListener(sel);
  }

}
