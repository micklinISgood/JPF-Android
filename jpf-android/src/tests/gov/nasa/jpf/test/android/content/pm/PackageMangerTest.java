package gov.nasa.jpf.test.android.content.pm;

import gov.nasa.jpf.util.test.TestJPF;
import junit.framework.Assert;

import org.junit.Test;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PackageMangerTest extends TestJPF {

  @Test
  public void testPackageInfo() {
    if (verifyNoPropertyViolation()) {
      String pPackageName = "za.vdm.android.testapp";
      String pActivity1 = "<activity android:name=\"com.example.broadcast.MainActivity\" android:label=\"@string/app_name\" />";
      String pActivity2 = "<activity android:name=\"com.example.broadcast.MainActivity2\" android:label=\"@string/app_name\" />";
      String s = "<manifest package=\"" + pPackageName + "\" >" + "<application>" + pActivity1 + pActivity2
          + "</application></manifest>";
      PackageInfo pi = null;
      try {
        PackageManager pm = new PackageManager(s);
        System.out.println("****************" + pm.packageInfo.activities[0].name);

        pi = pm.getPackageInfo("", 0);
        // Assert.assertEquals(pPackageName, pi.packageName);
        return;
      } catch (Exception e) {
        // Assert.fail();
      }
    }
    // Assert.fail();
  }

  @Test
  public void testApplicationInfo() {
    // String s = "<manifest package=\"za.vdm.android.testapp\" >" + "<application></application>";
    // if (verifyNoPropertyViolation("stream=" + s)) {
    // PackageManager pm = new PackageManager();
    // try {
    // ApplicationInfo info = pm.getApplicationInfo("", 0);
    // } catch (NameNotFoundException e) {
    // }
    //
    // }
  }

  @Test
  public void testGetActivityInfo() {
    Assert.assertTrue(false);
  }

  @Test
  public void testResolveActivity() {
    Assert.assertTrue(false);

  }

  @Test
  public void testGetServiceInfo() {
    Assert.assertTrue(false);

  }

  @Test
  public void testResolveServiceInfo() {
    Assert.assertTrue(false);

  }

}
