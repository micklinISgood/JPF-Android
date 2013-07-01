package gov.nasa.jpf.android.checkpoint;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.android.checkpoint.ChecklistManager.CheckList;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.PublisherExtensionAdapter;

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
public class ChecklistReporter extends PublisherExtensionAdapter {
  private static final String TAG = ChecklistReporter.class.getSimpleName();
  private static Logger logger = JPF.getLogger(ChecklistReporter.class.getName());

  private List<CheckList> violations = new LinkedList<CheckList>();

  private ChecklistManager manager;

  public ChecklistReporter(ChecklistManager manager) {
    this.manager = manager;
  }

  /**
   * Processed and returns a String representation of the list of violating
   * Checklists.
   * 
   * @return
   */
  private String getViolations() {
    StringBuilder s = new StringBuilder("Checklist:\tLine Nr: \tList: \t\t\t\t\t Reason:\n");
    
    manager.getUnfinishedCheckLists();
    
    for (CheckList list : violations) {
      list.set(list.getCurrentIndex()+1, "-->" + list.getNextItem());
      s.append(list.getName() + "\t\t");
      s.append(list.getLineNumber() + "\t\t");
      s.append(list + "\t\t");

      if (list.get(list.getCurrentIndex()).startsWith("-->!")) {
        s.append("Failed because checkpoint \"" + list.getCurrentItem() + "\" was reached.\n");
      } else {
        s.append("Checkpoints " + list.subList(list.getCurrentIndex() + 1, list.size())
            + " not visited before unregistration.\n");
      }
    }
    
    
    return s.toString();
  }

  /**
   * Registers checklist as a violating {@link CheckList} due to not reaching
   * all
   * Checkpoints before being unregistered.
   * 
   * @param checklist
   */
  public void checkListNotFinished(CheckList checklist) {
    logger.info(TAG + ": Violating Checklist " + checklist
        + " added to ChecklistReporter for not reaching Checkpoint " + checklist.getCurrentIndex()+1
        + " before being unregistered.");
    violations.add(checklist);
  }

  /**
   * Registers checklists as violating {@link CheckList}s due to reaching an
   * invalid Checkpoint.
   * 
   * @param checklist
   */
  public void checkPointViolated(List<CheckList> violatingList) {
    if (violatingList != null && violatingList.size() > 0) {
      logger.info(TAG + ": Violating Checklists " + violatingList
          + " added to ChecklistReporter for reaching an invalid Checkpoint. ");

      for (CheckList list : violatingList) {
        violations.add(list);
      }
    }

  }

  @Override
  public void publishPropertyViolation(Publisher publisher) {
    publisher.getOut().println(
        "====================================================== violating CheckLists\n" + getViolations());
  }

  @Override
  public void publishConstraintHit(Publisher publisher) {
    publisher.getOut().println(
        "====================================================== violating CheckLists\n" + getViolations());
  }

  @Override
  public void publishFinished(Publisher publisher) {
    publisher.getOut().println(
        "====================================================== violating CheckLists\n" + getViolations());
  }

}
