package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.NaryGate;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IntNaryGate extends NaryGate<Expression> {
  public IntNaryGate(@NonNull IntegerAccumulator acc, long label) {
    super(convert(acc), acc.operator(), label);
  }

  private static Expression[] convert(@NonNull IntegerAccumulator acc) {
    final Expression[] inputs = new Expression[acc.size()];

    int i = 0;
    for (Expression e : acc) {
      inputs[i++] = e;
    }

    return inputs;
  }

}
