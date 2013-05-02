package gov.nasa.jpf.test.mc.basic;

/**
 * Parses a nondeterministic script and prints out all the sequences.
 * 
 * @author Heila van der Merwe
 * 
 */
public class ScriptParser {

  /**
   * The number of ScriptElements that have been parsed before this element in a
   * specific branch of the script.
   */
  int numElementsParsed = 0;


  public ScriptParser(String script) {
    // to be intercepted
  }

  /**
   *  Returns a sequence of ScriptElements the script.
   * @return
   */
  public String parseScript() {
    
    StringBuilder outputSequence = new StringBuilder();
    String s = "";
    do {

      s = getNextScriptElement();
      if (s != null && s.length() > 0) {
        numElementsParsed++;
        outputSequence.append("#" + numElementsParsed + ": " + s + "\n");
      }
    } while (s != null && s.length() > 0);
    
    return outputSequence.toString();
  }

  private native String getNextScriptElement();

}
