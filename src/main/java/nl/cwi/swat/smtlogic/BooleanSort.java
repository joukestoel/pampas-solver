package nl.cwi.swat.smtlogic;

public class BooleanSort extends Sort {
  public static BooleanSort BOOLEAN = new BooleanSort();

  @Override
  public Expression newVar(String name) {
    return new BooleanVariable(name);
  }
}
