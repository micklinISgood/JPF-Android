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

  private int pathID;
  private int eventID;

  public int getPathID() {
    return pathID;
  }

  public void setPathID(int pathID) {
    this.pathID = pathID;
  }

  public int getEventID() {
    return eventID;
  }

  public void setEventID(int eventID) {
    this.eventID = eventID;
  }

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
   * Same as {@link #obtain()}, but sets the values for both <em>target</em> and
   * <em>what</em> members on the Message.
   * 
   * @param h
   *          Value to assign to the <em>target</em> member.
   * @param what
   *          Value to assign to the <em>what</em> member.
   * @return A Message object from the global pool.
   */
  public static Message obtain(Handler h, int what) {
    Message m = new Message();
    m.target = h;
    m.what = what;

    return m;
  }

  /**
   * Same as {@link #obtain()}, but sets the values of the <em>target</em>,
   * <em>what</em>, and <em>obj</em> members.
   * 
   * @param h
   *          The <em>target</em> value to set.
   * @param what
   *          The <em>what</em> value to set.
   * @param obj
   *          The <em>object</em> method to set.
   * @return A Message object from the global pool.
   */
  public static Message obtain(Handler h, int what, Object obj) {
    Message m = new Message();
    m.target = h;
    m.what = what;
    m.obj = obj;

    return m;
  }

  /**
   * Same as {@link #obtain()}, but sets the values of the <em>target</em>,
   * <em>what</em>, <em>arg1</em>, and <em>arg2</em> members.
   * 
   * @param h
   *          The <em>target</em> value to set.
   * @param what
   *          The <em>what</em> value to set.
   * @param arg1
   *          The <em>arg1</em> value to set.
   * @param arg2
   *          The <em>arg2</em> value to set.
   * @return A Message object from the global pool.
   */
  public static Message obtain(Handler h, int what, int arg1, int arg2) {
    Message m = new Message();
    m.target = h;
    m.what = what;
    m.arg1 = arg1;
    m.arg2 = arg2;

    return m;
  }

  /**
   * Same as {@link #obtain()}, but sets the values of the <em>target</em>,
   * <em>what</em>, <em>arg1</em>, <em>arg2</em>, and <em>obj</em> members.
   * 
   * @param h
   *          The <em>target</em> value to set.
   * @param what
   *          The <em>what</em> value to set.
   * @param arg1
   *          The <em>arg1</em> value to set.
   * @param arg2
   *          The <em>arg2</em> value to set.
   * @param obj
   *          The <em>obj</em> value to set.
   * @return A Message object from the global pool.
   */
  public static Message obtain(Handler h, int what, int arg1, int arg2, Object obj) {
    Message m = new Message();
    m.target = h;
    m.what = what;
    m.arg1 = arg1;
    m.arg2 = arg2;
    m.obj = obj;

    return m;
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
   * This object must implement Runnable. This is called by the <em>target</em>
   * {@link Handler} that is receiving this Message to
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
