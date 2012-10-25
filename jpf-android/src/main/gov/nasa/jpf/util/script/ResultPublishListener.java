package gov.nasa.jpf.util.script;

import gov.nasa.jpf.android.UIAction;
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
    if (search.isEndState()) {
      if (search.getVM().getChoiceGenerator() instanceof IntIntervalGenerator)
        choices++;

    }
  }

  public ResultPublishListener(StateExtensionClient<ScriptState> cli) {
    super(cli);
  }

  int choices = 0;
  String out = "";

  @Override
  public void searchFinished(Search search) {
    ScriptState ss = client.getStateExtension();
    // UIActionGenerator[] cgs = jvm.getChoiceGeneratorsOfType(UIActionGenerator.class);
    for (UIAction s : ss.actions) {
      try {
        out += "\n" + s.toString();
      } catch (Exception e) {

      }
    }

  }

  @Override
  public void choiceGeneratorRegistered(JVM vm) {

  }

  @Override
  public void publishPropertyViolation(Publisher publisher) {
    publisher.getOut().println("====================================================== error input sequence");
    // super.publishPropertyViolation(publisher);
  }

  @Override
  public void publishConstraintHit(Publisher publisher) {
    publisher.getOut().println("====================================================== error input sequence");
  }

  @Override
  public void publishFinished(Publisher publisher) {
    publisher.getOut().println(out);
    publisher.getOut().println("number of sequences: " + choices);
  }
}
