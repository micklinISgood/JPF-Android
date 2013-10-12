package gov.nasa.jpf.android.checkpoint;

public class CheckpointInstance {

  private String name;
  private String threadName;
  private String threadAlias;
  private int threadID;
  private Path path;

  public CheckpointInstance(String name, String threadName, String threadAlias, int threadID, int eventID,
      String pathID) {
    this.setName(name);
    this.setThreadName(threadName);
    this.setThreadAlias(threadAlias);
    this.threadID = threadID;
    path = new Path(eventID, pathID);
  }

  public CheckpointInstance(Checkpoint cp, int threadID, int eventID, String pathID) {
    this.setName(cp.getName());
    this.setThreadName(cp.getThreadName());
    this.threadID = threadID;
    path = new Path(eventID, pathID);
  }

  public Checkpoint toCheckpoint() {
    return new Checkpoint(getName(), "");
  }

  @Override
  public String toString() {
    return "CheckpointInstance [name=" + getName() + ", threadName=" + getThreadName() + ", threadAlias=" + getThreadAlias()
        + ", threadID=" + threadID + ", path=" + path + "]";
  }

  @Override
  public Object clone() {
    CheckpointInstance ins = new CheckpointInstance(new String(getName()), new String(getThreadName()), new String(
        getThreadAlias()), threadID,  path.eventID, new String(path.pathID));
    return ins;
  }
  
  public int getEventID(){
    return path.getEventID();
  }
  
  public String getPathID(){
    return path.getPathID();
  }
  
  public Path getPath(){
    return path;
  }

  public String getThreadName() {
    return threadName;
  }

  public void setThreadName(String threadName) {
    this.threadName = threadName;
  }

  public String getThreadAlias() {
    return threadAlias;
  }

  public void setThreadAlias(String threadAlias) {
    this.threadAlias = threadAlias;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
