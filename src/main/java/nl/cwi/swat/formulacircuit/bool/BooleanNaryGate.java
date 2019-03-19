package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.NaryGate;

public class BooleanNaryGate extends NaryGate<Formula> implements Formula<Formula> {
  private Formula negation;

  public BooleanNaryGate(BooleanAccumulator acc, long label) {
    super(convert(acc), acc.operator(), label);
  }

  private static Formula[] convert(BooleanAccumulator acc) {
    final Formula[] inputs = new Formula[acc.size()];

    int i = 0;
    for (Formula f : acc) {
      inputs[i++] = f;
    }

    return inputs;
  }

  @Override
  public Formula negation() {
    if (negation == null) {
      negation = new NotGate(this);
    }
    return negation;
  }

}
