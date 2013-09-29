package za.android.vdm.rssreader;

import za.android.vdm.rssreader.provider.DatabaseInterface;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Renders the list view of all RSSFeed items on screen in the timeline
 * Activity.
 * 
 * @author Heila van der Merwe
 * @date 25 May 2013
 * @version 2.0
 * 
 */
public class TimelineAdapter extends SimpleCursorAdapter {
  private static final String TAG = "TimelineAdapter";
  static final String[] FROM = { DatabaseInterface.C_TITLE, DatabaseInterface.C_PUB_DATE };//, "feed_title" };
  static final int[] TO = { R.id.textTitle, R.id.textPubDate };// , R.id.textFeedTitle };

  // Constructor
  public TimelineAdapter(Context context, Cursor c) {
    super(context, R.layout.row, c, FROM, TO);
    Log.d(TAG, "Constructor");
  }

  // This is where the actual binding of a cursor to view happens
  @Override
  public void bindView(View row, Context context, Cursor cursor) {
    super.bindView(row, context, cursor);
    Log.d(TAG, "bindView");

    // Manually bind created at timestamp to its view
    long timestamp = cursor.getLong(cursor.getColumnIndex(DatabaseInterface.C_PUB_DATE));
    TextView textCreatedAt = (TextView) row.findViewById(R.id.textPubDate);
   // textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));

    // Manually set feedname
    //		String feed = cursor.getString(cursor
    //				.getColumnIndex("feed_title"));
    //		TextView textFeedTitle = (TextView) row.findViewById(R.id.textFeedTitle);
    //		textFeedTitle.setText(feed);

    // Manually set Title Type
    String title = cursor.getString(cursor.getColumnIndex(DatabaseInterface.C_TITLE));
    TextView text = (TextView) row.findViewById(R.id.textTitle);
    text.setText(title.substring(0, 30) + "...");
  }

}