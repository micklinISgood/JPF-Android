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
package gov.nasa.jpf.jvm.bytecode;

import gov.nasa.jpf.vm.ArrayIndexOutOfBoundsExecutiveException;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.SharednessPolicy;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;


/**
 * abstraction for all array load instructions
 *
 * ..., array, index => ..., value
 */
public abstract class ArrayLoadInstruction extends JVMArrayElementInstruction implements JVMInstruction {
  
  @Override
  public Instruction execute (ThreadInfo ti) {
    StackFrame frame = ti.getModifiableTopFrame();

    // we need to get the object first, to check if it is shared
    int aref = frame.peek(1); // ..,arrayRef,idx
    if (aref == MJIEnv.NULL) {
      return ti.createAndThrowException("java.lang.NullPointerException");
    }
    
    ElementInfo eiArray = ti.getElementInfo(aref);
    
    if (ti.isArraySharednessRelevant(this, eiArray)){
      eiArray = ti.checkSharedArrayAccess( this, eiArray, frame.peek());
      if (ti.hasNextChoiceGenerator()) {
        return this;
      }
    }
    
    index = frame.pop();

    // we should not set 'arrayRef' before the CG check
    // (this would kill the CG loop optimization)
    arrayRef = frame.pop();
    
    try {
      push(frame, eiArray, index);

      Object attr = eiArray.getElementAttr(index);
      if (attr != null) {
        if (getElementSize() == 1) {
          frame.setOperandAttr(attr);
        } else {
          frame.setLongOperandAttr(attr);
        }
      }
      
      return getNext(ti);
    } catch (ArrayIndexOutOfBoundsExecutiveException ex) {
      return ex.getInstruction();
    }
  }

  protected boolean isReference () {
    return false;
  }

  /**
   * only makes sense pre-exec
   */
  @Override
  public int peekArrayRef (ThreadInfo ti){
    return ti.getTopFrame().peek(1);
  }

  // wouldn't really be required for loads, but this is a general
  // ArrayInstruction API
  @Override
  public int peekIndex (ThreadInfo ti){
    return ti.getTopFrame().peek();
  }

  protected abstract void push (StackFrame frame, ElementInfo e, int index)
                throws ArrayIndexOutOfBoundsExecutiveException;

  
  @Override
  public boolean isRead() {
    return true;
  }
  
  @Override
  public void accept(JVMInstructionVisitor insVisitor) {
	  insVisitor.visit(this);
  }
 }
