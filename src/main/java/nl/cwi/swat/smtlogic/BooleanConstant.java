package nl.cwi.swat.smtlogic;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public class BooleanConstant extends Literal implements Formula {
  private final boolean value;
  private final long label;

  public static final BooleanConstant TRUE = new BooleanConstant(true, Long.MAX_VALUE);
  public static final BooleanConstant FALSE = new BooleanConstant(false, Long.MIN_VALUE);

  private BooleanConstant(boolean value, long label) {
    this.value = value;
    this.label = label;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BooleanConstant formulas = (BooleanConstant) o;
    return value == formulas.value &&
            label == formulas.label;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, label);
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
  public int hash(Operator operator) {
    return hashCode();
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Formula input(int pos) {
    throw new IndexOutOfBoundsException();
  }

  @Override
  public Iterator<Formula> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public Operator operator() {
    return Operator.CONST;
  }

  @Override
  public String toString() {
    return this == TRUE ? "TRUE" : "FALSE";
  }
}
