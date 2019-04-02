package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.SolverVisitor;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;

public class BooleanConstant implements Formula {
  private final long label;

  private int hash;

  public static final BooleanConstant TRUE = new BooleanConstant(Long.MAX_VALUE);
  public static final BooleanConstant FALSE = new BooleanConstant(Long.MIN_VALUE);

  private BooleanConstant(long label) {
    this.label = label;
    this.hash = 0;
  }

  public static BooleanConstant byVal(boolean val) {
    return val ? TRUE : FALSE;
  }

  @Override
  public Term negation() {
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
    return this == TRUE ? "T" : "F";
  }

  @Override
  public <T> T accept(SolverVisitor<T> visitor) {
    return visitor.visit(this);
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
    if (hash == 0) {
      hash = (int) (label ^ (label >>> 32));
    }

    return hash;
  }

  @NonNull
  @Override
  public Iterator<Term> iterator() {
    return Collections.emptyIterator();
  }
}
