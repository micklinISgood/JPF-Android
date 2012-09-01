//
// Copyright  (C) 2006 United States Government as represented by the
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

package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.util.script.Alternative;
import gov.nasa.jpf.util.script.ESParser;
import gov.nasa.jpf.util.script.Event;
import gov.nasa.jpf.util.script.EventGeneratorFactory;
import gov.nasa.jpf.util.script.Repetition;
import gov.nasa.jpf.util.script.ScriptElement;
import gov.nasa.jpf.util.script.Section;

import java.io.PrintWriter;


public class UIActionGeneratorFactory extends EventGeneratorFactory {
      
  // we keep track of the last encountered toplevel as a default
  String lastToplevel = null;
       
  public UIActionGeneratorFactory (String fname) throws ESParser.Exception {
    init(fname);
  }
  
  public UIActionGeneratorFactory (Config conf) throws JPFConfigException, ESParser.Exception {
    this.conf = conf;
    
    String fname = conf.getString("awt.script");
    if (fname == null) {
      throw conf.exception("missing \"awt.script\" property"); 
    }

    init(fname);
  }
      
  public UIActionGenerator getNext() {
    return (UIActionGenerator) getNextEventGenerator();
  }
 
  public Class<?> getEventType() {
    return UIAction.class;
  }
    
  public void process (Event e) {
    for (Event ee : e.expand()) {
      UIAction action = new UIAction(ee);
      queue.add( new UIActionSingleChoice("processSingle", action));
    }
  }

  public void process (Alternative a) {
    UIActionFromSet cg = new UIActionFromSet("processAlt");
    
    for (ScriptElement e = a.getFirstChild(); e != null; e = e.getNextSibling()) {
      if (e instanceof Event) {
        for (Event ee : ((Event)e).expand()) {
          UIAction action = new UIAction(ee);
          cg.add(action);
        }
      }
    }
    queue.add(cg);
  }
  
  public void process (Repetition r) {
    int n = r.getRepeatCount();
    for (int i=0; i<n; i++) {
      r.processChildren(this);
    }
  }
  
  

  public void process (Section sec) {
    // we don't really process sections for UIActions (yet)
    sec.processChildren(this);
  }
  
 
  public static void main (String[] args) {
    try {
      UIActionGeneratorFactory gen = new UIActionGeneratorFactory(args[0]);
      
      PrintWriter pw = new PrintWriter(System.out, true);
      gen.printOn(pw);
      
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

}
