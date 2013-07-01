package android.app;

import java.util.HashMap;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;

/**
 * Local state maintained about a currently loaded .apk.
 * 
 * TODO:
 * 
 * ServiceDispatcher to handle service connections
 * 
 */
public final class LoadedApk {

  private final ActivityThread mActivityThread;
  private final ApplicationInfo mApplicationInfo;
  final String mPackageName;
  // private final String mAppDir;
  // private final String mResDir;
  // private final String[] mSharedLibraries;
  // private final String mDataDir;
  // private final String mLibDir;
  // private final File mDataDirFile;
  private final ClassLoader mBaseClassLoader;
  // private final boolean mSecurityViolation;
  // private final boolean mIncludeCode;
  // public final CompatibilityInfoHolder mCompatibilityInfo = new CompatibilityInfoHolder();
  Resources mResources;
  private ClassLoader mClassLoader;
  private Application mApplication;

  /**
   * We need this to store references to broadcast receivers per context that
   * started them. A context (component) can register many broadcast receivers
   * and a broadcast receiver can be registered multiple times by different
   * contexts.
   * 
   * Why do we need this? We can use this to remove all registrations when a
   * context is destroyed --- in other words leaked BR
   */
  private final HashMap<Context, HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher>> mReceivers = new HashMap<Context, HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher>>();
  private final HashMap<Context, HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher>> mUnregisteredReceivers = new HashMap<Context, HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher>>();

  // private final HashMap<Context, HashMap<ServiceConnection, LoadedApk.ServiceDispatcher>> mServices = new
  // HashMap<Context, HashMap<ServiceConnection, LoadedApk.ServiceDispatcher>>();
  // private final HashMap<Context, HashMap<ServiceConnection, LoadedApk.ServiceDispatcher>> mUnboundServices
  // = new HashMap<Context, HashMap<ServiceConnection, LoadedApk.ServiceDispatcher>>();

  /**
   * Create information about a new .apk
   * 
   * NOTE: This constructor is called with ActivityThread's lock held, so MUST
   * NOT call back out to the activity manager.
   */
  public LoadedApk(ActivityThread activityThread, ApplicationInfo aInfo, ActivityThread mainThread) {
    mActivityThread = activityThread;
    mApplicationInfo = aInfo;
    mPackageName = aInfo.packageName;
    // mAppDir = aInfo.sourceDir;
    // mResDir = aInfo.uid == Process.myUid() ? aInfo.sourceDir
    // : aInfo.publicSourceDir;
    // mSharedLibraries = aInfo.sharedLibraryFiles;
    // mDataDir = aInfo.dataDir;
    // mDataDirFile = mDataDir != null ? new File(mDataDir) : null;
    // mLibDir = aInfo.nativeLibraryDir;
    mBaseClassLoader = ClassLoader.getSystemClassLoader();
    // mSecurityViolation = securityViolation;
    // mIncludeCode = includeCode;
    // mCompatibilityInfo.set(compatInfo);
    //
    // if (mAppDir == null) {
    if (ActivityThread.mSystemContext == null) {
      ActivityThread.mSystemContext = ContextImpl.createSystemContext(mainThread);
      // ActivityThread.mSystemContext.getResources().updateConfiguration(mainThread.getConfiguration(),
      // mainThread.getDisplayMetricsLocked(compatInfo, false), compatInfo);
    }
    mClassLoader = ClassLoader.getSystemClassLoader();
    mResources = ActivityThread.mSystemContext.getResources();
  }

  public ClassLoader getClassLoader() {
    return mClassLoader;
  }

  public String getPackageName() {
    return mPackageName;
  }

  public Application getApplication() {
    return mApplication;
  }

  public ApplicationInfo getApplicationInfo() {
    return mApplicationInfo;
  }

  public Resources getResources(ActivityThread mainThread) {
    return mResources;
  }

  public Application makeApplication(boolean forceDefaultAppClass, Instrumentation instrumentation) {
    if (mApplication != null) {
      return mApplication;
    }

    Application app = null;

    String appClass = mApplicationInfo.className;
    if (forceDefaultAppClass || (appClass == null) || (appClass.trim().equals(""))) {
      appClass = "android.app.Application";
    }

    try {
      java.lang.ClassLoader cl = getClassLoader();
      ContextImpl appContext = new ContextImpl();
      appContext.init(this, null, mActivityThread);
      app = mActivityThread.mInstrumentation.newApplication(cl, appClass, appContext);
      appContext.setOuterContext(app);
    } catch (Exception e) {
      if (!mActivityThread.mInstrumentation.onException(app, e)) {
        throw new RuntimeException("Unable to instantiate application " + appClass + ": " + e.toString(), e);
      }
    }
    mApplication = app;

    return app;
  }

  public void removeContextRegistrations(Context context, String who, String what) {
    HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher> rmap = mReceivers.remove(context);
    if (rmap != null) {
      Iterator<LoadedApk.ReceiverDispatcher> it = rmap.values().iterator();
      while (it.hasNext()) {
        LoadedApk.ReceiverDispatcher rd = it.next();
        IntentReceiverLeaked leak = new IntentReceiverLeaked(what + " " + who + " has leaked IntentReceiver "
            + rd.getIntentReceiver() + " that was " + "originally registered here. Are you missing a "
            + "call to unregisterReceiver()?");
        leak.setStackTrace(rd.getLocation().getStackTrace());
        Slog.e(ActivityThread.TAG, leak.getMessage(), leak);
        try {
          ActivityManagerNative.getDefault().unregisterReceiver(rd.getIIntentReceiver());
        } catch (RemoteException e) {
          // system crashed, nothing we can do
        }
      }
    }
    mUnregisteredReceivers.remove(context);
    // Slog.i(TAG, "Receiver registrations: " + mReceivers);
    // TODO HashMap<ServiceConnection, LoadedApk.ServiceDispatcher> smap = mServices.remove(context);
    // if (smap != null) {
    // Iterator<LoadedApk.ServiceDispatcher> it = smap.values().iterator();
    // while (it.hasNext()) {
    // LoadedApk.ServiceDispatcher sd = it.next();
    // ServiceConnectionLeaked leak = new ServiceConnectionLeaked(what + " " + who
    // + " has leaked ServiceConnection " + sd.getServiceConnection()
    // + " that was originally bound here");
    // leak.setStackTrace(sd.getLocation().getStackTrace());
    // Slog.e(ActivityThread.TAG, leak.getMessage(), leak);
    // try {
    // ActivityManagerNative.getDefault().unbindService(sd.getIServiceConnection());
    // } catch (RemoteException e) {
    // // system crashed, nothing we can do
    // }
    // sd.doForget();
    // }
    // }
    // mUnboundServices.remove(context);
    // Slog.i(TAG, "Service registrations: " + mServices);
  }

  public IIntentReceiver getReceiverDispatcher(BroadcastReceiver r, Context context, Handler handler,
                                               Instrumentation instrumentation, boolean registered) {
    synchronized (mReceivers) {
      LoadedApk.ReceiverDispatcher rd = null;
      HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher> map = null;
      if (registered) {
        map = mReceivers.get(context);
        if (map != null) {
          rd = map.get(r);
        }
      }
      if (rd == null) {
        rd = new ReceiverDispatcher(r, context, handler, instrumentation, registered);
        if (registered) {
          if (map == null) {
            map = new HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher>();
            mReceivers.put(context, map);
          }
          map.put(r, rd);
        }
      } else {
        rd.validate(context, handler);
      }
      rd.mForgotten = false;
      return rd.getIIntentReceiver();
    }
  }

  public IIntentReceiver forgetReceiverDispatcher(Context context, BroadcastReceiver r) {
    synchronized (mReceivers) {
      HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher> map = mReceivers.get(context);
      LoadedApk.ReceiverDispatcher rd = null;
      if (map != null) {
        rd = map.get(r);
        if (rd != null) {
          map.remove(r);
          if (map.size() == 0) {
            mReceivers.remove(context);
          }
          if (r.getDebugUnregister()) {
            HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher> holder = mUnregisteredReceivers
                .get(context);
            if (holder == null) {
              holder = new HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher>();
              mUnregisteredReceivers.put(context, holder);
            }
            RuntimeException ex = new IllegalArgumentException("Originally unregistered here:");
            ex.fillInStackTrace();
            rd.setUnregisterLocation(ex);
            holder.put(r, rd);
          }
          return rd.getIIntentReceiver();
        }
      }
      HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher> holder = mUnregisteredReceivers.get(context);
      if (holder != null) {
        rd = holder.get(r);
        if (rd != null) {
          RuntimeException ex = rd.getUnregisterLocation();
          throw new IllegalArgumentException(
              "Unregistering Receiver " + r + " that was already unregistered", ex);
        }
      }
      if (context == null) {
        throw new IllegalStateException("Unbinding Receiver " + r
            + " from Context that is no longer in use: " + context);
      } else {
        throw new IllegalArgumentException("Receiver not registered: " + r);
      }

    }
  }

  static final class ReceiverDispatcher {

    final static class IntentReceiver extends IIntentReceiver {

      final LoadedApk.ReceiverDispatcher mStrongRef;

      IntentReceiver(LoadedApk.ReceiverDispatcher rd) {
        mStrongRef = rd;
      }

      @Override
      public String toString() {
        return "BroadcastReceiver [ name=" + mStrongRef.mReceiver.getClass().getName() + " ]";
      }

      public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered,
                                 boolean sticky) {
        LoadedApk.ReceiverDispatcher rd = mStrongRef;
        if (ActivityThread.DEBUG_BROADCAST) {
          int seq = intent.getIntExtra("seq", -1);
          Slog.i(ActivityThread.TAG, "Receiving broadcast " + intent.getAction() + " seq=" + seq + " to "
              + (rd != null ? rd.mReceiver : null));
        }
        if (rd != null) {
          rd.performReceive(intent, resultCode, data, extras, ordered, sticky);
        } else {
          // The activity manager dispatched a broadcast to a registered
          // receiver in this process, but before it could be delivered the
          // receiver was unregistered. Acknowledge the broadcast on its
          // behalf so that the system's broadcast sequence can continue.
          if (ActivityThread.DEBUG_BROADCAST)
            Slog.i(ActivityThread.TAG, "Finishing broadcast to unregistered receiver");
          IActivityManager mgr = ActivityManagerNative.getDefault();
          try {
            if (extras != null) {
              extras.setAllowFds(false);
            }
            mgr.finishReceiver(this, resultCode, data, extras, false);
          } catch (RemoteException e) {
            Slog.w(ActivityThread.TAG, "Couldn't finish broadcast to unregistered receiver");
          }
        }
      }

    }

    final IIntentReceiver mIIntentReceiver;
    final BroadcastReceiver mReceiver;
    final Context mContext;
    final Handler mActivityThread;
    final Instrumentation mInstrumentation;
    final boolean mRegistered;
    boolean mForgotten;

    final class Args extends BroadcastReceiver.PendingResult implements Runnable {
      private Intent mCurIntent;
      private final boolean mOrdered;

      public Args(Intent intent, int resultCode, String resultData, Bundle resultExtras, boolean ordered,
          boolean sticky) {
        super(resultCode, resultData, resultExtras, mRegistered ? TYPE_REGISTERED : TYPE_UNREGISTERED,
            ordered, sticky, mIIntentReceiver.asBinder());
        mCurIntent = intent;
        mOrdered = ordered;
      }

      public void run() {
        final BroadcastReceiver receiver = mReceiver;
        final boolean ordered = mOrdered;

        if (ActivityThread.DEBUG_BROADCAST) {
          int seq = mCurIntent.getIntExtra("seq", -1);
          Slog.i(ActivityThread.TAG, "Dispatching broadcast " + mCurIntent.getAction() + " seq=" + seq
              + " to " + mReceiver);
          Slog.i(ActivityThread.TAG, "  mRegistered=" + mRegistered + " mOrderedHint=" + ordered);
        }

        final IActivityManager mgr = ActivityManagerNative.getDefault();
        final Intent intent = mCurIntent;
        mCurIntent = null;

        if (receiver == null || mForgotten) {
          if (mRegistered && ordered) {
            if (ActivityThread.DEBUG_BROADCAST)
              Slog.i(ActivityThread.TAG, "Finishing null broadcast to " + mReceiver);
            sendFinished(mgr);
          }
          return;
        }

        try {
          ClassLoader cl = mReceiver.getClass().getClassLoader();
          intent.setExtrasClassLoader(cl);
          setExtrasClassLoader(cl);
          receiver.setPendingResult(this);
          Log.i("BroadcastReceiver", receiver.getClass().getName() + ".onReceive with " + intent);
          receiver.onReceive(mContext, intent);
        } catch (Exception e) {
          if (mRegistered && ordered) {
            if (ActivityThread.DEBUG_BROADCAST)
              Slog.i(ActivityThread.TAG, "Finishing failed broadcast to " + mReceiver);
            sendFinished(mgr);
          }
          if (mInstrumentation == null || !mInstrumentation.onException(mReceiver, e)) {
            throw new RuntimeException("Error receiving broadcast " + intent + " in " + mReceiver, e);
          }
        }

        if (receiver.getPendingResult() != null) {
          finish();
        }
      }
    }

    ReceiverDispatcher(BroadcastReceiver receiver, Context context, Handler activityThread,
        Instrumentation instrumentation, boolean registered) {
      if (activityThread == null) {
        throw new NullPointerException("Handler must not be null");
      }

      mIIntentReceiver = new IntentReceiver(this);
      mReceiver = receiver;
      mContext = context;
      mActivityThread = activityThread;
      mInstrumentation = instrumentation;
      mRegistered = registered;
    }

    void validate(Context context, Handler activityThread) {
      if (mContext != context) {
        throw new IllegalStateException("Receiver " + mReceiver + " registered with differing Context (was "
            + mContext + " now " + context + ")");
      }
      if (mActivityThread != activityThread) {
        throw new IllegalStateException("Receiver " + mReceiver + " registered with differing handler (was "
            + mActivityThread + " now " + activityThread + ")");
      }
    }

    IntentReceiverLeaked getLocation() {
      return null;
    }

    BroadcastReceiver getIntentReceiver() {
      return mReceiver;
    }

    IIntentReceiver getIIntentReceiver() {
      return mIIntentReceiver;
    }

    void setUnregisterLocation(RuntimeException ex) {
      // mUnregisterLocation = ex;
    }

    RuntimeException getUnregisterLocation() {
      return null;
    }

    public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered,
                               boolean sticky) {
      if (ActivityThread.DEBUG_BROADCAST) {
        int seq = intent.getIntExtra("seq", -1);
        Slog.i(ActivityThread.TAG, "Enqueueing broadcast " + intent.getAction() + " seq=" + seq + " to "
            + mReceiver);
      }
      Args args = new Args(intent, resultCode, data, extras, ordered, sticky);
      if (!mActivityThread.post(args)) {
        if (mRegistered && ordered) {
          IActivityManager mgr = ActivityManagerNative.getDefault();
          if (ActivityThread.DEBUG_BROADCAST)
            Slog.i(ActivityThread.TAG, "Finishing sync broadcast to " + mReceiver);
          args.sendFinished(mgr);
        }
      }
    }

  }

}
