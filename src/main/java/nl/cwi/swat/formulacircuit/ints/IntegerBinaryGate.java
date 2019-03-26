package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.BinaryGate;
import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IntegerBinaryGate extends BinaryGate implements Expression {
  public IntegerBinaryGate(@NonNull Operator operator, @NonNull Term e1, @NonNull Term e2, long label) {
    super(e1, e2, operator, label);
  }

  @Override
  public Term negation() {
    throw new UnsupportedOperationException("Can't negate binary integer gate");
  }
}
