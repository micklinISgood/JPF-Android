package gov.nasa.jpf.android.checkpoint;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.android.checkpoint.ThreadManagerListener.ThreadState;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.util.StateExtensionClient;
import gov.nasa.jpf.util.StateExtensionListener;
import gov.nasa.jpf.android.checkpoint.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author Heila van der Merwe
 * 
 */
public class ThreadManagerListener implements StateExtensionClient<ThreadState> {

  public static ThreadState state = new ThreadState();

  ChecklistManager mgr;

  public ThreadManagerListener(ChecklistManager mgr) {
    this.mgr = mgr;
  }

  @Override
  public ThreadState getStateExtension() {
    return (ThreadState) state.clone();
  }

  @Override
  public void restore(ThreadState stateExtension) {
    if (stateExtension != null) {
      ThreadState newState = (ThreadState) stateExtension.clone();
      state.threadToPathMapping = newState.threadToPathMapping;
      state.eventToPathCountMapping = newState.eventToPathCountMapping;
    }
  }

  @Override
  public void registerListener(JPF jpf) {
    StateExtensionListener<ThreadState> sel = new StateExtensionListener<ThreadState>(this);
    jpf.addSearchListener(sel);

    ListenerAdapter l = new ThreadListener();
    jpf.addVMListener(l);

  }

  public class ThreadListener extends ListenerAdapter {

    @Override
    public void threadStarted(JVM vm) {
      //Thread that started this thread
      ThreadInfo parentThread = vm.getCurrentThread();
      // The Thread that was just started
      ThreadInfo childThread = vm.getLastThreadInfo();

      if (childThread.getName().equals("main")) {
        //if this is main thread  -- ignore it
        return;

      } else if (parentThread.getName().equals("main")) {
        // if this thread was started by the main thread we must
        //adopt the main thead's event id from message queue 

        // get eventID
        StaticElementInfo mq = vm.getClassReference("android.os.MessageQueue");
        int eventID = mq.getIntField("eventID");
        int pathID = mq.getIntField("pathID");

        // get new pathID
        Integer pathCount = state.eventToPathCountMapping.get(eventID);
        if (pathCount == null)
          pathCount = new Integer(pathID);

        pathCount++;
        state.eventToPathCountMapping.put(eventID, pathCount);

        //make new Path
        Path newPath = new Path(pathCount, eventID);

        // store for this thread
        state.threadToPathMapping.put(childThread.getId(), newPath);

        // split checklists
        mgr.splitPath(new Path(pathID, eventID), newPath);

      } else {
        // if this thread was started by other thread
        // we must adopt the previous thread's eventID

        //get parent thread's Path
        Path path = state.threadToPathMapping.get(parentThread.getId());

        // get new pathID
        int pathCount = state.eventToPathCountMapping.get(path.eventID);
        pathCount++;
        state.eventToPathCountMapping.put(path.eventID, pathCount);

        //make new Path
        Path newPath = new Path(pathCount, path.eventID);

        // store for this thread
        state.threadToPathMapping.put(childThread.getId(), newPath);

        // split checklists
        mgr.splitPath(path, newPath);

      }

    }

  }

  public static Path getPathInfo(int threadId) {
    return state.threadToPathMapping.get(threadId);
  }

  public static class ThreadState {

    /** Maps threadID to eventID and pathCount - not applicable for main thread */
    public Map<Integer, Path> threadToPathMapping = new HashMap<Integer, Path>();
    public Map<Integer, Integer> eventToPathCountMapping = new HashMap<Integer, Integer>();

    @SuppressWarnings("unchecked")
    @Override
    protected Object clone() {
      ThreadState newState = new ThreadState();

      newState.threadToPathMapping = new HashMap<Integer, Path>();
      for (java.util.Map.Entry<Integer, Path> e : state.threadToPathMapping.entrySet()) {
        newState.threadToPathMapping.put(e.getKey(), (Path) e.getValue().clone());
      }

      newState.eventToPathCountMapping = (Map<Integer, Integer>) ((HashMap<Integer, Integer>) this.eventToPathCountMapping)
          .clone();
      return newState;
    }

  }

}
