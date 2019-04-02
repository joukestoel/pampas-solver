package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.*;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BooleanBinaryGate extends BinaryGate implements Formula {
  private Formula negation;

  public BooleanBinaryGate(@NonNull Operator operator, long label, @NonNull Term low, @NonNull Term high) {
    super(low, high, operator, label);
  }

  @Override
  public Formula negation() {
    if (negation == null) {
      negation = new NotGate(this);
    }

    return negation;
  }

  @Override
  public <T> T accept(SolverVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
