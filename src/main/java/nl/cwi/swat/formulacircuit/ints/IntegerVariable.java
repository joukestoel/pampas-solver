package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.SolverVisitor;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;

public class IntegerVariable implements Expression {
  private final long label;
  private final String name;
  private int hash;

  public IntegerVariable(@NonNull String name, long label) {
    this.label = label;
    this.name = name;
  }

  @Override
  public long label() {
    return label;
  }

  @Override
  public int size() {
    return 0;
  }

  public String getName() {
    return name;
  }

  @Override
  public Term input(int pos) {
    throw new IndexOutOfBoundsException("Can not get input from a zero-arity expression");
  }

  @Override
  public Operator operator() {
    return IntegerOperator.INT_VAR;
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

    IntegerVariable that = (IntegerVariable) o;

    if (label != that.label) return false;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    if (hash == 0) {
      hash = (int) (label ^ (label >>> 32));
      hash = 31 * hash + name.hashCode();
    }

    return hash;
  }
}
