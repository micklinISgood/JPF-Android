package android.os;

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
	private static final String TAG = "Handler";

	/**
	 * Callback interface you can use when instantiating a Handler to avoid
	 * having to implement your own subclass of Handler.
	 */
	public interface Callback {
		public boolean handleMessage(Message msg);
	}

	public Handler() {
		mLooper = Looper.myLooper();
		if (mLooper == null) {
			throw new RuntimeException(
					"Can't create handler inside thread that has not called Looper.prepare()");
		}
		mQueue = mLooper.mQueue;
		mCallback = null;
	}

	/**
	 * Constructor associates this handler with the queue for the current thread
	 * and takes a callback interface in which you can handle messages.
	 */
	public Handler(Callback callback) {
		mLooper = Looper.myLooper();
		if (mLooper == null) {
			throw new RuntimeException(
					"Can't create handler inside thread that has not called Looper.prepare()");
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
	 * Subclasses must implement this to receive messages.
	 */
	public void handleMessage(Message msg) {
	}

	public void dispatchMessage(Message msg) {
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

	public final boolean post(Runnable r) {
		Message m = new Message();
		m.callback = r;
		return sendMessage(m);
	}

	public final boolean sendMessage(Message msg) {
		// enqueue message
		boolean sent = false;
		MessageQueue queue = mQueue;
		msg.target = this;
		sent = queue.enqueueMessage(msg);
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
			RuntimeException e = new RuntimeException(this
					+ " sendMessageAtTime() called with no mQueue");
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
		return "Handler (" + getClass().getName() + ") {"
				+ Integer.toHexString(System.identityHashCode(this)) + "}";
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