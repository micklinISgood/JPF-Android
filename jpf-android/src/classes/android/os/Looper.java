package android.os;

import android.util.Printer;

/**
 * Model of Android's {@link Looper} object. This class has minimal changes from
 * the original. At some point it will be removed and the original class will be
 * used.
 * 
 */
public class Looper {
	private static final String TAG = "Looper";

	// sThreadLocal.get() will return null unless you've called prepare().
	static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
	
	//List<Handler> handlers; Keep reference to all handlers
	final MessageQueue mQueue;
	final Thread mThread;
	volatile boolean mRun;

	private Printer mLogging = null;
	private static Looper mMainLooper = null; // guarded by Looper.class

	/**
	 * Initialize the current thread as a looper. This gives you a chance to
	 * create handlers that then reference this looper, before actually starting
	 * the loop. Be sure to call {@link #loop()} after calling this method, and
	 * end it by calling {@link #quit()}.
	 */
	public static void prepare() {
		if (sThreadLocal.get() != null) {
			throw new RuntimeException(
					"Only one Looper may be created per thread");
		}
		sThreadLocal.set(new Looper());
	}

	/**
	 * Initialize the current thread as a looper, marking it as an application's
	 * main looper. The main looper for your application is created by the
	 * Android environment, so you should never need to call this function
	 * yourself. See also: {@link #prepare()}
	 */
	public static void prepareMainLooper() {
		prepare();
		setMainLooper(myLooper());
		myLooper().mQueue.mQuitAllowed = false;
	}

	private synchronized static void setMainLooper(Looper looper) {
		mMainLooper = looper;
	}

	/**
	 * Returns the application's main looper, which lives in the main thread of
	 * the application.
	 */
	public synchronized static Looper getMainLooper() {
		return mMainLooper;
	}

	/**
	 * Run the message queue in this thread. Be sure to call {@link #quit()} to
	 * end the loop.
	 */
	public static void loop() {
		Looper me = myLooper();
		if (me == null) {
			throw new RuntimeException(
					"No Looper; Looper.prepare() wasn't called on this thread.");
		}
		MessageQueue queue = me.mQueue;

		// Make sure the identity of this thread is that of the local process,
		// and keep track of what that identity token actually is.
		// Binder.clearCallingIdentity();
		// final long ident = Binder.clearCallingIdentity();

		while (true) {
			// This must be in a local variable, in case a UI event sets the
			// logger
			Printer logging = me.mLogging;
			Message msg = queue.next(); // might block
			if (msg != null) {
				if (msg.target == null) {
					// No target is a magic identifier for the quit message.
					if (logging != null) {
						logging.println(">>>>> Stopping loop " + msg.target
								+ " " + msg.callback + ": " + msg.what);

					}
					return;
				}

				if (logging != null) {
					logging.println(">>>>> Dispatching to " + msg.target + " "
							+ msg.callback + ": " + msg.what);
				}

				msg.target.dispatchMessage(msg);

				if (logging != null) {
					logging.println("<<<<< Finished to " + msg.target + " "
							+ msg.callback);
				}

				msg.recycle();
			}
		}
	}

	/**
	 * Return the Looper object associated with the current thread. Returns null
	 * if the calling thread is not associated with a Looper.
	 */
	public static Looper myLooper() {
		return sThreadLocal.get();
	}

	/**
	 * Control logging of messages as they are processed by this Looper. If
	 * enabled, a log message will be written to <var>printer</var> at the
	 * beginning and ending of each message dispatch, identifying the target
	 * Handler and message contents.
	 * 
	 * @param printer
	 *            A Printer object that will receive log messages, or null to
	 *            disable message logging.
	 */
	public void setMessageLogging(Printer printer) {
		mLogging = printer;
	}

	/**
	 * Return the {@link MessageQueue} object associated with the current
	 * thread. This must be called from a thread running a Looper, or a
	 * NullPointerException will be thrown.
	 */
	public static MessageQueue myQueue() {
		return myLooper().mQueue;
	}

	private Looper() {
		mQueue = new MessageQueue();
		mRun = true;
		mThread = Thread.currentThread();
	}

	public void quit() {
		Message msg = new Message();
		// NOTE: By enqueueing directly into the message queue, the
		// message is left with a null target. This is how we know it is
		// a quit message.
		mQueue.enqueueMessage(msg, 0);
	}

	/**
	 * Return the Thread associated with this Looper.
	 */
	public Thread getThread() {
		return mThread;
	}

	/** @hide */
	public MessageQueue getQueue() {
		return mQueue;
	}

	public void dump(Printer pw, String prefix) {
//		pw = PrefixPrinter.create(pw, prefix);
//		pw.println(this.toString());
//		pw.println("mRun=" + mRun);
//		pw.println("mThread=" + mThread);
//		pw.println("mQueue=" + ((mQueue != null) ? mQueue : "(null"));
//		if (mQueue != null) {
//			synchronized (mQueue) {
//				long now = SystemClock.uptimeMillis();
//				Message msg = null;
//				int i = 0;
//				for (; i < mQueue.getSize(); i++) {
//					msg = mQueue.getMessage(i);
//					pw.println("  Message " + i + ": " + msg.toString(now));
//				}
//				pw.println("(Total messages: " + (i + 1) + ")");
//			}
//		}
	}

	public String toString() {
		return "Looper{" + Integer.toHexString(System.identityHashCode(this))
				+ "}";
	}

	/**
	 * @hide
	 */
	public static interface Profiler {
		void profile(Message message, long wallStart, long wallTime,
				long threadStart, long threadTime);
	}
}
