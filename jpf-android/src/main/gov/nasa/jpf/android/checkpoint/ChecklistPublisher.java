package gov.nasa.jpf.android.checkpoint;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.PublisherExtension;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.script.AndroidScriptEnvironment;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadList;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Stores a list of violated {@link CheckList}. Publishes a report of which
 * {@link CheckList} were violated at the end of execution.
 * 
 * @author Heila van der Merwe
 * @date 2 June 2013
 * 
 */
public class ChecklistPublisher extends ListenerAdapter implements PublisherExtension {
  private static final String TAG = ChecklistPublisher.class.getSimpleName();
  private static Logger logger = JPF.getLogger(ChecklistPublisher.class.getName());

  private final static int REASON_NOT_COMPLETE = 0;
  private final static int REASON_MATCH_FAILED = 1;

  private List<ChecklistViolation> violatingChecklists = new LinkedList<ChecklistViolation>();
  private List<ChecklistCompleted> completedChecklists = new LinkedList<ChecklistCompleted>();

  private ChecklistReporter reporter;
  private AndroidScriptEnvironment scriptEnv;

  private Search search;

  public ChecklistPublisher(ChecklistReporter reporter, AndroidScriptEnvironment scriptEnv) {
    this.reporter = reporter;
    this.scriptEnv = scriptEnv;
  }

  private void printCompletions(PrintWriter pw) {
    for (ChecklistCompleted violation : completedChecklists) {
      pw.println("Checklist Name: " + violation.getName());
      pw.println("Completed: " + violation.isCompleted());
      pw.println("EventID: " + violation.checklist.getPath().eventID + " PathID: "
          + violation.checklist.getPath().pathID);
      pw.println("Checklist:" + violation.getChecklist());
      pw.println("Script Events:" + violation.scriptActions);
      pw.println();
    }
  }

  private void printViolations(PrintWriter pw) {
    for (ChecklistViolation violation : violatingChecklists) {
      pw.println("Checklist Name: " + violation.getName());
      pw.println("EventID: " + violation.checklist.getPath().eventID + " PathID: "
          + violation.checklist.getPath().pathID);
      pw.println("Checklist:" + violation.getChecklist());
      pw.print("Reason: ");
      if (violation.reason == REASON_MATCH_FAILED)
        pw.println(printMatchFailed(violation));
      else
        pw.println(printNotComplete(violation));

      pw.println("Script Events:" + violation.scriptActions);
      pw.println("Thread state:\n\t" + violation.threadState);
      pw.println();
    }
  }

  private String printMatchFailed(ChecklistViolation violation) {
    StringBuilder s = new StringBuilder();
    Checklist cl = violation.checklist.getChecklist();
    ChecklistInstance cli = violation.checklist;
    CheckpointInstance ins = cli.checkpointsMatched[cli.getIndex()];
    Checkpoint cp = cl.getCheckpoint(cli.getIndex());
    s.append("\nFailed because checkpoint reached (" + ins.getName() + ", tn=" + ins.getThreadAlias()
        + ") did not match checkpoint (" + ((cp.isNegative()) ? "!" : "") + cp.getName() + ", tn="
        + ins.getThreadName() + ")");
    return s.toString();
  }

  private String printNotComplete(ChecklistViolation violation) {
    StringBuilder s = new StringBuilder();
    Checklist cl = violation.checklist.getChecklist();
    Checkpoint cp = cl.getCheckpoint(violation.checklist.getIndex());
    s.append("Checkpoint  (" + ((cp.isNegative()) ? "!" : "") + cp.getName()
        + ", tn=?) not visited before search ended.");
    return s.toString();
  }

  public void addViolatingChecklist(ChecklistInstance checklist, int reason) {
    String actions = getActions();
    String threadString = getThreadState();

    violatingChecklists.add(new ChecklistViolation(checklist, actions, threadString, reason));

  }

  public void addCompletedChecklist(ChecklistInstance checklist) {
    String actions = getActions();
    completedChecklists.add(new ChecklistCompleted(checklist, actions));

  }

  @Override
  public void publishPropertyViolation(Publisher publisher) {
    PrintWriter pw = publisher.getOut();
    publisher.publishTopicStart("checklist completions " + publisher.getLastErrorId());
    printCompletions(pw);
    publisher.publishTopicStart("checklist violations " + publisher.getLastErrorId());
    printViolations(pw);


  }

  @Override
  public void publishConstraintHit(Publisher publisher) {
    PrintWriter pw = publisher.getOut();
    publisher.publishTopicStart("checklist completions " + publisher.getLastErrorId());
    printCompletions(pw);
    publisher.publishTopicStart("checklist violations " + publisher.getLastErrorId());
    printViolations(pw);


  }

  @Override
  public void publishFinished(Publisher publisher) {
    PrintWriter pw = publisher.getOut();
    publisher.publishTopicStart("checklist completions " + publisher.getLastErrorId());
    printCompletions(pw);
    publisher.publishTopicStart("checklist violations " + publisher.getLastErrorId());
    printViolations(pw);
   

  }

  @Override
  public void searchStarted(Search search) {
    this.search = search;
  }

  @Override
  public void stateAdvanced(Search search) {
    // If we have reached an end state it means that this execution trace is complete and 
    // Checklists NOT complete have to be reported. We will save these checklists here in
    // a list of violations. Each violation has to store the script actions that brought
    // it here as well as the thread interleaving at this time.

    if (search.isEndState()) {
      List<ChecklistInstance> lists = reporter.getFailedChecklists();
      List<ChecklistInstance> lists2 = reporter.getCompletedChecklists();
      //add these lists to violating checklists
      this.search = search;
      for (ChecklistInstance instance : lists) {
        addViolatingChecklist(instance, REASON_NOT_COMPLETE);
      }

      for (ChecklistInstance instance : lists2) {
        addCompletedChecklist(instance);
      }
    }
  }

  private String getThreadState() {
    String out = "";
    if (this.search != null) {
      ThreadList list = this.search.getVM().getThreadList();
      for (ThreadInfo ti : list.getThreads()) {
        out += "ThreadID: " + ti.getId() + " ";
        out += "ThreadName: " + ti.getName() + " ";
        out += "ThreadState: " + ti.getState().name() + " ";
        out += ti.getStackTrace();
        out += "\n";
      }
    }
    return out;
  }

  private String getActions() {
    return scriptEnv.getActions();
  }

  protected static class ChecklistViolation {
    ChecklistInstance checklist;
    String scriptActions;
    String threadState;
    int reason = -1;

    public ChecklistViolation(ChecklistInstance checklist, String scriptActions, String threadState,
        int reason) {
      super();
      this.checklist = checklist;
      this.scriptActions = scriptActions;
      this.threadState = threadState;
      this.reason = reason;
      
         }

    public String getName() {
      return this.checklist.getChecklist().getName();
    }

    public String getChecklist() {
      
      for (int i = checklist.getIndex() + 1; i < checklist.getChecklist().size(); i++) {
        if (checklist.getChecklist().getCheckpoint(i).isNegative()&&checklist.checkpointsMatched[i]!=null) {
          checklist.setIndex(i);
          break;
        }
      }

      StringBuilder sb = new StringBuilder("");

      CheckpointInstance[] matched = this.checklist.checkpointsMatched;
      for (int i = 0; i < this.checklist.getChecklist().size(); i++) {
        Checkpoint c = this.checklist.getChecklist().getCheckpoint(i);
        if (i == this.checklist.getIndex()) {
          sb.append("-->");
        }

        sb.append(" (" + ((c.isNegative()) ? "!" : "") + c.getName() + ", tn= "
            + ((matched[i] != null) ? matched[i].getThreadName() : "?") + ")");
        if (this.checklist.getChecklist().getConditionIndex() == i) {
          sb.append("=>");
        } else {
          sb.append(",");
        }
      }
      return sb.toString();
    }

  }

  protected static class ChecklistCompleted {
    ChecklistInstance checklist;
    String scriptActions;

    public ChecklistCompleted(ChecklistInstance checklist, String scriptActions) {
      super();
      this.checklist = checklist;
      this.scriptActions = scriptActions;
    }

    public boolean isCompleted() {
      return checklist.isCompleted();
    }

    public String getName() {
      return this.checklist.getChecklist().getName();
    }

    public String getChecklist() {
      StringBuilder sb = new StringBuilder("");

      CheckpointInstance[] matched = this.checklist.checkpointsMatched;
      for (int i = 0; i < this.checklist.getChecklist().size(); i++) {
        Checkpoint c = this.checklist.getChecklist().getCheckpoint(i);
        if (i == this.checklist.getIndex()) {
          sb.append("-->");
        }

        sb.append(" (" + ((c.isNegative()) ? "!" : "") + c.getName() + ", tn= "
            + ((matched[i] != null) ? matched[i].getThreadName() : "?") + ")");
        if (this.checklist.getChecklist().getConditionIndex() == i) {
          sb.append("=>");
        } else {
          sb.append(",");
        }
      }
      return sb.toString();
    }

  }
}
