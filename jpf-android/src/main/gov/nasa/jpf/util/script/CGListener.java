package gov.nasa.jpf.util.script;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class CGListener extends ListenerAdapter {
  private static final JPFLogger logger = JPF.getLogger("CGListener");

  public void choiceGeneratorRegistered(VM vm, ChoiceGenerator<?> nextCG, ThreadInfo currentThread,
                                        Instruction executedInstruction) {

    logger.info("#CG registered: " + nextCG.toString());
  }

  public void choiceGeneratorSet(VM vm, ChoiceGenerator<?> newCG) {
    logger.info("#CG set:        " + newCG.toString());

  }

  public void choiceGeneratorAdvanced(VM vm, ChoiceGenerator<?> currentCG) {
    logger.info("#CG advanced:   " + currentCG.toString());
  }

  public void choiceGeneratorProcessed(VM vm, ChoiceGenerator<?> processedCG) {
    logger.info("#CG processed:  " + processedCG.toString());
  }

}
