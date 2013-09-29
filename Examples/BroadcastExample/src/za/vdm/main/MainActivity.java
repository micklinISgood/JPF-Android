package za.vdm.main;

import gov.nasa.jpf.annotation.Checkpoint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

  public int count = 0;

  LocalReceiver r = null;

  @Override
  @Checkpoint("MainOnCreate")
  protected void onCreate(Bundle savedInstanceState) {
    //    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
    //    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
    //        .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    LinearLayout layout = (LinearLayout) findViewById(R.id.back_layout);

    Button button1 = (Button) findViewById(R.id.button1);
    button1.setText("test1");
    button1.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //sendIntent();
        //checkNetworkState();
        System.out.println("CLick  &&&&&&&&&&&&&&&&&");
      }

    });
    
    
    button1.setOnTouchListener(new OnTouchListener() {
      
      @Override
      public boolean onTouch(View arg0, MotionEvent arg1) {
        System.out.println("Touch event " + arg1);
        return true;
      }
    });
    
    EditText box = (EditText) findViewById(R.id.editText1);
    box.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
        System.out.println("KEY EVENTS  " + arg2);
        return false;
      }
    });
    

    r = new LocalReceiver();
    registerReceiver(r, new IntentFilter("za.vdm.main.LOCAL_BROADCAST"));

    Button button2 = (Button) findViewById(R.id.button2);
    button2.setText("test localReceiver");
    button2.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, SecondActivity.class));
      }
    });

  }

  @Override
  public void onBackPressed() {
    System.out.println("onBackPressed ");
    super.onBackPressed();
  }

  @Override
  public boolean onGenericMotionEvent(MotionEvent event) {
    System.out.println("onGenericMotionEvent " + event);
    return super.onGenericMotionEvent(event);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
System.out.println("KEY_DOWN " + keyCode + " " + event);
return super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    System.out.println("KEY_LONG " + keyCode + " " + event);
    return super.onKeyLongPress(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    System.out.println("KEY_UP " + keyCode + " " + event);
    return super.onKeyUp(keyCode, event);
  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(r);

  }

  public void sendIntent() {
    Intent i = new Intent("za.vdm.main.LOCAL_BROADCAST");
    Bundle b = new Bundle();
    b.putInt("value", 4);
    i.putExtras(b);
    sendBroadcast(i);
  }

  /**
   * Statically registered BR receives B + enabled/disable + export=false
   */
  public void test1() {
    System.out.println("sending Intent");
    Intent i = new Intent(this, za.vdm.receivers.normal.NormalReceiver1.class);
    Bundle b = new Bundle();
    b.putInt("value", count++);
    i.putExtras(b);
    sendBroadcast(i);
    b.putInt("value", count++);
    i.putExtras(b);
    sendBroadcast(i);
    b.putInt("value", count++);
    i.putExtras(b);
    sendBroadcast(i);
    //		int flag = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    //		ComponentName component = new ComponentName(this,
    //				za.vdm.receivers.normal.NormalReceiver1.class);
    //
    //		getPackageManager().setComponentEnabledSetting(component, flag,
    //				PackageManager.DONT_KILL_APP);
    b.putInt("value", count++);
    i.putExtras(b);
    sendBroadcast(i);
  }

  /**
   * Statically registered BR receives BR
   */
  @Checkpoint("checkNetworkState")
  public void checkNetworkState() {

    ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    if (conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
      System.out.println("########################");
    } else if (conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
      //notify user you are not online
      System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }
    //http://stackoverflow.com/questions/2326767/how-do-you-check-the-internet-connection-in-android
  }

  public void checkBatteryState() {
    //http://developer.android.com/training/monitoring-device-state/battery-monitoring.html#DetermineChargeState

  }

  public void test4() {

  }

  public void test5() {

  }

  public void test6() {

  }

  public class LocalReceiver extends BroadcastReceiver {
    private final static String tag = "LocalReceiver1";

    int i = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
      System.out.println("LocalReceiver.onReceive: count=" + i++);
      MainActivity.this.count++;
    }

  }
}
