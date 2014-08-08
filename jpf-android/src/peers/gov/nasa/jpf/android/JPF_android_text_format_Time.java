package gov.nasa.jpf.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.os.SystemClock;
import android.text.format.Time;
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

public class JPF_android_text_format_Time extends NativePeer {

  // Local variable per instance - this can also be made static?
  Calendar cal = Calendar.getInstance();

  // used to store to temporarily store these values of the Android Time class
  int isDst = -1;
  long gmtoff = 0;
  boolean allDay = false;

  /**
   * Sets the time of the given Time object to the current time.
   * 
   */
  @MJI
  public void setToNow(MJIEnv env, int objectRef) {
    cal.setTimeInMillis(env.currentTimeMillis());
    setTime(env, objectRef);

  }

  /**
   * Converts this time to milliseconds. Suitable for interacting with the
   * standard java libraries. The time is in UTC milliseconds since the epoch.
   * This does an implicit normalization to compute the milliseconds but does
   * <em>not</em> change any of the fields in this Time object. If you want
   * to normalize the fields in this Time object and also get the milliseconds
   * then use {@link #normalize(boolean)}.
   * 
   * <p>
   * If "ignoreDst" is false, then this method uses the current setting of the
   * "isDst" field and will adjust the returned time if the "isDst" field is
   * wrong for the given time. See the sample code below for an example of this.
   * 
   * <p>
   * If "ignoreDst" is true, then this method ignores the current setting of the
   * "isDst" field in this Time object and will instead figure out the correct
   * value of "isDst" (as best it can) from the fields in this Time object. The
   * only case where this method cannot figure out the correct value of the
   * "isDst" field is when the time is inherently ambiguous because it falls in
   * the hour that is repeated when switching from Daylight-Saving Time to
   * Standard Time.
   * 
   * <p>
   * Here is an example where <tt>toMillis(true)</tt> adjusts the time, assuming
   * that DST changes at 2am on Sunday, Nov 4, 2007.
   * 
   * <pre>
   * Time time = new Time();
   * time.set(4, 10, 2007);  // set the date to Nov 4, 2007, 12am
   * time.normalize();       // this sets isDst = 1
   * time.monthDay += 1;     // changes the date to Nov 5, 2007, 12am
   * millis = time.toMillis(false);   // millis is Nov 4, 2007, 11pm
   * millis = time.toMillis(true);    // millis is Nov 5, 2007, 12am
   * </pre>
   * 
   * <p>
   * To avoid this problem, use <tt>toMillis(true)</tt> after adding or
   * subtracting days or explicitly setting the "monthDay" field. On the other
   * hand, if you are adding or subtracting hours or minutes, then you should
   * use <tt>toMillis(false)</tt>.
   * 
   * <p>
   * You should also use <tt>toMillis(false)</tt> if you want to read back the
   * same milliseconds that you set with {@link #set(long)} or
   * {@link #set(Time)} or after parsing a date string.
   */
  @MJI
  public long toMillis(MJIEnv env, int objref, boolean ignoreDst) {
    // getTime retrieves the Time object's fields and sets the calendar 
    // to this time. When the calendar is set, the time is normalized 
    // automatically but we do not update the of the actual Time object.
    getTime(env, objref);

    if (ignoreDst) {
      /*
       * If "ignoreDst" is true, then this method ignores the current setting of
       * the "isDst" field in this Time object and will instead figure out the
       * correct value of "isDst" (as best it can) from the fields in this Time
       * object.
       */

    } else {
      /*
       * If "ignoreDst" is false, then this method uses the current setting of
       * the
       * "isDst" field and will adjust the returned time if the "isDst" field is
       * wrong for the given time.
       */

      //TODO I am not sure what on earth they want here...
    }

    // Calendar automatically determined if DST is active from Timezone if if can.

    //The time is in UTC milliseconds since the epoch.
    long time = cal.getTimeInMillis();

    return time;
  }

  /**
   * Sets the fields in this Time object given the UTC milliseconds. After
   * this method returns, all the fields are normalized.
   * This also sets the "isDst" field to the correct value.
   * 
   * @param millis
   *          the time in UTC milliseconds since the epoch.
   */
  @MJI
  public void set__L__V(MJIEnv env, int objref, long millis) {

    cal.setTimeInMillis(millis);
    setTime(env, objref);

  }

  /**
   * Format according to RFC 2445 DATETIME type.
   * 
   * return String
   * <p>
   * The same as format("%Y%m%dT%H%M%S").
   */
  @MJI
  public int format2445(MJIEnv env, int objectRef) {
    return env.newString("");
  }

  @MJI
  public boolean nativeParse3339(MJIEnv env, int objectRef, int refString) {
    return false;
  }

  /**
   * Parse a time in the current zone in YYYYMMDDTHHMMSS format.
   */
  @MJI
  public boolean nativeParse(MJIEnv env, int objectRef, int refString) {
    String dateString = env.getStringObject(refString);
    getTime(env, objectRef);

    SimpleDateFormat df = new SimpleDateFormat("YYYYMMDDTHHMMSS");

    Date result = null;
    try {
      result = df.parse(dateString);
    } catch (ParseException e) {
      env.throwException("java.text.ParseException", e.getMessage());
      return false;
    }
    // cal might have changed timezone 
    cal.setTime(result);
    setTime(env, objectRef);
    return true;
  }

  /**
   * Return the current time in YYYYMMDDTHHMMSS<tz> format
   */
  @MJI
  public int toString(MJIEnv env, int objectRef) {
    getTime(env, objectRef);
    return env.newString("");
  }

  @MJI
  public int format1(MJIEnv env, int objectRef, int refStringFormat) {
    String dateFormat = env.getStringObject(refStringFormat);

    getTime(env, objectRef);

    String result = String.format(dateFormat, cal);
    return env.newString(result);
  }

  @MJI
  public static int nativeCompare(MJIEnv env, int classRef, int timeRefA, int timeRefB) {

    //    Time a = env.getO
    //    
    //    Calendar c = Calendar.getInstance();
    //    c.compareTo(anotherCalendar);

    return MJIEnv.NULL;
  }

  /**
   * Ensures the values in each field are in range. For example if the
   * current value of this calendar is March 32, normalize() will convert it
   * to April 1. It also fills in weekDay, yearDay, isDst and gmtoff.
   * 
   * <p>
   * If "ignoreDst" is true, then this method sets the "isDst" field to -1 (the
   * "unknown" value) before normalizing. It then computes the correct value for
   * "isDst".
   * 
   * <p>
   * See {@link #toMillis(boolean)} for more information about when to use
   * <tt>true</tt> or <tt>false</tt> for "ignoreDst".
   * 
   * @return the UTC milliseconds since the epoch
   */
  @MJI
  public long normalize(MJIEnv env, int objectRef, boolean ignoreDst) {
    getTime(env, objectRef);

    setTime(env, objectRef);

    env.setIntField(objectRef, "isDst", (cal.getTimeZone().inDaylightTime(cal.getTime())) ? 1 : 0);

    return toMillis(env, objectRef, ignoreDst);
  }

  /**
   * Convert this time object so the time represented remains the same, but is
   * instead located in a different timezone. This method automatically calls
   * normalize() in some cases
   */
  @MJI
  public void switchTimezone(MJIEnv env, int objectRef, int timezoneStringRef) {
    getTime(env, objectRef);
    cal.setTimeZone(TimeZone.getTimeZone(env.getStringObject(timezoneStringRef)));
    setTime(env, objectRef);
  }

  /**
   * Sets the time fields of the JPF Time.java model from the native calendar
   * field.
   * 
   * @param env
   * @param objref
   */
  protected void setTime(MJIEnv env, int objref) {
    env.setReferenceField(objref, "timezone", env.newString(cal.getTimeZone().getID()));

    env.setIntField(objref, "year", cal.get(Calendar.YEAR));
    env.setIntField(objref, "month", cal.get(Calendar.MONTH));
    env.setIntField(objref, "monthDay", cal.get(Calendar.DAY_OF_MONTH));
    env.setIntField(objref, "hour", cal.get(Calendar.HOUR_OF_DAY));
    env.setIntField(objref, "minute", cal.get(Calendar.MINUTE));
    env.setIntField(objref, "second", cal.get(Calendar.SECOND));
    env.setIntField(objref, "weekDay", cal.get(Calendar.DAY_OF_WEEK) - 1);
    env.setIntField(objref, "yearDay", cal.get(Calendar.DAY_OF_YEAR) - 1);
    env.setIntField(objref, "isDst", isDst);
    env.setLongField(objref, "gmtoff", ((TimeZone) cal.getTimeZone()).getOffset(cal.getTimeInMillis()) / 1000);
    env.setBooleanField(objref, "allDay", allDay);

  }

  /**
   * Initializes the native calendar by getting the time fields of the JPF Time
   * class.
   * 
   * @param env
   * @param objref
   */
  protected void getTime(MJIEnv env, int objref) {
    // Timezone should not be empty - this is an error
    String timezone = env.getStringField(objref, "timezone");

    cal.setTimeZone(TimeZone.getTimeZone(timezone));

    // get time info
    int year = env.getIntField(objref, "year"); // set in constructor - will always be set
    int month = env.getIntField(objref, "month"); // not set in constructor - may not be set in this case make 1
    int date = env.getIntField(objref, "monthDay"); // set in constructor so will always be set
    int hourOfDay = env.getIntField(objref, "hour"); // not set in constructor might be null
    int minute = env.getIntField(objref, "minute");
    int second = env.getIntField(objref, "second");

    int weekDay = env.getIntField(objref, "weekDay");
    int yearDay = env.getIntField(objref, "yearDay");

    // set calendar to this time
    cal.set(year, month, date, hourOfDay, minute, second);

    isDst = env.getIntField(objref, "isDst"); // set to -1 in constructor
    gmtoff = env.getLongField(objref, "gmtoff");
    allDay = env.getBooleanField(objref, "allDay");

  }

}
