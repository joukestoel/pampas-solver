package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.SolverVisitor;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;

public class IntegerConstant implements Expression {
  private final long label;
  private final int val;
  private int hash;

  public IntegerConstant(long label, int val) {
    this.label = label;
    this.val = val;
  }

  @Override
  public long label() {
    return label;
  }

  @Override
  public int size() {
    return 0;
  }

  public int getVal() {
    return val;
  }

  @Override
  public Term input(int pos) {
    throw new IndexOutOfBoundsException("Constant is zero arity");
  }

  @Override
  public Operator operator() {
    return IntegerOperator.INT_CONST;
  }

  @Override
  public Term negation() {
    throw new UnsupportedOperationException();
  }

  @NonNull
  @Override
  public Iterator<Term> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public <T> T accept(SolverVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    IntegerConstant that = (IntegerConstant) o;

    if (label != that.label) return false;
    return val == that.val;
  }

  @Override
  public int hashCode() {
    if (hash == 0) {
      hash = (int) (label ^ (label >>> 32));
      hash = 31 * hash + val;
    }

    return hash;
  }
}
