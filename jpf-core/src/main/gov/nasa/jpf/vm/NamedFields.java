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

package gov.nasa.jpf.vm;

import gov.nasa.jpf.util.HashData;
import gov.nasa.jpf.util.IntVector;

/**
 * value container for non-array classes
 */
public class NamedFields extends Fields {

  /** this is where we store the instance data. Since field types are
   * heterogenous, we have to map everything into int
   */
  protected int[] values;

  public NamedFields (int dataSize) {
    values = new int[dataSize];
  }

  public int[] asFieldSlots() {
    return values;
  }

  /**
   * give an approximation of the heap size in bytes - we assume fields are word
   * aligned, hence the number of values*4 should be good. Note that this is
   * overridden by ArrayFields (arrays would be packed)
   */
  public int getHeapSize () {
    return values.length*4;
  }

  // our low level getters and setters
  public int getIntValue (int index) {
    return values[index];
  }

  public boolean isEqual(Fields o, int off, int len, int otherOff) {
    if (o instanceof NamedFields) {
      NamedFields other = (NamedFields) o;
      int iEnd = off + len;
      int jEnd = otherOff + len;
      int[] v = other.values;

      if ((iEnd > values.length) || (jEnd > v.length)) {
        return false;
      }

      for (int i = off, j = otherOff; i < iEnd; i++, j++) {
        if (values[i] != v[j]) {
          return false;
        }
      }

      return true;
    } else {
      return false;
    }
  }

  // same as above, just here to make intentions clear
  public int getReferenceValue (int index) {
    return values[index];
  }

  public long getLongValue (int index) {
    return Types.intsToLong(values[index + 1], values[index]);
  }

  public boolean getBooleanValue (int index) {
    return Types.intToBoolean(values[index]);
  }

  public byte getByteValue (int index) {
    return (byte) values[index];
  }

  public char getCharValue (int index) {
    return (char) values[index];
  }

  public short getShortValue (int index) {
    return (short) values[index];
  }

  // <2do> get rid of it!! this is only for internal use, to increase efficiency
  public int[] getValues() {
    return values;
  }

  //--- the field modifier methods (both instance and static)

  public void setReferenceValue (int index, int newValue) {
    values[index] = newValue;
  }

  public void setBooleanValue (int index, boolean newValue) {
    values[index] = newValue ? 1 : 0;
  }

  public void setByteValue (int index, byte newValue) {
    values[index] = newValue;
  }

  public void setCharValue (int index, char newValue) {
    values[index] = (int)newValue;
  }

  public void setShortValue (int index, short newValue) {
    values[index] = newValue;
  }

  public void setFloatValue (int index, float newValue) {
    values[index] = Types.floatToInt(newValue);
  }

  public void setIntValue (int index, int newValue) {
    values[index] = newValue;
  }

  public void setLongValue (int index, long newValue) {
		values[index++] = Types.hiLong(newValue);
    values[index] = Types.loLong(newValue);
  }

  public void setDoubleValue (int index, double newValue) {
    values[index++] = Types.hiDouble(newValue);
    values[index] = Types.loDouble(newValue);
  }


  public float getFloatValue (int index) {
    return Types.intToFloat(values[index]);
  }

  public double getDoubleValue (int index) {
    return Types.intsToDouble( values[index+1], values[index]);
  }

  /**
   * Creates a clone.
   */
  public NamedFields clone () {
    NamedFields f = (NamedFields) cloneFields();
    f.values = values.clone();
    return f;
  }

  /**
   * Checks for equality.
   */
  public boolean equals (Object o) {
    if (o instanceof NamedFields) {
      NamedFields other = (NamedFields) o;

      //--- check values
      int[] v1 = values;
      int[] v2 = other.values;
      int l = v1.length;
      if (l != v2.length) {
        return false;
      }
      for (int i = 0; i < l; i++) {
        if (v1[i] != v2[i]) {
          return false;
        }
      }
      
      return super.compareAttrs(other);

    } else {
      return false;
    }
  }

  // serialization interface
  public void appendTo(IntVector v) {
    v.append(values);
  }


  /**
   * Adds some data to the computation of an hashcode.
   */
  public void hash (HashData hd) {
    int[] v = values;
    for (int i=0, l=v.length; i < l; i++) {
      hd.add(v[i]);
    }
  }

  /**
   * Size of the fields.
   */
  public int size () {
    return values.length;
  }

  public String toString () {
    StringBuilder sb = new StringBuilder("NamedFields[");

    sb.append("values=");
    sb.append('[');

    for (int i = 0; i < values.length; i++) {
      if (i != 0) {
        sb.append(',');
      }

      sb.append(values[i]);
    }

    sb.append(']');
    sb.append(',');

    sb.append(']');

    return sb.toString();
  }

  // <2do> replace with copyTo() !!
  public int[] getRawValues() {
    return values;
  }

  public void copyFrom(Fields other) {
    System.arraycopy(((NamedFields)other).values, 0, this.values, 0, values.length);
    super.copyAttrs(other);
  }

}
