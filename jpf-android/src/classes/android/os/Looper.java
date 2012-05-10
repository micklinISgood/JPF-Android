package android.os;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.util.JPFLogger;

public class Looper {
	private static final String TAG = "Looper";
	static JPFLogger logger = JPF.getLogger(TAG);
	static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

	final MessageQueue mQueue;
	final Thread mThread;
	volatile boolean mRun;

	private Looper() {
		mQueue = new MessageQueue();
		mRun = true;
		mThread = Thread.currentThread();
	}

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
		Looper l = new Looper();
		sThreadLocal.set(l);
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
		for (int i = 0; i < 10000000; i++) {

			Message msg = queue.next(); // might block
			if (msg != null) {
				if (msg.target == null) {
					// Quit has been called
					return;
				}
				msg.target.dispatchMessage(msg);
				msg.recycle();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
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
	 * Return the {@link MessageQueue} object associated with the current
	 * thread. This must be called from a thread running a Looper, or a
	 * NullPointerException will be thrown.
	 */
	public static MessageQueue myQueue() {
		return myLooper().mQueue;
	}

	public void quit() {
		Message msg = Message.obtain();
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

	// public void dump(Printer pw, String prefix) {
	// pw = PrefixPrinter.create(pw, prefix);
	// pw.println(this.toString());
	// pw.println("mRun=" + mRun);
	// pw.println("mThread=" + mThread);
	// pw.println("mQueue=" + ((mQueue != null) ? mQueue : "(null"));
	// if (mQueue != null) {
	// synchronized (mQueue) {
	// long now = SystemClock.uptimeMillis();
	// Message msg = mQueue.mMessages;
	// int n = 0;
	// while (msg != null) {
	// pw.println("  Message " + n + ": " + msg.toString(now));
	// n++;
	// msg = msg.next;
	// }
	// pw.println("(Total messages: " + n + ")");
	// }
	// }
	// }

	public String toString() {
		return "Looper{" + Integer.toHexString(System.identityHashCode(this))
				+ "}";
	}

}
