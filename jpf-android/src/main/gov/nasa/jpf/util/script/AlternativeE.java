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

package gov.nasa.jpf.util.script;

/**
 * ScriptElement that represents an alternative between choices. At runtime, this usually gets translated into
 * a ChoiceGenerator instance, to specify an event context that should facilitate state space exploration
 */
public class AlternativeE extends Alternative {

  public AlternativeE(ScriptElement parent, int line) {
    super(parent, line);
  }

  AlternativeIterator it = null;

  class AlternativeIterator extends ScriptElementContainer.SECIterator {
    int groupId = 0;

    protected AlternativeIterator() {
      super();
    }

    public void setGroup(int groupId) {
      this.groupId = groupId;
      current = null;
      next = getFirstChild();
      for (int i = 1; i < groupId; i++) {
        next = next.getNextSibling();
      }
      next = ((ScriptElementContainer) next).getFirstChild();

    }
  }

  public SECIterator iterator(int i) {
    if (it == null) {
      it = new AlternativeIterator();
    }
    it.setGroup(i);
    return it;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + line;
    result = prime * result +  this.toString().hashCode();
    result = prime * result + ((nextSibling == null) ? 0 : nextSibling.toString().hashCode());
    result = prime * result + ((parent == null) ? 0 : parent.toString().hashCode());
    return result;
  }

}
