package com.example.com;

import android.app.Activity;
import android.app.ActivityThread;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SampleProjectActivity extends Activity {
	int i = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		i++;
		System.out.println("act1");

		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Button Clicked!!!   " + i);
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