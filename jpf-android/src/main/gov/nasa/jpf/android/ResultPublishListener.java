package gov.nasa.jpf.android;

import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.PublisherExtensionAdapter;
import gov.nasa.jpf.util.script.ScriptState;
import gov.nasa.jpf.util.script.AndroidScriptEnvironment;

public class ResultPublishListener extends PublisherExtensionAdapter {

	AndroidScriptEnvironment cli;
	int choices = 0;
	String out = "";

	public ResultPublishListener(AndroidScriptEnvironment cli) {
		this.cli = cli;
	}

	public void getActions() {
		ScriptState ss = cli.getCur();
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
		publisher.getOut().println(
				"====================================================== error input sequence\n"
						+ out);
	}

	@Override
	public void publishConstraintHit(Publisher publisher) {
		getActions();
		publisher.getOut().println(
				"====================================================== error input sequence\n"
						+ out);
	}
}
