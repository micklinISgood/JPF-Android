package gov.nasa.jpf.test.android.content.pm;

import gov.nasa.jpf.util.test.TestJPF;

public class PackageManagerModelTest extends TestJPF {

//  @Test
//  public void testPackageInfo2() {
//    if (verifyNoPropertyViolation()) {
//      String pPackageName = "za.vdm.android.testapp";
//      String pActivity1 = "<activity android:name=\"com.example.broadcast.MainActivity\" android:label=\"@string/app_name\" />";
//      String pActivity2 = "<activity android:name=\"com.example.broadcast.MainActivity2\" android:label=\"@string/app_name\" />";
//
//      String s = "<manifest package=\"" + pPackageName + "\" >" + "<application>" + pActivity1 + pActivity2
//          + "</application></manifest>";
//
//      PackageInfo pi = null;
//      try {
//        PackageManager pm = new PackageManager(s);
//        pi = pm.getPackageInfo("", 0);
//        System.out.println(pi.packageName + " " + pPackageName);
//        assert pPackageName.equals(pi.packageName);
//      } catch (NameNotFoundException e) {
//        assert false;
//      }
//    }
//
//  }
//
//  @Test
//  public void testNoPackageInfo2() {
//    if (verifyNoPropertyViolation()) {
//      String pActivity1 = "<activity android:name=\"com.example.broadcast.MainActivity\" android:label=\"@string/app_name\" />";
//      String pActivity2 = "<activity android:name=\"com.example.broadcast.MainActivity2\" android:label=\"@string/app_name\" />";
//      String s = "<manifest>" + "<application>" + pActivity1 + pActivity2 + "</application></manifest>";
//      PackageInfo pi = null;
//      PackageManager pm = new PackageManager(s);
//      try {
//        pi = pm.getPackageInfo("", 0);
//      } catch (NameNotFoundException e) {
//        assert false;
//      }
//      assert pi == null : "Exception was expected";
//    }
//  }
//
//  @Test
//  public void testApplicationInfo2() {
//    String pPackageName = "za.vdm.android.testapp";
//    String s = "<manifest package=\"" + pPackageName + "\" >"
//        + "<application android:name=\"za.android.vdm.CustomAppClass\">" + "</application></manifest>";
//
//    if (verifyNoPropertyViolation()) {
//      PackageManager pm = new PackageManager(s);
//      try {
//        PackageInfo pi = pm.getPackageInfo();
//        ApplicationInfo info = pi.applicationInfo;
//        assert info != null;
//        assert info.packageName.equals(pPackageName);
//        assert info.name.equals("za.android.vdm.CustomAppClass");
//
//        info = pm.getApplicationInfo("");
//        assert info != null;
//        assert info.packageName.equals(pPackageName);
//        assert info.name.equals("za.android.vdm.CustomAppClass");
//
//      } catch (Exception e) {
//        assert false : "ApplicationInfo should not be null";
//      }
//    }
//  }
//
//  @Test
//  public void testActivities2() {
//    String pPackageName = "za.vdm.android.testapp";
//    String pActivity1 = "<activity android:name=\".MainActivity\" android:label=\"@string/app_name\" />";
//    String pActivity2 = "<activity android:name=\"com.example.broadcast.MainActivity2\" android:label=\"@string/app_name\" />";
//
//    String s = "<manifest package=\"" + pPackageName + "\" >" + "<application>" + pActivity1 + pActivity2
//        + "</application></manifest>";
//
//    if (verifyNoPropertyViolation()) {
//      PackageManager pm = new PackageManager(s);
//      try {
//        PackageInfo pi = pm.getPackageInfo();
//        assert pi.activities.length == 2;
//        assert (pi.activities[0].packageName + "/" + pi.activities[0].name)
//            .equals("za.vdm.android.testapp/.MainActivity");
//        assert (pi.activities[1].packageName + "/" + pi.activities[1].name)
//            .equals("za.vdm.android.testapp/com.example.broadcast.MainActivity2");
//      } catch (Exception e) {
//        assert false : "Activities should be populated";
//      }
//    }
//
//  }
//
//  @Test
//  public void testServices2() {
//
//  }
//
//  @Test
//  public void testReceivers2() {
//
//  }

}
