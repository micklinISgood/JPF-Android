package com.example.com;

import android.app.Activity;
import android.app.ActivityThread;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SampleProjectActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		System.out.println("act1");

		Button b1 = (Button) findViewById(R.id.buttonPrint1);
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Button 1 Clicked!!!");
				// Intent i = new Intent(SampleProjectActivity.this,
				// com.example.vdm.SampleProjectActivity.class);
				// startActivity(i);
			}
		});

		Button b2 = (Button) findViewById(R.id.buttonPrint2);
		b2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Button 2 Clicked!!!");
				Intent i = new Intent(SampleProjectActivity.this,
						com.example.vdm.SampleProjectActivity.class);
				startActivity(i);
			}
		});
	}

	/**
	 * The main entry point to the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ActivityThread.main(null);
	}
}