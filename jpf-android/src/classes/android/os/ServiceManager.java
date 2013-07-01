package android.os;

import java.util.HashMap;

import android.app.ActivityThread;
import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.view.WindowManager;

import com.android.server.am.ActivityManagerService;

/**
 * Keeps references to System services
 * 
 * @author Heila
 * 
 */
public class ServiceManager {
  private static final String TAG = "ServiceManager";

  private static HashMap<String, Object> SYSTEM_SERVICE_MAP;

  private static ConnectivityManager connectionManager;
  private static AlarmManager alarmManager;
  private static PowerManager powerMananger;

  private static WindowManager windowManager;
  private static PackageManager packageManager;
  private static ActivityManagerService activityManager;

  private static ActivityThread activityThread;

  public static void createServiceManagers(ActivityThread activityThread) {
    ServiceManager.activityThread = activityThread;

    SYSTEM_SERVICE_MAP = new HashMap<String, Object>();

    //Setup PackageManager
    packageManager = new PackageManager();
    SYSTEM_SERVICE_MAP.put("package", packageManager);

    // Setup ActivityManager
    activityManager = new ActivityManagerService(packageManager.getPackageInfo());
    SYSTEM_SERVICE_MAP.put(Context.ACTIVITY_SERVICE, activityManager);

    // Setup WindowManager
    windowManager = WindowManager.getInstance();
    SYSTEM_SERVICE_MAP.put(Context.WINDOW_SERVICE, windowManager);

    connectionManager = new ConnectivityManager(activityThread);
    SYSTEM_SERVICE_MAP.put(Context.CONNECTIVITY_SERVICE, connectionManager);

    //    powerMananger = new PowerManager();

    init0(connectionManager);
  }

  private static native void init0(ConnectivityManager connectionManager);

  public static Object getSystemService(String name) {
    return SYSTEM_SERVICE_MAP.get(name);
  }

}
