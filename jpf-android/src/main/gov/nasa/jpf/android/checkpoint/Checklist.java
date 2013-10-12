package gov.nasa.jpf.android.checkpoint;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a list of checkpoints that has to be reached/may not be reached in
 * the application.
 * 
 * @author Heila van der Merwe
 * @date 2 June 2013
 */
public class Checklist {

  /** The unique name of the CheckList. */
  private final String name;

  /** Number of times it has completed successfully */
  private int count;

  /** Index of the last condition item */
  private int conditionIndex = -1;

  /** List of Checkpoints */
  private List<Checkpoint> list;

  public Checklist(String name) {
    this.name = name;
    this.list = new LinkedList<Checkpoint>();
  }

  public Checklist(String name, List<Checkpoint> list, int conditionIndex) {
    this.name = name;
    this.conditionIndex = conditionIndex;
    this.list = list;

  }

  public String getName() {
    return name;
  }

  public int getConditionIndex() {
    return conditionIndex;
  }

  public void setConditionIndex(int conditionIndex) {
    this.conditionIndex = conditionIndex;
  }

  public Checkpoint getCheckpoint(int currentIndex) {
    return list.get(currentIndex);
  }

  public int size() {
    return this.list.size();
  }

  public void increaseCount() {
    count++;
  }

  public int getCount() {
    return count;
  }

  public void addCheckpoint(Checkpoint cp) {
    this.list.add(cp);
  }

  /**
   * Returns true if the checkpoint matches a negative or first positive point
   * in the condition.
   * 
   * @param name
   * @return
   */
  public boolean startsWith(Checkpoint point) {
    Checkpoint item = null;
    // get the first positive item in the list until condition
    for (int i = 0; i < size(); i++) {
      item = list.get(i);
      // we have reached a new checkpoint
      if (!item.isNegative() && !item.equals(point)) {
        return false;
      } else if (item.isNegative() && !item.equals(point)) {
        continue;
      } else if (item.equals(point)) {
        // we have found a !checkpoint equal to this checkpoint
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "Checklist [name=" + name + ", count=" + count + ", conditionIndex=" + conditionIndex + ", list="
        + list.toString() + "]";
  }

}
