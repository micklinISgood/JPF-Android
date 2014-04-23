package com.example;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class CounterService extends Service {
  // Binder given to clients
  private final IBinder mBinder = new LocalBinder();
  private int counter = 0;

  /**
   * Class used for the client Binder. Because we know this service always runs in the same process as its
   * clients, we don't need to deal with IPC.
   */
  public class LocalBinder extends Binder {
    CounterService getService() {
      // Return this instance of LocalService so clients can call public methods
      return CounterService.this;
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
    System.out.println("service: onCreate");
  }

  @Override
  @Deprecated
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    System.out.println("onStart");
    ++counter;

    System.out.println("counter: " + counter);

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    System.out.println("onStartCommand" + startId);
//    stopSelf(startId);
   // stopSelf(-1);
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    System.out.println("onDestroy");

  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    System.out.println("onLowMemory");

  }

  @Override
  public void onTrimMemory(int level) {
    super.onTrimMemory(level);
    System.out.println("onTrimMemory");

  }

  @Override
  public boolean onUnbind(Intent intent) {
    System.out.println("onUnbind");

    return super.onUnbind(intent);
  }

  @Override
  public void onRebind(Intent intent) {
    ++counter;
    System.out.println("counter: " + counter);
    System.out.println("onRebind");
    super.onRebind(intent);
  }

  @Override
  public IBinder onBind(Intent intent) {
    ++counter;

    System.out.println("onBind");
    System.out.println("counter: " + counter);
    return mBinder;
  }

}
