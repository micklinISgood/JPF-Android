package gov.nasa.jpf.jvm;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.VM;

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
  public void classLoaded(VM vm, ClassInfo loadedClass) {

    if (loadedClass.getName().equals(target)) {

      MethodInfo[] methods = loadedClass.getDeclaredMethodInfos();
      boolean found = false;
      for (MethodInfo method : methods) {
        if (method.getFullName().contains("\\.main([Ljava/lang/String;)V")) {
          found = true;

        }
      }

      if (!found) {
        MethodInfo m = generateMethodInfo(loadedClass);
        loadedClass.putDeclaredMethod(m);
        logger.info("main() method injected into class: " + loadedClass.getName());

      } else {
        logger.info("main() method found in class: " + loadedClass.getName());

      }

    }
  }

  /**
   * 
   * @param ci
   *          the ClassInfo
   * @return
   */
  protected MethodInfo generateMethodInfo(ClassInfo ci) {
    MethodInfo m = new MethodInfo(ci, "main", "([Ljava/lang/String;)V", Modifier.PUBLIC | Modifier.STATIC, 1,
        0);
    Instruction[] i = new Instruction[2];

    JVMInstructionFactory insnFactory = JVMInstructionFactory.getFactory();
    i[0] = insnFactory.invokestatic("android/os/ServiceManager", "start", "()V");
    i[1] = insnFactory.return_();
    m.setCode(i);
    return m;
  }

}