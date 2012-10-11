package gov.nasa.jpf.util.script;

public class SectionState implements Cloneable {
  String sectionName; // the name of the section
  Section section; // pointer to the section object
  SequenceInterpreter intrp; // keeps track of the current position of the

  // section events

  public SectionState(String sectionName, Section section, SequenceInterpreter intrp) {
    this.sectionName = sectionName;
    this.section = section;
    this.intrp = intrp;
  }

  public Object clone() {
    try {
      SectionState as = (SectionState) super.clone();
      as.intrp = (SequenceInterpreter) intrp.clone();
      return as;
    } catch (CloneNotSupportedException nonsense) {
      return null; // we are a Cloneable, so we don't get here
    }
  }

  public boolean isDone() {
    return intrp.isDone();
  }

}
