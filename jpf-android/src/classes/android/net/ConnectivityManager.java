package android.net;

import java.util.HashMap;
import java.util.Map;

import android.app.ActivityThread;
import android.content.Intent;
import android.net.NetworkInfo.DetailedState;
import android.provider.Settings;
import android.util.Log;

public class ConnectivityManager {
  private static final String TAG = "ConnectivityManager";

  /**
   * A change in network connectivity has occurred. A connection has either
   * been established or lost. The NetworkInfo for the affected network is
   * sent as an extra; it should be consulted to see what kind of
   * connectivity event occurred.
   * <p/>
   * If this is a connection that was the result of failing over from a
   * disconnected network, then the FAILOVER_CONNECTION boolean extra is set to
   * true.
   * <p/>
   * For a loss of connectivity, if the connectivity manager is attempting to
   * connect (or has already connected) to another network, the NetworkInfo for
   * the new network is also passed as an extra. This lets any receivers of the
   * broadcast know that they should not necessarily tell the user that no data
   * traffic will be possible. Instead, the reciever should expect another
   * broadcast soon, indicating either that the failover attempt succeeded (and
   * so there is still overall data connectivity), or that the failover attempt
   * failed, meaning that all connectivity has been lost.
   * <p/>
   * For a disconnect event, the boolean extra EXTRA_NO_CONNECTIVITY is set to
   * {@code true} if there are no connected networks at all.
   */
  public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

  /**
   * Identical to {@link #CONNECTIVITY_ACTION} broadcast, but sent without any
   * applicable {@link Settings.Secure#CONNECTIVITY_CHANGE_DELAY}.
   * 
   * @hide
   */
  public static final String CONNECTIVITY_ACTION_IMMEDIATE = "android.net.conn.CONNECTIVITY_CHANGE_IMMEDIATE";

  /**
   * The lookup key for a {@link NetworkInfo} object. Retrieve with
   * {@link android.content.Intent#getParcelableExtra(String)}.
   * 
   * @deprecated Since {@link NetworkInfo} can vary based on UID, applications
   *             should always obtain network information through
   *             {@link #getActiveNetworkInfo()} or {@link #getAllNetworkInfo()}
   *             .
   */
  @Deprecated
  public static final String EXTRA_NETWORK_INFO = "networkInfo";

  /**
   * The lookup key for a boolean that indicates whether a connect event
   * is for a network to which the connectivity manager was failing over
   * following a disconnect on another network.
   * Retrieve it with
   * {@link android.content.Intent#getBooleanExtra(String,boolean)}.
   */
  public static final String EXTRA_IS_FAILOVER = "isFailover";
  /**
   * The lookup key for a {@link NetworkInfo} object. This is supplied when
   * there is another network that it may be possible to connect to. Retrieve
   * with {@link android.content.Intent#getParcelableExtra(String)}.
   */
  public static final String EXTRA_OTHER_NETWORK_INFO = "otherNetwork";
  /**
   * The lookup key for a boolean that indicates whether there is a
   * complete lack of connectivity, i.e., no network is available.
   * Retrieve it with
   * {@link android.content.Intent#getBooleanExtra(String,boolean)}.
   */
  public static final String EXTRA_NO_CONNECTIVITY = "noConnectivity";
  /**
   * The lookup key for a string that indicates why an attempt to connect
   * to a network failed. The string has no particular structure. It is
   * intended to be used in notifications presented to users. Retrieve
   * it with {@link android.content.Intent#getStringExtra(String)}.
   */
  public static final String EXTRA_REASON = "reason";
  /**
   * The lookup key for a string that provides optionally supplied
   * extra information about the network state. The information
   * may be passed up from the lower networking layers, and its
   * meaning may be specific to a particular network type. Retrieve
   * it with {@link android.content.Intent#getStringExtra(String)}.
   */
  public static final String EXTRA_EXTRA_INFO = "extraInfo";
  /**
   * The lookup key for an int that provides information about
   * our connection to the internet at large. 0 indicates no connection,
   * 100 indicates a great connection. Retrieve it with
   * {@link android.content.Intent#getIntExtra(String, int)}. {@hide}
   */
  public static final String EXTRA_INET_CONDITION = "inetCondition";

  /**
   * Broadcast Action: The setting for background data usage has changed
   * values. Use {@link #getBackgroundDataSetting()} to get the current value.
   * <p>
   * If an application uses the network in the background, it should listen for
   * this broadcast and stop using the background data if the value is
   * {@code false}.
   */
  public static final String ACTION_BACKGROUND_DATA_SETTING_CHANGED = "android.net.conn.BACKGROUND_DATA_SETTING_CHANGED";

  /**
   * Broadcast Action: The network connection may not be good
   * uses {@code ConnectivityManager.EXTRA_INET_CONDITION} and
   * {@code ConnectivityManager.EXTRA_NETWORK_INFO} to specify
   * the network and it's condition.
   * 
   * @hide
   */
  public static final String INET_CONDITION_ACTION = "android.net.conn.INET_CONDITION_ACTION";

  /**
   * Broadcast Action: A tetherable connection has come or gone
   * TODO - finish the doc
   * 
   * @hide
   */
  public static final String ACTION_TETHER_STATE_CHANGED = "android.net.conn.TETHER_STATE_CHANGED";

  /**
   * @hide
   *       gives a String[]
   */
  public static final String EXTRA_AVAILABLE_TETHER = "availableArray";

  /**
   * @hide
   *       gives a String[]
   */
  public static final String EXTRA_ACTIVE_TETHER = "activeArray";

  /**
   * @hide
   *       gives a String[]
   */
  public static final String EXTRA_ERRORED_TETHER = "erroredArray";

  /**
   * The absence of APN..
   * 
   * @hide
   */
  public static final int TYPE_NONE = -1;

  /**
   * The Default Mobile data connection. When active, all data traffic
   * will use this connection by default.
   */
  public static final int TYPE_MOBILE = 0;
  /**
   * The Default WIFI data connection. When active, all data traffic
   * will use this connection by default.
   */
  public static final int TYPE_WIFI = 1;
  /**
   * An MMS-specific Mobile data connection. This connection may be the
   * same as {@link #TYPE_MOBILE} but it may be different. This is used
   * by applications needing to talk to the carrier's Multimedia Messaging
   * Service servers. It may coexist with default data connections.
   */
  public static final int TYPE_MOBILE_MMS = 2;
  /**
   * A SUPL-specific Mobile data connection. This connection may be the
   * same as {@link #TYPE_MOBILE} but it may be different. This is used
   * by applications needing to talk to the carrier's Secure User Plane
   * Location servers for help locating the device. It may coexist with
   * default data connections.
   */
  public static final int TYPE_MOBILE_SUPL = 3;
  /**
   * A DUN-specific Mobile data connection. This connection may be the
   * same as {@link #TYPE_MOBILE} but it may be different. This is used
   * by applicaitons performing a Dial Up Networking bridge so that
   * the carrier is aware of DUN traffic. It may coexist with default data
   * connections.
   */
  public static final int TYPE_MOBILE_DUN = 4;
  /**
   * A High Priority Mobile data connection. This connection is typically
   * the same as {@link #TYPE_MOBILE} but the routing setup is different.
   * Only requesting processes will have access to the Mobile DNS servers
   * and only IP's explicitly requested via {@link #requestRouteToHost} will
   * route over this interface if a default route exists.
   */
  public static final int TYPE_MOBILE_HIPRI = 5;
  /**
   * The Default WiMAX data connection. When active, all data traffic
   * will use this connection by default.
   */
  public static final int TYPE_WIMAX = 6;

  /**
   * The Default Bluetooth data connection. When active, all data traffic
   * will use this connection by default.
   */
  public static final int TYPE_BLUETOOTH = 7;

  /**
   * Dummy data connection. This should not be used on shipping devices.
   */
  public static final int TYPE_DUMMY = 8;

  /**
   * The Default Ethernet data connection. When active, all data traffic
   * will use this connection by default.
   */
  public static final int TYPE_ETHERNET = 9;

  /**
   * Over the air Adminstration. {@hide}
   */
  public static final int TYPE_MOBILE_FOTA = 10;

  /**
   * IP Multimedia Subsystem {@hide}
   */
  public static final int TYPE_MOBILE_IMS = 11;

  /**
   * Carrier Branded Services {@hide}
   */
  public static final int TYPE_MOBILE_CBS = 12;

  /**
   * A Wi-Fi p2p connection. Only requesting processes will have access to
   * the peers connected. {@hide}
   */
  public static final int TYPE_WIFI_P2P = 13;

  /** {@hide} */
  public static final int MAX_RADIO_TYPE = TYPE_WIFI_P2P;

  /** {@hide} */
  public static final int MAX_NETWORK_TYPE = TYPE_WIFI_P2P;

  public static final int DEFAULT_NETWORK_PREFERENCE = TYPE_WIFI;

  public static boolean isNetworkTypeValid(int networkType) {
    return networkType >= 0 && networkType <= MAX_NETWORK_TYPE;
  }

  /** {@hide} */
  public static String getNetworkTypeName(int type) {
    switch (type) {
    case TYPE_MOBILE:
      return "MOBILE";
    case TYPE_WIFI:
      return "WIFI";
    case TYPE_MOBILE_MMS:
      return "MOBILE_MMS";
    case TYPE_MOBILE_SUPL:
      return "MOBILE_SUPL";
    case TYPE_MOBILE_DUN:
      return "MOBILE_DUN";
    case TYPE_MOBILE_HIPRI:
      return "MOBILE_HIPRI";
    case TYPE_WIMAX:
      return "WIMAX";
    case TYPE_BLUETOOTH:
      return "BLUETOOTH";
    case TYPE_DUMMY:
      return "DUMMY";
    case TYPE_ETHERNET:
      return "ETHERNET";
    case TYPE_MOBILE_FOTA:
      return "MOBILE_FOTA";
    case TYPE_MOBILE_IMS:
      return "MOBILE_IMS";
    case TYPE_MOBILE_CBS:
      return "MOBILE_CBS";
    case TYPE_WIFI_P2P:
      return "WIFI_P2P";
    default:
      return Integer.toString(type);
    }
  }

  /** {@hide} */
  public static boolean isNetworkTypeMobile(int networkType) {
    switch (networkType) {
    case TYPE_MOBILE:
    case TYPE_MOBILE_MMS:
    case TYPE_MOBILE_SUPL:
    case TYPE_MOBILE_DUN:
    case TYPE_MOBILE_HIPRI:
    case TYPE_MOBILE_FOTA:
    case TYPE_MOBILE_IMS:
    case TYPE_MOBILE_CBS:
      return true;
    default:
      return false;
    }
  }

  /**
   * This is the map described in the Javadoc comment above. The positions
   * of the elements of the array must correspond to the ordinal values
   * of <code>DetailedState</code>.
   */
  private static final Map<String, DetailedState> stateMap = new HashMap<String, DetailedState>();

  static {
    stateMap.put("CONNECTING", DetailedState.CONNECTING);
    stateMap.put("CONNECTED", DetailedState.CONNECTED);
    stateMap.put("SUSPENDED", DetailedState.SUSPENDED);
    stateMap.put("DISCONNECTING", DetailedState.DISCONNECTING);
    stateMap.put("DISCONNECTED", DetailedState.DISCONNECTED);
  }

  /**
   * This is the map described in the Javadoc comment above. The positions
   * of the elements of the array must correspond to the ordinal values
   * of <code>DetailedState</code>.
   */
  private static final Map<String, Integer> typeMap = new HashMap<String, Integer>();

  static {
    typeMap.put("MOBILE", TYPE_MOBILE);

    typeMap.put("WIFI", TYPE_WIFI);
    typeMap.put("MOBILE_MMS", TYPE_MOBILE_MMS);
    typeMap.put("MOBILE_SUPL", TYPE_MOBILE_SUPL);
    typeMap.put("MOBILE_DUN", TYPE_MOBILE_DUN);
    typeMap.put("MOBILE_HIPRI", TYPE_MOBILE_HIPRI);
    typeMap.put("WIMAX", TYPE_WIMAX);
    typeMap.put("BLUETOOTH", TYPE_BLUETOOTH);
    typeMap.put("DUMMY", TYPE_DUMMY);
    typeMap.put("ETHERNET", TYPE_ETHERNET);
    typeMap.put("MOBILE_FOTA", TYPE_MOBILE_FOTA);
    typeMap.put("MOBILE_IMS", TYPE_MOBILE_IMS);
    typeMap.put("MOBILE_CBS", TYPE_MOBILE_CBS);
    typeMap.put("WIFI_P2P", TYPE_WIFI_P2P);
  }

  HashMap<Integer, NetworkInfo> infos = new HashMap<Integer, NetworkInfo>();
  NetworkInfo activeInfo;
  ActivityThread activityThread;

  public ConnectivityManager(ActivityThread activityThread) {
    Log.i(TAG, "Started ConnectivityManager");
    this.activityThread = activityThread;
  }

  public NetworkInfo getNetworkInfo(int networkType) {
    NetworkInfo info = infos.get(networkType);
    if (info == null) {
      info = new NetworkInfo(networkType, 0, getNetworkTypeName(networkType), "");
      info.setDetailedState(DetailedState.CONNECTED, "", "");
    }
    return info;
  }

  public void changeNetworkState(String networkType, String state, String reason, String extraInfo) {
    // lookup the int network type value from the networkType String
    Integer type = typeMap.get(networkType);
    System.out.println("Got Type " + networkType + ": " + (type != null));

    // lookup the DetailedState Enum of the state of the network described by the String state argument.
    DetailedState detailedState = stateMap.get(state.toUpperCase());
    System.out.println("Got state " + state + ": " + (detailedState != null));

    // check that both was found 
    if (detailedState != null && type != null) {

      // Get the networkinfo of the network. If one does not exist yet, create one.
      NetworkInfo netInfo = infos.get(type);
      if (netInfo == null) {
        System.out.println("Creating new netInfo");

        netInfo = new NetworkInfo(type, 0, networkType, "");
        infos.put(type, netInfo);
      }

      //set the state of the netinfo
      netInfo.setDetailedState(detailedState, reason, extraInfo);

      if (detailedState == DetailedState.CONNECTED) {
        activeInfo = netInfo;
      } else if (detailedState == DetailedState.DISCONNECTED) {
        if (activeInfo == null) {
          activeInfo = netInfo;
        }
      }
      this.activityThread.getSystemContext()
          .sendBroadcast(new Intent("android.net.conn.CONNECTIVITY_CHANGE"));

    } else if (detailedState == null) {
      Log.e(TAG, "Could not apply network state. No such network state \"" + state + "\".");
    } else if (type == null) {
      Log.e(TAG, "Could not apply network state. No such network type \"" + networkType + "\".");
    }
  }

  public NetworkInfo getActiveNetworkInfo() {
    return activeInfo;
  }
}
