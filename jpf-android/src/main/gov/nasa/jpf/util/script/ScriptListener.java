package gov.nasa.jpf.util.script;

import gov.nasa.jpf.android.UIActionGenerator;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.StateExtensionClient;
import gov.nasa.jpf.util.StateExtensionListener;

import java.util.List;
import java.util.Stack;

public class ScriptListener extends StateExtensionListener<ScriptState> {

  public ScriptListener(StateExtensionClient<ScriptState> cli) {
    super(cli);
  }

  String out = "";

  @Override
  public void searchFinished(Search search) {
    JVM jvm = search.getVM();
    UIActionGenerator[] cgs = jvm.getChoiceGeneratorsOfType(UIActionGenerator.class);
    for (ChoiceGenerator s : cgs) {
      try {
        out += "\n" + s.toString();
      } catch (Exception e) {

      }
    }

  }

  @Override
  public void publishPropertyViolation(Publisher publisher) {
    publisher.getOut().println("publishPropertyViolation");
    super.publishPropertyViolation(publisher);
  }

  @Override
  public void publishConstraintHit(Publisher publisher) {
    publisher.getOut().println("publishConstraintHit");
  }

  @Override
  public void publishFinished(Publisher publisher) {
    publisher.getOut().println(out);

  }

}
