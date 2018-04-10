package nl.cwi.swat.smtlogic;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public class BooleanVariable implements Formula, Expression {
  private final long label;
  private final String name;

  private Formula negation;

  public BooleanVariable(@NotNull String name, long label) {
    if (label < 0) {
      throw new IllegalArgumentException("Label must be positive");
    }

    this.name = name;
    this.label = label;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BooleanVariable formulas = (BooleanVariable) o;
    return label == formulas.label &&
            Objects.equals(name, formulas.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, name);
  }

  @Override
  public Formula negation() {
    if (negation == null) {
      this.negation = new NotGate(this);
    }

    return negation;
  }

  @Override
  public long label() {
    return this.label;
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
    return Operator.VAR;
  }

  @Override
  public String toString() {
    return name;
  }
}
