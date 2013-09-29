package gov.nasa.jpf.android;

import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.util.script.UIAction;

public class JPF_android_os_ServiceManager {

  static int classRef;

  public static void init0(MJIEnv env, int classRef) {
    AndroidPathManager.init(env.getConfig());
  }

  public static void performDeviceAction(MJIEnv env, UIAction uiaction) {
    
    String action = uiaction.getAction().toLowerCase();
    if(action.contains("battery") || action.contains("charging")){
      JPF_com_android_server_BatteryService.handleAction(env, uiaction);
    } else if (action.contains("wifi") || action.contains("mobile") ){
      JPF_android_net_ConnectivityManager.handleAction(env,uiaction);
     }
    
    
    
  }
  
  
  
  
  

}
