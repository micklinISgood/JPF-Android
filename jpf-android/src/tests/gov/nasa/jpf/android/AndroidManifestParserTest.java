package gov.nasa.jpf.android;

import gov.nasa.jpf.android.AndroidManifestParser.InvalidManifestException;
import gov.nasa.jpf.util.test.TestJPF;

import java.io.ByteArrayInputStream;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class AndroidManifestParserTest extends TestJPF {

  AndroidManifestParser parser;

  @Before
  public void setUp() {
    parser = AndroidManifestParser.getInstance();
  }

  /* *********************** Component tests ******************************* */

  @Test
  public void testParseActivity() {
    String pPackageName = "za.vdm.android.testapp";
    String s = "<manifest package=\"" + pPackageName + "\" >" + "<application>"
        + "<activity android:name=\"Activity1\"></activity>"
        + "<activity android:name=\"com.vdm.Activity2\"></activity>" + "</application>" + "</manifest>";

    try {
      AndroidManifest androidManifest = parser.parse(new ByteArrayInputStream(s.getBytes("UTF-8")));

      assertEquals(2, androidManifest.getPackageInfo().activities.length);
      assertEquals("Activity1", androidManifest.getPackageInfo().activities[0].name);
      assertEquals("com.vdm.Activity2", androidManifest.getPackageInfo().activities[1].name);
    } catch (Exception e) {
      assertEquals(true, false);
    }
  }

  @Test
  public void testParseActivityWithoutName() {
    String pPackageName = "za.vdm.android.testapp";
    String s = "<manifest package=\"" + pPackageName + "\" >" + "<application>"
        + "<activity android:name=\"Activity1\"></activity>" + "<activity></activity>" + "</application>"
        + "</manifest>";

    try {
      AndroidManifest androidManifest = parser.parse(new ByteArrayInputStream(s.getBytes("UTF-8")));
    } catch (SAXException e) {
      assertTrue(true);
      return;
    } catch (Exception e) {
    }

    fail("Required Activity Name atrtibute not specified.");

  }

  @Test
  public void testParseService() {
    // TODO
  }

  @Test
  public void testParseProvider() {
    // TODO
  }

  @Test
  public void testParseReceiver() {
    // TODO
  }

  @Test
  public void testParseApplication() {
    // TODO
  }

  @Test
  public void testParseIntentFilter() {
    // TODO
  }

  @Test
  public void testParseData() {
    // TODO
  }

  @Test
  public void testParseAction() {
    // TODO
  }

  @Test
  public void testParseCategory() {
    // TODO
  }

  /**
   * Tests that the name of the package of the application is polulated correctly into the packageinfo.
   */
  @Test
  public void testParseManifest() {
    String pPackageName = "za.vdm.android.testapp";
    String s = "<manifest package=\"" + pPackageName + "\" >" + "<application></application></manifest>";

    try {
      AndroidManifest androidManifest = parser.parse(new ByteArrayInputStream(s.getBytes("UTF-8")));
      assertEquals(pPackageName, androidManifest.getPackageInfo().packageName);
    } catch (Exception e) {
      fail();
    }
  }

  /* *********************** Other tests ******************************* */

  /**
   * Tests that a name without a package prefix is parsed correctly for example "Activity1"
   */
  //@Test
//  public void testParseName() {
//    String sParam = "Activity1";
//    String sPackage = "za.vdm.android";
//
//    parser.getPackageInfo().packageName = sPackage;
//    AttributesImpl att = new AttributesImpl();
//    att.addAttribute("", "", "android:name", "", sParam);
//
//    try {
//      parser.parseName(att);
//    } catch (InvalidManifestException e) {
//      fail(e.getLocalizedMessage());
//    }
//    assertEquals(parser.getComponent().name, sParam);
//    assertEquals(parser.getComponent().packageName, sPackage);
//
//  }

  /**
   * Tests that a name without a package prefix is parsed correctly for example ".Activity1"
   */
//  @Test
//  public void testParseDotName() {
//    String sParam = ".Activity1";
//    String sPackage = "za.vdm.android";
//
//    parser.getPackageInfo().packageName = sPackage;
//    AttributesImpl att = new AttributesImpl();
//    att.addAttribute("", "", "android:name", "", sParam);
//
//    try {
//      parser.parseName(att);
//    } catch (InvalidManifestException e) {
//      fail(e.getLocalizedMessage());
//    }
//    assertEquals(parser.getComponent().name, sParam);
//    assertEquals(parser.getComponent().packageName, sPackage);
//
//  }

  /**
   * Tests that a full qualified component name is parsed correctly for example "za.vdm.android.Activity1"
   */
  // @Test
  // public void testParseFullName() {
  // String sParam = "com.android.Activity1";
  // String sPackage = "za.vdm.android";
  // parser.getPackageInfo().packageName = sPackage;
  // AttributesImpl att = new AttributesImpl();
  // att.addAttribute("", "", "android:name", "", sParam);
  //
  // try {
  // parser.parseName(att);
  // } catch (InvalidManifestException e) {
  // fail(e.getLocalizedMessage());
  // }
  // assertEquals(parser.getComponent().name, sParam);
  // assertEquals(parser.getComponent().packageName, "za.vdm.android");
  //
  // }

  /* **************************** parseString() tests ************************** */

  /**
   * Tests that the a String attribute that is not required, not null and length > 0 is returned by the parser
   * and that no exception is thrown or the default value returned by mistake.
   */
  @Test
  public void testParseString() {
    String sParam = "btnSayHallo";
    String rParam = null;
    try {
      rParam = parser.parseString(sParam, "Hallo", false);
    } catch (InvalidManifestException e) {
      fail(e.getLocalizedMessage());
    }
    assertEquals(sParam, rParam);
  }

  /**
   * Tests that the a String attribute that is required, not null and length > 0 is returned by the parser and
   * that no exception is thrown or the default value returned by mistake.
   */
  @Test
  public void testParseRequiredString() {
    String sParam = "btnSayHallo";
    String rParam = null;
    try {
      rParam = parser.parseString(sParam, "Hallo", true);
    } catch (InvalidManifestException e) {
      fail(e.getLocalizedMessage());
    }
    assertEquals(sParam, rParam);
  }

  /**
   * Tests that <code>parseString()</code> returns the default value when a null or length < 1 String
   * attribute that is not required is given as argument to the method.
   */
  @Test
  public void testParseEmptyString() {
    String sParam = "";
    String rParam = null;
    try {
      rParam = parser.parseString(sParam, "Hallo", false);
    } catch (InvalidManifestException e) {
      fail(e.getLocalizedMessage());
    }
    assertEquals(rParam, "Hallo");
  }

  /**
   * Tests that <code>InvalidManifestException</code> is thrown when a null or length < 1 String attribute
   * that is required is given as argument to the <code>parseString()</code> method.
   */
  @Test
  public void testParseEmptyRequiredString() {
    String sParam = "";
    String rParam = null;
    try {
      rParam = parser.parseString(sParam, "Hallo", true);
    } catch (InvalidManifestException e) {
      assertTrue(true);
      return;
    }
    fail("Should throw InvalidManifestException as required String attribute has not been set");
  }

  /**
   * Tests that a <code>InvalidManifestException</code> is not thrown when a null or length < 1 String
   * attribute that is not required is given as argument to the <code>parseString()</code> method and the
   * default value that has to be returned is null.
   */
  @Test
  public void testParseEmptyDefaultString() {
    String rParam = null;
    try {
      rParam = parser.parseString("", null, false);
    } catch (InvalidManifestException e) {
      fail("Empty default value is acceptable");
    }

    try {
      rParam = parser.parseString(null, "", false);
    } catch (InvalidManifestException e) {
      fail("Empty default value is acceptable");
    }

    assertTrue(true);

  }

  /* **************************** parseInt() tests ************************** */

  /**
   * Tests that the an integer attribute that is not required, not null and length > 0 is returned by the
   * parser and that no exception is thrown or the default value returned by mistake.
   */
  @Test
  public void testParseInt() {
    int sParam = 3;
    int rParam = -1;
    try {
      rParam = parser.parseInt(String.valueOf(sParam), 5, false);
    } catch (InvalidManifestException e) {
      fail(e.getLocalizedMessage());
    }
    assertEquals(sParam, rParam);
  }

  /**
   * Tests that the an integer attribute that is required, not null and length > 0 is returned by the parser
   * and that no exception is thrown or the default value returned by mistake.
   */
  @Test
  public void testParseRequiredInt() {
    int sParam = 3;
    int rParam = -1;
    try {
      rParam = parser.parseInt(String.valueOf(sParam), 5, true);
    } catch (InvalidManifestException e) {
      fail(e.getLocalizedMessage());
    }
    assertEquals(sParam, rParam);
  }

  /**
   * Tests that <code>parseInt()</code> returns the default value when a null or length < 1 integer attribute
   * that is not required is given as argument to the method.
   */
  @Test
  public void testParseEmptyInt() {
    String sParam = "";
    int dParam = 5;
    int rParam = -1;
    try {
      rParam = parser.parseInt(sParam, dParam, false);
    } catch (InvalidManifestException e) {
      fail(e.getLocalizedMessage());
    }
    assertEquals(rParam, 5);
  }

  /**
   * Tests that <code>InvalidManifestException</code> is thrown when a null or length < 1 integer attribute
   * that is required is given as argument to the <code>parseInt()</code> method.
   */
  @Test
  public void testParseEmptyRequiredInt() {
    String sParam = "";
    int dParam = 5;
    int rParam = -1;
    try {
      rParam = parser.parseInt(sParam, dParam, true);
    } catch (InvalidManifestException e) {
      assertTrue(true);
      return;
    }
    fail("Should throw InvalidManifestException as required integer attribute not set");
  }

  /* **************************** parseBoolean() tests ************************** */

  /**
   * Tests that the a boolean attribute that is not required, not null and length > 0 is returned by the
   * parser and that no exception is thrown or the default value returned by mistake.
   */
  @Test
  public void testParseBoolean() {
    boolean sParam = true;
    boolean rParam = false;
    try {
      rParam = parser.parseBoolean(String.valueOf(sParam), false, false);
    } catch (InvalidManifestException e) {
      fail(e.getLocalizedMessage());
    }
    assertEquals(sParam, rParam);
  }

  /**
   * Tests that the a boolean attribute that is required, not null and length > 0 is returned by the parser
   * and that no exception is thrown or the default value returned by mistake.
   */
  @Test
  public void testParseRequiredBoolean() {
    boolean sParam = true;
    boolean rParam = false;
    try {
      rParam = parser.parseBoolean(String.valueOf(sParam), false, true);
    } catch (InvalidManifestException e) {
      fail(e.getLocalizedMessage());
    }
    assertEquals(sParam, rParam);
  }

  /**
   * Tests that <code>parseBoolean()</code> returns the default value when a null or length < 1 boolean
   * attribute that is not required is given as argument to the method.
   */
  @Test
  public void testParseEmptyBoolean() {
    String sParam = "";
    boolean dParam = true;
    boolean rParam = false;
    try {
      rParam = parser.parseBoolean(sParam, dParam, false);
    } catch (InvalidManifestException e) {
      fail(e.getLocalizedMessage());
    }
    assertEquals(rParam, dParam);
  }

  /**
   * Tests that <code>InvalidManifestException</code> is thrown when a null or length < 1 boolean attribute
   * that is required is given as argument to the <code>parseBoolean()</code> method.
   */
  @Test
  public void testParseEmptyRequiredBoolean() {
    String sParam = "";
    boolean dParam = true;
    boolean rParam = false;
    try {
      rParam = parser.parseBoolean(sParam, dParam, true);
    } catch (InvalidManifestException e) {
      assertTrue(true);
      return;
    }
    fail("Should throw InvalidManifestException as required boolean attribute not set");
  }

  public static void main(String testMethods[]) throws Throwable {
    runTestsOfThisClass(testMethods);
  }
}
