package sun.misc;

//import java.io.Console;
import java.nio.charset.Charset;

/**
 * this is a placeholder for a Java 6 class, which we only have here to
 * support both Java 1.5 and 6 with the same set of env/ classes
 *
 * see sun.msic.SharedSecrets for details
 *
 * <2do> THIS IS GOING AWAY AS SOON AS WE OFFICIALLY SWITCH TO JAVA 6
 */
public interface JavaIOAccess {
    //public Console console(); // not in Java 1.5, so we skip for now
    public Runnable consoleRestoreHook();
    public Charset charset();
}
