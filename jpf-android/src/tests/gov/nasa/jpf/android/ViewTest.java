//
// Copyright (C) 2010 United States Government as represented by the
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

import gov.nasa.jpf.util.test.TestJPF;
import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.junit.Test;

import android.app.Activity;

import com.android.internal.policy.impl.PhoneLayoutInflater;

/**
 * here, we will only work on focus testing
 */
public class ViewTest extends TestJPF {

	private final static String XMLFile = "main.xml";

	public static void main(String[] args) {
		runTestsOfThisClass(args);
	}

	@Test
  public void testAddFocusListener() {
	  
	  
    if (verifyNoPropertyViolation()) {
    	Activity acc = new Activity();
    	PhoneLayoutInflater layoutInf = new PhoneLayoutInflater(acc);
    }
  }
}