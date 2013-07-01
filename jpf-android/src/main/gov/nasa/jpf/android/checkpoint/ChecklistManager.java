package gov.nasa.jpf.android.checkpoint;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.android.checkpoint.ChecklistManager.CheckListsState;
import gov.nasa.jpf.annotation.Checkpoint;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.util.StateExtensionClient;
import gov.nasa.jpf.util.StateExtensionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The input script supports that the user can specify a specific list of
 * checkpoints (method annotations) that has to be passed, or that are not
 * allowed to be reached during a specific period.
 * 
 * For example lets say we have an email application and we want to check that
 * if the battery low broadcast is sent to the application the email being
 * composed is saved and we don't want email to be checked automatically.
 * 
 * registerChecklist("email saved", {"receiveLowBattery","saveEmailService",
 * "updateScreen"})
 * registerChecklist("no check mail", {"receiveLowBattery", "disableAutoCheck",
 * "!checkMailAuto"})
 * 
 * sendBroadcast(@lowBatteryIntent)
 * 
 * unregister("email saved")
 * unregister("no check mail")
 * 
 * sendBroadcast(@chargingIntent)
 * 
 * The ChecklistManager is responsible for keeping track of which
 * {@link CheckList} are registered at a specific point of execution. It need to
 * extend StateExtensionClient as it must backtrack the state of the
 * {@link CheckList}. The CheckpointListener notifies the ChecklistManager when
 * a Checkpoint is reached and the ChecklistManager notifies each of the
 * {@link CheckList} in turn. If one of the {@link CheckList} fails it is
 * removed from the registered {@link CheckList} and put in a list of violating
 * {@link CheckList} in the CheckpointListener.
 * 
 * @author Heila van der Merwe
 * @date 2 June 2013
 * 
 */
public class ChecklistManager implements StateExtensionClient<CheckListsState> {
  private static final String TAG = ChecklistManager.class.getSimpleName();
  private static Logger logger = JPF.getLogger(ChecklistManager.class.getSimpleName());

  /** Keeps the state of each currently registered {@link CheckList} */
  private CheckListsState checkListsState;

  /**
   * Listens for Checkpoints that are reached in the SUT and notifies the
   * ChecklistManager.
   */
  private CheckpointListener checkPointListener;

  /**
   * Keeps track of violating {@link CheckList} and reports them at the end of
   * execution
   */
  private ChecklistReporter checkListReporter;

  /** */
  public ChecklistManager() {
    checkListsState = new CheckListsState();
  }

  /**
   * Registers a new {@link CheckList}.
   * 
   * @param arguments
   *          Object array as parsed form script. arguments[0] contains the name
   *          of the {@link CheckList} and the rest of the values are the String
   *          names of the {@link Checkpoint}s that have to be reached.
   * @return returns true if the CheckList could be registered.
   * 
   */
  public boolean registerCheckList(Object[] arguments, int linenumber) {
    // create a new CheckList with name arguments[0]
    CheckList checkList = new CheckList((String) arguments[0], linenumber);

    // check to see that there is not currently such a CheckList registered.
    if (checkListsState.contains(checkList))
      return false;

    // add the CheckPoints 
    for (int i = 1; i < arguments.length; i++) {
      checkList.add((String) arguments[i]);
    }

    //register the new CheckList
    checkListsState.registerCheckList(checkList);

    return true;
  }

  /**
   * Unregisters a CheckList with name "name". The CheckList might already be
   * unregistered if it was violated. If a CheckList could not be unregisted we
   * will log a warning.
   * 
   * @param name
   *          the name of the CheckList
   */
  public void unregisterCheckList(String name) {
    // ask the CheckListsState object to unregister and return the CheckList.
    CheckList list = checkListsState.unregisterCheckList(name);

    // if this list was not finished (all checkpoints reached) report it
    if (list != null && !list.isFinished()) {
      checkListReporter.checkListNotFinished(list);
    } else {
      list = null;
    }
  }
  
  public void getUnfinishedCheckLists() {
    
    // ask the CheckListsState object to unregister and return the CheckList.
   for(CheckList l : checkListsState){
     // if this list was not finished (all checkpoints reached) report it
     if (!l.isFinished()) {
       checkListReporter.checkListNotFinished(l);
     } 
   }
  }

  /**
   * Notifies each registered CheckList that this Checkpoint has been reached.
   * If a CheckList was violated, we report it.
   * 
   * @param name
   *          the name of the {@link Checkpoint} that was reached.
   */
  public void checkpointReached(String name) {
    // notify the checkListsState of the checkPoint
    List<CheckList> violatedLists = checkListsState.checkpointReached(name);

    // report the violated CheckLists
    checkListReporter.checkPointViolated(violatedLists);
  }

  @Override
  public CheckListsState getStateExtension() {
    return checkListsState;
  }

  @Override
  public void restore(CheckListsState stateExtension) {
    checkListsState = stateExtension;
  }

  @Override
  public void registerListener(JPF jpf) {
    StateExtensionListener<CheckListsState> sel = new StateExtensionListener<CheckListsState>(this);
    jpf.addSearchListener(sel);

    checkPointListener = new CheckpointListener(this);
    jpf.addListener(checkPointListener);

    checkListReporter = new ChecklistReporter(this);
    jpf.addPublisherExtension(ConsolePublisher.class, checkListReporter);
  }

  ////////////////////////////////////////
  //           Inner classes            //
  ////////////////////////////////////////

  protected static class CheckListsState extends ArrayList<CheckList> {
    private static final long serialVersionUID = 6544563979638945432L;

    public void registerCheckList(CheckList checklist) {
      this.add(checklist);
    }

    /**
     * Returns the unregistered CheckList
     * 
     * @param name
     * @return
     */
    public CheckList unregisterCheckList(String name) {
      int i = 0;
      for (i = 0; i < this.size(); i++) {
        if (this.get(i).getName().equals(name)) {
          break;
        }
      }

      if (this.size() != 0 && this.get(i).getName().equals(name)) {
        CheckList list = this.remove(i);
        return list;
      } else {
        logger.warning(TAG + ": Could not unregister " + name + " as it was not registered.");
      }
      return null;
    }

    /**
     * Returns a list of CheckLists that violate the checkpoint
     * 
     * @param name
     * @return
     */
    public List<CheckList> checkpointReached(String name) {
      boolean pass = false;
      List<CheckList> failList = new ArrayList<ChecklistManager.CheckList>();

      CheckList list = null;
      for (int i = 0; i < this.size(); i++) {
        list = this.get(i);
        pass = list.checkAnnotation(name);
        if (!pass) {
          failList.add(list);
        }
      }
      for (CheckList listFail : failList) {
        this.remove(listFail);
      }
      return failList;
    }

    @Override
    public Object clone() {
      ArrayList<CheckList> returnList = new ArrayList<CheckList>(this.size());
      for (CheckList list : this) {
        returnList.add((CheckList) list.clone());
      }
      return returnList;
    }

  }

  /**
   * Represents a list of checkpoints( in the form of method annotations) that
   * has to be reached/not reached in the
   * application before the {@link CheckList} is unregistered.
   * 
   * @author Heila van der Merwe
   * @date 2 June 2013
   */
  protected static class CheckList extends ArrayList<String> {
    private static final long serialVersionUID = -3449938086683913845L;

    /** The name of the CheckList. */
    private String name;

    /** The line number on which the CheckList was registered in the Script. */
    private int lineNumber;

    private int currentIndex;

    /**
     * Constructor
     * 
     * @param name
     * @param linenumber
     */
    public CheckList(String name, int linenumber) {
      this.name = name;
      this.lineNumber = linenumber;
      this.currentIndex = -1;
    }

    public String getName() {
      return name;
    }

    public int getLineNumber() {
      return lineNumber;
    }

    /**
     * Adds a checkpoint to the end of the CheckList.
     * 
     * @param name
     *          the name of the checkpoint annotation.
     */
    public void addAnotation(String name) {
      this.add(name);
    }

    /**
     * The CheckList is finished when we have reached the last not negative
     * checkpoint.
     * 
     * @return
     */
    public boolean isFinished() {
      boolean finished = false;
      //if index is == size of list -1 (on last state)
      if (currentIndex == this.size() - 1) {
        finished = true;

      } else if (currentIndex > -1) {
        // if index 1 and no more reachable state in further in list
        finished = true;
        for (int i = currentIndex + 1; i < this.size(); i++) {
          if (!this.get(i).startsWith("!")) {
            finished = false;
          }
        }
      }

      return finished;
    }

    /**
     * Returns false if we have violated a checkpoint
     * 
     * @param name
     * @return
     */
    public boolean checkAnnotation(String name) {
      // if we are already at the last state - ignore the checkPoint
      if (this.currentIndex == this.size() - 1)
        return true;

      String item = null;
      // get the first item in the list that is positive without violating the negative checkpoints
      for (int i = currentIndex + 1; i < this.size(); i++) {
        item = this.get(i);
        if (item.equals(name)) {
          // we have reached a new checkpoint
          currentIndex = i;
          break;
        } else if (item.substring(1).equals(name)) {
          // we have found a !checkpoint equal to this checkpoint - we have been violated
          currentIndex = i;
          return false;
        } else if (item.startsWith("!") && !item.substring(1).equals(name)) {
          // we have found a ! not matching keep going until we either violate/ find
        }
      }
      return true;

    }

    public String getCurrentItem() {
      return this.get(currentIndex);
    }

    public int getCurrentIndex() {
      return currentIndex;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null) {
        return false;
      } else if (!(o instanceof CheckList)) {
        return false;
      }
      return getName().equals(((CheckList) o).getName());
    }

    @Override
    public Object clone() {
      CheckList returnList = new CheckList(this.name, this.lineNumber);
      for (String list : this) {
        returnList.add(new String(list));
      }
      return returnList;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("CheckList " + name + " [");
      for (String list : this) {
        sb.append(new String(list) + ", ");
      }
      sb.append(" ]");
      return super.toString();
    }

    public String getNextItem() {
      return this.get(currentIndex+1);
    }
  }

}
