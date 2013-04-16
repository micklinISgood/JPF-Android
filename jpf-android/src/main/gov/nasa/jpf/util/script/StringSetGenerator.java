//Copyright  (C) 2006 United States Government as represented by the

package gov.nasa.jpf.util.script;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class CG {}

class SingleChoice extends CG {
  Event event;

  SingleChoice (Event e) {
    event = e;
  }
  public String toString() {
    return event.toString();
  }
}

class SetChoice extends CG {
  ArrayList<Event> choices = new ArrayList<Event>();

  public void add(Event e) {
    choices.add(e);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    int i=0, n = choices.size();
    for (Event e : choices) {
      sb.append(e);
      if (++i < n) sb.append(',');
    }
    sb.append('}');
    return sb.toString();
  }
}

/**
 * that's mostly a test class to see what a script would be expanded to w/o
 * having any side effects in the ElementProcessor
 */
public class StringSetGenerator implements ElementProcessor {
  LinkedHashMap<String,ArrayList<CG>> sections;
  ArrayList<CG> queue;
  
  StringSetGenerator() {
    sections = new LinkedHashMap<String,ArrayList<CG>>();
    queue = new ArrayList<CG>();
    sections.put("default", queue);
  }
  
  public void process (Section sec) {
    queue = new ArrayList<CG>();    
    sec.processChildren(this);
    
    for (String id : sec.getIds()) {
      sections.put(id,queue);      
    }
  }
  
  public void process (Event e) {
    for (Event ee : e.expand()) {
      queue.add( new SingleChoice(ee));
    }    
  }

  public void process (Alternative a) {
    SetChoice cg = new SetChoice();
    for (ScriptElement e = a.getFirstChild(); e != null; e = e.getNextSibling()) {
      if (e instanceof Event) {
        for (Event ee : ((Event)e).expand()) {
          cg.add(ee);
        }
      }
    }
    queue.add(cg);
  }
  
  public void process(Group g) {
    g.processChildren(this);
  }
  

  public void process (Repetition r) {
    int n = r.getRepeatCount();
    for (int i=0; i<n; i++) {
      r.processChildren(this);
    }
  }
  
  public LinkedHashMap<String,ArrayList<CG>> getSections () {
    return sections;
  }

  public List<CG> getCGQueue() {
    return queue;
  }
}