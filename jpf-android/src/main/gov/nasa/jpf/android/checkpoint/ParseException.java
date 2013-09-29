package gov.nasa.jpf.android.checkpoint;

public class ParseException extends java.lang.Exception {
  private static final long serialVersionUID = 4125988745238609011L;

  public ParseException(String message) {
    super("Error Parsing Checklists: " + message);

  }

  public ParseException(String message, int linenumber) {
    super("Error Parsing Checklists on line " + linenumber + ": " + message);

  }

}