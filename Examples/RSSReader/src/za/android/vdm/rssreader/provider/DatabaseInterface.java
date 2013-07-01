package za.android.vdm.rssreader.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Interface to the sqlite database. Never lets any class query the database
 * directly to protect the integrity of the database and avoid SQL injections.
 * 
 * @author Heila van der Merwe
 * @date 25 May 2013
 * @date 12 June 2013  
 *  Changed to version that can be checked using jpf-android
 * 
 * @version 3.0
 * 
 */
public class DatabaseInterface {
  private static final String TAG = DatabaseInterface.class.getSimpleName();

  static final int VERSION = 1;

  // name of the database
  static final String DATABASE = "feeds.db";

  // name of the table containing status updates
  public static final String TABLE_RSSFEED = "rssfeed";
  public static final String TABLE_RSSITEM = "rssitem";

  // columns in database
  public static final String C_ID = "_id";
  public static final String C_FEED_ID = "feed_id";
  public static final String C_TITLE = "title";
  public static final String C_LINK = "link";
  public static final String C_PUB_DATE = "pub_date";
  public static final String C_DESCRIPTION = "description";
  public static final String C_CATEGORY = "category";
  public static final String C_LAST_TIME_INSERTED = "last_time_inserted";

  // sql to create rssfeed table
  private static final String SQL_CREATE_TABLE_RSSFEEDS = "CREATE TABLE " + TABLE_RSSFEED + "(" + C_ID
      + "  INTEGER PRIMARY KEY AUTOINCREMENT, " + C_TITLE + " String, " + C_DESCRIPTION + " text, " + C_LINK
      + " String, " + C_PUB_DATE + " String," + C_LAST_TIME_INSERTED + " long" + ");";

  // sql to create rssitem table
  private static final String SQL_CREATE_TABLE_RSSITEMS = "CREATE TABLE " + TABLE_RSSITEM + "(" + C_ID
      + "  INTEGER PRIMARY KEY AUTOINCREMENT, " + C_TITLE + " String, " + C_DESCRIPTION + " text, " + C_LINK
      + " String, " + C_PUB_DATE + " long, " + C_FEED_ID + " int )"; /*+ "FOREIGN KEY(" + C_FEED_ID
      + ") REFERENCES " + TABLE_RSSFEED + "( " + C_ID + " )" + ");"; */

  /** Instance of Database manager */
  private final DatabaseManager databaseConnection;

  private Object dbLock;

  /**
   * Constructor of the database interface
   * 
   * @param context
   */
  public DatabaseInterface(Context context) {
    Log.i(TAG, "Initialising database");
    dbLock = new Object();
    this.databaseConnection = new DatabaseManager(context);
  }

  /**
   * Inserts new RSSFeed updates into the RSSItem table.
   * 
   * @param values
   *          List of RSSItems to insert
   */
  public void insertFeedItems(ContentValues values) {
    Log.i(TAG, "Inserting new RSSFeed items in db.");
    SQLiteDatabase db = null;
    synchronized (dbLock) {
      try {
        db = this.databaseConnection.getWritableDatabase();
        db.insertOrThrow(TABLE_RSSITEM, null, values);
      } catch (Exception e) {
        Log.i(TAG, "Could not insert new RSSItems: " + e.getMessage());
      }
    }
  }

  /**
   * Returns a Cursor to RSSItems ordered in decreasing creation time.
   * 
   * @return Cursor where the columns are: RSSItem's Title, RSSItem's
   *         publication date, the RSSFeed's title where the article was
   *         published.
   */
  public Cursor getRSSFeedUpdates() {
    Log.i(TAG, "Looking up the RSSFeed Items in the database.");

    SQLiteDatabase db = null;
    Cursor c = null;

    //		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
    //		builder.setTables(TABLE_RSSITEM + " LEFT JOIN " + TABLE_RSSFEED
    //				+ " ON ( " + TABLE_RSSITEM + "." + C_FEED_ID + "="
    //				+ TABLE_RSSFEED + "." + C_ID + ")");
    //
    //		synchronized (dbLock) {
    //			try {
    //				db = this.databaseConnection.getReadableDatabase();
    //				c = builder.query(db, new String[] {
    //				    TABLE_RSSITEM + "." + C_ID,
    //            TABLE_RSSITEM + "." + C_LINK,
    //				    TABLE_RSSITEM + "." + C_TITLE,
    //						TABLE_RSSITEM + "." + C_PUB_DATE,
    //						TABLE_RSSFEED + "." + C_TITLE + " as feed_title" },
    //						null, null, null, null, null);
    //			} catch (Exception e) {
    //				Log.d(TAG,
    //						"Error retriving RSSItems from the database: "
    //								+ e.getLocalizedMessage());
    //			}
    //		}
    //		return c;
    synchronized (dbLock) {
      try {
        db = this.databaseConnection.getReadableDatabase();
        c = db.query(TABLE_RSSITEM, null, null, null, null, null, C_PUB_DATE + " DESC");
      } catch (Exception e) {
        Log.d(TAG, "Error retriving data from database: " + e.getLocalizedMessage());
      }
    }
    return c;

  }

  /**
   * Returns a Cursor to the list of RSSFeed to which the app is subscribed.
   * 
   * @return Cursor
   */
  public Cursor getRSSFeeds() {
    Log.i(TAG, "getRSSFeeds()");
    
    Cursor c = null;
    SQLiteDatabase db = null;

    synchronized (dbLock) {
      try {
        db = this.databaseConnection.getReadableDatabase();
        c = db.query(TABLE_RSSFEED, null, null, null, null, null, C_PUB_DATE + " DESC");
        

      } catch (Exception e) {
        Log.i(TAG, "Error retriving data from database: " + e.toString());
      }
    }
    Log.i(TAG, "getRSSFeeds method returning " + c);
    return c;

  }

  /**
   * Updates the RSSFeed after getting updates from it. Updates its title,
   * description and the time of the last RSSItem in the database.
   * 
   * @param id
   * @param title
   * @param description
   * @param lastTimeInserted
   */
  public void updatefeed(int id, String title, String description, long lastTimeInserted) {
    Log.i(TAG, "InsertOrIgnore feed: " + title);
    SQLiteDatabase db = null;
    synchronized (dbLock) {
      try {
        db = this.databaseConnection.getWritableDatabase();
        db.execSQL("update rssfeed set title='" + title + "', last_time_inserted=" + lastTimeInserted
            + ", description='" + description + "' where _id = " + id + "; ");
      } catch (Exception e) {
        Log.d(TAG, "insertOrIgnore error");
      }
    }

  }

  /**
   * Clears the data in the table
   */
  public void clearData() {
    Log.d(TAG, "clearData method");
    synchronized (dbLock) {
      databaseConnection.onUpgrade(this.databaseConnection.getReadableDatabase(), 0, 0);
    }
  }

  /**
   * Closes the connection to the database.
   */
  public void close() {
    Log.d(TAG, "Closing database connection");
    this.databaseConnection.close();
  }

  /**
   * Manages the sqlite database and its tables.
   * 
   * @author Heila van der Merwe
   * @date 24 May 2013
   * @version 1.0
   * 
   */
  class DatabaseManager extends SQLiteOpenHelper {

    /**
     * Constructs the database
     * 
     * @param context
     */
    public DatabaseManager(Context context) {
      super(context, DATABASE, null, VERSION);
    }

    /**
     * Creates the tables "rss_feeds" and "rss_items" in the SQLite database
     * db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
      Log.d(TAG, "Creating database: " + DATABASE);
      db.execSQL(SQL_CREATE_TABLE_RSSFEEDS);
      db.execSQL(SQL_CREATE_TABLE_RSSITEMS);
      //			db.execSQL("insert into rssfeed (title, link,last_time_inserted, description) values ('title', 'http://feeds.feedburner.com/Mobilecrunch.rss',0, 'Description'); ");
      ContentValues value = new ContentValues();
      value.put("title", "TechCrunch");
      value.put("link", "http://feeds.feedburner.com/Mobilecrunch.rss");
      value.put("last_time_inserted", 0);
      value.put("description", "Mobile rss feed");
      db.insert(TABLE_RSSFEED, null, value);

    }

    /**
     * Drops the tables containing the rssfeeds and rssfeed items and
     * recreates it.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.d(TAG, "Recreating database: " + DATABASE);
      db.delete(TABLE_RSSITEM, null, null);
      db.delete(TABLE_RSSFEED, null, null);
    }
  }

}