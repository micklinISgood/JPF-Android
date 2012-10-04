package com.example.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ResultActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    System.out.println("Result Activity: onCreate");

    Button b1 = (Button) findViewById(R.id.buttonText1);

    b1.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent t = new Intent();
        t.putExtra("hallo", true);
        setResult(RESULT_CANCELED, t);
        finish();
      }
    });
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    System.out.println("3 onPostCreate()");

    super.onPostCreate(savedInstanceState);

  }

  @Override
  protected void onStart() {
    super.onStart();
    System.out.println("3 onStart()");

  }

  @Override
  protected void onRestart() {
    super.onRestart();
    System.out.println("3 onRestart()");
  }

  @Override
  protected void onResume() {
    super.onResume();
    System.out.println("3  onResume()");

  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    System.out.println("3  onPostResume()");
  }

  @Override
  protected void onPause() {
    super.onPause();
    System.out.println("3  onPause()");
  }

  @Override
  protected void onStop() {
    super.onStop();
    System.out.println("3  onStop()");

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    System.out.println("3  onDestroy()");

  }

  @Override
  public Intent getIntent() {
    System.out.println("3  getIntent()");
    return super.getIntent();
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    System.out.println("3  onRestoreInstanceState()");
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    System.out.println("3  onNewIntent()");

    super.onNewIntent(intent);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    System.out.println("3  onSaveInstanceState()");
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    System.out.println("3  onActivityResult()");
    super.onActivityResult(requestCode, resultCode, data);
  }

}
