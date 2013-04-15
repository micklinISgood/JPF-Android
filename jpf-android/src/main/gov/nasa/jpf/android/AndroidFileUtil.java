package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;

/**
 * Helper class that retrieves the project's classpath from the JPF config object and then determines the
 * paths to certain important files in the project's directory.
 * 
 * @author Heila van der Merwe
 * 
 */
public class AndroidFileUtil {

  private static Config config;
  private static String classpath;
  private static String projectDir;

  public static void init(Config conf) {
    config = conf;
    classpath = config.getProperty("classpath");

    // We assume the first entry in the classpath is this project's entry
    projectDir = classpath.split(";")[0];

    // Trim away extra trailing path separators
    if (projectDir.endsWith("/")) {
      projectDir = projectDir.substring(0, projectDir.length() - 1);
    }

    // Trim away /bin/classes
    projectDir = projectDir.substring(0, projectDir.indexOf("/bin/classes"));

  }

  public static String getProjectDir() {
    return projectDir;
  }

  public static String getManifestPath() {
    if (projectDir == null || projectDir.length() <= 0)
      return null;
    return projectDir + "/AndroidManifest.xml";
  }

  public static String getRPath(String packageName) {
    if (projectDir == null || projectDir.length() <= 0)
      return null;
    return projectDir + "/gen/" + packageName + "/R.java";
  }

  public static String getLayoutPath() {
    if (projectDir == null || projectDir.length() <= 0)
      return null;
    return projectDir + "/res/layout/";
  }

}
