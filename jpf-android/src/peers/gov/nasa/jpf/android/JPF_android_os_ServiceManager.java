package gov.nasa.jpf.android;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.util.script.UIAction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

public class JPF_android_os_ServiceManager extends NativePeer {

  //static int classRef;

  @MJI
  public void init0(MJIEnv env, int classRef) {
    //this.classRef = classRef;
    //make sure the projectInfo is initialized as soon as possible
    AndroidProjectInfo p = AndroidProjectInfo.get();
  }

  public static void performDeviceAction(MJIEnv env, UIAction uiaction) {

    String action = uiaction.getAction().toLowerCase();
    if (action.contains("battery") || action.contains("charging")) {
      JPF_com_android_server_BatteryService.handleAction(env, uiaction);
    } else if (action.contains("wifi") || action.contains("mobile")) {
      JPF_android_net_ConnectivityManager.handleAction(env, uiaction);
    }

  }

}
