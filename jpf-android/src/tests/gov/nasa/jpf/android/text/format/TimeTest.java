package gov.nasa.jpf.android.text.format;

import java.util.TimeZone;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

import android.text.format.Time;

public class TimeTest extends TestJPF {

  @Test
  public void testDefaultConstructor() {
    if (verifyNoPropertyViolation()) {
      Time t = new Time("GMT+2");

      //System.out.println(t.year);
      assertEquals(1970, t.year);

      //System.out.println(t.timezone);
      assertEquals("GMT+2", t.timezone);

      //System.out.println(t.month);
      assertEquals(0, t.month);

      //System.out.println(t.monthDay);
      assertEquals(1, t.monthDay);

      //System.out.println(t.hour);
      assertEquals(0, t.hour);

      //System.out.println(t.minute);
      assertEquals(0, t.minute);

      //System.out.println(t.second);
      assertEquals(0, t.second);

      //System.out.println(t.weekDay);
      assertEquals(0, t.weekDay);

      //System.out.println(t.yearDay);
      assertEquals(0, t.yearDay);

      //System.out.println(t.gmtoff);
      assertEquals(0, t.gmtoff);

      //System.out.println(t.isDst);
      assertEquals(-1, t.isDst);
    }

  }

  @Test
  public void testConstructor() {
    if (verifyNoPropertyViolation()) {
      Time t = new Time(TimeZone.getTimeZone("US/Alaska").getID());

      //System.out.println(t.year);
      assertEquals(1970, t.year);

      //System.out.println(t.timezone);
      assertEquals("US/Alaska", t.timezone);

      //System.out.println(t.month);
      assertEquals(0, t.month);

      //System.out.println(t.monthDay);
      assertEquals(1, t.monthDay);

      //System.out.println(t.hour);
      assertEquals(0, t.hour);

      //System.out.println(t.minute);
      assertEquals(0, t.minute);

      //System.out.println(t.second);
      assertEquals(0, t.second);

      //System.out.println(t.weekDay);
      assertEquals(0, t.weekDay);

      //System.out.println(t.yearDay);
      assertEquals(0, t.yearDay);

      //System.out.println(t.gmtoff);
      assertEquals(0, t.gmtoff);

      //System.out.println(t.isDst);
      assertEquals(-1, t.isDst);
    }

  }

  @Test
  public void testTimeNormailizeTrue() {
    if (verifyNoPropertyViolation()) {
      Time t = new Time(TimeZone.getTimeZone("US/Alaska").getID());
      t.normalize(true);

      //System.out.println(t.year);
      assertEquals(1970, t.year);

      //System.out.println(t.timezone);
      assertEquals("US/Alaska", t.timezone);

      //System.out.println(t.month);
      assertEquals(0, t.month);

      //System.out.println(t.monthDay);
      assertEquals(1, t.monthDay);

      //System.out.println(t.hour);
      assertEquals(0, t.hour);

      //System.out.println(t.minute);
      assertEquals(0, t.minute);

      //System.out.println(t.second);
      assertEquals(0, t.second);

      //System.out.println(t.weekDay);
      assertEquals(4, t.weekDay);

      //System.out.println(t.yearDay);
      assertEquals(0, t.yearDay);

      //System.out.println(t.gmtoff);
      assertEquals(-36000, t.gmtoff);

      //System.out.println(t.isDst);
      assertEquals(0, t.isDst);
    }

  }

  @Test
  public void testTimeNormailizeFalse() {
    if (verifyNoPropertyViolation()) {
      Time t = new Time(TimeZone.getTimeZone("US/Alaska").getID());

      t.normalize(false);

      //System.out.println(t.year);
      assertEquals(1970, t.year);

      //System.out.println(t.timezone);
      assertEquals("US/Alaska", t.timezone);

      //System.out.println(t.month);
      assertEquals(0, t.month);

      //System.out.println(t.monthDay);
      assertEquals(1, t.monthDay);

      //System.out.println(t.hour);
      assertEquals(0, t.hour);

      //System.out.println(t.minute);
      assertEquals(0, t.minute);

      //System.out.println(t.second);
      assertEquals(0, t.second);

      //System.out.println(t.weekDay);
      assertEquals(4, t.weekDay);

      //System.out.println(t.yearDay);
      assertEquals(0, t.yearDay);

      //System.out.println(t.gmtoff);
      assertEquals(-36000, t.gmtoff);

      //System.out.println(t.isDst);
      assertEquals(0, t.isDst);
    }

  }

  @Test
  public void testDSTWithoutNormalize() {
    if (verifyNoPropertyViolation()) {
      Time t = new Time(TimeZone.getTimeZone("US/Alaska").getID());
      t.set(32, 4, 1970);

      //System.out.println(t.year);
      assertEquals(1970, t.year);

      //System.out.println(t.timezone);
      assertEquals("US/Alaska", t.timezone);

      //System.out.println(t.month);
      assertEquals(4, t.month);

      //System.out.println(t.monthDay);
      assertEquals(32, t.monthDay);

      //System.out.println(t.hour);
      assertEquals(0, t.hour);

      //System.out.println(t.minute);
      assertEquals(0, t.minute);

      //System.out.println(t.second);
      assertEquals(0, t.second);

      //System.out.println(t.weekDay);
      assertEquals(4, t.weekDay);

      //System.out.println(t.yearDay);
      assertEquals(0, t.yearDay);

      //System.out.println(t.gmtoff);
      assertEquals(-36000, t.gmtoff);

      //System.out.println(t.isDst);
      assertEquals(0, t.isDst);
    }

  }

  @Test
  public void testDSTNormalizeTrue() {
    if (verifyNoPropertyViolation()) {
      Time t = new Time(TimeZone.getTimeZone("US/Alaska").getID());
      t.set(32, 4, 1970);
      t.normalize(true);

      //System.out.println(t.year);
      assertEquals(1970, t.year);

      //System.out.println(t.timezone);
      assertEquals("US/Alaska", t.timezone);

      //System.out.println(t.month);
      assertEquals(5, t.month);

      //System.out.println(t.monthDay);
      assertEquals(1, t.monthDay);

      //System.out.println(t.hour);
      assertEquals(0, t.hour);

      //System.out.println(t.minute);
      assertEquals(0, t.minute);

      //System.out.println(t.second);
      assertEquals(0, t.second);

      //System.out.println(t.weekDay);
      assertEquals(4, t.weekDay);

      //System.out.println(t.yearDay);
      assertEquals(0, t.yearDay);

      //System.out.println(t.gmtoff);
      assertEquals(-36000, t.gmtoff);

      //System.out.println(t.isDst);
      assertEquals(0, t.isDst);
    }

  }
  @Test
  public void testDSTNormalizeFalse() {
    if (verifyNoPropertyViolation()) {
      Time t = new Time(TimeZone.getTimeZone("US/Alaska").getID());
      t.set(32, 4, 1970);
      t.normalize(false);
      
      //System.out.println(t.year);
      assertEquals(1970, t.year);

      //System.out.println(t.timezone);
      assertEquals("US/Alaska", t.timezone);

      //System.out.println(t.month);
      assertEquals(5, t.month);

      //System.out.println(t.monthDay);
      assertEquals(1, t.monthDay);

      //System.out.println(t.hour);
      assertEquals(0, t.hour);

      //System.out.println(t.minute);
      assertEquals(0, t.minute);

      //System.out.println(t.second);
      assertEquals(0, t.second);

      //System.out.println(t.weekDay);
      assertEquals(0, t.weekDay);

      //System.out.println(t.yearDay);
      assertEquals(0, t.yearDay);

      //System.out.println(t.gmtoff);
      assertEquals(-36000, t.gmtoff);

      //System.out.println(t.isDst);
      assertEquals(0, t.isDst);
    }

  }

}
