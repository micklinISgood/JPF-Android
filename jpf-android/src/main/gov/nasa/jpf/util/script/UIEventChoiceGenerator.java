package gov.nasa.jpf.util.script;

import gov.nasa.jpf.jvm.ChoiceGeneratorBase;
import gov.nasa.jpf.util.script.ScriptElementContainer.SECIterator;

public class UIEventChoiceGenerator extends ChoiceGeneratorBase<SECIterator> {

  ScriptElement se;
  int choice = 1;
  int numChoices;

  public UIEventChoiceGenerator(String id, ScriptElement scriptElement) {
    super(id);
    se = scriptElement;
    numChoices = ((ScriptElementContainer) se).getNumberOfChildren();

  }

  @Override
  public SECIterator getNextChoice() {
    SECIterator ret = ((Alternative) se).iterator(choice);
    choice++;
    return ret;
  }

  @Override
  public Class<SECIterator> getChoiceType() {
    return SECIterator.class;
  }

  @Override
  public boolean hasMoreChoices() {

    return choice < numChoices;
  }

  @Override
  public void advance() {
    choice++;
  }

  @Override
  public void reset() {
    choice = 1;

  }

  @Override
  public int getTotalNumberOfChoices() {

    return numChoices;
  }

  @Override
  public int getProcessedNumberOfChoices() {
    return choice;
  }

}
