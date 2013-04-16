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
 * ScriptElement that represents an alternative between choices. At runtime,
 * this usually gets translated into a ChoiceGenerator instance, to specify
 * an event context that should facilitate state space exploration
 */
public class Alternative extends ScriptElementContainer {

  class AlternativeIterator extends ScriptElementContainer.SECIterator {

    int groupId;
    int count = 0;

    AlternativeIterator(int groupId) {
      this.groupId = groupId;
      cur = firstChild;
    }

    public boolean hasNext() {
      return cur != null;
    }

    @Override
    public void previous(ScriptElement e) {
      e.nextSibling = cur;
      cur = e;
      count--;
    }

    public ScriptElement next() {
      ScriptElement ret = null;
      if (cur != null) {
        if (cur instanceof ScriptElementContainer) {
          count++;
          if (count == groupId) {
            ret = cur;
            cur = cur.nextSibling;
            return ret;
          }
        }
        cur = cur.nextSibling;
        ret = next();
      }
      return ret;
    }

    public void remove() {
      throw new UnsupportedOperationException("no ScriptElement removal supported");
    }
  }

  public boolean used = false;

  public Alternative (ScriptElement parent, int line) {
    super(parent, line);
  }

  public String toString() {
    return toString("ANY");
  }

  public void process (ElementProcessor p) {
    p.process(this);
  }

  public SECIterator iterator(int i) {
    return new AlternativeIterator(i);
  }

}
