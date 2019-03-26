package nl.cwi.swat.formulacircuit;

import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class Gate implements Term {
  private final long label;
  protected final Operator operator;

  public Gate(@NonNull Operator operator, long label) {
    this.operator = operator;
    this.label = label;
  }

  @Override
  public long label() {
    return label;
  }

  @Override
  public Operator operator() {
    return operator;
  }
}
