package gov.nasa.jpf.android;

import gov.nasa.jpf.android.RParser.RParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper class that retrieves and parses the project package.
 * 
 * @author Heila van der Merwe
 * 
 */
public class AndroidProjectInfo {

  public static enum BuildType {
    GRADLE, ANT;
  }

  public static String layoutType = "layout";

  public static class ProjectParseException extends Exception {

    public ProjectParseException() {
    }

    public ProjectParseException(String message) {
      super(message);
    }
  }

  private String projectDir;
  /**
   * The build tool that was used to build the project. This is important since it determines where the
   * compiled files are stored after a build.
   */
  private BuildType buildType;

  private String antBuildDirectoryPath;
  private String gradleBuildDirectoryPath;

  /** Parsed to get package information */
  private AndroidManifest androidManifest;

  /** Used to map resources hex values to filename */
  private RFile RFile;

  /** used to look for layout files */
  private String layoutDir;

  /** Used to map id's to Strings */
  private Map<String, String> stringNameToValueMap;

  private static AndroidProjectInfo instance;

  public static AndroidProjectInfo get() {
    if (instance == null) {
      try {
        instance = new AndroidProjectInfo();
      } catch (ProjectParseException e) {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
      }
    }
    return instance;
  }

  public AndroidProjectInfo() throws ProjectParseException {

    // search for bin(ant) or build(gradle) folder to confirm build tool
    // if none exist, throw error that project has not been not built.

    // TODO need to check this
    projectDir = System.getProperty("user.dir");
    System.out.println("ProjectDirectory: " + projectDir);

    antBuildDirectoryPath = projectDir + File.separator + "bin";
    File antBuildDirectory = new File(antBuildDirectoryPath);

    gradleBuildDirectoryPath = projectDir + File.separator + "build";
    File gradleBuildDirectory = new File(gradleBuildDirectoryPath);

    if (antBuildDirectory.exists()) {
      buildType = BuildType.ANT;
      System.out.println("Detected ANT Project");
    } else if (gradleBuildDirectory.exists()) {
      buildType = BuildType.GRADLE;
      System.out.println("Detected Gradle Project");

    } else {
      throw new ProjectParseException(
          "The Android project that is being run on JPF-Android has not been build. Please build it before running JPF-Android.");
    }

    // get path to manifest file (bin/AndroidManifest.xml or build/manifests/release/AndroidManifest.xml)
    String manifestPath = null;
    if (buildType.equals(BuildType.ANT)) {
      manifestPath = antBuildDirectoryPath + File.separator + "AndroidManifest.xml";
    } else {
      manifestPath = gradleBuildDirectoryPath + File.separator + "manifests" + File.separator + "release"
          + File.separator + "AndroidManifest.xml";
    }

    File androidManifestFile = new File(manifestPath);
    if (!androidManifestFile.exists()) {
      throw new ProjectParseException("Could not find AndroidManifest.xml file on path: " + manifestPath
          + ". Did build complete without errors?");
    } else {
      try {
        androidManifest = AndroidManifestParser.getInstance().parse(new FileInputStream(androidManifestFile));
      } catch (Exception s) {
        throw new ProjectParseException("AndroidManifest.xml could not parse correctly: " + s.getMessage());
      }
    }

    // Search for R.java file (either gen/package or /build/source/r/release/com/example/calculator/R.java)
    String RPath = null;
    if (buildType.equals(BuildType.ANT)) {
      RPath = projectDir + File.separator + "gen" + File.separator
          + androidManifest.getPackageNameAsDirectoryPath() + File.separator + "R.java";
    } else {
      RPath = projectDir + File.separator + "build" + File.separator + "source" + File.separator + "r"
          + File.separator + "release" + File.separator + androidManifest.getPackageNameAsDirectoryPath()
          + File.separator + "R.java";
    }

    File rfile = new File(RPath);
    if (!rfile.exists()) {
      throw new ProjectParseException("Could not find R.java file at: " + RPath
          + ". Did build complete without errors?");
    } else {
      try {
        RFile = RParser.getInstance().parse(new FileInputStream(rfile));
      } catch (RParseException e) {
        throw new ProjectParseException(e.getMessage());
      } catch (FileNotFoundException e) {
        throw new ProjectParseException(e.getMessage());
      }
    }

    // get path to layout files (/build/res/all/release/layout)
    if (buildType.equals(BuildType.ANT)) {
      layoutDir = projectDir + File.separator + "res" + File.separator + layoutType;
    } else {
      layoutDir = gradleBuildDirectoryPath + File.separator + "res" + File.separator + "all" + File.separator
          + "release" + File.separator + layoutType;
    }

    // get path to value files (/build/res/all/release/values)
    String stringDir = null;
    if (buildType.equals(BuildType.ANT)) {
      stringDir = projectDir + File.separator +"res" +  File.separator + "values";
    } else {
      stringDir = projectDir + File.separator + "build" + File.separator + "res" + File.separator + "all"
          + File.separator + "release" + File.separator + "values";
    }
    try {
      stringNameToValueMap = new HashMap<String, String>();
      parseValueFiles(stringDir);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ProjectParseException(e.getMessage());
    }

  }

  private void parseValueFiles(String stringDir) throws SAXException, IOException,
      ParserConfigurationException {
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    for (File file : new File(stringDir).listFiles()) {
      Document doc = db.parse(file);
      NodeList nodeList = doc.getElementsByTagName("string");
      Node node = null;
      for (int i = 0; i < nodeList.getLength(); i++) {
        node = nodeList.item(i);
        String nameAttributeString = node.getAttributes().getNamedItem("name").toString();
        String[] nameAttrSplit = nameAttributeString.split("=");
        stringNameToValueMap.put((String) nameAttrSplit[1].subSequence(1, nameAttrSplit[1].length() - 1),
            node.getTextContent());
      }
    }
  }

  /**
   * Returns the absolute path to the project on the file system.
   * 
   * @return the absolute path of the project directory.
   */
  public String getProjectDir() {
    return projectDir;
  }

  /**
   * Returns the absolute path to the Android manifest file of the project. This going to be a problem. Since
   * Android applications can have multiple AndroidManifest files we need to specify in Config which one we
   * want to use. For now we will assume the file is either in Project's directory or in the src/ directory
   * 
   * @return path to the manifest file.
   */
  public AndroidManifest getAndroidManifest() {
    return androidManifest;
  }

  /**
   * Returns the R file object
   * 
   * @param packageName
   *          the package name of the project
   * @return the path to the R.java file.
   */
  public RFile getRFile() {
    return RFile;
  }

  /**
   * Returns the absolute path to the layout files of the project.
   * 
   * @return the path to the layout files
   * @throws ProjectParseException
   */
  public String getLayoutFilename(Integer layoutID) throws ProjectParseException {

    String filename;
    try {
      filename = RFile.getLayoutNameForId(layoutID);
    } catch (Exception e) {
      throw new ProjectParseException(e.getMessage());
    }

    filename = layoutDir + File.separator + filename + ".xml";
    File layoutfile = new File(filename);
    if (!layoutfile.exists()) {
      throw new ProjectParseException("Could not find layout file: " + filename);
    }
    return filename;
  }

  /**
   * Returns the String value
   * 
   * @throws ProjectParseException
   * 
   * 
   */
  public String getStringValue(int id) throws ProjectParseException {
    String stringName;
    try {
      stringName = RFile.getStringNameForId(id);
    } catch (Exception e) {
      throw new ProjectParseException(e.getMessage());
    }
    return stringNameToValueMap.get(stringName);
  }

  /**
   * Returns the String value
   */
  public String getStringValue(String name) {
    return stringNameToValueMap.get(name);
  }

}
