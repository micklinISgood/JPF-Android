package android.app;

public class QueuedWork {

  public static void waitToFinish() {
    // TODO stub

  }

  public static boolean hasPendingWork() {
    return false;
  }

  public static Executor singleThreadExecutor() {
    return new Executor();
  }

  public static class Executor {
    public void execute(Runnable run) {
      Thread t = new Thread(run);
      t.run();
    }
  }
}
