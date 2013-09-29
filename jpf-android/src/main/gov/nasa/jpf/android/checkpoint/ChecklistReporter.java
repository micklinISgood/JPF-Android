package gov.nasa.jpf.android.checkpoint;

import java.util.List;

public interface ChecklistReporter {
  
  public List<ChecklistInstance> getFailedChecklists();
  public List<ChecklistInstance> getCompletedChecklists();
 
}
