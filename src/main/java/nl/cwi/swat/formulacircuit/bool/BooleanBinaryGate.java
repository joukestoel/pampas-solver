package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.BinaryGate;
import nl.cwi.swat.formulacircuit.Operator;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BooleanBinaryGate extends BinaryGate<Formula> implements Formula<Formula> {
  private Formula negation;

  public BooleanBinaryGate(@NonNull Operator operator, long label, @NonNull Formula low, @NonNull Formula high) {
    super(low, high, operator, label);
  }

  @Override
  public Formula negation() {
    if (negation == null) {
      negation = new NotGate(this);
    }

    return negation;
  }


}
