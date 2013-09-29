package gov.nasa.jpf.test.android.checkpoint;

import gov.nasa.jpf.android.checkpoint.Checkpoint;
import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

public class CheckpointTest extends TestJPF {

  Checkpoint c1 = new Checkpoint("First", "main");
  Checkpoint c2 = new Checkpoint("First", "main");
  Checkpoint c3 = new Checkpoint("First", "thread1");
  Checkpoint c4 = new Checkpoint("Second", "thread1");
  Checkpoint c5 = new Checkpoint("!Second", "thread1");
  Checkpoint c6 = new Checkpoint("!Second", "thread2");
  Checkpoint c7 = new Checkpoint("!Second", "thread1");

  @Test
  public void testEqualsCheckpoint() {
    assertEquals(true, c1.equals(c2));
    assertEquals(true, c2.equals(c1));
    
    assertEquals(true, c4.equals(c5));
    assertEquals(true, c5.equals(c4));
  
    assertEquals(true, c5.equals(c7));
    assertEquals(true, c7.equals(c5));

  }

  
  @Test
  public void testNotEqualsCheckpoint() {
    assertEquals(false, c1.equals(c3));
    assertEquals(false, c3.equals(c1));
    
    assertEquals(false, c3.equals(c4));
    assertEquals(false, c4.equals(c3));
  }

  @Test
  public void testisNegative() {
    assertEquals(false, c1.isNegative());
    assertEquals(true, c7.isNegative());    
  }
  
  @Test
  public void testToString() {
    
    assertEquals("Checkpoint [name=" + "First" + ", threadName=" + "main" + ", negative=" + "false" + "]", c1.toString()); 
    assertEquals("Checkpoint [name=" + "Second" + ", threadName=" + "thread1" + ", negative=" + "true" + "]", c7.toString());    

  }
  
  
}
