package com.example.vdm;

import android.app.Activity;
import android.app.ActivityThread;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.com.R;

public class SampleProjectActivity extends Activity {
	int i=0;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("onCreate");
		setContentView(R.layout.main2);
		System.out.println("act2");
		i++;
		Button b1 = (Button) findViewById(R.id.button1);
		final Button b2 = (Button) findViewById(R.id.button2);
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Button 1 Clicked!!!");
				// Intent i = new Intent(SampleProjectActivity.this,
				// com.example.vdm.SampleProjectActivity.class);
				// startActivity(i);
				b2.setEnabled(true);
			}
		});

		
		b2.setEnabled(false);
		b2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Button 2 Clicked!!!");
				Intent i = new Intent(SampleProjectActivity.this,
						com.example.com.SampleProjectActivity.class);
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