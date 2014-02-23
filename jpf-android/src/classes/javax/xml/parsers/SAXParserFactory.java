package javax.xml.parsers;

public class SAXParserFactory {

  public static SAXParserFactory newInstance() {
    return new SAXParserFactory();
  }

  public SAXParser newSAXParser() throws ParserConfigurationException {
    return new AndroidSAXParser();
  }
}
