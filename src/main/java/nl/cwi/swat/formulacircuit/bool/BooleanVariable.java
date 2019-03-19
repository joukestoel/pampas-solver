package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Operator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;

public class BooleanVariable implements Formula {
  private final String name;
  private final long label;

  private Formula negation;

  public BooleanVariable(@NonNull String name, long label) {
    this.name = name;
    this.label = label;
  }

  @Override
  public Operator operator() {
    return BooleanOperator.BOOLEAN_VAR;
  }

  @Override
  public Formula negation() {
    if (negation == null) {
      negation = new NotGate(this);
    }

    return negation;
  }

  @Override
  public long label() {
    return label;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Formula input(int pos) {
    throw new UnsupportedOperationException("Zero-arity boolean var gate has no inputs");
  }

  @NonNull
  @Override
  public Iterator<Formula> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BooleanVariable that = (BooleanVariable) o;

    if (label != that.label) return false;
    if (!name.equals(that.name)) return false;
    return negation.equals(that.negation);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + (int) (label ^ (label >>> 32));
    result = 31 * result + negation.hashCode();
    return result;
  }
}
