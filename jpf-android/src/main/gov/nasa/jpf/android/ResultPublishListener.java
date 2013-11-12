package gov.nasa.jpf.android;

import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.PublisherExtensionAdapter;
import gov.nasa.jpf.util.script.AndroidScriptEnvironment;
import gov.nasa.jpf.util.script.ScriptState;
import gov.nasa.jpf.util.script.UIAction;

public class ResultPublishListener extends PublisherExtensionAdapter {

  AndroidScriptEnvironment cli;
  int choices = 0;
  String out = "";

  public ResultPublishListener(AndroidScriptEnvironment cli) {
    this.cli = cli;
  }

  public void getActions() {
    ScriptState ss = cli.getCur();
    if (ss != null)
      for (UIAction s : ss.getActions()) {
        try {
          out += "\n" + s.toString();
        } catch (Exception e) {
        }
      }
  }

  @Override
  public void publishPropertyViolation(Publisher publisher) {
    getActions();

    if (out != "") {
      publisher.getOut().println(
          "====================================================== error input sequence\n" + out);
    }
  }

  @Override
  public void publishConstraintHit(Publisher publisher) {
    getActions();
    if (out != "") {
      publisher.getOut().println(
          "====================================================== error input sequence\n" + out);
    }
  }
}
