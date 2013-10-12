package gov.nasa.jpf.test.android.checkpoint;

import gov.nasa.jpf.android.checkpoint.Checklist;
import gov.nasa.jpf.android.checkpoint.Checkpoint;
import gov.nasa.jpf.util.test.TestJPF;

import java.util.ArrayList;

import org.junit.Test;

public class ChecklistTest extends TestJPF {

  @Test
  public void testStartsWith() {
    Checkpoint c = new Checkpoint("First", "main");

    ArrayList<Checkpoint> points = new ArrayList<Checkpoint>();
    points.add(new Checkpoint("First", "main"));
    points.add(new Checkpoint("!First", "main"));
    Checklist list = new Checklist("Test1", points, 0);

    assertEquals(true, list.startsWith(c));

  }

  @Test
  public void testStartsWith2() {
    Checkpoint c = new Checkpoint("First", "main");

    ArrayList<Checkpoint> points = new ArrayList<Checkpoint>();
    points.add(new Checkpoint("!First", "thread1"));
    points.add(new Checkpoint("First", "main"));
    Checklist list = new Checklist("Test1", points, 0);

    assertEquals(true, list.startsWith(c));

  }

  @Test
  public void testNotStartsWith() {
    Checkpoint c = new Checkpoint("First", "main");
    ArrayList<Checkpoint> points = new ArrayList<Checkpoint>();
    points.add(new Checkpoint("Second", "main"));
    points.add(new Checkpoint("!First", "main"));
    Checklist list = new Checklist("Test1", points, 0);

    assertEquals(false, list.startsWith(c));

  }

  @Test
  public void testNotStartsWith2() {
    Checkpoint c = new Checkpoint("First", "main");
    ArrayList<Checkpoint> points = new ArrayList<Checkpoint>();
    points.add(new Checkpoint("!First", "main"));
    points.add(new Checkpoint("First", "thread1"));
    Checklist list = new Checklist("Test1", points, 0);

    assertEquals(true, list.startsWith(c));

  }

}
