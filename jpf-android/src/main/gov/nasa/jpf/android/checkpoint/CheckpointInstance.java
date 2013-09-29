package gov.nasa.jpf.android.checkpoint;

public class CheckpointInstance {

  String name;
  String threadName;
  String threadAlias;
  int threadID;

  Path path;

  public CheckpointInstance(String name, String threadName, String threadAlias, int threadID, int pathID,
      int eventID) {
    this.name = name;
    this.threadName = threadName;
    this.threadAlias = threadAlias;
    this.threadID = threadID;
    path = new Path(pathID, eventID);
  }

  public CheckpointInstance(Checkpoint cp, int threadID, int pathID, int eventID) {
    this.name = cp.getName();
    this.threadName = cp.getThreadName();
    this.threadID = threadID;
    path = new Path(pathID, eventID);
  }

  public Checkpoint toCheckpoint() {
    return new Checkpoint(name, "");
  }

  @Override
  public String toString() {
    return "CheckpointInstance [name=" + name + ", threadName=" + threadName + ", threadAlias=" + threadAlias
        + ", threadID=" + threadID + ", path=" + path + "]";
  }

  @Override
  public Object clone() {
    CheckpointInstance ins = new CheckpointInstance(new String(name), new String(threadName), new String(
        threadAlias), threadID, path.pathID, path.eventID);
    return ins;
  }
}
