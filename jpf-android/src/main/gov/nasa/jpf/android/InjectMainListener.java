package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.CodeBuilder;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;

import java.lang.reflect.Modifier;
import java.util.logging.Logger;

/**
 * Injects a main method into the target Android class's {@link ClassInfo}.
 * 
 * @author Heila van der Merwe
 * 
 */
public class InjectMainListener extends ListenerAdapter {
  private static Logger logger = JPF.getLogger(InjectMainListener.class.getName());
  private String target;

  public InjectMainListener(Config config) {
    target = config.getString("target");
  }

  /**
   * Called when a class is loaded
   */
  @Override
  public void classLoaded(JVM vm) {
    ClassInfo last = vm.getLastClassInfo();
    if (last.getName().equals(target)) {

      MethodInfo[] methods = last.getDeclaredMethodInfos();
      boolean found = false;
      for (MethodInfo method : methods) {
        if (method.getClassName().contains("main")) {
          found = true;
        }
      }

      if (!found) {
        MethodInfo m = generateMethodInfo(last);
        last.putDeclaredMethod(m);
      }

      logger.info("main() method injected into class: " + last.getName());
    }
  }

  /**
   * 
   * @param ci
   *          the ClassInfo
   * @return
   */
  private MethodInfo generateMethodInfo(ClassInfo ci) {
    MethodInfo m = new MethodInfo(ci, "main", "([Ljava/lang/String;)V", 1, 1, Modifier.PUBLIC
        | Modifier.STATIC);
    CodeBuilder cb = m.createCodeBuilder();
    cb.aconst_null();
    cb.invokestatic("android/app/ActivityThread", "start", "([Ljava/lang/String;)V");
    cb.return_();
    cb.installCode();
    return m;
  }

}