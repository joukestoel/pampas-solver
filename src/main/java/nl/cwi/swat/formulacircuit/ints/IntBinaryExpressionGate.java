package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.BinaryGate;
import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Operator;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IntBinaryExpressionGate extends BinaryGate<Expression> implements Expression {

  public IntBinaryExpressionGate(@NonNull Operator operator, @NonNull Expression low, @NonNull Expression high, long label) {
    super(low, high, operator, label);
  }
}
