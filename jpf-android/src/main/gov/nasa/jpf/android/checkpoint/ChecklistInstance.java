package gov.nasa.jpf.android.checkpoint;

/**
 * 
 * @author Heila van der Merwe
 * 
 */
public class ChecklistInstance {
  private static int count = 0;

  private int id;

  /** Checklist this instance in running */
  private final Checklist checklist;

  public CheckpointInstance[] checkpointsMatched;

  /** Identifies the current path the Checklist is following */
  private Path path;

  /**
   * Maps thread name to id to make sure mutiple threads in the same path can be
   * distinguished
   */
  // private HashMap<String, Integer> threadIds;

  /** Current position in the Checklist */
  private int index = -1;

  public ChecklistInstance(Checklist checklist, int eventID, String pathID) {
    this.checklist = checklist;
    //  this.threadIds = new HashMap<String, Integer>();
    this.path = new Path(eventID, pathID);
    this.id = count++;
    this.checkpointsMatched = new CheckpointInstance[this.checklist.size()];
  }

  public boolean conditionMatched() {
    boolean matched = true;
    for (int i = index + 1; i <= checklist.getConditionIndex(); i++) {
      if (!checklist.getCheckpoint(i).isNegative()) {
        matched = false;
        break;
      } else if (checklist.getCheckpoint(i).isNegative() && this.checkpointsMatched[i] != null) {
        matched = false;
        break;
      }
    }
    return matched;
  }

  /**
   * The CheckList is finished when we have reached the last not negative
   * checkpoint.
   * 
   * @return
   */
  public boolean isCompleted() {
    // if no more reachable states further in list
   
    if (index == checklist.size() - 1)
      return true;
    else
      return false;
  }

  public Path getPath() {
    return path;
  }

  public int getEventID() {
    return path.getEventID();
  }

  public String getPathID() {
    return path.getPathID();
  }

  /**
   * Returns false if we have violated this checklist
   * 
   * @param name
   * @return
   */
  public boolean match(CheckpointInstance newPoint) {
    assert (newPoint.getPath().equals(this.getPath()));

    // if we are already at the last state - ignore the checkPoint
    if (index == checklist.size() - 1)
      return true;

    Checkpoint point = null;
    // get the first item in the list that is positive without violating the negative checkpoints
    for (int i = index + 1; i < checklist.size(); i++) {
      point = checklist.getCheckpoint(i);

      if (point.getName().equals(newPoint.getName())) {
        // we have reached this new checkpoint

        if (point.isNegative()) {

          // we have found a !checkpoint equal to this checkpoint
          checkpointsMatched[i] = newPoint;
          //index = i;
          return false;

        } else if (newPoint.getThreadAlias() != null
            && !newPoint.getThreadName().equals(newPoint.getThreadAlias())) {
          // this point was matched with the wrong thead
          checkpointsMatched[i] = newPoint;
          //index = i;
          return false;

        } else {
          checkpointsMatched[i] = newPoint;
          index = i;
          break;
        }
      } else if (!point.isNegative()) {
        //we do not match the next point which is fine, we just ignore this point
        // checkpointsMatched[i] = newPoint;
        break;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "ChecklistInstance [checklist=" + checklist + ", path=" + path.toString() + ", index=" + index
        + "]";
  }

  @Override
  public Object clone() {
    ChecklistInstance state = new ChecklistInstance(this.checklist, this.getEventID(), this.getPathID());
    state.id = this.id;
    state.checkpointsMatched = this.checkpointsMatched.clone();
    state.index = this.index;
    //state.threadIds = (HashMap<String, Integer>) this.threadIds.clone();
    return state;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ChecklistInstance other = (ChecklistInstance) obj;
    if (id != other.id)
      return false;
    return true;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public Checklist getChecklist() {
    return checklist;
  }

  public void setPath(Path path) {
    this.path = path;
  }

}