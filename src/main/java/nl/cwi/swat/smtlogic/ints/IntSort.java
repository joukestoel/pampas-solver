package nl.cwi.swat.smtlogic.ints;

import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.Sort;

public class IntSort extends Sort {
  public static final Sort INT = new IntSort();

  @Override
  public Expression newVar(String name, long label) {
    return null;
  }
}
