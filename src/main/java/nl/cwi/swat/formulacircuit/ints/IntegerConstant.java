package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Operator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;

public class IntegerConstant implements Expression {
  private final long label;
  private final int val;

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
  public Expression input(int pos) {
    throw new IndexOutOfBoundsException("Constant is zero arity");
  }

  @Override
  public Operator operator() {
    return IntegerOperator.INT_CONST;
  }

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return Collections.emptyIterator();
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
    int result = (int) (label ^ (label >>> 32));
    result = 31 * result + val;
    return result;
  }
}
