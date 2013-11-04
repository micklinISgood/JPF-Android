package java.util;


public class Timer {

  private static final class TimerImpl extends Thread {

    /**
     * True if the method cancel() of the Timer was called or the !!!stop()
     * method was invoked
     */
    private boolean cancelled;

    /**
     * True if the Timer has become garbage
     */
    private boolean finished;

    /**
     * Contains scheduled events, sorted according to {@code when} field of
     * TaskScheduled object.
     */
    ArrayList<TimerTask> tasks = new ArrayList<TimerTask>();

    /**
     * Starts a new timer.
     * 
     * @param name
     *          thread's name
     * @param isDaemon
     *          daemon thread or not
     */
    TimerImpl(String name, boolean isDaemon) {
      this.setName(name);
      this.setDaemon(isDaemon);
      this.start();
    }

    /**
     * This method will be launched on separate thread for each Timer
     * object.
     */
    @Override
    public void run() {
      while (true) {
        TimerTask task;
        synchronized (this) {
          // need to check cancelled inside the synchronized block
          if (cancelled) {
            return;
          }
          if (tasks.isEmpty()) {
            if (finished) {
              return;
            }
            // no tasks scheduled -- sleep until any task appear
            try {
              this.wait();
            } catch (InterruptedException ignored) {
            }
            continue;
          }

          long currentTime = System.currentTimeMillis();

          //                task = tasks.minimum();
          //                boolean sleep;
          //
          //                synchronized (task.lock) {
          //                    if (task.cancelled) {
          //                        tasks.delete(0);
          //                        continue;
          //                    }
          //
          //                    // check the time to sleep for the first task scheduled
          //                    sleep = Verify.randomBool();
          //                }

          //                if (sleep) {
          //                    // sleep!
          //                    try {
          //                        this.wait();
          //                    } catch (InterruptedException ignored) {
          //                    }
          //                    continue;
          //                }

          // no sleep is necessary before launching the task

          //                synchronized (task.lock) {
          //                    int pos = 0;
          //                    if (tasks.minimum().when != task.when) {
          //                        pos = tasks.getTask(task);
          //                    }
          //                    if (task.cancelled) {
          //                        tasks.delete(tasks.getTask(task));
          //                        continue;
          //                    }
          //
          //                    // set time to schedule
          //                    task.setScheduledTime(task.when);
          //
          //                    // remove task from queue
          //                    tasks.delete(pos);
          //
          //                    // set when the next task should be launched
          //                    if (task.period >= 0) {
          //                        // this is a repeating task,
          //                        if (task.fixedRate) {
          //                            // task is scheduled at fixed rate
          //                            task.when = task.when + task.period;
          //                        } else {
          //                            // task is scheduled at fixed delay
          //                            task.when = System.currentTimeMillis()
          //                                    + task.period;
          //                        }
          //
          //                        // insert this task into queue
          //                        insertTask(task);
          //                    } else {
          //                        task.when = 0;
          //                    }
          //                }
          //            }

          //            boolean taskCompletedNormally = false;
          //            try {
          //                task.run();
          //                taskCompletedNormally = true;
          //            } finally {
          //                if (!taskCompletedNormally) {
          //                    synchronized (this) {
          //                        cancelled = true;
          //                    }
          //                }
          //            }
        }
      }
    }

    private void insertTask(TimerTask newTask) {
      // callers are synchronized
      tasks.add(newTask);
      this.notify();
    }

    /**
     * Cancels timer.
     */
    public synchronized void cancel() {
      cancelled = true;
      reset();
      this.notify();
    }

    public void reset() {
      tasks = new ArrayList<TimerTask>();
    }

    //? public int purge() {
    //      if (tasks.isEmpty()) {
    //        return 0;
    //      }
    //      // callers are synchronized
    //      int deletedCancelledNumber = 0;
    //      deleteIfCancelled();
    //      return deletedCancelledNumber;
    //
    //    }
    //
    //    public void deleteIfCancelled(int deletedCancelledNumber) {
    //      for (int i = 0; i < size; i++) {
    //        if (tasks.get(i).cancelled) {
    //          deletedCancelledNumber++;
    //          delete(i);
    //          // re-try this point
    //          i--;
    //        }
    //      }
    //    }
    //}
  }
}
// private static long timerId;

/**
 * Creates a new named {@code Timer} which does not run as a daemon thread.
 * 
 * @param name
 *          the name of the Timer.
 * @throws NullPointerException
 *           is {@code name} is {@code null}
 */
//  public Timer(String name) {
//    this(name, false);
//  }
//
//  /**
//   * Creates a new {@code Timer} which may be specified to be run as a daemon
//   * thread.
//   * 
//   * @param isDaemon
//   *          {@code true} if the {@code Timer}'s thread should be a daemon
//   *          thread.
//   */
//  public Timer(boolean isDaemon) {
//    this("Timer-" + Timer.nextId(), isDaemon);
//  }
//
//  /**
//   * Creates a new non-daemon {@code Timer}.
//   */
//  public Timer() {
//    this(false);
//  }
//
//  TimerImpl timerImpl;
//
//  public Timer(String name, boolean daemon) {
//    this.timerImpl = new TimerImpl(name, daemon);
//
//  }
//
//  private synchronized static long nextId() {
//    return timerId++;
//  }
//}
