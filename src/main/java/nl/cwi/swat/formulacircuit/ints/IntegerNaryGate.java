package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.NaryGate;
import nl.cwi.swat.formulacircuit.SolverVisitor;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IntegerNaryGate extends NaryGate implements Expression {
  public IntegerNaryGate(@NonNull IntegerAccumulator acc, long label) {
    super(convert(acc), acc.operator(), label);
  }

  private static Term[] convert(@NonNull IntegerAccumulator acc) {
    final Term[] inputs = new Term[acc.size()];

    int i = 0;
    for (Term e : acc) {
      inputs[i++] = e;
    }

    return inputs;
  }

  @Override
  public <T> T accept(SolverVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public Term negation() {
    throw new UnsupportedOperationException();
  }
}
