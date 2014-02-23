package javax.xml.parsers;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import java.io.ByteArrayInputStream;

public class AndroidSAXParser extends SAXParser {
  DefaultHandler handler;

  private final int namespaceURI = 0;
  private final int localname = 1;
  private final int qname = 2;
  private final int attributes = 3;
  private final int content = 4;
  private final int nextNodeHash = 5;

  public AndroidSAXParser() {
  }

  public void parse(InputSource is, DefaultHandler theRssHandler) throws SAXException, IOException {
    //    InputStream inputStream = is.getByteStream();
    //    //Create BufferedReader object
    //    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
    //    StringBuffer sbfFileContents = new StringBuffer();
    //    String line = null;
    //
    //    //read file line by line
    //    while ((line = bReader.readLine()) != null) {
    //      sbfFileContents.append(line);
    //    }

    //finally convert StringBuffer object to String!



    int hash = parse(is.getByteStream());
    Log.i("SAXParserr", "Parsing url RSS feed at http://feeds.feedburner.com/Mobilecrunch.rss ");

    handler = theRssHandler;
    handler.startDocument();
    visit(hash);
    handler.endDocument();
  }

  public void visit(int nodeCode) throws SAXException {
    if (nodeCode == -1)
      return;
    String[] node = getNodeInfo(nodeCode);

    handler.startElement(node[namespaceURI], node[localname], node[qname], null);

    int[] children = getChildren(nodeCode);
    if (children != null) {
      for (int child : children) {
        visit(child);
      }
    }

    String contentString = node[content];
    if (contentString != null && contentString.length() > 0) {
      char[] contentArray = contentString.toCharArray();
      handler.characters(contentArray, 0, contentArray.length);
    }

    handler.endElement(node[namespaceURI], node[localname], node[qname]);
  }

  public native int parse(InputStream is);

  public native String[] getNodeInfo(int hash);

  public native int[] getChildren(int hash);

  public void parse(String filename, DefaultHandler handler) {
  }

  public void parse(InputStream is, DefaultHandler handler) {
  }

  @Override
  public Parser getParser() throws SAXException {
    return null;
  }

  @Override
  public XMLReader getXMLReader() throws SAXException {
    return null;
  }

  @Override
  public boolean isNamespaceAware() {
    return false;
  }

  @Override
  public boolean isValidating() {
    return false;
  }

  @Override
  public void setProperty(String name, Object value) throws SAXNotRecognizedException,
      SAXNotSupportedException {
    
  }

  @Override
  public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    return null;
  }

}
