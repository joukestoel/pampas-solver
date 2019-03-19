package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.BinaryGate;
import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.bool.NotGate;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IntBinaryEquationGate extends BinaryGate<Expression> implements Formula<Expression> {
  private Formula negation;

  public IntBinaryEquationGate(@NonNull Operator operator, @NonNull Expression e1, @NonNull Expression e2, long label) {
    super(e1, e2, operator, label);
  }

  @Override
  public Formula negation() {
    if (negation == null) {
      negation = new NotGate(this);
    }

    return negation;
  }
}
