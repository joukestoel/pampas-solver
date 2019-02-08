package nl.cwi.swat.smtlogic.ints;

import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.Operator;

public class IntVariable implements Expression {
  private final String name;

  public IntVariable(String name) {
    this.name = name;
  }

  @Override
  public Operator operator() {
    return null;
  }

  @Override
  public int hash(Operator op) {
    return 0;
  }

  @Override
  public long label() {
    return 0;
  }
}
