package com.example;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.CounterService.LocalBinder;

public class MainActivity extends Activity {
	CounterService mService;
	boolean mBound = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button bStart = (Button) findViewById(R.id.buttonStart);
		bStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startService(new Intent(MainActivity.this, CounterService.class));
				
			}
		});

		Button bStop = (Button) findViewById(R.id.buttonStop);
		bStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopService(new Intent(MainActivity.this, CounterService.class));
			}
		});

		Button bBind = (Button) findViewById(R.id.buttonBind);
		bBind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				bindService(
						new Intent(MainActivity.this, CounterService.class),
						mConnection, Context.BIND_AUTO_CREATE);
			}
		});

		Button bUnbind = (Button) findViewById(R.id.buttonUnbind);
		bUnbind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				unbindService(mConnection);
			}
		});

	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

}
