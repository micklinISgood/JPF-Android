package gov.nasa.jpf.android;

public class IntentEntry {

  private String mPackage;
  private String mClass;

  public IntentEntry() {

  }

  public void setComponent(String componentName) {
    String[] name = componentName.split("\\.");
    if (name.length > 1) {
      mPackage = componentName.substring(0, componentName.lastIndexOf("."));
      if (mPackage.length() == 0) {
        mPackage = getPackageName();
      }
    }
    mClass = name[name.length - 1]; // get the string after the last ".", this will be the name
  }

  private String getPackageName() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getPackage() {
    return mPackage;
  }

  public void setPackage(String mPackage) {
    this.mPackage = mPackage;
  }

  public String getClassName() {
    return mClass;
  }

  public void setClassName(String mClass) {
    this.mClass = mClass;
  }

}
