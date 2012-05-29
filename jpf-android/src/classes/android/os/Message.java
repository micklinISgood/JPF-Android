package android.os;

/**
 * Model of an Android Message Object. Messenger not implemented currently will
 * be done as soon as Binder communication is modelled
 * 
 * @see {@link Handler}.
 */
public final class Message {
	public int what;
	public int arg1;
	public int arg2;

	public Object obj;
	long when;
	Handler target;
	Runnable callback;

	// public Messenger replyTo;
	// Bundle data;

	public Message() {
		// do nothing
	}

	public Message(Handler h) {
		this.target = h;
	}

	public Message(Handler h, Runnable callback) {
		this.target = h;
		this.callback = callback;
	}

	/**
	 * Return the targeted delivery time of this message, in milliseconds.
	 */
	public long getWhen() {
		return when;
	}

	public void setTarget(Handler target) {
		this.target = target;
	}

	/**
	 * Retrieve the a {@link android.os.Handler Handler} implementation that
	 * will receive this message. The object must implement
	 * {@link android.os.Handler#handleMessage(android.os.Message)
	 * Handler.handleMessage()}. Each Handler has its own name-space for message
	 * codes, so you do not need to worry about yours conflicting with other
	 * handlers.
	 */
	public Handler getTarget() {
		return target;
	}

	/**
	 * Retrieve callback object that will execute when this message is handled.
	 * This object must implement Runnable. This is called by the
	 * <em>target</em> {@link Handler} that is receiving this Message to
	 * dispatch it. If not set, the message will be dispatched to the receiving
	 * Handler's {@link Handler#handleMessage(Message Handler.handleMessage())}.
	 */
	public Runnable getCallback() {
		return callback;
	}

	/**
	 * Sets a Bundle of arbitrary data values. Use arg1 and arg1 members as a
	 * lower cost way to send a few simple integer values, if you can.
	 * 
	 * @see #getData()
	 * @see #peekData()
	 */
	// public void setData(Bundle data) {
	// this.data = data;
	// }

	/**
	 * Sends this Message to the Handler specified by {@link #getTarget}. Throws
	 * a null pointer exception if this field has not been set.
	 */
	public void sendToTarget() {
		target.sendMessage(this);
	}

	public String toString() {
		return "";
	}

	String toString(long now) {
		StringBuilder b = new StringBuilder();

		b.append("{ what=");
		b.append(what);

		b.append(" when=");

		if (arg1 != 0) {
			b.append(" arg1=");
			b.append(arg1);
		}

		if (arg2 != 0) {
			b.append(" arg2=");
			b.append(arg2);
		}

		if (obj != null) {
			b.append(" obj=");
			b.append(obj);
		}

		b.append(" }");

		return b.toString();
	}

	public void recycle() {

	}

//	public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
//		public Message createFromParcel(Parcel source) {
//			Message msg = new Message();
//			msg.readFromParcel(source);
//			return msg;
//		}
//
//		public Message[] newArray(int size) {
//			return new Message[size];
//		}
//	};

	public int describeContents() {
		return 0;
	}

//	public void writeToParcel(Parcel dest, int flags) {
//		if (callback != null) {
//			throw new RuntimeException(
//					"Can't marshal callbacks across processes.");
//		}
//		dest.writeInt(what);
//		dest.writeInt(arg1);
//		dest.writeInt(arg2);
//		if (obj != null) {
//			try {
//				Parcelable p = (Parcelable) obj;
//				dest.writeInt(1);
//				dest.writeParcelable(p, flags);
//			} catch (ClassCastException e) {
//				throw new RuntimeException(
//						"Can't marshal non-Parcelable objects across processes.");
//			}
//		} else {
//			dest.writeInt(0);
//		}
//		dest.writeLong(when);
		// dest.writeBundle(data);
		// Messenger.writeMessengerOrNullToParcel(replyTo, dest);
	//}

//	private final void readFromParcel(Parcel source) {
//		what = source.readInt();
//		arg1 = source.readInt();
//		arg2 = source.readInt();
//		if (source.readInt() != 0) {
//			obj = source.readParcelable(getClass().getClassLoader());
//		}
//		when = source.readLong();
		// data = source.readBundle();
		// replyTo = Messenger.readMessengerOrNullFromParcel(source);
	//}

}
