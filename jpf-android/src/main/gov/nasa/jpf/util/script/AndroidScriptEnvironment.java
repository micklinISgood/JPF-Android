package gov.nasa.jpf.util.script;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.android.ResultPublishListener;
import gov.nasa.jpf.android.UIAction;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.util.StateExtensionClient;
import gov.nasa.jpf.util.StateExtensionListener;
import gov.nasa.jpf.util.script.ScriptEnvironment.ActiveSnapshot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the script an its current state
 */
public class AndroidScriptEnvironment implements StateExtensionClient<ScriptState> {

  static final String DEFAULT = "default";

  String scriptName;
  Reader scriptReader;
  Script script;
  public ScriptState cur;

  static HashMap<String, Section> sections = new HashMap<String, Section>();
  static Section defaultSection;

  public AndroidScriptEnvironment(String fname) throws FileNotFoundException {
    this(fname, new FileReader(fname));
  }

  public AndroidScriptEnvironment(String name, Reader r) {
    this.scriptName = name;
    this.scriptReader = r;
  }

  public void parseScript() throws ESParserE.Exception {
    ESParserE parser = new ESParserE(scriptName, scriptReader);
    script = parser.parse();
    initSections();
    setCur(new ScriptState());
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
  public UIAction getNext(String id, String activeState, MJIEnv vm) {
    ScriptState state = getCur();
    if (state != null) {
      setCur(state.advance(activeState, getSection(activeState)));
      UIAction event = null; // space
      for (SectionState as : getCur().sectionsState) { // for all sections

        if (activeState.equals(as.sectionName)) {
          ScriptElement se = ((AndroidSequenceIntpr) as.intrp).getNext(vm);
          if (se != null) {
            if (se instanceof Event) {
              event = new UIAction((Event) se);
              break;
            } else {
              // get next event
            }
          } else {
            break; // process next active sequence
          }
        }

      }
      getCur().getActions().push(event);
      return event;
    }
    return null;
  }

  // --- StateExtension interface
  public ScriptState getStateExtension() {
    return getCur();
  }

  public void restore(ScriptState stateExtension) {
    setCur(stateExtension);
  }

  public void registerListener(JPF jpf) {
    StateExtensionListener<ActiveSnapshot> sel = new StateExtensionListener(this);
    jpf.addSearchListener(sel);

    ResultPublishListener result = new ResultPublishListener(this);
    jpf.addPublisherExtension(ConsolePublisher.class, result);
  }

  public ScriptState getCur() {
    return cur;
  }

  public void setCur(ScriptState cur) {
    this.cur = cur;
  }

}
