//
// Copyright (C) 2007 United States Government as represented by the
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
package gov.nasa.jpf.test.java.concurrent;

import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.vm.Verify;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import org.junit.Test;

/**
 * raw test for java.util.concurrent.atomic.AtomicLongFieldUpdater
 */
public class AtomicLongFieldUpdaterTest extends TestJPF {

  static {
    Verify.setProperties("cg.enumerate_cas=true");
  }
  long value;

  @Test
  public void testField() {
    if (verifyNoPropertyViolation("+cg.enumerate_cas=true")) {
      AtomicLongFieldUpdater<AtomicLongFieldUpdaterTest> upd =
              AtomicLongFieldUpdater.newUpdater(AtomicLongFieldUpdaterTest.class, "value");

      final long v1 = 723489234098734534L;
      final long v2 = 256092348679304843L;
      final long nogo = 823468902346907854L;
      value = v1;

      assert upd.compareAndSet(this, v1, v2);
      assert value == v2;

      assert !upd.compareAndSet(this, v1, nogo);
      assert value == v2;

      assert value == upd.get(this);

      assert v2 == upd.getAndSet(this, v1);
      assert value == v1;

      upd.set(this, v2);
      assert value == v2;

      upd.lazySet(this, v1);
      assert value == v1;

      assert upd.weakCompareAndSet(this, v1, v2);
      assert value == v2;

      assert !upd.weakCompareAndSet(this, v1, nogo);
      assert value == v2;

      assert v2 == upd.getAndAdd(this, 5);
      assert v2 + 5 == value;
    }
  }
}
