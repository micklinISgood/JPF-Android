package gov.nasa.jpf.util.script;

import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.choice.IntIntervalGenerator;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.StateExtensionClient;
import gov.nasa.jpf.util.StateExtensionListener;

public class ResultPublishListener extends StateExtensionListener<ScriptState> {

  @Override
  public void stateAdvanced(Search search) {
    super.stateAdvanced(search);
    ChoiceGenerator<?> cg = search.getVM().getChoiceGenerator();
    if (cg instanceof IntIntervalGenerator) {
      //choices++;
     // System.out.println((IntIntervalGenerator) cg);
    }
  }

  public ResultPublishListener(StateExtensionClient<ScriptState> cli) {
    super(cli);
  }

  int choices = 0;
  String out = "";

  @Override
  public void searchFinished(Search search) {
    System.out.println("Choices" + choices);
  }

  @Override
  public void choiceGeneratorRegistered(JVM vm) {
    ChoiceGenerator<?> cg = vm.getChoiceGenerator();
    if (cg instanceof IntIntervalGenerator) {
      choices++;
      System.out.println((IntIntervalGenerator) cg);
    }
  }

  public void getActions() {
    // ScriptState ss = getClient().getStateExtension();
    // UIActionGenerator[] cgs = jvm.getChoiceGeneratorsOfType(UIActionGenerator.class);
    // for (UIAction s : ss.actions) {
    // try {
    // out += "\n" + s.toString();
    // } catch (Exception e) {
    //
    // }
    // }
  }

  @Override
  public void publishPropertyViolation(Publisher publisher) {
    getActions();
    publisher.getOut().println(
        "====================================================== error input sequence\n" + out);
    // super.publishPropertyViolation(publisher);
  }

  @Override
  public void publishConstraintHit(Publisher publisher) {
    getActions();
    publisher.getOut().println(
        "====================================================== error input sequence\n" + out);
  }

  @Override
  public void publishFinished(Publisher publisher) {
    // publisher.getOut().println(out);
    // publisher.getOut().println("number of sequences: " + publisher.);
  }
}
