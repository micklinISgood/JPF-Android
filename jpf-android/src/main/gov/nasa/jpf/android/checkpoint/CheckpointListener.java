package gov.nasa.jpf.android.checkpoint;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.AnnotationInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.android.checkpoint.CheckpointProcessor;

import java.util.Map;
import java.util.logging.Logger;

/**
 * When a method is methodExited we check if it is a Checkpoint. If
 * it is, we notify the CheckpointManager that checks if this is a valid method
 * call according to the registered CheckLists.
 * 
 * Depends on ThreadManagerListener to get the eventID and pathID of this thread
 * 
 * Depends on MessageQueue to get the eventID and pathID of the main tread.
 * 
 * @author Heila van der Merwe
 */
public class CheckpointListener extends ListenerAdapter {
  private static Logger logger = JPF.getLogger(CheckpointListener.class.getName());

  private final CheckpointProcessor checkpointProcessor;

  private final Map<String, Checkpoint> mappings;

  public CheckpointListener(CheckpointProcessor checkpointProcessor) {
    this.checkpointProcessor = checkpointProcessor;
    this.mappings = this.checkpointProcessor.getMethodMappings();
  }

  @Override
  public void methodEntered(JVM vm) {
    MethodInfo methodInfo = vm.getCurrentThread().getMethod();

    // check to see if this method has an annotation "Checkpoint"
    AnnotationInfo info = methodInfo.getAnnotation("gov.nasa.jpf.annotation.Checkpoint");

    if (info != null) {
      // this method has a checkpoint annotation

      /////////////////////////////////
      //  Get info of the Checkpoint //
      /////////////////////////////////

      //1. the name of the checkpoint
      String name = (String) info.value();

      //2. the alias of the thread
      String threadAlias = (String) info.getValueAsString("threadName");
      if (threadAlias == null)
        threadAlias = "main";

      CheckpointInstance c = getCheckpointInstance(vm, name, threadAlias);

      // notify ChecklistManager
      checkpointProcessor.match(c);
    } else {

      if (mappings != null) {
        String methodSignature = methodInfo.getFullName();
        Checkpoint cp = mappings.get(methodSignature);

        if (cp != null) {
          CheckpointInstance ins = getCheckpointInstance(vm, cp.getName(), cp.getThreadName());

          // notify ChecklistManager
          checkpointProcessor.match(ins);
        }
      }
    }

  }

  private CheckpointInstance getCheckpointInstance(JVM vm, String name, String threadAlias) {
    ThreadInfo ti = vm.getCurrentThread();
   
    //2. get the actual alias of this thread
    String threadName = ti.getName();

    //3. the thread id
    int threadID = ti.getId();

    //4. get the eventID
    int eventID = -1;
    int pathID = -1;

    if (ti.getName().equals("main")) {
      // if this thread is the main thread
      // get the current event being executed
      StaticElementInfo mq = vm.getClassReference("android.os.MessageQueue");
      eventID = mq.getIntField("eventID");
      pathID = mq.getIntField("pathID");

    } else {
      Path p = ThreadManagerListener.getPathInfo(ti.getId());
      eventID = p.eventID;
      pathID = p.pathID;
    }
    logger.info("CheckPointListener: " + "Checkpoint reached \"" + name + "\" for path(e_ID:" +eventID + " p_id:" + pathID + ")");
    CheckpointInstance point = new CheckpointInstance(name, threadName, threadAlias, threadID, pathID,
        eventID);
    return point;
  }

}