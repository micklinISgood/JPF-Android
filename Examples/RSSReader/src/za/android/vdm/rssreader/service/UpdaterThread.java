package za.android.vdm.rssreader.service;

import gov.nasa.jpf.annotation.Checkpoint;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import za.android.vdm.rssreader.RSSReaderApplication;
import za.android.vdm.rssreader.models.RSSFeed;
import za.android.vdm.rssreader.models.RSSItem;
import za.android.vdm.rssreader.models.RSSfeedXMLParser;
import za.android.vdm.rssreader.provider.DatabaseInterface;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

/**
 * This thread requests that the authenticated accounts download their new
 * status updates and insert them into the database.
 */
public class UpdaterThread extends Thread {
	private final String TAG = this.getClass().getSimpleName();

	/** Pointer to database interface */
	private DatabaseInterface database;
	private Context context;

	public UpdaterThread(Context context) {
		super("RSSUpdaterThread");
		this.context = context;
		this.database = ((RSSReaderApplication) context.getApplicationContext())
				.getDatabase();
	}

	@Checkpoint(value = "getUpdates", threadName = "RSSUpdaterThread")
	@Override
	public void run() {
		SAXParser parser = null;
		RSSfeedXMLParser theRssHandler = null;

		String link = null;
		URL url = null;
		InputSource is = null;
		List<RSSItem> items = null;
		long lastTimeInserted = 0;
		RSSFeed feed = null;

		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
			theRssHandler = new RSSfeedXMLParser();

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// get cursor of feeds
		Cursor c = database.getRSSFeeds();

		// loop through rss feeds
		while (c != null && c.moveToNext()) {

			try {

				// the String url of the rss
				link = c.getString(c.getColumnIndex("link"));

				// setup the url
				url = new URL(link);

				// get our data through the url class
				is = new InputSource(url.openStream());

				// perform the synchronous parse
				parser.parse(is, theRssHandler);

				// get the results - should be a fully populated RSSFeed
				// instance,
				// or null on error
				items = theRssHandler.getFeedItems();
				feed = theRssHandler.getFeed();
				Log.i(TAG, "Parsed " + items.size() + " items from RSSfeed "
						+ c.getString(c.getColumnIndex("title")));

				lastTimeInserted = parseRSSFeedUpdates(
						items,
						c.getInt(c.getColumnIndex(DatabaseInterface.C_ID)),
						c.getLong(c
								.getColumnIndex(DatabaseInterface.C_LAST_TIME_INSERTED)));

				if (lastTimeInserted > c
						.getLong(c
								.getColumnIndex(DatabaseInterface.C_LAST_TIME_INSERTED)))
					database.updatefeed(
							c.getInt(c.getColumnIndex(DatabaseInterface.C_ID)),
							feed.getTitle(), feed.getDescription(),
							lastTimeInserted);

			} catch (Exception e) {
				Log.e(TAG, "Could not retrieve rss items from " + link);
				e.printStackTrace();
			}
		}
		c.close();
		((RSSFeedUpdaterService) context).finishUpdate();
		System.out.println("FINSHEDSINISHEDFINISHEDFINISHED");
	}

	@Checkpoint(value = "parseFeedItems", threadName = "RSSUpdaterThread")
	private long parseRSSFeedUpdates(List<RSSItem> updates, int id,
			long lastTimeInserted) {
		// count the number of new posts
		int count = 0;

		// Create a Linked list containing the posts to insert in the db
		LinkedList<ContentValues> posts = new LinkedList<ContentValues>();
		ContentValues value;

		for (RSSItem item : updates) {
			value = new ContentValues();
			value.put(DatabaseInterface.C_TITLE, item.getTitle());
			value.put(DatabaseInterface.C_DESCRIPTION, item.getDescription());
			value.put(DatabaseInterface.C_LINK, item.getLink());
			value.put(DatabaseInterface.C_FEED_ID, id);

			long createdAt = item.getPubdate();
			value.put(DatabaseInterface.C_PUB_DATE, createdAt);

			// count number of new posts
			if (createdAt > lastTimeInserted) {
				count++;
				posts.add(value);
			}

		}
		if (posts.size() > 0) {
			Log.i(TAG, posts.size() + " new items in RSSFeed.");
			lastTimeInserted = posts.getFirst().getAsLong(
					DatabaseInterface.C_PUB_DATE);
			storeStatusUpdates(posts, count); // store the updates in the db
		}
		return lastTimeInserted;
	}

	/**
	 * Inserts the status updates passed as parameter into the database. It is
	 * synchronous to avoid more than one method trying to insert new updates
	 * into the database.
	 * 
	 * Then broadcasts intend RECEIVE_TIMELINE_NOTIFICATIONS if new message was
	 * received.
	 * 
	 * @param posts
	 * @param newposts
	 */
	@Checkpoint(value = "storeInDB", threadName = "RSSUpdaterThread")
	public synchronized void storeStatusUpdates(List<ContentValues> posts,
			long newposts) {
		Log.d(TAG, "Storing status updates");

		// insert posts into db
		for (ContentValues value : posts) {
			database.insertFeedItems(value);
		}

		// if new posts were found, notify TimelineActivity
		Log.d(TAG, "We have a new RSSFeed updates");
		Intent intent = new Intent(RSSFeedUpdaterService.NEW_STATUS_INTENT);
		intent.putExtra(RSSFeedUpdaterService.NEW_STATUS_EXTRA_COUNT, newposts);
		context.sendBroadcast(intent,
				RSSFeedUpdaterService.RECEIVE_TIMELINE_NOTIFICATIONS);
		Log.d(TAG, (posts.size() > 0) ? "Got feed updates: " + posts.size()
				: "No new item updates");
	}

}