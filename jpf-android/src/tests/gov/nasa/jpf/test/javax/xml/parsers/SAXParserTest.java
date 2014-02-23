package gov.nasa.jpf.test.javax.xml.parsers;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.android.JPF_java_net_URL;
import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.vm.VM;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserTest extends TestJPF {

  public static class URLInjector extends ListenerAdapter {

    public void vmInitialized(VM vm) {
      JPF_java_net_URL.mapURLToFile("http://www.sun.ac.za/index.html",
          "src/tests/gov/nasa/jpf/test/javax/xml/parsers/index.html");
    }
  }

  @Test
  public void newURLTest() {
    if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.test.javax.xml.parsers.SAXParserTest$URLInjector")) {

      try {
        URL url = new URL("http://www.sun.ac.za/index.html");
        // get our data through the url class
        InputSource is = new InputSource(url.openStream());

        DefaultHandler handler = new TestHandler();
        SAXParser parser;
        parser = SAXParserFactory.newInstance().newSAXParser();

        // perform the parse
        parser.parse(is, handler);

      } catch (MalformedURLException e) {
        System.out.println(e.toString());
        assertTrue(false);
      } catch (ParserConfigurationException e) {
        System.out.println(e.toString());
        assertTrue(false);
      } catch (IOException e) {
        System.out.println(e.toString());
        assertTrue(true);
      } catch (SAXException e) {
        System.out.println(e.toString());
        assertTrue(false);
      }
    }

  }

  public static class TestHandler extends DefaultHandler {
    private StringBuffer builder;

    /**
     * Default Constructor for the feed
     */
    public TestHandler() {
    }

    public void startDocument() throws SAXException {
      builder = new StringBuffer();
      System.out.println("startDocument()");

    }

    public void endDocument() throws SAXException {
      System.out.println("endDocument()");
      System.out.println("builder:" + builder.toString());

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      super.characters(ch, start, length);
      builder.append(ch, start, length);

    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
        throws SAXException {
      System.out.print("startElement(): " + qName + " " + localName + " " + namespaceURI);
      if (atts != null) {
        System.out.print(" (");
        for (int i = 0; i < atts.getLength(); i++) {
          System.out.print(atts.getLocalName(i) + " : " + atts.getValue(i) + ", ");
        }
        System.out.println(" )");
      } else
        System.out.println();
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      System.out.println("endElement(): " + qName + " " + localName + " " + namespaceURI + " ");

    }

  }

  public static void main(String[] testMethods) {
    runTestsOfThisClass(testMethods);
    testDefaultHandler();
  }

  public static void testDefaultHandler() {
    DefaultHandler handlerD = new TestHandler();
    SAXParser parserD;
    try {
      parserD = SAXParserFactory.newInstance().newSAXParser();

      // perform the parse
      parserD.parse(new InputSource(new FileInputStream(
          "src/tests/gov/nasa/jpf/test/javax/xml/parsers/index.html")), handlerD);

    } catch (ParserConfigurationException e1) {
      System.out.println(e1.toString());
    } catch (SAXException e1) {
      System.out.println(e1.toString());
    } catch (FileNotFoundException e) {
      System.out.println(e.toString());
    } catch (IOException e) {
      System.out.println(e.toString());
    }
  }

}