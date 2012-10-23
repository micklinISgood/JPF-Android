package gov.nasa.jpf.test.android.os;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

import android.os.MessageQueue;

public class MessageQueueTest extends TestJPF {

  @Test
  public void testProcessScriptAction() {
    if (verifyNoPropertyViolation("+awt.script=${jpf-android}/src/tests/gov/nasa/jpf/test/android/os/Test.es")) {
      // MessageQueue queue = new MessageQueue();
      // for (int i = 0; i < 10; i++)
      // queue.processScriptAction();

    }

  }
}
