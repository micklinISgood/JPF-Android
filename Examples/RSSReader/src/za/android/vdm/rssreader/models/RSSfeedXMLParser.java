package za.android.vdm.rssreader.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

// import android.util.Log;

public class RSSfeedXMLParser extends DefaultHandler {
  private static final String TAG = RSSfeedXMLParser.class.getSimpleName();

  RSSFeed feed;
  List<RSSItem> items;
  RSSItem tempItem;

  final int RSS_TITLE = 1;
  final int RSS_LINK = 2;
  final int RSS_DESCRIPTION = 3;
  final int RSS_PUBDATE = 4;
  private StringBuffer builder;
  int currentElement = 0;

  /**
   * Default Constructor for the feed
   */
  public RSSfeedXMLParser() {
  }

  public void startDocument() throws SAXException {
    feed = new RSSFeed();
    tempItem = null;
    items = new ArrayList<RSSItem>();
    builder = new StringBuffer();

  }

  public void endDocument() throws SAXException {

  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    super.characters(ch, start, length);
    builder.append(ch, start, length);

  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
      throws SAXException {
    builder = new StringBuffer();

    if (localName.equals("item")) {

      // create a new item
      tempItem = new RSSItem();
      return;
    }

  }

  public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    RSSBase base = null;
    if (tempItem == null)
      base = feed;
    else
      base = tempItem;

    if (localName.equalsIgnoreCase("title") && base.getTitle() == null) {
      base.setTitle(builder.toString());
    } else if (localName.equalsIgnoreCase("link") && base.getLink() == null) {
      if (base != null)
        base.setLink(builder.toString());
    } else if (localName.equalsIgnoreCase("description")) {
      base.setDescription(builder.toString());
    } else if (localName.equalsIgnoreCase("pubdate")) {
      base.setPubdate(convertDateToLong(builder.toString()));
    } else if (localName.equalsIgnoreCase("item")) {
      items.add(tempItem);
      tempItem = null;
    } else
      return;

  }

  /**
   * Converts the String pubdate of a feed to a long value
   * 
   * @param dateString
   * @return
   */
  public long convertDateToLong(String dateString) {
    SimpleDateFormat formatter;
    Date date = null;
    try {
      formatter = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
      date = (Date) formatter.parse(dateString);
    } catch (ParseException e) {
      Log.e(TAG, "Error parsing date");
    }
    return date.getTime();
  }

  /*
   * getFeed - this returns our feed when all of the parsing is complete
   */
  public RSSFeed getFeed() {
    return feed;
  }

  public List<RSSItem> getFeedItems() {
    return items;
  }

}