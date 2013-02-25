package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.MJIEnv;

import org.xml.sax.helpers.DefaultHandler;

public class JPF_android_app_LoadedApk extends DefaultHandler {


  public static int init(MJIEnv env, int objref){
    Config conf = env.getConfig();
    String lPath = conf.getString("path") + "/AndroidManifest.xml"; // TODO if specified in
                    
    
  }



}
