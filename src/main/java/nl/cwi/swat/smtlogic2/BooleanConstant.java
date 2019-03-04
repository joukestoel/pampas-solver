package nl.cwi.swat.smtlogic2;

public class BooleanConstant implements Formula {
  private final boolean value;
  private final long label;

  public static final BooleanConstant TRUE = new BooleanConstant(true, Long.MAX_VALUE);
  public static final BooleanConstant FALSE = new BooleanConstant(false, Long.MIN_VALUE);

  private BooleanConstant(boolean value, long label) {
    this.value = value;
    this.label = label;
  }

  @Override
  public Formula negation() {
    return this == TRUE ? FALSE : TRUE;
  }

  @Override
  public long label() {
    return label;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Term input(int pos) {
    throw new IndexOutOfBoundsException();
  }

  @Override
  public BooleanOperator operator() {
    return BooleanOperator.CONST;
  }

  @Override
  public String toString() {
    return this == TRUE ? "TRUE" : "FALSE";
  }

}
