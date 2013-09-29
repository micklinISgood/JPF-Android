package gov.nasa.jpf.android.checkpoint;

import java.util.Map;

public interface CheckpointProcessor {
  public void match(CheckpointInstance point);
  public Map<String, Checkpoint> getMethodMappings();

}
