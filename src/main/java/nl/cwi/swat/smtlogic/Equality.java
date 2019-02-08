package nl.cwi.swat.smtlogic;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class Equality implements Formula {
  private final Operator operator;
  private final long label;

  private final Expression low;
  private final Expression high;

  private final int hash;

  private Formula negation;

  public Equality(@NotNull Operator operator, long label, Expression low, Expression high) {
    this.operator = operator;
    this.label = label;

    this.low = low;
    this.high = high;

    hash = low.hash(operator) + high.hash(operator);
  }

  @Override
  public Operator operator() {
    return Operator.EQ;
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
    return 0;
  }

  @Override
  public int hash(Operator operator) {
    return 0;
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public Formula input(int pos) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<Formula> iterator() {
    throw new UnsupportedOperationException();
  }
}
