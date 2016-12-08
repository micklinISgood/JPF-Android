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

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;


/**
 * Load long from local variable
 * ... => ..., value
 */
public class LLOAD extends JVMLocalVariableInstruction {

  public LLOAD(int localVarIndex){
    super(localVarIndex);
  }

  @Override
  public Instruction execute (ThreadInfo ti) {
    StackFrame frame = ti.getModifiableTopFrame();
    
    frame.pushLongLocal(index);

    return getNext(ti);
  }

  public int getLength() {
    if (index > 3){
      return 2; // opcode, index
    } else {
      return 1;
    }
  }
  
  public int getByteCode () {
    switch (index) {
      case 0: return 0x1e;
      case 1: return 0x1f;
      case 2: return 0x20;
      case 3: return 0x21;
    }
    
    return 0x16; // ?? wide
  }
  
  public String getBaseMnemonic() {
    return "lload";
  }
  
  public void accept(JVMInstructionVisitor insVisitor) {
	  insVisitor.visit(this);
  }
}
