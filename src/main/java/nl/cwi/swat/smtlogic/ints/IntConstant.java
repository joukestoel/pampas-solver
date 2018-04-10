package nl.cwi.swat.smtlogic.ints;

import nl.cwi.swat.smtlogic.Literal;
import nl.cwi.swat.smtlogic.Operator;

public class IntConstant extends Literal{
  private final int value;

  public IntConstant(int value) {
    this.value = value;
  }

  @Override
  public Operator operator() {
    return null;
  }
}
