package nl.cwi.swat.smtlogic;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

public abstract class MultiGate implements Formula {
  protected final Operator.Nary operator;

  private final long label;

  protected final int labelHash;

  private Formula negation;

  public MultiGate(@NotNull Operator.Nary operator, long label) {
    if (label < 0) {
      throw new IllegalArgumentException("Label should be positive");
    }

    this.operator = operator;
    this.label = label;

    this.labelHash = Long.valueOf(label).hashCode();
  }

  @Override
  public Operator operator() {
    return this.operator;
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
    return this.label;
  }

  @Override
  public abstract Iterator<Formula> iterator();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MultiGate formulas = (MultiGate) o;
    return label == formulas.label &&
            Objects.equals(operator, formulas.operator);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operator, label);
  }
}
