package nl.cwi.swat.smtlogic.ints;

import nl.cwi.swat.smtlogic.Expression;

public class IntVariable extends Expression {
  private final String name;

  public IntVariable(String name) {
    this.name = name;
  }
}
