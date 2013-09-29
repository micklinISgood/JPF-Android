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
  private static final String TAG = ChecklistManager.class.getSimpleName();
  private static Logger logger = JPF.getLogger(TAG);

  /** Publishes checklist violations */
  private ChecklistPublisher checkListPublisher;

  /** Active Checklists */
  private List<Checklist> checklists;

  /** Running ChecklistInstances */
  private Map<Path, List<ChecklistInstance>> checklistInstances;

  /** Mappings */
  private Map<String, Checkpoint> mappings;

  public ChecklistManager(String filename, String[] activeChecklists) throws ParseException {
    logger.info("ChecklistManager constrcutor");
    if (checklistInstances == null) {

      // parse checklists definition file
      ChecklistParser parser = new ChecklistParser(filename);

      // parse and store active checklists in checklistList
      ChecklistDefinitions def = parser.parse();
      checklists = new LinkedList<Checklist>();
      mappings = def.getMappings();

      if (activeChecklists != null)
        for (String c : activeChecklists) {
          registerChecklist(def.getChecklists().get(c.trim()));
        }

      // create new list of running checklists
      checklistInstances = new HashMap<Path, List<ChecklistInstance>>();
    }
  }

  /**
   * Registers a new {@link Checklist}.
   * 
   */
  protected void registerChecklist(Checklist checklist) {
    logger.fine(TAG + ": Registering  checklist " + checklist.toString());

    // add new checklist to checklists list
    if (checklist != null)
      this.checklists.add(checklist);
  }

  public void splitPath(Path oldPath, Path newPath) {
    logger.info(TAG + ": Splitting path " + oldPath.toString() + " --> " + newPath.toString());

    // get all checklists for old path
    List<ChecklistInstance> oldChecklists = checklistInstances.get(oldPath);
    List<ChecklistInstance> newChecklists = null;

    ChecklistInstance newList = null;

    
    // for each path clone for new path
    if(oldChecklists != null){
      for (ChecklistInstance instance : oldChecklists) {

      // new list instance
      newList = (ChecklistInstance) instance.clone();
      newList.changePath(newPath);

      //get checklists for new path
      newChecklists = checklistInstances.get(newPath);

      if (newChecklists == null) {
        newChecklists = new LinkedList<ChecklistInstance>();
        checklistInstances.put(newPath, newChecklists);
      }
      // add this new checklist to the new path
        newChecklists.add(newList);
    }
    }

  }

  @Override
  public void match(CheckpointInstance point) {

    ChecklistInstance newList = null;
    List<ChecklistInstance> instances = null;

    //match with Checklists
    for (Checklist c : checklists) {
      if (c.startsWith(point.toCheckpoint())) {

        //create new ChecklistInstance
        newList = new ChecklistInstance(c, point.path);

        //add to ChecklistInstances for this path
        instances = checklistInstances.get(point.path);

        if (instances == null) {
          instances = new LinkedList<ChecklistInstance>();
          checklistInstances.put(point.path, instances);
        }

        instances.add(newList);

      }
    }

    //match with checklistInstances
    instances = checklistInstances.get(point.path);
    if (instances != null) {
      List<ChecklistInstance> failList = new LinkedList<ChecklistInstance>();
      List<ChecklistInstance> removeList = new LinkedList<ChecklistInstance>();

      for (ChecklistInstance list : instances) {
        if (!list.match(point)) {
          if (list.conditionMatched()) {
            checkListPublisher.addViolatingChecklist(list, 1);
            failList.add(list);
          } else if (!list.conditionMatched()) {
            removeList.add(list);
          }
        } else if (list.isCompleted()) {
          removeList.add(list);
        }
      }
      for (ChecklistInstance list : failList) {
        instances.remove(list);
      }

//      for (ChecklistInstance list : removeList) {
//        instances.remove(list);
//      }
    }
  }

  @Override
  public List<ChecklistInstance> getFailedChecklists() {
    // stores checklists that fail
    List<ChecklistInstance> failedChecklists = new LinkedList<ChecklistInstance>();

    for (Entry<Path, List<ChecklistInstance>> entryset : checklistInstances.entrySet()) {
      for (ChecklistInstance list : entryset.getValue()) {
        if (list.conditionMatched() && !list.isCompleted()) {
          

          failedChecklists.add(list);
        }
      }
    }
    return failedChecklists;
  }

  @Override
  public List<ChecklistInstance> getCompletedChecklists() {
    // stores checklists that fail
    List<ChecklistInstance> failedChecklists = new LinkedList<ChecklistInstance>();

    for (Entry<Path, List<ChecklistInstance>> entryset : checklistInstances.entrySet()) {
      for (ChecklistInstance list : entryset.getValue()) {
        failedChecklists.add(list);
      }
    }
    return failedChecklists;
  }

  @Override
  public ChecklistsState getStateExtension() {
    ChecklistsState state = new ChecklistsState();

    //clone instances map
    Map<Path, List<ChecklistInstance>> newChecklistInstances = new HashMap<Path, List<ChecklistInstance>>();

    ChecklistInstance instance = null;
    List<ChecklistInstance> instancelist = null;

    for (List<ChecklistInstance> values : checklistInstances.values()) {

      for (ChecklistInstance c : values) {

        instance = (ChecklistInstance) c.clone();
        instancelist = newChecklistInstances.get(instance.getPath());

        if (instancelist == null) {
          instancelist = new LinkedList<ChecklistInstance>();
          newChecklistInstances.put(instance.getPath(), instancelist);
        }
        instancelist.add(instance);
      }
    }

    state.setRunnningChecklists(newChecklistInstances);

    return state;

  }

  @Override
  public void restore(ChecklistsState stateExtension) {

    //clone instances map
    Map<Path, List<ChecklistInstance>> newChecklistInstances = new HashMap<Path, List<ChecklistInstance>>();

    ChecklistInstance instance = null;
    List<ChecklistInstance> instancelist = null;

    if (stateExtension != null) {
      for (List<ChecklistInstance> values : stateExtension.checklistInstances.values()) {

        for (ChecklistInstance c : values) {

          instance = (ChecklistInstance) c.clone();
          instancelist = newChecklistInstances.get(instance.getPath());

          if (instancelist == null) {
            instancelist = new LinkedList<ChecklistInstance>();
            newChecklistInstances.put(instance.getPath(), instancelist);
          }
          instancelist.add(instance);
        }
      }
    }
    this.checklistInstances = newChecklistInstances;

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

    ThreadManagerListener mgr = new ThreadManagerListener(this);
    mgr.registerListener(jpf);

  }

  public static class ChecklistsState {

    Map<Path, List<ChecklistInstance>> checklistInstances;

    public Map<Path, List<ChecklistInstance>> getChecklistInstances() {
      return checklistInstances;
    }

    public void setRunnningChecklists(Map<Path, List<ChecklistInstance>> checklistInstances) {
      this.checklistInstances = checklistInstances;

    }

  }

  @Override
  public Map<String, Checkpoint> getMethodMappings() {
    return mappings;
  }

}