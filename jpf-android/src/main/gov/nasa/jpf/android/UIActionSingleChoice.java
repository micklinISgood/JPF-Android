//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
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
import gov.nasa.jpf.jvm.ChoiceGenerator;

/**
 * not really a choice generator, since we have only a single action
 */
public class UIActionSingleChoice extends UIActionGenerator {

  UIAction action;
  int state;

  public UIActionSingleChoice (String id, UIAction action) {
    super(id);

    this.action = action;
    state = -1;
  }


  public UIAction getNextChoice () {
    if (state == 0) {
      return action;
    } else {
      return null;
    }
  }

  public void advance () {
    state++;
  }

  public int getProcessedNumberOfChoices () {
    return (state == -1) ? 0 : 1;
  }

  public int getTotalNumberOfChoices () {
    return 1;
  }

  public boolean hasMoreChoices () {
    return !isDone && (state < 0);
  }

  public ChoiceGenerator randomize () {
    // it's hard to randomize this
    return this;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getName());

    sb.append("[id=\"");
    sb.append(id);
    sb.append("\",");

    if (state >= 0) {
      sb.append(MARKER);
    }
    sb.append(action.toString());

    sb.append(']');
    return sb.toString();
  }

  public void reset () {
    state = -1;
  }

}
