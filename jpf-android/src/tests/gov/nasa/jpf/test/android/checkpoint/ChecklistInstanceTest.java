package gov.nasa.jpf.test.android.checkpoint;

import gov.nasa.jpf.android.checkpoint.Checklist;
import gov.nasa.jpf.android.checkpoint.ChecklistInstance;
import gov.nasa.jpf.android.checkpoint.Checkpoint;
import gov.nasa.jpf.android.checkpoint.CheckpointInstance;
import gov.nasa.jpf.android.checkpoint.Path;
import gov.nasa.jpf.util.test.TestJPF;

import java.util.ArrayList;

import org.junit.Test;

public class ChecklistInstanceTest extends TestJPF {
//
//  Checkpoint cp1 = new Checkpoint("First", "main");
//  Checkpoint cp2 = new Checkpoint("second", "main");
//  Checkpoint cp3 = new Checkpoint("second", "thread1");
//  Checkpoint cp4 = new Checkpoint("!second", "thread1");
//
//  @Test
//  public void test1() {
//    ArrayList<Checkpoint> points = new ArrayList<Checkpoint>();
//    points.add(cp1);
//    points.add(cp2);
//    points.add(cp2);
//    points.add(cp3);
//
//    Checklist list1 = new Checklist("Test1", points, 0);
//    ChecklistInstance cl = new ChecklistInstance(list1, new Path(1, 1));
//    CheckpointInstance cpi1 = new CheckpointInstance(cp1, 0, 1, 1);
//    CheckpointInstance cpi2 = new CheckpointInstance(cp2, 0, 1, 1);
//    CheckpointInstance cpi3 = new CheckpointInstance(cp3, 0, 1, 1);
//
//    assertEquals(true, cl.match(cpi1));
//    assertEquals(true, cl.match(cpi2));
//    assertEquals(false, cl.match(cpi3));
//
//    assertEquals(true, cl.match(cpi2));
//    assertEquals(true, cl.match(cpi3));
//  }
//
//  @Test
//  public void test2() {
//    ArrayList<Checkpoint> points = new ArrayList<Checkpoint>();
//    points.add(cp1);
//    points.add(cp2);
//    points.add(cp1);
//    points.add(cp4);
//    Checklist list1 = new Checklist("Test2", points, 1);
//
//    ChecklistInstance cl = new ChecklistInstance(list1, new Path(1, 1));
//    CheckpointInstance cpi1 = new CheckpointInstance(cp1, 0, 1, 1);
//    CheckpointInstance cpi2 = new CheckpointInstance(cp2, 0, 1, 1);
//    CheckpointInstance cpi3 = new CheckpointInstance(cp1, 0, 1, 1);
//
//    assertEquals(true, cl.match(cpi1));
//    assertEquals(true, cl.match(cpi2));
//    assertEquals(true, cl.match(cpi1));
//    assertEquals(true, cl.isCompleted());
//
//  }

}
