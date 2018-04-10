package nl.cwi.swat.smtlogic;

public class BooleanSort extends Sort {
  public static BooleanSort BOOLEAN = new BooleanSort();

  public Expression newVar(String name, long label) {
    return new BooleanVariable(name, label);
  }
}
