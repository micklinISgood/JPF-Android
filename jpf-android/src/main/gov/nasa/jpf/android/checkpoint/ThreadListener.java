package gov.nasa.jpf.android.checkpoint;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author Heila van der Merwe
 * 
 */
public class ThreadListener extends ListenerAdapter {

  /** Maps search global threadID to path */
  public static Map<Integer, Path> threadToPathMapping = new HashMap<Integer, Path>();

  public ThreadListener() {
  }

  @Override
  public void threadStarted(VM vm, ThreadInfo childThread) {
    //Thread that started this thread
    ThreadInfo parentThread = vm.getCurrentThread();

    if (parentThread.getName().equals("main")) {
      // if this thread was started by the main thread we must
      //adopt the main thead's event id from message queue 

      // get current values of this path
      ClassInfo ci = ClassLoaderInfo.getCurrentResolvedClassInfo("android.os.MessageQueue");
      int eventID = ci.getStaticElementInfo().getIntField("currentEvent");
      String pathID = ci.getStaticElementInfo().getStringField("currentPath");

      // change path of main thread
      ci.getModifiableStaticElementInfo().setReferenceField("currentPath",
          vm.getSystemState().getHeap().newString(pathID + "0", parentThread).getObjectRef());

      //make new Path
      Path newPath = new Path(eventID, pathID + "1");

      // store for this thread
      threadToPathMapping.put(childThread.getGlobalId(), newPath);

    } else {
      // if this thread was started by other thread
      // we must adopt the previous thread's eventID

      //get parent thread's Path
      Path path = threadToPathMapping.get(parentThread.getGlobalId());

      // get new pathID
      String pathID = path.getPathID();
      path.setPathID(pathID + "0");

      //make new Path
      Path newPath = new Path(path.eventID, pathID + "1");

      // store for this thread
      threadToPathMapping.put(childThread.getGlobalId(), newPath);

    }

  }
  
  

  public static Path getPath(int threadId) {
    return threadToPathMapping.get(threadId);
  }

}
