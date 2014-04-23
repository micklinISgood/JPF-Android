package com.example.jpf;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Very basic Android application containing only this DeadlockActivity. The Activity starts two Async tasks. These
 * Async tasks will deadlock when both Friends bow waiting for the other to bow back.
 * 
 * @author Heila van der Merwe
 * 
 */
public class DeadlockActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // create two friends
    final Friend alphonse = new Friend("Alphonse");
    final Friend gaston = new Friend("Gaston");

    // button 1 starts Gastron bowing at Alphonse
    Button b1 = (Button) findViewById(R.id.button1);
    b1.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        BowingTask bt = new BowingTask();
        bt.execute(gaston, alphonse);
      }
    });

    // button 2 starts Alphonse bowing at Gastron
    Button b2 = (Button) findViewById(R.id.button2);
    b2.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        BowingTask bt = new BowingTask();
        bt.execute(alphonse, gaston);
      }
    });

  }

  private class BowingTask extends AsyncTask<Friend, Integer, String> {

    @Override
    protected String doInBackground(Friend... friend) {
      int i = 0;
      while (i < 20000) {
        friend[0].bow(friend[1]);
        System.out.println(friend[0].getName() + " i = " + i);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
         //we don't mind
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
      System.out.format("%s: %s" + "  has bowed to me!%n", this.name, bower.getName());
      bower.bowBack(this);
    }

    public synchronized void bowBack(Friend bower) {
      System.out.format("%s: %s" + " has bowed back to me!%n", this.name, bower.getName());
    }
  }

}