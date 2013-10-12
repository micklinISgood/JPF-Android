package android.os;

import java.lang.reflect.Modifier;

import android.util.Log;
import android.util.Printer;

/**
 * Model of the Android Handler class. The remove message functionality is
 * removed. The Messager functionality is currently disabled. This will be added
 * when the Binder communication is modeled.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class Handler {
  /*
   * Set this flag to true to detect anonymous, local or member classes
   * that extend this Handler class and that are not static. These kind
   * of classes can potentially create leaks.
   */
  private static final boolean FIND_POTENTIAL_LEAKS = true;
  private static final String TAG = "Handler";

  /**
   * Callback interface you can use when instantiating a Handler to avoid
   * having to implement your own subclass of Handler.
   */
  public interface Callback {
    public boolean handleMessage(Message msg);
  }

  /**
   * Subclasses must implement this to receive messages.
   */
  public void handleMessage(Message msg) {
  }

  /**
   * Handle system messages here.
   */
  public void dispatchMessage(Message msg) {
    //set the current thread's event id to msg.eventid

    if (msg.callback != null) {
      handleCallback(msg);
    } else {
      if (mCallback != null) {
        if (mCallback.handleMessage(msg)) {
          return;
        }
      }
      handleMessage(msg);
    }
  }

  /**
   * Default constructor associates this handler with the queue for the
   * current thread.
   * 
   * If there isn't one, this handler won't be able to receive messages.
   */
  public Handler() {
    if (FIND_POTENTIAL_LEAKS) {
      final Class<? extends Handler> klass = getClass();
      if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass())
          && (klass.getModifiers() & Modifier.STATIC) == 0) {
        Log.w(TAG,
            "The following Handler class should be static or leaks might occur: " + klass.getCanonicalName());
      }
    }
    mLooper = Looper.getMainLooper();
    if (mLooper == null) {
      throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
    }
    mQueue = mLooper.mQueue;
    mCallback = null;
  }

  /**
   * Constructor associates this handler with the queue for the current thread
   * and takes a callback interface in which you can handle messages.
   */
  public Handler(Callback callback) {
    if (FIND_POTENTIAL_LEAKS) {
      final Class<? extends Handler> klass = getClass();
      if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass())
          && (klass.getModifiers() & Modifier.STATIC) == 0) {
        Log.w(TAG,
            "The following Handler class should be static or leaks might occur: " + klass.getCanonicalName());
      }
    }

    mLooper = Looper.getMainLooper();
    if (mLooper == null) {
      throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
    }
    mQueue = mLooper.mQueue;
    mCallback = callback;
  }

  /**
   * Use the provided queue instead of the default one.
   */
  public Handler(Looper looper) {
    mLooper = looper;
    mQueue = looper.mQueue;
    mCallback = null;
  }

  /**
   * Use the provided queue instead of the default one and take a callback
   * interface in which to handle messages.
   */
  public Handler(Looper looper, Callback callback) {
    mLooper = looper;
    mQueue = looper.mQueue;
    mCallback = callback;
  }

  /**
   * Returns a string representing the name of the specified message.
   * The default implementation will either return the class name of the
   * message callback if any, or the hexadecimal representation of the
   * message "what" field.
   * 
   * @param message
   *          The message whose name is being queried
   */
  public String getMessageName(Message message) {
    if (message.callback != null) {
      return message.callback.getClass().getName();
    }
    return "0x" + Integer.toHexString(message.what);
  }

  /**
   * Returns a new {@link android.os.Message Message} from the global message
   * pool. More efficient than
   * creating and allocating new instances. The retrieved message has its
   * handler set to this instance (Message.target == this).
   * If you don't want that facility, just call Message.obtain() instead.
   */
  public final Message obtainMessage() {
    return new Message(this);
  }

  /**
   * 
   * Same as {@link #obtainMessage()}, except that it also sets the what and obj
   * members
   * of the returned Message.
   * 
   * @param what
   *          Value to assign to the returned Message.what field.
   * @param obj
   *          Value to assign to the returned Message.obj field.
   * @return A Message from the global message pool.
   */
  public final Message obtainMessage(int what, Object obj) {
    return Message.obtain(this, what, obj);
  }

  /**
   * 
   * Same as {@link #obtainMessage()}, except that it also sets the what, arg1
   * and arg2 members of the returned
   * Message.
   * 
   * @param what
   *          Value to assign to the returned Message.what field.
   * @param arg1
   *          Value to assign to the returned Message.arg1 field.
   * @param arg2
   *          Value to assign to the returned Message.arg2 field.
   * @return A Message from the global message pool.
   */
  public final Message obtainMessage(int what, int arg1, int arg2) {
    return Message.obtain(this, what, arg1, arg2);
  }

  /**
   * 
   * Same as {@link #obtainMessage()}, except that it also sets the what, obj,
   * arg1,and arg2 values on the
   * returned Message.
   * 
   * @param what
   *          Value to assign to the returned Message.what field.
   * @param arg1
   *          Value to assign to the returned Message.arg1 field.
   * @param arg2
   *          Value to assign to the returned Message.arg2 field.
   * @param obj
   *          Value to assign to the returned Message.obj field.
   * @return A Message from the global message pool.
   */
  public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
    return Message.obtain(this, what, arg1, arg2, obj);
  }

  public final boolean post(Runnable r) {

    Message m = new Message();
    m.callback = r;
    return sendMessage(m);
  }

  public final boolean sendMessage(Message msg) {

    // enqueue message
    boolean sent = false;
    MessageQueue queue = mQueue;
    if (queue != null) {
      msg.target = this;
      sent = queue.enqueueMessage(msg);
    } else {
      RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
      Log.w("Looper", e.getMessage(), e);
    }
    return sent;
  }

  public final boolean sendStopMessage(Message msg) {
    // enqueue message
    boolean sent = false;
    MessageQueue queue = mQueue;
    sent = queue.enqueueMessage(msg);
    return sent;
  }

  /**
   * Enqueue a message at the front of the message queue, to be processed on
   * the next iteration of the message loop. You will receive it in
   * {@link #handleMessage}, in the thread attached to this handler. <b>This
   * method is only for use in very special circumstances -- it can easily
   * starve the message queue, cause ordering problems, or have other
   * unexpected side-effects.</b>
   * 
   * @return Returns true if the message was successfully placed in to the
   *         message queue. Returns false on failure, usually because the
   *         looper processing the message queue is exiting.
   */
  public final boolean sendMessageAtFrontOfQueue(Message msg) {
    boolean sent = false;
    MessageQueue queue = mQueue;
    if (queue != null) {
      msg.target = this;
      sent = queue.enqueueMessage(msg, 0);
    } else {
      RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
      Log.w("Looper", e.getMessage(), e);
    }
    return sent;
  }

  public final Looper getLooper() {
    return mLooper;
  }

  public final void dump(Printer pw, String prefix) {
    pw.println(prefix + this + " @ " + SystemClock.uptimeMillis());
    if (mLooper == null) {
      pw.println(prefix + "looper uninitialized");
    } else {
      mLooper.dump(pw, prefix + "  ");
    }
  }

  @Override
  public String toString() {
    return "Handler (" + getClass().getName() + ") {" + Integer.toHexString(System.identityHashCode(this))
        + "}";
  }

  // final IMessenger getIMessenger() {
  // synchronized (mQueue) {
  // if (mMessenger != null) {
  // return mMessenger;
  // }
  // mMessenger = new MessengerImpl();
  // return mMessenger;
  // }
  // }
  //
  // private final class MessengerImpl extends IMessenger.Stub {
  // public void send(Message msg) {
  // Handler.this.sendMessage(msg);
  // }
  // }

  private final Message getPostMessage(Runnable r) {
    Message m = new Message();
    m.callback = r;
    return m;
  }

  private final Message getPostMessage(Runnable r, Object token) {
    Message m = new Message();
    m.obj = token;
    m.callback = r;
    return m;
  }

  private final void handleCallback(Message message) {
    message.callback.run();
  }

  final MessageQueue mQueue;
  final Looper mLooper;
  final Callback mCallback;
  // IMessenger mMessenger;

}