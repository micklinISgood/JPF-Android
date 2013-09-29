package gov.nasa.jpf.android.checkpoint;

import java.util.List;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.android.checkpoint.ChecklistManager.ChecklistsState;
import gov.nasa.jpf.util.StateExtensionClient;

public interface ChecklistStateManager extends StateExtensionClient<ChecklistsState> {

  public ChecklistsState getStateExtension();

  public void restore(ChecklistsState stateExtension);

  public void registerListener(JPF jpf);


}
