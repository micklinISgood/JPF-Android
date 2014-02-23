package gov.nasa.jpf.test.java.net;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.android.JPF_java_net_URL;
import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.vm.VM;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class URLTest extends TestJPF {
  
  public static class URLInjector extends ListenerAdapter {

    public void vmInitialized(VM vm) {
     JPF_java_net_URL.mapURLToFile("http://www.sun.ac.za/index.html", "src/tests/gov/nasa/jpf/test/java/net/index.html");
    }
  }
  
  @Test
  public void newURLTest() {
    if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.test.java.net.URLTest$URLInjector")) {

      try {
        URL url = new URL("http://www.sun.ac.za/index.html");
        InputStream is = url.openStream();
        InputStreamReader reader = new InputStreamReader(is);
        char[] contents = new char[15];
        int num = reader.read(contents);
        
        String s =  new String(contents);
        System.out.println(s);
        assertTrue(s.trim().equals("<html></html>"));
        
      } catch (MalformedURLException e) {
        System.out.println(e.toString());
        assertTrue(false);

      } catch (IOException e) {
        System.out.println(e.toString());
        assertTrue(true);
      }
    }

  }
  
  public static void main(String[] testMethods) {
    runTestsOfThisClass(testMethods);
  }
}
