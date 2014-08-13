package gov.nasa.jpf.android;

import java.io.InputStream;
import java.util.Scanner;

public class RParser {

  public static class RParseException extends Exception {
    public RParseException(String message) {
      super(message);
    }
  }

  private static RParser instance;

  private static final String LAYOUT_HEADER = "public static final class layout {";
  private static final String STRING_HEADER = "public static final class string {";
  private static final String ID_HEADER = "public static final class id {";

  // TODO
  private static final String MENU_HEADER = "public static final class menu {";
  private static final String STYLE_HEADER = "public static final class style {";
  private static final String BOOL_HEADER = "public static final class bool {";
  private static final String COLOR_HEADER = "public static final class color {";
  private static final String ARRAY_HEADER = "public static final class array {";
  private static final String ANIM_HEADER = "public static final class anim {";
  private static final String INTEGER_HEADER = "public static final class int {";

  private static final String FOOTER = "}";

  private RParser() {
  }

  public static RParser getInstance() {
    if (instance == null) {
      instance = new RParser();
    }
    return instance;
  }

  public RFile parse(InputStream is) throws RParseException {
    RFile rfile = new RFile();
    String nextLine;
    Scanner scanner = new Scanner(is);
    try {

      while (scanner != null && scanner.hasNextLine()) {
        nextLine = scanner.nextLine().trim();
        if (nextLine.equals(STRING_HEADER)) {
          parseStrings(scanner, rfile);
        } else if (nextLine.equals(LAYOUT_HEADER)) {
          parseLayouts(scanner, rfile);
        } else if (nextLine.equals(ID_HEADER)) {
          parseViews(scanner, rfile);
        }
      }
    } catch (Exception e) {
      throw new RParseException("Could not parse R.java file: " + e.getMessage());
    } finally {
      scanner.close();
    }
    return rfile;
  }

  private void parseStrings(Scanner scanner, RFile rfile) {
    String next = "";
    String[] list = null;

    while (scanner.hasNextLine()) {
      next = scanner.nextLine().trim();
      if (next.equals(FOOTER))
        break;
      list = getFields(next);
      rfile.stringIdToNameMap.put(Integer.parseInt(list[1].substring(2), 16), list[0]);
      System.out.println("RParser parsed String: " + Integer.parseInt(list[1].substring(2), 16) + " "
          + list[0]);
    }
  }

  /**
   * Parse the name and ID's of the View objects in the R.java file.
   * 
   * @param scanner
   */
  private void parseViews(Scanner scanner, RFile rfile) {
    String next = "";
    String[] list;
    while (scanner.hasNextLine()) {
      next = scanner.nextLine().trim();
      if (next.equals(FOOTER))
        break;
      list = getFields(next);
      rfile.viewIdToNameMap.put(Integer.parseInt(list[1].substring(2), 16), list[0]);
      rfile.viewNameToIdMap.put(list[0], Integer.parseInt(list[1].substring(2), 16));

      System.out
          .println("RParser parsed View: " + Integer.parseInt(list[1].substring(2), 16) + " " + list[0]);

    }
    // TODO Add window to the componentMap to catch window events
    rfile.viewIdToNameMap.put(0, "window");
    System.out.println("RParser parsed View: " + 0 + " " + "window");

  }

  /**
   * Parse the name and ID's of the layout file in the R.java file.
   * 
   * @param scanner
   */
  private void parseLayouts(Scanner scanner, RFile rfile) {
    String next = "";
    String[] list;
    while (scanner.hasNextLine()) {
      next = scanner.nextLine().trim();
      if (next.equals(FOOTER))
        break;
      list = getFields(next);
      rfile.layoutIdToNameMap.put(Integer.parseInt(list[1].substring(2), 16), list[0]);
      System.out.println("RParser parsed layout: " + Integer.parseInt(list[1].substring(2), 16) + " "
          + list[0]);

    }

  }

  /**
   * Returns the NAME and ID of an object given its declaration.
   * 
   * @param line
   * @return
   */
  private String[] getFields(String line) {
    String[] list;
    line = line.substring(0, line.length() - 1); // throw ';' away
    list = line.split(" ");
    line = list[list.length - 1]; // last item in list
    list = line.split("=");
    return list;

  }
}