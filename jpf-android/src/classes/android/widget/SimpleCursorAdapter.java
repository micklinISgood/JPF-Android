package android.widget;

import java.util.Arrays;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/**
 * What would we want to check:
 * data comming through in cursor might be null or of "" length of even correct
 * and then we parse it incorrectly. In other words we want to execute the the
 * bindView method for each of the values in the cursor to make sure it does not
 * crash in there.
 * 
 * Check that the columns in "from" are valid.
 * 
 * Set the data in the widgets so that we can use it later maybe for a next view or something
 * 
 * @author Heila
 * 
 */
public class SimpleCursorAdapter implements ListAdapter {
  Context context;
  String[] mOriginalFrom;
  protected int[] mTo;
  /**
   * A list of columns containing the data to bind to the UI.
   */
  protected int[] mFrom;
  int layout;
  Cursor mCursor;

  @Deprecated
  public SimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
    this.context = context;
    mTo = to;
    mOriginalFrom = from;
    this.mCursor = c;
    this.layout = layout;
    findColumns(from);
  }

  /**
   * Create a map from an array of strings to an array of column-id integers in
   * mCursor.
   * If mCursor is null, the array will be discarded.
   * 
   * @param from
   *          the Strings naming the columns of interest
   */
  private void findColumns(String[] from) {
    if (mCursor != null) {
      int i;
      int count = from.length;
      if (mFrom == null || mFrom.length != count) {
        mFrom = new int[count];
      }
      for (i = 0; i < count; i++) {
        mFrom[i] = mCursor.getColumnIndexOrThrow(from[i]);
      }
    } else {
      mFrom = null;
    }
  }

  public Cursor swapCursor(Cursor c) {
    mCursor.close();
    Cursor temp = mCursor;
    mCursor = c;
    return temp;
  }

  public void bindView(View row, Context context, Cursor cursor) {
  }

  /**
   * Notifies the attached observers that the underlying data has been changed
   * and any View reflecting the data set should refresh itself.
   */
  public void notifyDataSetChanged() {
  }
  
  public Cursor getCursor(){
    return mCursor;
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
  public int getCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean hasStableIds() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getItemViewType(int position) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getViewTypeCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean areAllItemsEnabled() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isEnabled(int position) {
    // TODO Auto-generated method stub
    return false;
  }
  
  public void changeCursor(Cursor cursor) {
    Cursor old = swapCursor(cursor);
    if (old != null) {
        old.close();
    }
}
}
