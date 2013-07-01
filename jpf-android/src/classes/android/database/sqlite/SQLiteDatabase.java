package android.database.sqlite;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class SQLiteDatabase {
  private final String TAG = SQLiteDatabase.this.getClass().getSimpleName();
  private static final int SELECT = 0;
  private static final int INSERT = 1;
  private static final int UPDATE = 2;

  public class CursorFactory {

  }

  /**
   * Stores all the ContentValues in each table
   */
  Map<String, LinkedList<ContentValues>> table = new HashMap<String, LinkedList<ContentValues>>();

  public long insertOrThrow(String tableName, String nullColumnHack, ContentValues values)
      throws SQLException {
    String title = (String) values.get("title");
    Log.i(TAG, "Inserting value " + ((title == null)? null:(title.length()>10)?title.substring(0,11):title) + " into " + tableName);
    LinkedList<ContentValues> val = table.get(tableName);
    if (val == null) {
      val = new LinkedList<ContentValues>();
    }
    values.put("_id", val.size());
    val.add(values);
    table.put(tableName, val);
    return val.size();
  }

  public long insert(String tableName, String nullColumnHack, ContentValues values) {
    String title = (String) values.get("title");

    Log.i(TAG, "Inserting value " + ((title == null)? null:(title.length()>10)?title.substring(0,11):title) + " into " + tableName);

    LinkedList<ContentValues> val = table.get(tableName);
    if (val == null) {
      val = new LinkedList<ContentValues>();
    }
    values.put("_id", val.size());
    val.add(values);
    table.put(tableName, val);
    return val.size();
  }

  public Cursor query(String tableName, String[] columns, String selection, String[] selectionArgs,
                      String groupBy, String having, String orderBy) {
    Log.i(TAG, "Querying " + columns + " from " + tableName);

    //get the values in the table
    LinkedList<ContentValues> val = table.get(tableName);

    //filter the values in the table
    
    //i the columns parameter is empty, get all columns
    if (columns == null) {
      columns = getColumns(tableName);
      Log.i(TAG, "Gotten all Columns: " + Arrays.toString(columns));
    }
    
    // if the table does not exists
    return new AndroidCursor(val, columns);

  }

  public void execSQL(String string) {
    Log.i(TAG, "Executing SQL " + string);
    int type = parseSQL(string);
    switch (type) {
    case INSERT:
      //get table
      //get columns
      //get value
    case SELECT:

    default:

    }
  }

  private native String[] getColumns(String tablename);

  private native int parseSQL(String sql);

  public void delete(String tableRssitem, Object object, Object object2) {

  }

}
