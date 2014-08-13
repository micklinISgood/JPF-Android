package gov.nasa.jpf.android.checkpoint;

import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.ResultPublishListener;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.PublisherExtension;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.script.AndroidScriptEnvironment;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.VM;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Stores a list of violated {@link CheckList}. Publishes a report of which
 * {@link CheckList} were violated at the end of execution.
 * 
 * @author Heila van der Merwe
 * @date 2 June 2013
 * 
 */
public class ChecklistPublisher extends PropertyListenerAdapter implements PublisherExtension {

  private final static int REASON_NOT_COMPLETE = 0;
  private final static int REASON_MATCH_FAILED = 1;

  /** Stores list of violating Checklists */
  private List<ChecklistViolation> violatingChecklists = new LinkedList<ChecklistViolation>();

  /** Reports checklists that where violated */
  private ChecklistReporter reporter;

  /** st true when violation occurs */
  private boolean violation = false;

  private boolean published = false;

  private Search search;

  public ChecklistPublisher(ChecklistReporter reporter, AndroidScriptEnvironment scriptEnv) {
    this.reporter = reporter;
  }

  public boolean check(Search search, VM vm) {
    return !violation;
  }

  private void printViolations(PrintWriter pw) {
    for (ChecklistViolation violation : violatingChecklists) {
      pw.println("Checklist Name: " + violation.getName());
      pw.println("EventID: " + violation.checklistInstance.getPath().eventID + " PathID: "
          + violation.checklistInstance.getPath().pathID);
      pw.println("Checklist:" + violation.getChecklist());
      pw.print("Reason: ");
      if (violation.reason == REASON_MATCH_FAILED)
        pw.println(printMatchFailed(violation));
      else
        pw.println(printNotComplete(violation));
    }
  }

  private String printMatchFailed(ChecklistViolation violation) {
    StringBuilder s = new StringBuilder();
    Checklist cl = violation.checklist;
    ChecklistInstance cli = violation.checklistInstance;
    CheckpointInstance ins = cli.checkpointsMatched[cli.getIndex()];
    Checkpoint cp = cl.getCheckpoint(cli.getIndex());
    s.append("\nFailed because checkpoint reached (" + ins.getName() + ", tn=" + ins.getThreadAlias()
        + ") did not match checkpoint (" + ((cp.isNegative()) ? "!" : "") + cp.getName() + ", tn="
        + ins.getThreadName() + ")");
    return s.toString();
  }

  private String printNotComplete(ChecklistViolation violation) {
    StringBuilder s = new StringBuilder();
    Checklist cl = violation.checklist;
    Checkpoint cp = cl.getCheckpoint(violation.checklistInstance.getIndex());
    s.append("Checkpoint  (" + ((cp.isNegative()) ? "!" : "") + cp.getName()
        + ", tn=?) not visited before search ended.");
    return s.toString();
  }

  public void addViolatingChecklist(ChecklistInstance checklist, int reason) {
    String threadString = getThreadState();

    violatingChecklists.add(new ChecklistViolation(checklist, threadString, reason));
    violation = true;
  }

  @Override
  public String getErrorMessage() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    printViolations(pw);
    //TOD fix
    return sw.toString();
  }

  @Override
  public void publishPropertyViolation(Publisher publisher) {
    if (violatingChecklists.size() > 0) {
      PrintWriter pw = publisher.getOut();
      publisher.publishTopicStart("checklist violations " + publisher.getLastErrorId());
      printViolations(pw);
      published = true;

    }
  }

  @Override
  public void publishFinished(Publisher publisher) {
    if (violatingChecklists.size() > 0) {

      if (!published) {
        PrintWriter pw = publisher.getOut();

        for (PublisherExtension e : publisher.getExtensions()) {
          if (e instanceof ResultPublishListener) {
            e.publishPropertyViolation(publisher);
          }

        }

        publisher.publishTopicStart("checklist violations " + publisher.getLastErrorId());
        printViolations(pw);
      }
    }
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
      //add these lists to violating checklists
      this.search = search;
      for (ChecklistInstance instance : lists) {
        addViolatingChecklist(instance, REASON_NOT_COMPLETE);
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

  protected static class ChecklistViolation {

    ChecklistInstance checklistInstance;
    Checklist checklist;
    CheckpointInstance[] matchedPoints;
    String threadState;
    int reason = -1;

    public ChecklistViolation(ChecklistInstance checklistInstance, String threadState, int reason) {
      super();
      this.checklistInstance = checklistInstance;
      this.checklist = this.checklistInstance.getChecklist();
      this.matchedPoints = this.checklistInstance.checkpointsMatched;
      this.threadState = threadState;
      this.reason = reason;

      //stores the current index and size of checklist
      int currentIndex = checklistInstance.getIndex();
      int size = this.checklist.size();

      if (reason == REASON_MATCH_FAILED) {
        // this only happens when a negative checkpoint is reached, 
        // now we search for this point that was reached

        for (int i = currentIndex + 1; i < size; i++) {
          if (checklist.getCheckpoint(i).isNegative() && matchedPoints[i] != null) {
            checklistInstance.setIndex(i);
            currentIndex = i;
            break;
          }
        }

      } else {
        // this happens when a checkpoint was not reach before the search ended.
        // this checkpoint can be after some negative checkpoints, so we need
        // to search for this first positive point after the current point

        for (int i = currentIndex + 1; i < size; i++) {
          if (!checklist.getCheckpoint(i).isNegative()) {
            checklistInstance.setIndex(i);
            currentIndex = i;
            break;
          }
        }

      }

    }

    public String getName() {
      return this.checklistInstance.getChecklist().getName();
    }

    public String getChecklist() {

      StringBuilder sb = new StringBuilder("");

      for (int i = 0; i < checklist.size(); i++) {
        Checkpoint c = checklist.getCheckpoint(i);

        if (i == checklistInstance.getIndex()) {
          sb.append("-->");
        }

        sb.append(" (" + ((c.isNegative()) ? "!" : "") + c.getName() + ", tn= "
            + ((matchedPoints[i] != null) ? matchedPoints[i].getThreadName() : "?") + ")");

        if (this.checklist.getConditionIndex() == i) {
          sb.append("=>");
        } else {
          sb.append(",");
        }

      }
      return sb.toString();
    }

  }

}
