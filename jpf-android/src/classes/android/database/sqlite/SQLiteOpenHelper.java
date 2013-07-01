package android.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public abstract class SQLiteOpenHelper {
  private static final String TAG = SQLiteOpenHelper.class.getSimpleName();

  private final Context mContext;
  private final String mName;
  private final int mNewVersion;

  private SQLiteDatabase mDatabase = null;
  private boolean mIsInitializing = false;

  public SQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
    if (version < 1)
      throw new IllegalArgumentException("Version must be >= 1, was " + version);
    mContext = context;
    mName = name;
    mNewVersion = version;
    mDatabase = new SQLiteDatabase();
    onCreate(mDatabase);
  }

  /**
   * Return the name of the SQLite database being opened, as given tp
   * the constructor.
   */
  public String getDatabaseName() {
    return mName;
  }

  /**
   * Create and/or open a database that will be used for reading and writing.
   * The first time this is called, the database will be opened and
   * {@link #onCreate}, {@link #onUpgrade} and/or {@link #onOpen} will be
   * called.
   * 
   * <p>
   * Once opened successfully, the database is cached, so you can call this
   * method every time you need to write to the database. (Make sure to call
   * {@link #close} when you no longer need the database.) Errors such as bad
   * permissions or a full disk may cause this method to fail, but future
   * attempts may succeed if the problem is fixed.
   * </p>
   * 
   * <p class="caution">
   * Database upgrade may take a long time, you should not call this method from
   * the application main thread, including from
   * {@link android.content.ContentProvider#onCreate ContentProvider.onCreate()}.
   * 
   * @throws SQLiteException
   *           if the database cannot be opened for writing
   * @return a read/write database object valid until {@link #close} is called
   */
  public synchronized SQLiteDatabase getWritableDatabase() {
    return mDatabase;
  }

  /**
   * Create and/or open a database. This will be the same object returned by
   * {@link #getWritableDatabase} unless some problem, such as a full disk,
   * requires the database to be opened read-only. In that case, a read-only
   * database object will be returned. If the problem is fixed, a future call
   * to {@link #getWritableDatabase} may succeed, in which case the read-only
   * database object will be closed and the read/write object will be returned
   * in the future.
   * 
   * <p class="caution">
   * Like {@link #getWritableDatabase}, this method may take a long time to
   * return, so you should not call it from the application main thread,
   * including from {@link android.content.ContentProvider#onCreate
   * ContentProvider.onCreate()}.
   * 
   * @throws SQLiteException
   *           if the database cannot be opened
   * @return a database object valid until {@link #getWritableDatabase} or
   *         {@link #close} is called.
   */
  public synchronized SQLiteDatabase getReadableDatabase() {
    return mDatabase;
  }

  /**
   * Close any open database object.
   */
  public synchronized void close() {
  }

  /**
   * Called when the database is created for the first time. This is where the
   * creation of tables and the initial population of the tables should happen.
   * 
   * @param db
   *          The database.
   */
  public abstract void onCreate(SQLiteDatabase db);

  /**
   * Called when the database needs to be upgraded. The implementation
   * should use this method to drop tables, add tables, or do anything else it
   * needs to upgrade to the new schema version.
   * 
   * <p>
   * The SQLite ALTER TABLE documentation can be found <a
   * href="http://sqlite.org/lang_altertable.html">here</a>. If you add new
   * columns you can use ALTER TABLE to insert them into a live table. If you
   * rename or remove columns you can use ALTER TABLE to rename the old table,
   * then create the new table and then populate the new table with the contents
   * of the old table.
   * 
   * @param db
   *          The database.
   * @param oldVersion
   *          The old database version.
   * @param newVersion
   *          The new database version.
   */
  public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

  /**
   * Called when the database needs to be downgraded. This is stricly similar to
   * onUpgrade() method, but is called whenever current version is newer than
   * requested one.
   * However, this method is not abstract, so it is not mandatory for a customer
   * to
   * implement it. If not overridden, default implementation will reject
   * downgrade and
   * throws SQLiteException
   * 
   * @param db
   *          The database.
   * @param oldVersion
   *          The old database version.
   * @param newVersion
   *          The new database version.
   */
  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    throw new SQLiteException("Can't downgrade database from version " + oldVersion + " to " + newVersion);
  }

  /**
   * Called when the database has been opened. The implementation
   * should check {@link SQLiteDatabase#isReadOnly} before updating the
   * database.
   * 
   * @param db
   *          The database.
   */
  public void onOpen(SQLiteDatabase db) {
    mDatabase = new SQLiteDatabase();
  }

}
