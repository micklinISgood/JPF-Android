package gov.nasa.jpf.os;

public class JPF_android_os_SystemClock {
	
	/**
     * Sets the current wall time, in milliseconds.  Requires the calling
     * process to have appropriate permissions.
     *
     * @return if the clock was successfully set to the specified time.
     */
    native public static boolean setCurrentTimeMillis(long millis);

    /**
     * Returns milliseconds since boot, not counting time spent in deep sleep.
     * <b>Note:</b> This value may get reset occasionally (before it would
     * otherwise wrap around).
     *
     * @return milliseconds of non-sleep uptime since boot.
     */
    native public static long uptimeMillis();

    /**
     * Returns milliseconds since boot, including time spent in sleep.
     *
     * @return elapsed milliseconds since boot.
     */
    native public static long elapsedRealtime();
    
    /**
     * Returns milliseconds running in the current thread.
     * 
     * @return elapsed milliseconds in the thread
     */
    public static native long currentThreadTimeMillis();

    /**
     * Returns microseconds running in the current thread.
     * 
     * @return elapsed microseconds in the thread
     * 
     * @hide
     */
    public static native long currentThreadTimeMicro();

    /**
     * Returns current wall time in  microseconds.
     * 
     * @return elapsed microseconds in wall time
     * 
     * @hide
     */
    public static native long currentTimeMicro();

}
