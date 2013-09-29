package gov.nasa.jpf.android.checkpoint;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Map;

public class ChecklistParser {

  char TOKEN_OPEN_BRACKET = '[';
  char TOKEN_CLOSE_BRACKET = ']';
  String TOKEN_S_MAPPING = "Mappings";
  String TOKEN_S_CHECKLISTS = "Checklists";
  char TOKEN_EQUALS = '=';
  char TOKEN_GREATER_THAN = '>';
  char TOKEN_COLON = ':';
  char TOKEN_COMMA = ',';
  char TOKEN_TERMINATOR = ';';

  protected StreamTokenizer scanner;

  protected boolean done = false;

  public ChecklistParser(String name) throws ParseException {
    try {
      scanner = createScanner(new FileReader(name));
      scanner.nextToken();

    } catch (FileNotFoundException exception) {
      throw new ParseException("Could not find file \"" + name + "\".");
    } catch (IOException IOException) {
      throw new ParseException("Could not read file \"" + name + "\".");
    }

  }

  public ChecklistParser(Reader r) throws ParseException {
    try {

      scanner = createScanner(r);
      scanner.nextToken();

    } catch (IOException IOException) {
      throw new ParseException("Error reading input.");
    }
  }

  StreamTokenizer createScanner(Reader r) {
    StreamTokenizer s = new StreamTokenizer(r);

    // disable number parsing, since it doesn't work in the context of string expansion
    // and we also would have to preserve the number type (int or double)
    s.wordChars('0', '9');

    // those are used to expand events
    s.wordChars('*', '*');
    s.wordChars('!', '!');
    s.wordChars('+', '+');
    s.wordChars('_', '_');
    s.wordChars('(', '(');
    s.wordChars(')', ')');


    s.quoteChar('"');

    s.slashSlashComments(true);
    s.slashStarComments(true);

    return s;
  }

  public ChecklistDefinitions parse() throws ParseException {

    ChecklistDefinitions definitions = new ChecklistDefinitions();

    while (!done) {
      if (isMatch(TOKEN_OPEN_BRACKET)) {
        if (isMatch(TOKEN_S_CHECKLISTS)) {
          match(TOKEN_CLOSE_BRACKET);
          checklistSection(definitions);
        } else if (isMatch(TOKEN_S_MAPPING)) {
          match(TOKEN_CLOSE_BRACKET);
          mappingsSection(definitions);
        } else {
          throw new ParseException("Expected SECTION definition found " + scanner.sval + ".",
              scanner.lineno());
        }
      } else {
        checklistSection(definitions);
        done = true;
      }
    }
    return definitions;

  }

  protected void mappingsSection(ChecklistDefinitions definitions) throws ParseException {
    while (!done && scanner.ttype != TOKEN_OPEN_BRACKET && scanner.ttype != StreamTokenizer.TT_EOF) {
      Checkpoint cp = mapping();
      definitions.addMapping(cp.getMethodSignature(), cp);
    }
  }

  protected Checkpoint mapping() throws ParseException {
    // Create new  checkpoint
    Checkpoint cp = new Checkpoint(matchWord());

    match(TOKEN_COLON);
    cp.setMethodSignature(matchWord());
    match(TOKEN_COMMA);
    cp.setThreadName(matchWord());
    match(TOKEN_TERMINATOR);

    return cp;
  }

  protected void checklistSection(ChecklistDefinitions definitions) throws ParseException {
    while (!done && scanner.ttype != TOKEN_OPEN_BRACKET && scanner.ttype != StreamTokenizer.TT_EOF) {
      definitions.addChecklist(checklist());
    }
  }

  /**
   * Parses a Checklist
   * 
   * @return
   * @throws ParseException
   */
  protected Checklist checklist() throws ParseException {
    // make new Checklist definition object
    Checklist checklist = new Checklist(matchWord());

    match(TOKEN_COLON);
    do {

      // Create new  checkpoint
      Checkpoint cp = new Checkpoint(matchWord());
      checklist.addCheckpoint(cp);

      if (isMatch(TOKEN_EQUALS)) {
        match(TOKEN_GREATER_THAN);
        checklist.setConditionIndex(checklist.size() - 1);
      } else if (isMatch(TOKEN_COMMA)) {
        // skip comma
      }

    } while (!isMatch(TOKEN_TERMINATOR));

    return checklist;
  }

  void nextToken() throws ParseException {
    try {
      if (scanner.nextToken() == StreamTokenizer.TT_EOF) {
        done = true;
      }
    } catch (IOException iox) {
      throw new ParseException("Could not read input" + iox.toString() + ".", scanner.lineno());
    }
  }

  protected void match(char c) throws ParseException {
    if (scanner.ttype == /* (int) */c) {
      nextToken();
    } else {
      throw new ParseException("Char '" + c + "' expected got Char \'" + (char) scanner.ttype + "'.",
          scanner.lineno());
    }
  }

  protected boolean isMatch(char c) throws ParseException {
    if (scanner.ttype == c) {
      nextToken();
      return true;
    } else {
      return false;
    }
  }

  protected boolean isMatch(String token) throws ParseException {
    if (scanner.ttype == StreamTokenizer.TT_WORD) {
      if (scanner.sval.equals(token.toString())) {
        nextToken();
        return true;
      }
    }
    return false;
  }

  protected String match(String token) throws ParseException {
    String s = matchWord();
    if (!s.equals(token.toString())) {
      throw new ParseException(
          "Tried to match \"" + token.toString() + "\", but got \"" + scanner.sval + ".", scanner.lineno());
    }
    return s;
  }

  private String matchWord() throws ParseException {
    if (scanner.ttype == StreamTokenizer.TT_WORD) {
      String s = scanner.sval;
      if (s != null && s.length() > 0) {
        nextToken();
        return s;
      } else {
        throw new ParseException("Expected ID got " + s + ".", scanner.lineno());
      }

    } else {
      throw new ParseException("ID or keyword expected got \"" + (char) scanner.ttype + "\".",
          scanner.lineno());
    }
  }

  public static class ChecklistDefinitions {
    Map<String, Checklist> checklists;
    Map<String, Checkpoint> mappings;

    public ChecklistDefinitions() {
      checklists = new HashMap<String, Checklist>();
      mappings = new HashMap<String, Checkpoint>();
    }

    public Map<String, Checklist> getChecklists() {
      return checklists;
    }

    public void addChecklist(Checklist checklist) {
      checklists.put(checklist.getName(), checklist);
    }

    public void addMapping(String methodSignature, Checkpoint mapping) {
      mappings.put(methodSignature, mapping);
    }

    public Map<String, Checkpoint> getMappings() {
      return mappings;
    }

  }

}
