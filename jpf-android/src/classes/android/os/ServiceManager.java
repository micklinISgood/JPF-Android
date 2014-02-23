package android.os;

import java.util.HashMap;

import android.app.ActivityThread;
import android.app.AlarmManager;
import android.app.ContextImpl;
import android.app.LoadedApk;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.WindowManager;

import com.android.server.BatteryService;
import com.android.server.am.ActivityManagerService;

/**
 * Keeps references to all system services and stores main system context.
 * 
 * @author Heila
 * 
 */
public class ServiceManager {
  private static final String TAG = "ServiceManager";

  private static HashMap<String, Object> SYSTEM_SERVICE_MAP;

  private ConnectivityManager connectionManager;
  private AlarmManager alarmManager;
  private PowerManager powerMananger;
  private BatteryService batteryService;

  private WindowManager windowManager;
  private PackageManager packageManager;
  private ActivityManagerService activityManager;

  private Context mSystemContext;

  static int count = 0;

  public ServiceManager() {
    Log.i(TAG, "Starting up the Android ServiceManager..." + count);
    count++;
    init0();

    SYSTEM_SERVICE_MAP = new HashMap<String, Object>();

    //Setup PackageManager
    packageManager = new PackageManager();
    SYSTEM_SERVICE_MAP.put("package", packageManager);

    mSystemContext = createSystemContext();

    // Setup ActivityManager
    activityManager = new ActivityManagerService(packageManager.getPackageInfo());
    SYSTEM_SERVICE_MAP.put(Context.ACTIVITY_SERVICE, activityManager);

    // Setup WindowManager
    windowManager = WindowManager.getInstance();
    SYSTEM_SERVICE_MAP.put(Context.WINDOW_SERVICE, windowManager);

    // Setup ConnectivityManager
    connectionManager = new ConnectivityManager(mSystemContext);
    SYSTEM_SERVICE_MAP.put(Context.CONNECTIVITY_SERVICE, connectionManager);

    // Setup BatteryManager
    batteryService = new BatteryService(mSystemContext);
  }

  public static void start() {
    ServiceManager s = new ServiceManager();
    ActivityThread.main(null);
  }

  private native void init0();

  public static Object getSystemService(String name) {
    return SYSTEM_SERVICE_MAP.get(name);
  }

  public Context createSystemContext() {
    if (mSystemContext == null) {
      ContextImpl context = ContextImpl.createSystemContext(null);
      LoadedApk info = new LoadedApk(null, "android", context, null, null);
      //      context.init(info, null, null);
      //      context.getResources().updateConfiguration(getConfiguration(),
      //          getDisplayMetricsLocked(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, false));
      mSystemContext = context;
      //Slog.i(TAG, "Created system resources " + context.getResources()
      //        + ": " + context.getResources().getConfiguration());
    }
    return mSystemContext;
  }

}
