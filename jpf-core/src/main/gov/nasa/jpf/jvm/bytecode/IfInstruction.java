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
import gov.nasa.jpf.vm.BooleanChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * abstraction for all comparison instructions
 */
public abstract class IfInstruction extends Instruction implements JVMInstruction {
  protected int targetPosition;  // insn position at jump insnIndex
  protected Instruction target;  // jump target
  
  protected boolean conditionValue;  /** value of last evaluation of branch condition */

  protected IfInstruction(int targetPosition){
    this.targetPosition = targetPosition;
  }

  /**
   * return which branch was taken. Only useful after instruction got executed
   * WATCH OUT - 'true' means the jump condition is met, which logically is
   * the 'false' branch
   */
  public boolean getConditionValue() {
    return conditionValue;
  }
    
  /**
   *  Added so that SimpleIdleFilter can detect do-while loops when 
   * the while statement evaluates to true.
   */
  public boolean isBackJump () { 
    return (conditionValue) && (targetPosition <= position);
  }
    
  /** 
   * retrieve value of jump condition from operand stack
   * (not ideal to have this public, but some listeners might need it for
   * skipping the insn, plus we require it for subclass factorization)
   */
  public abstract boolean popConditionValue(StackFrame frame);
  
  public Instruction getTarget() {
    if (target == null) {
      target = mi.getInstructionAt(targetPosition);
    }
    return target;
  }
  
  public Instruction execute (ThreadInfo ti) {
    StackFrame frame = ti.getModifiableTopFrame();

    conditionValue = popConditionValue(frame);
    if (conditionValue) {
      return getTarget();
    } else {
      return getNext(ti);
    }
  }

  /**
   * use this as a delegatee in overridden executes of derived IfInstructions
   * (e.g. for symbolic execution)
   */
  protected Instruction executeBothBranches (SystemState ss, KernelState ks, ThreadInfo ti){
    if (!ti.isFirstStepInsn()) {
      BooleanChoiceGenerator cg = new BooleanChoiceGenerator(ti.getVM().getConfig(), "ifAll");
      if (ss.setNextChoiceGenerator(cg)){
        return this;

      } else {
        StackFrame frame = ti.getModifiableTopFrame();
        // some listener did override the CG, fallback to normal operation
        conditionValue = popConditionValue(frame);
        if (conditionValue) {
          return getTarget();
        } else {
          return getNext(ti);
        }
      }
      
    } else {
      BooleanChoiceGenerator cg = ss.getCurrentChoiceGenerator("ifAll", BooleanChoiceGenerator.class);
      assert (cg != null) : "no BooleanChoiceGenerator";
      
      StackFrame frame = ti.getModifiableTopFrame();
      popConditionValue(frame); // we are not interested in concrete values
      
      conditionValue = cg.getNextChoice();
      
      if (conditionValue) {
        return getTarget();
      } else {
        return getNext(ti);
      }

    }
  }
  
  public String toString () {
    return getMnemonic() + " " + targetPosition;
  }
  
  public int getLength() {
    return 3; // usually opcode, bb1, bb2
  }
  
  public void accept(JVMInstructionVisitor insVisitor) {
	  insVisitor.visit(this);
  }

  @Override
  public Instruction typeSafeClone(MethodInfo mi) {
    IfInstruction clone = null;

    try {
      clone = (IfInstruction) super.clone();

      // reset the method that this insn belongs to
      clone.mi = mi;

      clone.target = null;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }

    return clone;
  }
}
