//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA). All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3. The NOSA has been approved by the Open Source
// Initiative. See the file NOSA-1.3-JPF at the top of the distribution
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

import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.MethodLocator;

/**
 * this models a single user interaction, which maps to a (reflection) call of a
 * method in a
 * java.awt.Component instance (or a static method if there is no target spec)
 */
public class UIAction implements MethodLocator {
  public final static String NONE = "NONE";
  public final char TRANSFER_FOCUS_MARKER = '!';

  public String target; // the component id (enumeration number)
  public String action; // the method name

  protected boolean transferFocus; // transfer focus before action is executed

  public Object[] arguments;

  protected int line; // from script

  int id = -1;

  /**
   * 'spec' has the form $target.action target is either a number or a
   * hierarchical string of ids, e.g.
   * $MyFrame/Ok
   */
  public UIAction(Event e) {

    String spec = e.getId();

    if (NONE.equals(spec)) {
      action = spec;
      target = null;
      arguments = null;

    } else {
      if (spec.charAt(0) == TRANSFER_FOCUS_MARKER) {
        spec = spec.substring(1);
        transferFocus = true;
      }

      int i = spec.lastIndexOf('.');
      if (i > 0) {
        target = spec.substring(0, i);
        spec = spec.substring(i + 1);
      }

      action = spec;
      line = e.getLine();

      arguments = e.getConcreteArguments();
    }
  }

  public boolean isNone() {
    return (NONE.equals(action));
  }

  public boolean transferFocus() {
    return transferFocus;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String s) {
    target = s;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String s) {
    action = s;
  }

  public Object[] getArguments() {
    return arguments;
  }

  public int getLine() {
    return line;
  }

  public Class[] getArgumentTypes() {
    if (arguments == null) {
      return new Class[0];
    } else {
      Class[] list = new Class[arguments.length];
      for (int i = 0; i < arguments.length; i++) {
        Object a = arguments[i];

        if (a instanceof String) {
          list[i] = String.class;
        } else if (a instanceof Double) {
          list[i] = double.class;
        } else if (a instanceof Integer) {
          list[i] = int.class;
        } else {
          assert false : "unsupported argument type in event: " + a;
        }
      }
      return list;
    }
  }

  public Object[] getBoxedArguments(Class<?>[] argTypes) {
    if (arguments == null) {
      return new Object[0];
    } else {
      Object[] list = new Object[arguments.length];
      for (int i = 0; i < arguments.length; i++) {
        Object a = arguments[i];

        if (a instanceof String) {
          list[i] = a;
        } else if (a instanceof Double) {
          list[i] = new Double(((Number) a).doubleValue());
        } else if (a instanceof Integer) {
          list[i] = new Integer(((Number) a).intValue());
        } else {
          assert false : "unsupported argument type in UIAction: " + a;
        }
      }
      return list;
    }
  }

  // <2do> this thing is screwed - there is not enough type info in UIAction to get the method
  public String getMethodName() {
    StringBuilder sb = new StringBuilder(action);

    sb.append('(');
    if (arguments != null) {
      for (Object a : arguments) {
        if (a == null) {
          // nope, could be anything
          sb.append("Ljava/lang/Object;");
        } else if (a instanceof String) {
          sb.append("Ljava/lang/String;");
        } else if (a instanceof Double) {
          sb.append('J');
        } else if (a instanceof Integer) {
          sb.append('I');
        } else {
          assert false : "unsupported argument type in UIAction: " + a;
        }
      }
    }

    // Hmm, we should handle non-void return types
    sb.append(")V");

    return sb.toString();
  }

  public boolean match(MethodInfo mi) {
    if (mi.getName().equals(action)) {
      byte[] atypes = mi.getArgumentTypes();
      if (atypes.length == arguments.length) {
        for (int i = 0; i < atypes.length; i++) {
          Object a = arguments[i];

        }
      }
    }
    return false;
  }

  public String toString() {
    StringBuilder b = new StringBuilder();

    if (transferFocus) {
      b.append(TRANSFER_FOCUS_MARKER);
    }

    if (target != null) {
      b.append(target);
      b.append('.');
    }

    if (action != null) {
      b.append(action);

      if (!NONE.equals(action)) {
        b.append('(');

        if (arguments != null) {
          for (int i = 0; i < arguments.length; i++) {
            Object a = arguments[i];
            if (i > 0) {
              b.append(',');
            }
            if (a instanceof String) {
              b.append('"');
              b.append(a);
              b.append('"');
            } else if (a instanceof Integer) {
              b.append(((Integer) a).intValue());
            } else if (a instanceof Double) {
              b.append(((Double) a).doubleValue());
            }
          }
        }
        b.append(')');
      }
    }

    return b.toString();
  }

  public void setID(int eventid) {
    this.id = eventid;
  }
}
