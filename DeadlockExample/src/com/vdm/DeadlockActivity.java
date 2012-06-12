package com.vdm;

import android.app.Activity;
import android.app.ActivityThread;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Very basic Android application containing only DeadlockActivity. The Activity
 * starts two Async tasks. These Async tasks will deadlock when both Friends bow
 * waiting for the other to bow back.
 * 
 * @author Heila van der Merwe
 * 
 */
public class DeadlockActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // not used currently
		setContentView(R.layout.main);
		
		
		Button b = (Button)findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			System.out.println("hoop dit werk");
				
			}
		});
//		Button b = new Button(this);
//
//		b.setID(Integer.parseInt("0x7f050000".substring(2), 16)); // temporarily
//																	// until
//																	// this gets
//																	// set by
//																	// the
//																	// layout
//																	// xml
//																	// parser
//		b.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				System.out.println("Hallo!!!");
//
//			}
//		});
//
//		LinearLayout L = new LinearLayout(this);
//		L.addView(b);
//
//		setContentView(L); // This will be converted back to the
							// setContentView(int resourceId as soon as the
							// layout inflator is implemented to lookup the
							// layout resource)

		// final Friend alphonse = new Friend("Alphonse");
		// final Friend gaston = new Friend("Gaston");
		// // Button button1 = (Button) findViewById(R.id.button1);
		// // button1.setOnClickListener(new View.OnClickListener() {
		// // public void onClick(View v) {
		// // new Task().execute(alphonse, gaston);
		// // }
		// // });
		// System.out.println("onCreate");
		//
		//
		// Task t1 = new Task();
		// Task t2 = new Task();
		// t1.execute(gaston, alphonse);
		// t2.execute(alphonse, gaston);
	}

	private class Task extends AsyncTask<Friend, Integer, String> {

		@Override
		protected String doInBackground(Friend... friend) {
			System.out.println("doInBackground");
			int i = 0;
			while (i < 20000) {
				friend[0].bow(friend[1]);
				System.out.println(friend[0].getName() + " i = " + i);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
			return "Bowed";
		}
	}

	static class Friend {
		private final String name;

		public Friend(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public synchronized void bow(Friend bower) {
			System.out.format("%s: %s" + "  has bowed to me!%n", this.name,
					bower.getName());
			bower.bowBack(this);
		}

		public synchronized void bowBack(Friend bower) {
			System.out.format("%s: %s" + " has bowed back to me!%n", this.name,
					bower.getName());
		}
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