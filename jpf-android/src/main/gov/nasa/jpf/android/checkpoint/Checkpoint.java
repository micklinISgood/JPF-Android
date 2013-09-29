package gov.nasa.jpf.android.checkpoint;

/**
 * Represents a method in the code which has to be reached by threadname
 * 
 * @author Heila van der Merwe
 * 
 */
public class Checkpoint {

  /** Uniquely identifiable name */
  private final String name;

  /** the name of the thread that has to cover this checkpoint */
  private String threadName;

  /** Stores it this point is negative */
  private final boolean negative;

  /** method signature */
  private String methodSignature;

  public Checkpoint(String name, String threadAlias) {
    if (name.startsWith("!")) {
      negative = true;
      this.name = name.substring(1);
    } else {
      this.name = name;
      this.negative = false;

    }
    this.threadName = threadAlias;

  }

  public Checkpoint(String name) {
    if (name.startsWith("!")) {
      negative = true;
      this.name = name.substring(1);
    } else {
      this.name = name;
      this.negative = false;

    }
    this.threadName = "";

  }

  public String getMethodSignature() {
    return methodSignature;
  }

  public void setMethodSignature(String methodSignature) {
    this.methodSignature = methodSignature;
  }

  protected String getName() {
    return this.name;
  }

  protected String getThreadName() {
    return this.threadName;
  }
  
  protected void setThreadName(String threadName) {
    this.threadName = threadName;
  }


  public boolean isNegative() {
    return negative;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((threadName == null) ? 0 : threadName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Checkpoint other = (Checkpoint) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (threadName == null) {
      if (other.threadName != null)
        return false;
    } else if (!threadName.equals(other.threadName))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Checkpoint [name=" + name + ", threadName=" + threadName + ", negative=" + negative + "]";
  }

}
