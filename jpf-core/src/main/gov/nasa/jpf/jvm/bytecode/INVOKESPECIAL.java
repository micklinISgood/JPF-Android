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

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LoadOnJPFRequired;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;


/**
 * Invoke instance method; special handling for superclass, private,
 * and instance initialization method invocations
 * ..., objectref, [arg1, [arg2 ...]] => ...
 *
 * invokedMethod is supposed to be constant (ClassInfo can't change)
 */
public class INVOKESPECIAL extends InstanceInvocation {

  public INVOKESPECIAL (String clsDescriptor, String methodName, String signature){
    super(clsDescriptor, methodName, signature);
  }


  public int getByteCode () {
    return 0xB7;
  }

  public Instruction execute (ThreadInfo ti) {
    int argSize = getArgSize();
    int objRef = ti.getCalleeThis( argSize);
    lastObj = objRef;

    // we don't have to check for NULL objects since this is either a ctor, a 
    // private method, or a super method

    MethodInfo callee;
    
    try {
      callee = getInvokedMethod(ti);
    } catch(LoadOnJPFRequired rre) {
      return ti.getPC();
    }      

    if (callee == null){
      return ti.createAndThrowException("java.lang.NoSuchMethodException", "Calling " + cname + '.' + mname);
    }

    ElementInfo ei = ti.getElementInfoWithUpdatedSharedness(objRef);

    if (callee.isSynchronized()){
      if (checkSyncCG(ei, ti)){
        return this;
      }
    }

    setupCallee( ti, callee); // this creates, initializes and pushes the callee StackFrame

    return ti.getPC(); // we can't just return the first callee insn if a listener throws an exception
  }

  /**
   * If the current thread already owns the lock, then the current thread can go on.
   * For example, this is a recursive acquisition.
   */
  protected boolean isLockOwner(ThreadInfo ti, ElementInfo ei) {
    return ei.getLockingThread() == ti;
  }

  /**
   * If the object will still be owned, then the current thread can go on.
   * For example, all but the last monitorexit for the object.
   */
  protected boolean isLastUnlock(ElementInfo ei) {
    return ei.getLockCount() == 1;
  }

  /**
    * we can do some more caching here - the MethodInfo should be const
    */
  public MethodInfo getInvokedMethod (ThreadInfo th) {

    // since INVOKESPECIAL is only used for private methods and ctors,
    // we don't have to deal with null object calls

    if (invokedMethod == null) {
      ClassInfo ci = ClassLoaderInfo.getCurrentResolvedClassInfo(cname);
      boolean recursiveLookup = (mname.charAt(0) != '<'); // no hierarchy lookup for <init>
      invokedMethod = ci.getMethod(mname, recursiveLookup);
    }

    return invokedMethod; // we can store internally
  }

  public String toString() {
    return ("invokespecial " + cname + '.' + mname);
  }

  @Override
  public Object getFieldValue (String id, ThreadInfo ti) {
    int objRef = getCalleeThis(ti);
    ElementInfo ei = ti.getElementInfo(objRef);

    Object v = ei.getFieldValueObject(id);

    if (v == null){ // try a static field
      v = ei.getClassInfo().getStaticFieldValueObject(id);
    }

    return v;
  }

  public void accept(JVMInstructionVisitor insVisitor) {
	  insVisitor.visit(this);
  }
}
