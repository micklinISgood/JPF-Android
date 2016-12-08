//
// Copyright (C) 2012 United States Government as represented by the
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
package gov.nasa.jpf.test.mc.threads;

import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.vm.Verify;

/**
 * regression test for concurrent clinit execution
 */
public class ClinitTest extends TestJPF {

  static class X {
    static int x;
    static {
      Verify.threadPrintln("initializing X");
      assertTrue( x == 0);
      x++;
    }
  }
  
  @Test
  public void testNoConcurrentClinit () {
    if (verifyNoPropertyViolation()) {
   
      Runnable r = new Runnable() {
        public void run() {
          int x = X.x;
        }
      };
      Thread t = new Thread(r);
      t.start();
      
      int x = X.x;
      assertTrue( "x = " + x, x == 1);
    }
  }
  
  
  static class Y {
    static long y;
    
    static {
      Thread t = Thread.currentThread();
       Verify.threadPrintln("initializing Y");
      y = t.getId();
    }
  }
  
  @Test
  public void testClinitChoices() {
    if (verifyAssertionErrorDetails("gotcha")) {
      Runnable r = new Runnable() {
        public void run() {
          long y = Y.y;
        }
      };
      Thread t = new Thread(r);
      t.start();
      
      long y = Y.y;
      Thread tCur = Thread.currentThread();
      Verify.threadPrintln("testing Y.y");
      assertTrue( "gotcha", y == tCur.getId());
    }
  }
}
