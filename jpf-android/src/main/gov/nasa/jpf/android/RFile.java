package gov.nasa.jpf.android;

import java.util.HashMap;

public class RFile {

  /**
   * Maps the resource ID of a layout to the name of the layout file. This information is read from the R.java
   * class and stored as a map fir quick lookup by the LayoutInflater.
   */
  HashMap<Integer, String> layoutIdToNameMap = new HashMap<Integer, String>();

  HashMap<Integer, String> viewIdToNameMap = new HashMap<Integer, String>();

  HashMap<Integer, String> stringIdToNameMap = new HashMap<Integer, String>();

  HashMap<String, Integer> viewNameToIdMap = new HashMap<String, Integer>();

  public String getLayoutNameForId(Integer layoutID) throws Exception {
    String layoutFilename = layoutIdToNameMap.get(layoutID);
    if (layoutFilename == null) {
      throw new Exception("RFile: No layout with id " + layoutID);
    }
    return layoutFilename;
  }

  public String getViewNameForId(String viewID) throws Exception {
    String viewFilename = viewIdToNameMap.get(viewID);
    if (viewFilename == null) {
      throw new Exception("RFile: No view with id " + viewID);
    }
    return viewFilename;
  }

  public String getStringNameForId(int stringID) throws Exception {
    String stringName = stringIdToNameMap.get(stringID);
    if (stringName == null) {
      throw new Exception("RFile: No string with id " + stringID);
    }
    return stringName;
  }

  public Integer getViewIdForName(String name) throws Exception {
    Integer id = viewNameToIdMap.get(name);
    if (id == null) {
      throw new Exception("RFile: No view with name " + name);
    }
    return id;
  }
}
