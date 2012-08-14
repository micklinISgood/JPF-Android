package gov.nasa.jpf.android;

import gov.nasa.jpf.util.script.Event;
import gov.nasa.jpf.util.script.ScriptEnvironment;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class UIScriptEnvironment extends ScriptEnvironment<UIActionGenerator> {

  public UIScriptEnvironment (String fname) throws FileNotFoundException {
    super( fname, new FileReader(fname));
  }

  public UIScriptEnvironment (String name, Reader r) {
    super(name,r);
  }

  protected UIActionGenerator createCGFromEvents(String id, List<Event> events) {
    if (events.isEmpty()) {
      return null;
    } else if (events.size() == 1) {
      return new UIActionSingleChoice( id, new UIAction(events.get(0)));
    } else {
      UIActionFromSet cg = new UIActionFromSet(id);
      for (Event e : events) {
        cg.add( new UIAction(e));
      }
      return cg;
    }
  }

}
