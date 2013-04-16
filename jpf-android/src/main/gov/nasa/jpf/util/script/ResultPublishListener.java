package gov.nasa.jpf.util.script;

import gov.nasa.jpf.android.UIAction;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.PublisherExtensionAdapter;

public class ResultPublishListener extends PublisherExtensionAdapter {

	ScriptingEnvironment cli;
	int choices = 0;
	String out = "";

	public ResultPublishListener(ScriptingEnvironment cli) {
		this.cli = cli;
	}

	public void getActions() {
		ScriptState ss = cli.cur;
		for (UIAction s : ss.actions) {
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
