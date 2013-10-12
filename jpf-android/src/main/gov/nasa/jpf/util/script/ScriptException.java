package gov.nasa.jpf.util.script;

public class ScriptException extends Exception {
  private static final long serialVersionUID = -5021301564851614652L;

  public ScriptException(String message) {
    super(message);
  }

  public ScriptException(String message, Throwable cause) {
    super(message, cause);
  }

}
