package za.android.vdm.rssreader;

import za.android.vdm.rssreader.provider.DatabaseInterface;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Main application class that acts as interface from activities to the database connection.
 * 
 * @author Heila van der Merwe
 * @date 24 May 2013
 * @version 1.0
 */
public class RSSReaderApplication extends Application {
  /** Identifies the name of the class in debugging */
  private static final String TAG = RSSReaderApplication.class.getSimpleName();

//  /** Preferences saved on the phone */
//  private SharedPreferences prefs; // Saved preferences

  /** Database connection */
  private DatabaseInterface rssfeedDatabase;

  
  static int count =0;
  /**
   * This method is called only the first time the application is started
   */
  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "onCreate()" + count++);

//    // Setup the preferences from disk and register change listener
//    this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

    // Create new connection to DB
    rssfeedDatabase = new DatabaseInterface(getApplicationContext());
  }

  /**
   * Called just before the application terminates
   */
  @Override
  public void onTerminate() {
    super.onTerminate();
    Log.i(TAG, "onTerminated");
  }

  /**
   * 
   * @return
   */
  public DatabaseInterface getDatabase() {
    return rssfeedDatabase;
  }

  /**
   * Returns a reference to the shared saved preferences
   * 
   * @return
   */
  //  public SharedPreferences getPrefs() {
  //    return prefs;
  //
  //  }
}