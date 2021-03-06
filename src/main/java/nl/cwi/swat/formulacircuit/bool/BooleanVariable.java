package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.SolverVisitor;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;

public class BooleanVariable implements Formula {
  private final String name;
  private final long label;
  private int hash;

  private Term negation;

  public BooleanVariable(@NonNull String name, long label) {
    this.name = name;
    this.label = label;

    hash = 0;
  }

  @Override
  public Operator operator() {
    return BooleanOperator.BOOLEAN_VAR;
  }

  @Override
  public Term negation() {
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
  public Term input(int pos) {
    throw new UnsupportedOperationException("Zero-arity boolean var gate has no inputs");
  }

  @NonNull
  @Override
  public Iterator<Term> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BooleanVariable terms = (BooleanVariable) o;

    if (label != terms.label) return false;
    return name.equals(terms.name);
  }

  @Override
  public <T> T accept(SolverVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public int hashCode() {
    if (hash == 0) {
      hash = name.hashCode();
      hash = 31 * hash + (int) (label ^ (label >>> 32));
    }

    return hash;
  }
}
