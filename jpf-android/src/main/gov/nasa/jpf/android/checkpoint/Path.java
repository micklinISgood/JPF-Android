package gov.nasa.jpf.android.checkpoint;

public class Path {
  protected String pathID;
  protected int eventID;

  public Path(int eventID,String pathID) {
    this.pathID = pathID;
    this.eventID = eventID;
  }
 

  @Override
  public Object clone() {
    return new Path(eventID,new String(pathID));
  }

  @Override
  public String toString() {
    return "Path [ eventID=" + eventID +", pathID=" + pathID + "]";
  }

 

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + eventID;
    result = prime * result + ((pathID == null) ? 0 : pathID.hashCode());
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
    if (pathID == null) {
      if (other.pathID != null)
        return false;
    } else if (!pathID.equals(other.pathID))
      return false;
    return true;
  }


  public String getPathID() {
    return pathID;
  }


  public void setPathID(String pathID) {
    this.pathID = pathID;
  }


  public int getEventID() {
    return eventID;
  }

  public void setEventID(int eventID) {
    this.eventID = eventID;
  }

}
