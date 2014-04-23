package za.vdm.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class SecondActivity extends Activity {
  LocalReceiver r = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    LinearLayout layout = (LinearLayout) findViewById(R.id.back_layout);

    Button button1 = (Button) findViewById(R.id.button1);
    button1.setText("test1");
    button1.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        sendIntent();
      }
    });

    r = new LocalReceiver();
    registerReceiver(r, new IntentFilter("za.vdm.main.LOCAL_BROADCAST"));

  }

  public void sendIntent() {
    Intent i = new Intent("za.vdm.main.LOCAL_BROADCAST");
    Bundle b = new Bundle();
    b.putInt("value", 4);
    i.putExtras(b);
    sendBroadcast(i);
  }

  public class LocalReceiver extends BroadcastReceiver {
    private final static String tag = "LocalReceiver1";

    int i = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
      System.out.println("LocalReceiver.onReceive: count=" + i++);
    }

  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(r);

    super.onDestroy();

  }

}
