//
//Copyright (C) 2006 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.
//
//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.
//
//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.util.script;

import gov.nasa.jpf.vm.ChoiceGeneratorBase;
import gov.nasa.jpf.vm.IntChoiceGenerator;

/**
 * AlternativeChoiceGenerator enumerates an interval of int choices.
 * 
 */
public class AlternativeChoiceGenerator extends ChoiceGeneratorBase<Integer> implements IntChoiceGenerator {

  protected int max;
  protected int next;

  public void reset() {
    next = 0;
  }

  public AlternativeChoiceGenerator(String id, int max) {
    super(id);
    this.max = max;
    reset();
  }

  public Integer getNextChoice() {
    return new Integer(next);
  }

  public boolean hasMoreChoices() {
    if (isDone) {
      return false;
    } else {
      if (next >= max) {
        return false;
      } else {
        return true;
      }
    }
  }

  public void advance() {
    next += 1;
  }

  public int getTotalNumberOfChoices() {
    return max;
  }

  public int getProcessedNumberOfChoices() {
    if (next >= max) {
      return 0;
    } else {
      return next;
    }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder(getClass().getName());
    sb.append("[id=\"");
    sb.append(id);
    sb.append('"');

    sb.append(",isCascaded:");
    sb.append(isCascaded);

    sb.append(",");
    sb.append(max);
    sb.append(",cur=");
    sb.append(getNextChoice());
    sb.append(']');
    return sb.toString();
  }

  @Override
  public Class<Integer> getChoiceType() {
    return Integer.class;
  }
}
