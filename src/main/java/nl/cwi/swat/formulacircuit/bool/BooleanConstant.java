package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;

public class BooleanConstant implements Formula {
  private final long label;

  public static final BooleanConstant TRUE = new BooleanConstant(Long.MAX_VALUE);
  public static final BooleanConstant FALSE = new BooleanConstant(Long.MIN_VALUE);

  private BooleanConstant(long label) {
    this.label = label;
  }

  public static BooleanConstant byVal(boolean val) {
    return val ? TRUE : FALSE;
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
    return BooleanOperator.BOOLEAN_CONST;
  }

  @Override
  public String toString() {
    return this == TRUE ? "TRUE" : "FALSE";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BooleanConstant that = (BooleanConstant) o;

    return label == that.label;
  }

  @Override
  public int hashCode() {
    return (int) (label ^ (label >>> 32));
  }

  @NonNull
  @Override
  public Iterator<Formula> iterator() {
    return Collections.emptyIterator();
  }
}
