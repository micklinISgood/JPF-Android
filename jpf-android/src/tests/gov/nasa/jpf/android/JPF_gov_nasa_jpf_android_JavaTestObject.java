package gov.nasa.jpf.android;

import gov.nasa.jpf.jvm.MJIEnv;

import java.util.logging.Logger;

public class JPF_gov_nasa_jpf_android_JavaTestObject {
 public  static Logger logger = Logger.getLogger("JPF_gov_nasa_jpf_util_JavaTestObject");

  public static void $init(MJIEnv env, int robj) {
    JavaTestObject2 o = new JavaTestObject2();
    int ref = AndroidObjectConverter.JPFObjectFromJavaObject(env, o);
    env.setReferenceField(robj, "s2",  env.newString("www" + ref));
    env.setReferenceField(robj, "o", ref);
  }

}
