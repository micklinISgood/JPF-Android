package com.example.vdm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.com.R;

public class SampleProjectActivity extends Activity {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main2);
    System.out.println("2  onCreate");
    System.out.println("act2");

    Bundle bundle = getIntent().getExtras();

    if (bundle != null) { // forgot to null check
      String sName = bundle.getString("name");
      String sSurname = bundle.getString("surname");
      int sAge = bundle.getInt("age");

      TextView l = (TextView) findViewById(R.id.nameEdit);
      l.setText(sName + " " + sSurname + " age: " + sAge);
      System.out.println(sName + " " + sSurname + " age: " + sAge);
    }

    Button b1 = (Button) findViewById(R.id.button1);
    final Button b2 = (Button) findViewById(R.id.button2);
    b1.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        System.out.println("Button Enabled Clicked!!!");
        b2.setEnabled(true);
      }
    });

    b2.setEnabled(false);
    b2.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        System.out.println("Button start Clicked!!!");
        Intent i = new Intent(SampleProjectActivity.this, com.example.com.SampleProjectActivity.class);
        startActivity(i);
      }
    });

    Intent t = new Intent();
    t.putExtra("hallo", true);
    setResult(RESULT_OK, t);
  }

  @Override
  protected void onStart() {
    super.onStart();
    System.out.println("2 onStart()" + toString());

  }

  @Override
  protected void onRestart() {
    super.onRestart();
    System.out.println("2 onRestart()");
  }

  @Override
  protected void onResume() {
    super.onResume();
    System.out.println("2  onResume()");

  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    System.out.println("2  onPostResume()");
  }

  @Override
  protected void onPause() {
    super.onPause();
    System.out.println("2  onPause()");
  }

  @Override
  protected void onStop() {
    super.onStop();
    System.out.println("2  onStop()");

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    System.out.println("2  onDestroy()");

  }

  @Override
  public Intent getIntent() {
    System.out.println("2  getIntent()");
    return super.getIntent();
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    System.out.println("2  onRestoreInstanceState()");
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    System.out.println("2  onNewIntent()");

    super.onNewIntent(intent);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    System.out.println("2  onSaveInstanceState()");
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    System.out.println("2  onActivityResult()");
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    System.out.println("2  onKeyDown()");
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public void finish() {
    System.out.println("2  finish()");
    super.finish();
  }

}