/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.os;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.SparseArray;
import android.util.SparseBooleanArray;

public final class Parcel {

  public final static Parcelable.Creator<String> STRING_CREATOR = new Parcelable.Creator<String>() {
    public String createFromParcel(Parcel source) {
      return source.readString();
    }

    public String[] newArray(int size) {
      return new String[size];
    }
  };

  /**
   * Retrieve a new Parcel object from the pool.
   */
  public static Parcel obtain() {
    return new Parcel(0);
  }

  private Parcel(int i) {

  }

  /**
   * Put a Parcel object back into the pool. You must not touch the object after this call.
   */
  public final void recycle() {
  }

  /**
   * Returns the total amount of data contained in the parcel.
   */
  public final native int dataSize();

  /**
   * Returns the amount of data remaining to be read from the parcel. That is, {@link #dataSize}-
   * {@link #dataPosition}.
   */
  public final native int dataAvail();

  /**
   * Returns the current position in the parcel data. Never more than {@link #dataSize}.
   */
  public final native int dataPosition();

  /**
   * Returns the total amount of space in the parcel. This is always >= {@link #dataSize}. The difference
   * between it and dataSize() is the amount of room left until the parcel needs to re-allocate its data
   * buffer.
   */
  public final native int dataCapacity();

  /**
   * Change the amount of data in the parcel. Can be either smaller or larger than the current size. If larger
   * than the current capacity, more memory will be allocated.
   * 
   * @param size
   *          The new number of bytes in the Parcel.
   */
  public final native void setDataSize(int size);

  /**
   * Move the current read/write position in the parcel.
   * 
   * @param pos
   *          New offset in the parcel; must be between 0 and {@link #dataSize}.
   */
  public final native void setDataPosition(int pos);

  /**
   * Change the capacity (current available space) of the parcel.
   * 
   * @param size
   *          The new capacity of the parcel, in bytes. Can not be less than {@link #dataSize} -- that is, you
   *          can not drop existing data with this method.
   */
  public final void setDataCapacity(int size) {
  }

  /** @hide */
  public final boolean pushAllowFds(boolean allowFds) {
    return false;
  }

  /** @hide */
  public final void restoreAllowFds(boolean lastValue) {
  }

  public final byte[] marshall() {
    return null;
  };

  public final void unmarshall(byte[] data, int offest, int length) {
  }

  public final void appendFrom(Parcel parcel, int offset, int length) {
  }

  public final boolean hasFileDescriptors() {
    return false;
  }

  public final void writeInterfaceToken(String interfaceName) {
  }

  public final void enforceInterface(String interfaceName) {
  }

  public final void writeByteArray(byte[] b) {
  }

  public final void writeByteArray(byte[] b, int offset, int len) {

  }

  public final void writeInt(int val) {
  };

  public final void writeLong(long val) {
  };

  public final void writeFloat(float val) {
  };

  public final void writeDouble(double val) {
  };

  public final void writeString(String val) {
  };

  public final void writeCharSequence(CharSequence val) {
  }

  public final void writeStrongBinder(IBinder val) {
  };

  public final void writeStrongInterface(IInterface val) {
  }

  public final void writeFileDescriptor(FileDescriptor val) {
  }

  public final void writeByte(byte val) {
  }

  public final void writeMap(Map val) {
  }

  /* package */void writeMapInternal(Map<String, Object> val) {
  }

  public final void writeBundle(Bundle val) {
  }

  public final void writeList(List val) {
  }

  public final void writeArray(Object[] val) {
  }

  public final void writeSparseArray(SparseArray<Object> val) {
  }

  public final void writeSparseBooleanArray(SparseBooleanArray val) {
  }

  public final void writeBooleanArray(boolean[] val) {
  }

  public final boolean[] createBooleanArray() {
    return null;
  }

  public final void readBooleanArray(boolean[] val) {
  }

  public final void writeCharArray(char[] val) {
  }

  public final char[] createCharArray() {
    return null;
  }

  public final void readCharArray(char[] val) {
  }

  public final void writeIntArray(int[] val) {
  }

  public final int[] createIntArray() {
    return null;
  }

  public final void readIntArray(int[] val) {
  }

  public final void writeLongArray(long[] val) {
  }

  public final long[] createLongArray() {
    return null;
  }

  public final void readLongArray(long[] val) {
  }

  public final void writeFloatArray(float[] val) {
  }

  public final float[] createFloatArray() {
    return null;
  }

  public final void readFloatArray(float[] val) {
  }

  public final void writeDoubleArray(double[] val) {
  }

  public final double[] createDoubleArray() {
    return null;
  }

  public final void readDoubleArray(double[] val) {
  }

  public final void writeStringArray(String[] val) {
  }

  public final String[] createStringArray() {
    return null;
  }

  public final void readStringArray(String[] val) {
  }

  public final void writeBinderArray(IBinder[] val) {
  }

  public final void writeCharSequenceArray(CharSequence[] val) {
  }

  public final IBinder[] createBinderArray() {
    return null;
  }

  public final void readBinderArray(IBinder[] val) {
  }

  public final <T extends Parcelable> void writeTypedList(List<T> val) {
  }

  public final void writeStringList(List<String> val) {
  }

  public final void writeBinderList(List<IBinder> val) {
  }

  public final <T extends Parcelable> void writeTypedArray(T[] val, int parcelableFlags) {
  }

  public final void writeValue(Object v) {
  }

  public final void writeParcelable(Parcelable p, int parcelableFlags) {
  }

  public final void writeSerializable(Serializable s) {
  }

  public final void writeException(Exception e) {
  }

  public final void writeNoException() {
  }

  /**
   * Special function for reading an exception result from the header of a parcel, to be used after receiving
   * the result of a transaction. This will throw the exception for you if it had been written to the Parcel,
   * otherwise return and let you read the normal result data from the Parcel.
   * 
   * @see #writeException
   * @see #writeNoException
   */
  public final void readException() {
  }

  /**
   * Parses the header of a Binder call's response Parcel and returns the exception code. Deals with lite or
   * fat headers. In the common successful case, this header is generally zero. In less common cases, it's a
   * small negative number and will be followed by an error string.
   * 
   * This exists purely for android.database.DatabaseUtils and insulating it from having to handle fat headers
   * as returned by e.g. StrictMode-induced RPC responses.
   * 
   * @hide
   */
  public final int readExceptionCode() {
    return 0;
  }

  /**
   * Use this function for customized exception handling. customized method call this method for all unknown
   * case
   * 
   * @param code
   *          exception code
   * @param msg
   *          exception message
   */
  public final void readException(int code, String msg) {

  }

  /**
   * Read an integer value from the parcel at the current dataPosition().
   */
  public final int readInt() {
    return -1;
  }

  /**
   * Read a long integer value from the parcel at the current dataPosition().
   */
  public final long readLong() {
    return -1;
  }

  /**
   * Read a floating point value from the parcel at the current dataPosition().
   */
  public final float readFloat() {
    return -1;
  }

  /**
   * Read a double precision floating point value from the parcel at the current dataPosition().
   */
  public final double readDouble() {
    return -1;
  }

  /**
   * Read a string value from the parcel at the current dataPosition().
   */
  public final String readString() {
    return null;
  }

  /**
   * Read a CharSequence value from the parcel at the current dataPosition().
   * 
   * @hide
   */
  public final CharSequence readCharSequence() {
    return null;
  }

  /**
   * Read an object from the parcel at the current dataPosition().
   */
  public final native IBinder readStrongBinder();

  /**
   * Read a FileDescriptor from the parcel at the current dataPosition().
   */
  public final ParcelFileDescriptor readFileDescriptor() {
    return null;
  }

  private native FileDescriptor internalReadFileDescriptor();

  /* package */static FileDescriptor openFileDescriptor(String file, int mode) throws FileNotFoundException {
    return null;
  }

  /* package */static FileDescriptor dupFileDescriptor(FileDescriptor orig) throws IOException {
    return null;
  }

  /* package */static void closeFileDescriptor(FileDescriptor desc) throws IOException {
  }

  /* package */static void clearFileDescriptor(FileDescriptor desc) {
  }

  /**
   * Read a byte value from the parcel at the current dataPosition().
   */
  public final byte readByte() {
    return 0;
  }

  /**
   * Please use {@link #readBundle(ClassLoader)} instead (whose data must have been written with
   * {@link #writeBundle}. Read into an existing Map object from the parcel at the current dataPosition().
   */
  public final void readMap(Map outVal, ClassLoader loader) {
  }

  /**
   * Read into an existing List object from the parcel at the current dataPosition(), using the given class
   * loader to load any enclosed Parcelables. If it is null, the default class loader is used.
   */
  public final void readList(List outVal, ClassLoader loader) {
  }

  /**
   * Please use {@link #readBundle(ClassLoader)} instead (whose data must have been written with
   * {@link #writeBundle}. Read and return a new HashMap object from the parcel at the current dataPosition(),
   * using the given class loader to load any enclosed Parcelables. Returns null if the previously written map
   * object was null.
   */
  public final HashMap readHashMap(ClassLoader loader) {
    return null;
  }

  /**
   * Read and return a new Bundle object from the parcel at the current dataPosition(). Returns null if the
   * previously written Bundle object was null.
   */
  public final Bundle readBundle() {
    return readBundle(null);
  }

  /**
   * Read and return a new Bundle object from the parcel at the current dataPosition(), using the given class
   * loader to initialize the class loader of the Bundle for later retrieval of Parcelable objects. Returns
   * null if the previously written Bundle object was null.
   */
  public final Bundle readBundle(ClassLoader loader) {
    int length = readInt();
    if (length < 0) {
      return null;
    }

    final Bundle bundle = new Bundle(this, length);
    if (loader != null) {
      bundle.setClassLoader(loader);
    }
    return bundle;
  }

  public final byte[] createByteArray() {
    return null;
  }

  public final void readByteArray(byte[] val) {
  }

  public final String[] readStringArray() {
    return null;

  }

  public final CharSequence[] readCharSequenceArray() {
    return null;

  }

  public final ArrayList readArrayList(ClassLoader loader) {
    return null;

  }

  public final Object[] readArray(ClassLoader loader) {
    return null;

  }

  public final SparseArray readSparseArray(ClassLoader loader) {
    return null;

  }

  public final SparseBooleanArray readSparseBooleanArray() {
    return null;

  }

  public final <T> ArrayList<T> createTypedArrayList(Parcelable.Creator<T> c) {
    return null;

  }

  public final <T> void readTypedList(List<T> list, Parcelable.Creator<T> c) {
  }

  public final ArrayList<String> createStringArrayList() {
    return null;

  }

  public final ArrayList<IBinder> createBinderArrayList() {
    return null;

  }

  public final void readStringList(List<String> list) {
  }

  public final void readBinderList(List<IBinder> list) {
  }

  public final <T> T[] createTypedArray(Parcelable.Creator<T> c) {
    return null;

  }

  public final <T> void readTypedArray(T[] val, Parcelable.Creator<T> c) {
  }

  @Deprecated
  public final <T> T[] readTypedArray(Parcelable.Creator<T> c) {
    return null;
  }

  public final <T extends Parcelable> void writeParcelableArray(T[] value, int parcelableFlags) {
    return;

  }

  public final Object readValue(ClassLoader loader) {
    return null;
  }

  public final <T extends Parcelable> T readParcelable(ClassLoader loader) {
    return null;
  }

  public final Parcelable[] readParcelableArray(ClassLoader loader) {
    return null;
  }

  public final Serializable readSerializable() {
    return null;
  }

  static protected final Parcel obtain(int obj) {
    return null;
  }

  @Override
  protected void finalize() throws Throwable {
  }

}
