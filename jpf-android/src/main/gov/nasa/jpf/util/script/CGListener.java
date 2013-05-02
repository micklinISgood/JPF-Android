package gov.nasa.jpf.util.script;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.util.JPFLogger;

public class CGListener extends ListenerAdapter {
  private static final JPFLogger logger = JPF.getLogger("CGListener");

  public void choiceGeneratorRegistered(JVM vm) {
    ChoiceGenerator<?> cg = vm.getLastChoiceGenerator();
    logger.info("#CG registered: " + cg.getClass());
  }

  public void choiceGeneratorSet(JVM vm) {
    ChoiceGenerator<?> cg = vm.getLastChoiceGenerator();
    logger.info("#CG set:        " +cg.getClass());

  }

  public void choiceGeneratorAdvanced(JVM vm) {
    ChoiceGenerator<?> cg = vm.getLastChoiceGenerator();
    logger.info("#CG advanced:   " + cg.getClass());
  }

  public void choiceGeneratorProcessed(JVM vm) {
    ChoiceGenerator<?> cg = vm.getLastChoiceGenerator();
    logger.info("#CG processed:  " + cg.getClass());
  } 
  
}
