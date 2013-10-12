package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.util.Slog;

public class BatteryService {
  private static final String TAG = BatteryService.class.getSimpleName();

  private static final boolean LOCAL_LOGV = false;

  static final int BATTERY_SCALE = 100;    // battery capacity is a percentage
  
  // Used locally for determining when to make a last ditch effort to log
  // discharge stats before the device dies.
  private int mCriticalBatteryLevel = 1;

  // This should probably be exposed in the API, though it's not critical
  private static final int BATTERY_PLUGGED_NONE = 0;

  private final Context mContext;

  private boolean mAcOnline;
  private boolean mUsbOnline;
  private int mBatteryStatus;
  private int mBatteryHealth;
  private boolean mBatteryPresent;
  private int mBatteryLevel;
  private int mBatteryVoltage;
  private int mBatteryTemperature;
  private String mBatteryTechnology;
  private boolean mBatteryLevelCritical;
  private int mInvalidCharger;

  private int mLowBatteryWarningLevel = 5;
  private int mLowBatteryCloseWarningLevel =3;

  private int mLastPlugType;
  private int mPlugType;

  //  private long mDischargeStartTime;
  //  private int mDischargeStartLevel;

  private boolean mSentLowBatteryBroadcast = false;

  public BatteryService(Context context, LightsService lights) {
    mContext = context;

    // set initial status

    //not connected to a charger
    mAcOnline = false;
    mUsbOnline = false;

    // status when charger not attached is BATTERY_STATUS_DISCHARGING
    mBatteryStatus = BatteryManager.BATTERY_STATUS_DISCHARGING;
    mBatteryHealth = BatteryManager.BATTERY_HEALTH_GOOD;
    
    // battery is present
    mBatteryPresent = true;
    mBatteryLevel = 100;
    mBatteryVoltage = 4048;
    mBatteryTemperature = 250;
    mBatteryTechnology = "Li-Ion";
    mInvalidCharger = 0;
    mLowBatteryWarningLevel = 10;
    mLowBatteryCloseWarningLevel = 1;

    mLastPlugType = BatteryManager.BATTERY_PLUGGED_AC;
    mPlugType = BATTERY_PLUGGED_NONE;
    init0();
    sendIntent(); 
    Log.i(TAG, "Ready!");

  }

  private native void init0();

  private void processValues() {

    // check if we are at critical level
    mBatteryLevelCritical = mBatteryLevel <= mCriticalBatteryLevel;

    // get plugtype
    if (mAcOnline) {
      mPlugType = BatteryManager.BATTERY_PLUGGED_AC;
    } else if (mUsbOnline) {
      mPlugType = BatteryManager.BATTERY_PLUGGED_USB;
    } else {
      mPlugType = BATTERY_PLUGGED_NONE;
    }
    final boolean plugged = mPlugType != BATTERY_PLUGGED_NONE;

    /*
     * The ACTION_BATTERY_LOW broadcast is sent in these situations:
     * - is just un-plugged (previously was plugged) and battery level is less than or equal to WARNING, or
     * - is not plugged and battery level falls to WARNING boundary (becomes <= mLowBatteryWarningLevel).
     */
    final boolean sendBatteryLow = !plugged && mBatteryStatus != BatteryManager.BATTERY_STATUS_UNKNOWN
        && mBatteryLevel <= mLowBatteryWarningLevel;

    // notify system of change
    sendIntent();

    // Separate broadcast is sent for power connected / not connected
    // since the standard intent will not wake any applications and some
    // applications may want to have smart behavior based on this.
    Intent statusIntent = new Intent();
    statusIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
    if (mPlugType != 0 && mLastPlugType == 0) {
      statusIntent.setAction(Intent.ACTION_POWER_CONNECTED);
      mContext.sendBroadcast(statusIntent);
    } else if (mPlugType == 0 && mLastPlugType != 0) {
      statusIntent.setAction(Intent.ACTION_POWER_DISCONNECTED);
      mContext.sendBroadcast(statusIntent);
    }

    if (sendBatteryLow) {
      mSentLowBatteryBroadcast = true;
      statusIntent.setAction(Intent.ACTION_BATTERY_LOW);
      mContext.sendBroadcast(statusIntent);
    } else if (mSentLowBatteryBroadcast && mBatteryLevel >= mLowBatteryCloseWarningLevel) {
      mSentLowBatteryBroadcast = false;
      statusIntent.setAction(Intent.ACTION_BATTERY_OKAY);
      mContext.sendBroadcast(statusIntent);
    }

    mLastPlugType = mPlugType;
  }

  private final void sendIntent() {
    //  Pack up the values and broadcast them to everyone
    Intent intent = new Intent(Intent.ACTION_BATTERY_CHANGED);
    intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY | Intent.FLAG_RECEIVER_REPLACE_PENDING);

    intent.putExtra(BatteryManager.EXTRA_STATUS, mBatteryStatus);
    intent.putExtra(BatteryManager.EXTRA_HEALTH, mBatteryHealth);
    intent.putExtra(BatteryManager.EXTRA_PRESENT, mBatteryPresent);
    intent.putExtra(BatteryManager.EXTRA_LEVEL, mBatteryLevel);
    intent.putExtra(BatteryManager.EXTRA_SCALE, BATTERY_SCALE);
    intent.putExtra(BatteryManager.EXTRA_ICON_SMALL, -1);
    intent.putExtra(BatteryManager.EXTRA_PLUGGED, mPlugType);
    intent.putExtra(BatteryManager.EXTRA_VOLTAGE, mBatteryVoltage);
    intent.putExtra(BatteryManager.EXTRA_TEMPERATURE, mBatteryTemperature);
    intent.putExtra(BatteryManager.EXTRA_TECHNOLOGY, mBatteryTechnology);
    intent.putExtra(BatteryManager.EXTRA_INVALID_CHARGER, mInvalidCharger);

    if (true) {
      Slog.d(TAG, "level:" + mBatteryLevel + " scale:" + BATTERY_SCALE + " status:" + mBatteryStatus
          + " health:" + mBatteryHealth + " present:" + mBatteryPresent + " voltage: " + mBatteryVoltage
          + " temperature: " + mBatteryTemperature + " technology: " + mBatteryTechnology + " AC powered:"
          + mAcOnline + " USB powered:" + mUsbOnline + " icon:" + -1 + " invalid charger:" + mInvalidCharger);
    }

    mContext.sendStickyBroadcast(intent);
  }

}
