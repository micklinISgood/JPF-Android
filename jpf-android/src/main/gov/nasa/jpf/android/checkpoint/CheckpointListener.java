package gov.nasa.jpf.android.checkpoint;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.AnnotationInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.util.logging.Logger;

/**
 * When a method is methodExited we check if it has a Checkpoint annotation. If it does
 * we notify the CheckpointManager that checks if this is a valid method call
 * according to the registered CheckLists.
 * 
 * @author Heila van der Merwe
 * @date 2 June 2013
 */
public class CheckpointListener extends ListenerAdapter {
  private static Logger logger = JPF.getLogger(CheckpointListener.class.getName());

  private ChecklistManager manager = null;

  public CheckpointListener(ChecklistManager manager) {
    this.manager = manager;
  }

  @Override
  public void methodExited(JVM vm) {
    ThreadInfo ti = vm.getCurrentThread();
    MethodInfo methodInfo = ti.getMethod();
    
    // check to see if this method has an annotation "Checkpoint"
    AnnotationInfo info = methodInfo.getAnnotation("gov.nasa.jpf.annotation.Checkpoint");
    
    if (info != null) {
      // this method is a checkpoint
      
      String name = (String) info.value();
      logger.info("CheckPointListener: " + "Checkpoint reached \"" + name + "\"");
      
      // notify ChecklistManager
      manager.checkpointReached(name);
    }
  }
}