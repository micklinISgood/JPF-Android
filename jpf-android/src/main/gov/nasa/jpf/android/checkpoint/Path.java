package gov.nasa.jpf.android.checkpoint;


public class Path {
  protected int pathID;
  protected int eventID;
  public Path(int pathID, int eventID) {
    this.pathID = pathID;
    this.eventID = eventID;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + eventID;
    result = prime * result + pathID;
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Path other = (Path) obj;
    if (eventID != other.eventID)
      return false;
    if (pathID != other.pathID)
      return false;
    return true;
  }
  
  @Override
  public Object clone() {
    return new Path(pathID, eventID);
  }

  @Override
  public String toString() {
    return "Path [pathID=" + pathID + ", eventID=" + eventID + "]";
  }

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
  
  
}
