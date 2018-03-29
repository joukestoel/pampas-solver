package nl.cwi.swat.smtlogic.ints;

import nl.cwi.swat.smtlogic.Literal;

public class IntConstant extends Literal{
  private final int value;

  public IntConstant(int value) {
    this.value = value;
  }
}
