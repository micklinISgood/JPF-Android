package gov.nasa.jpf.test.android.checkpoint;

import gov.nasa.jpf.android.checkpoint.Checklist;
import gov.nasa.jpf.android.checkpoint.ChecklistParser;
import gov.nasa.jpf.android.checkpoint.ChecklistParser.ChecklistDefinitions;
import gov.nasa.jpf.android.checkpoint.ParseException;
import gov.nasa.jpf.util.test.TestJPF;

import java.io.StringReader;

import org.junit.Test;

public class ChecklistParserTest extends TestJPF {

  @Test
  public void testNoDefChecklists() throws ParseException {
    String s = "";
    ChecklistParser cpp = new ChecklistParser(new StringReader(s));
    ChecklistDefinitions def = cpp.parse();
    assertEquals(0, def.getChecklists().size());
  }
  
  @Test
  public void testNoChecklists() throws ParseException {
    String s = "[Checklists]";
    ChecklistParser cpp = new ChecklistParser(new StringReader(s));
    ChecklistDefinitions def = cpp.parse();
    assertEquals(0, def.getChecklists().size());
  }

  @Test
  public void testSingleChecklist() throws ParseException {
    String s = "[Checklists] button_update_clicked: onClick, run => doInBackground;";
    ChecklistParser cpp = new ChecklistParser(new StringReader(s));
    ChecklistDefinitions def = cpp.parse();
    assertEquals(1, def.getChecklists().size());
    Checklist cl = def.getChecklists().get("button_update_clicked");
    assertEquals("button_update_clicked", cl.getName());
    assertEquals(1, cl.getConditionIndex());
    assertEquals(3, cl.size());

  }
  
  @Test
  public void testNoDefSingleChecklist() throws ParseException {
    String s = "button_update_clicked: onClick, run => doInBackground;";
    ChecklistParser cpp = new ChecklistParser(new StringReader(s));
    ChecklistDefinitions def = cpp.parse();
    assertEquals(1, def.getChecklists().size());
    Checklist cl = def.getChecklists().get("button_update_clicked");
    assertEquals("button_update_clicked", cl.getName());
    assertEquals(1, cl.getConditionIndex());
    assertEquals(3, cl.size());

  }

  @Test
  public void testNoNameChecklist() throws ParseException {
    String s = "[Checklists] : onClick, run => doInBackground;";
    ChecklistParser cpp = new ChecklistParser(new StringReader(s));
    try {
      ChecklistDefinitions def = cpp.parse();
    } catch (ParseException e) {
      assertEquals("Error Parsing Checklists on line 1: ID or keyword expected got \":\".", e.getMessage());
    }
  }

  @Test
  public void testNoColonChecklist() throws ParseException {
    String s = "[Checklists] button_update_clicked onClick, run => doInBackground;";
    ChecklistParser cpp = new ChecklistParser(new StringReader(s));
    try {
      ChecklistDefinitions def = cpp.parse();
    } catch (ParseException e) {
      assertEquals(true, e.getMessage()!= null);
    }
  }

  @Test
  public void testNoColonNameChecklist() throws ParseException {
    String s = "[Checklists] onClick, run => doInBackground;";
    ChecklistParser cpp = new ChecklistParser(new StringReader(s));
    try {
      ChecklistDefinitions def = cpp.parse();
    } catch (ParseException e) {
      assertEquals("Error Parsing Checklists on line 1: Char ':' expected got Char ','.", e.getMessage());
    }
  }
  
  
  @Test
  public void testNoDefMutipleChecklist() throws ParseException {
    String s = "button_update_clicked: onClick, run => doInBackground; button_update_clicked2: onClick2, run2 => doInBackground2;";
    ChecklistParser cpp = new ChecklistParser(new StringReader(s));
    ChecklistDefinitions def = cpp.parse();
    assertEquals(2, def.getChecklists().size());
    Checklist cl = def.getChecklists().get("button_update_clicked2");
    assertEquals("button_update_clicked2", cl.getName());
    assertEquals(1, cl.getConditionIndex());
    assertEquals(3, cl.size());

  }
  

}
