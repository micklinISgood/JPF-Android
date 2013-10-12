package gov.nasa.jpf.android.checkpoint;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.android.checkpoint.ChecklistParser.ChecklistDefinitions;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.util.StateExtensionListener;
import gov.nasa.jpf.util.script.AndroidScriptEnvironment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * The ChecklistManager is responsible for keeping track of which
 * {@link Checklist} are registered at a specific point of execution. It need to
 * extend StateExtensionClient as it must backtrack the state of the
 * {@link Checklist}. The CheckpointListener notifies the ChecklistManager when
 * a Checkpoint is reached and the ChecklistManager notifies each of the
 * {@link Checklist} in turn. If one of the {@link Checklist} fails it is
 * removed from the registered {@link Checklist} and put in a list of violating
 * {@link Checklist} in the CheckpointListener.
 * 
 * @author Heila van der Merwe
 * @date 2 June 2013
 * 
 */
public class ChecklistManager implements ChecklistReporter, CheckpointProcessor, ChecklistStateManager {
  public static final String TAG = ChecklistManager.class.getSimpleName();
  private static Logger logger = JPF.getLogger(TAG);

  /** Publishes checklist violations */
  private ChecklistPublisher checkListPublisher;

  /** Active Checklists */
  private List<Checklist> checklists;

  /** Running ChecklistInstances */
  private Map<Integer, List<ChecklistInstance>> checklistInstances;

  /** Mappings */
  private Map<String, Checkpoint> mappings;

  private ChecklistDefinitions def;

  public ChecklistManager(String filename) throws ParseException {
    if (checklistInstances == null) {

      // parse checklists definition file
      ChecklistParser parser = new ChecklistParser(filename);

      // parse and store active checklists in checklistList
      def = parser.parse();

      checklists = new LinkedList<Checklist>();

      // create new list of running checklists
      checklistInstances = new HashMap<Integer, List<ChecklistInstance>>();

      mappings = def.getMappings();
    }
    logger.info("ChecklistManager: Ready!");
  }

  public void activateChecklists(String[] activeChecklists) throws ChecklistException {
    if (activeChecklists != null) {
      for (String c : activeChecklists) {
        Checklist list = def.getChecklists().get(c.trim());
        if (list != null) {
          registerChecklist(list);
        } else {
          throw new ChecklistException("Activated checklist " + c
              + " not defined in Checklist definition file.");
        }
      }
    } else {
      logger.warning(TAG + ": no active Checklists defined in config.");
      ;
    }
  }

  /**
   * Registers a new {@link Checklist}.
   * 
   */
  private void registerChecklist(Checklist checklist) {
    logger.fine(TAG + ": Registering  checklist " + checklist.toString());

    // add new checklist to checklists list
    if (checklist != null)
      this.checklists.add(checklist);
  }

  @Override
  public void match(CheckpointInstance point) {

    ChecklistInstance newList = null;
    List<ChecklistInstance> instances = null;

    //match with Checklists
    for (Checklist c : checklists) {
      if (c.startsWith(point.toCheckpoint())) {

        //create new ChecklistInstance
        newList = new ChecklistInstance(c, point.getEventID(), point.getPathID());

        //add to ChecklistInstances for this path
        instances = checklistInstances.get(point.getEventID());

        if (instances == null) {
          instances = new LinkedList<ChecklistInstance>();
          checklistInstances.put(point.getEventID(), instances);
        }

        instances.add(newList);

      }
    }

    //get all checklists for this event
    instances = checklistInstances.get(point.getEventID());

    if (instances != null) { // if none has been fired yet

      // stores finished and violated checklist instances
      List<ChecklistInstance> removeList = new LinkedList<ChecklistInstance>();

      for (ChecklistInstance list : instances) {

        if (point.getPathID().startsWith(list.getPath().getPathID())) {
          // if this checkpoint was on a child path of the checklist's path

          boolean match = list.match(point);
          if (!match) {
            // this point violated the checklist

            if (list.conditionMatched()) {
              // this list is a violation

              checkListPublisher.addViolatingChecklist(list, 1);
              removeList.add(list);

            } else if (!list.conditionMatched()) {
              //this is ignored as condition was not met

              removeList.add(list);
            }
          } else if (match && list.isCompleted()) {
            checkListPublisher.addCompletedChecklist(list);
            removeList.add(list);

          }
        }
      }

      for (ChecklistInstance list : removeList) {
        instances.remove(list);
      }
    }
  }

  @Override
  public List<ChecklistInstance> getFailedChecklists() {
    // stores checklists that fail
    List<ChecklistInstance> failedChecklists = new LinkedList<ChecklistInstance>();

    for (Entry<Integer, List<ChecklistInstance>> entryset : checklistInstances.entrySet()) {
      for (ChecklistInstance list : entryset.getValue()) {
        if (list.conditionMatched() && !list.isCompleted()) {

          boolean failed = false;
          int index = list.getIndex();
          for (int i = index + 1; i < list.getChecklist().size(); i++) {
            if (!list.getChecklist().getCheckpoint(i).isNegative()) {
              failed = true;
              break;
            } else {
              if (list.checkpointsMatched[i] != null) {
                failed = true;
                break;
              }
            }
          }
          if (failed)
            failedChecklists.add(list);
        }
      }
    }
    return failedChecklists;
  }

  @Override
  public List<ChecklistInstance> getCompletedChecklists() {
    // stores checklists that fail
    List<ChecklistInstance> completedChecklists = new LinkedList<ChecklistInstance>();

    for (Entry<Integer, List<ChecklistInstance>> entryset : checklistInstances.entrySet()) {
      for (ChecklistInstance list : entryset.getValue()) {
        completedChecklists.add(list);
      }
    }
    return completedChecklists;
  }

  @Override
  public ChecklistsState getStateExtension() {
    ChecklistsState state = new ChecklistsState();

    //clone instances map
    Map<Integer, List<ChecklistInstance>> newChecklistInstances = new HashMap<Integer, List<ChecklistInstance>>();

    ChecklistInstance instance = null;
    List<ChecklistInstance> instancelist = null;

    for (List<ChecklistInstance> values : checklistInstances.values()) {

      for (ChecklistInstance c : values) {

        instance = (ChecklistInstance) c.clone();
        instancelist = newChecklistInstances.get(instance.getPath().getEventID());

        if (instancelist == null) {
          instancelist = new LinkedList<ChecklistInstance>();
          newChecklistInstances.put(instance.getPath().getEventID(), instancelist);
        }
        instancelist.add(instance);
      }
    }

    state.setRunnningChecklists(newChecklistInstances);

    Map<Integer, Path> threadMap = new HashMap<Integer, Path>();
    for (Entry<Integer, Path> path : ThreadListener.threadToPathMapping.entrySet()) {
      threadMap.put(path.getKey(), (Path) path.getValue().clone());
    }
    state.setThreadMap(threadMap);

    return state;

  }

  @Override
  public void restore(ChecklistsState stateExtension) {

    //clone instances map
    Map<Integer, List<ChecklistInstance>> newChecklistInstances = new HashMap<Integer, List<ChecklistInstance>>();
    Map<Integer, Path> threadMap = new HashMap<Integer, Path>();

    ChecklistInstance instance = null;
    List<ChecklistInstance> instancelist = null;

    if (stateExtension != null) {

      for (List<ChecklistInstance> values : stateExtension.checklistInstances.values()) {

        for (ChecklistInstance c : values) {

          instance = (ChecklistInstance) c.clone();
          instancelist = newChecklistInstances.get(instance.getPath().getEventID());

          if (instancelist == null) {
            instancelist = new LinkedList<ChecklistInstance>();
            newChecklistInstances.put(instance.getPath().getEventID(), instancelist);
          }
          instancelist.add(instance);
        }
      }

      for (Entry<Integer, Path> path : stateExtension.getThreadMap().entrySet()) {
        threadMap.put(path.getKey(), (Path) path.getValue().clone());
      }

    }
    this.checklistInstances = newChecklistInstances;
    ThreadListener.threadToPathMapping = threadMap;

  }

  public void createReporter(AndroidScriptEnvironment env) {
    checkListPublisher = new ChecklistPublisher(this, env);
  }

  @Override
  public void registerListener(JPF jpf) {

    StateExtensionListener<ChecklistsState> sel = new StateExtensionListener<ChecklistsState>(this);
    jpf.addSearchListener(sel);

    CheckpointListener checkPointListener = new CheckpointListener((CheckpointProcessor) this);
    jpf.addListener(checkPointListener);

    jpf.addPublisherExtension(ConsolePublisher.class, checkListPublisher);
    jpf.addSearchListener(checkListPublisher);

    ThreadListener mgr = new ThreadListener();
    jpf.addListener(mgr);

  }

  public static class ChecklistsState {

    Map<Integer, List<ChecklistInstance>> checklistInstances;
    Map<Integer, Path> threadMap;

    public Map<Integer, Path> getThreadMap() {
      return threadMap;
    }

    public void setThreadMap(Map<Integer, Path> threadMap) {
      this.threadMap = threadMap;
    }

    public Map<Integer, List<ChecklistInstance>> getChecklistInstances() {
      return checklistInstances;
    }

    public void setRunnningChecklists(Map<Integer, List<ChecklistInstance>> checklistInstances) {
      this.checklistInstances = checklistInstances;

    }

  }

  @Override
  public Map<String, Checkpoint> getMethodMappings() {
    return mappings;
  }

}