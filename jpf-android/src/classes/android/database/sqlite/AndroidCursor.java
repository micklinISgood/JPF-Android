package android.database.sqlite;

import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import java.util.Iterator;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;

/**
 * Models a cursor over records in a table
 * 
 * @author Heila van der Merwe
 * 
 */
public class AndroidCursor implements Cursor {

  /** List of values in the table */
  List<ContentValues> values;

  /** Iterator over the */
  Iterator<ContentValues> iterator;
  ContentValues current = null;

  String[] columns = null;

  boolean closed;

  //  public AndroidCursor(String[] columns) {
  //
  //    closed = false;
  //  }

  public AndroidCursor(List<ContentValues> values, String[] columns) {
    this.values = values;
    if (values != null)
      iterator = (Iterator<ContentValues>) values.iterator();
    this.columns = columns;

    closed = false;

  }

  public void close() {
    closed = true;
  }

  public boolean isClosed() {
    return closed;
  }

  public boolean moveToNext() {
    if (iterator.hasNext())
      current = iterator.next();
    else
      current = null;
    return current != null;
  }

  public int getColumnCount() {
    return (columns != null) ? columns.length : 0;
  }

  public int getColumnIndex(String columnName) {
    for (int i = 0; i < columns.length; i++) {
      if (columns[i].equals(columnName))
        return i;
    }
    return -1;
  }

  public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
    int result = getColumnIndex(columnName);
    if (result > -1) {
      return result;
    }
    throw new IllegalArgumentException("Trying to find non-exisiting column " + columnName);
  }

  public String getColumnName(int columnIndex) {
    return columns[columnIndex];
  }

  public String[] getColumnNames() {
    return columns;
  }

  public int getCount() {
    return (values!=null)? values.size():0;
  }

  public int getInt(int columnIndex) {
    return (Integer) current.get(getColumnName(columnIndex));
  }

  public long getLong(int columnIndex) {
    return new Long((Integer) current.get(getColumnName(columnIndex)));
  }

  public short getShort(int columnIndex) {
    return (Short) current.get(getColumnName(columnIndex));
  }

  public float getFloat(int columnIndex) {
    return (Float) current.get(getColumnName(columnIndex));
  }

  public double getDouble(int columnIndex) {
    return (Double) current.get(getColumnName(columnIndex));
  }

  public String getString(int columnIndex) {
    return (String) current.get(getColumnName(columnIndex));

  }

  @Override
  public int getPosition() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean move(int offset) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean moveToPosition(int position) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean moveToFirst() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean moveToLast() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean moveToPrevious() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isFirst() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isLast() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isBeforeFirst() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isAfterLast() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public byte[] getBlob(int columnIndex) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int getType(int columnIndex) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isNull(int columnIndex) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean requery() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void registerContentObserver(ContentObserver observer) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void unregisterContentObserver(ContentObserver observer) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void registerDataSetObserver(DataSetObserver observer) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void unregisterDataSetObserver(DataSetObserver observer) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setNotificationUri(ContentResolver cr, Uri uri) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean getWantsAllOnMoveCalls() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Bundle getExtras() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Bundle respond(Bundle extras) {
    // TODO Auto-generated method stub
    return null;
  }

}
