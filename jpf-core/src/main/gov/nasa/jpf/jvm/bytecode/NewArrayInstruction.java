//
// Copyright (C) 2011 United States Government as represented by the
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

import gov.nasa.jpf.vm.bytecode.NewInstruction;
import gov.nasa.jpf.vm.Types;

public abstract class NewArrayInstruction extends NewInstruction implements JVMInstruction {

  protected String type;
  protected String typeName; // deferred initialization
  
  protected int arrayLength = -1;

  /**
   * this only makes sense post-execution since the array dimension
   * is obtained from the operand stack
   * 
   * @return length of allocated array
   */
  public int getArrayLength(){
    return arrayLength;
  }
  
  public String getType(){
    return type;
  }
  
  public String getTypeName() {
    if (typeName == null){
      typeName = Types.getTypeName(type);
    }
    return typeName;
  }
  
  @Override
  public void cleanupTransients(){
    arrayLength = -1;
  }
}
