package com.example.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SampleProjectActivity extends Activity {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    // Object state = getLastNonConfigurationInstance();
    // if (state != null && state.equals(new String("Hallo"))) {
    // System.out.println("Retained resource");
    // }

    System.out.println("1 onCreate() " + toString());

    Button b1 = (Button) findViewById(R.id.buttonPrintHallo);
    b1.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        System.out.println("1 Printing Hallo");
      }
    });

    // start Actvitiy for result
    Button b111 = (Button) findViewById(R.id.buttonResult);
    b111.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        System.out.println("1 Start for result");
        startActivityForResult(new Intent(SampleProjectActivity.this,
            com.example.vdm.SampleProjectActivity.class), 1);
      }
    });

    Button b2 = (Button) findViewById(R.id.buttonStart);
    b2.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        System.out.println("1 Starting");
        Intent i = new Intent(SampleProjectActivity.this, com.example.vdm.SampleProjectActivity.class);
        startActivity(i);
      }
    });

    final Button b3 = (Button) findViewById(R.id.buttonStartBundle);
    b3.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        System.out.println("1 Start with bundle");
        Intent i = new Intent(SampleProjectActivity.this, com.example.vdm.SampleProjectActivity.class);

        // Create the bundle
        Bundle bundle = new Bundle();

        // Add your stuff
        bundle.putString("name", "heidi");
        bundle.putString("surname", "van der merwe");
        bundle.putInt("age", 83);

        // Add the bundle to the intent
        i.putExtras(bundle);

        startActivity(i);
      }
    });

    final Button b4 = (Button) findViewById(R.id.buttonStartList);
    b4.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        System.out.println("1 Start List");
        Intent i = new Intent(SampleProjectActivity.this, com.example.com.MylistView.class);
        startActivity(i);
      }
    });

  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

  }

  public Object onRetainNonConfigurationInstance() {
    System.out.println("1 onRetainNonConfigurationInstance()");
    return new String("Hallo");
  }

  @Override
  public void startActivityForResult(Intent intent, int requestCode) {
    super.startActivityForResult(intent, requestCode);
  }

  @Override
  protected void onStart() {
    super.onStart();
    System.out.println("1 onStart()" + toString());

  }

  @Override
  protected void onRestart() {
    super.onRestart();
    System.out.println("1 onRestart()");
  }

  @Override
  protected void onResume() {
    super.onResume();
    System.out.println("1  onResume()");

  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    System.out.println("1  onPostResume()");
  }

  @Override
  protected void onPause() {
    super.onPause();
    System.out.println("1  onPause()");
  }

  @Override
  protected void onStop() {
    super.onStop();
    System.out.println("1  onStop()");

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    System.out.println("1  onDestroy()");

  }

  @Override
  public Intent getIntent() {
    System.out.println("1  getIntent()");
    return super.getIntent();
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    System.out.println("1  onRestoreInstanceState()");
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    System.out.println("1  onNewIntent()");

    super.onNewIntent(intent);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    System.out.println("1  onSaveInstanceState()");
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    System.out.println("1  onActivityResult() = " + requestCode + " " + resultCode);
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    System.out.println("1  onKeyDown()");
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public void finish() {
    System.out.println("1  finish()");
    super.finish();
  }

  public static void main(String[] args) {
  }

}