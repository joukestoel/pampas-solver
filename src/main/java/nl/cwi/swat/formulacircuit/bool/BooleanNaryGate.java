package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Term;
import nl.cwi.swat.formulacircuit.NaryGate;

public class BooleanNaryGate extends NaryGate implements Formula {
  private Term negation;

  public BooleanNaryGate(BooleanAccumulator acc, long label) {
    super(convert(acc), acc.operator(), label);
  }

  private static Term[] convert(BooleanAccumulator acc) {
    final Term[] inputs = new Term[acc.size()];

    int i = 0;
    for (Term f : acc) {
      inputs[i++] = f;
    }

    return inputs;
  }

  @Override
  public Term negation() {
    if (negation == null) {
      negation = new NotGate(this);
    }
    return negation;
  }
}
