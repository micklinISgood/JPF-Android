package android.os;

import java.util.LinkedList;

/**
 * Model of Android's MessageQueue. A {@link LinkedList} of messages are kept
 * instead of a pool of messages. Idlehandlers have been removed. Further
 * instead of polling for user in in the next() method when there are no current
 * messages to process, the thread does not block and calls native method
 * processScriptAction to generate new events. The original MessageQueue Queued
 * the messages in a priorityQueue by time. The time is not currently used in
 * this message queue.
 */
public class MessageQueue {
	private LinkedList<Message> mMessages = new LinkedList<Message>();
	Message mMessage;

	private boolean mQuiting;
	boolean mQuitAllowed = true;

	native boolean processScriptAction();

	public MessageQueue() {
		// nothing
	}

	/**
	 * Return the next message in the message queue. If the queue is empty new
	 * events are generated by calling the native method processScriptAction to
	 * generate new events.
	 * 
	 * @return The next {@link Message} object to handle.
	 */
	final protected Message next() {
		Message m = null;
		do {
			if ((m = nextNonBlocked()) != null) {
				return m;
			}

		} while (processScriptAction());
		// this must not happen! an ending message (with target will be
		// dispatched before this is executed)
		return new Message();
	}

	/**
	 * Without blocking returns the next message in the queue or null if the
	 * queue is empty.
	 * 
	 * @return
	 */
	final synchronized Message nextNonBlocked() {
		if (mMessages.isEmpty()) {
			return null;
		} else {
			return mMessages.removeFirst();
		}
	}

	final synchronized boolean enqueueMessage(Message msg) {
		return mMessages.add(msg);
	}

	final synchronized boolean enqueueMessage(Message msg, long when) {
		// TODO when?
		if (when <= 0) {
			mMessages.push(msg);
			return true;
		}
		return mMessages.add(msg);
	}

	final void removeMessages(Handler h, Runnable r, Object object) {
		// not necessary to implement
	}

	final void removeCallbacksAndMessages(Handler h, Object object) {
		// not necessary to implement
	}

	public int getSize() {
		return mMessages.size();
	}

	public Message getMessage(int i) {
		return mMessages.get(i);
	}

}