package nl.cwi.swat.smtlogic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FormulaFactory {
  private final SimplificationFactory sfactory;

  @Inject
  public FormulaFactory(SimplificationFactory sfactory) {
    this.sfactory = sfactory;
  }

  public Formula accumulate(FormulaAccumulator accumulator) {
    return sfactory.reduce(accumulator);
  }

  public Formula and(Formula f1, Formula f2) {
    return sfactory.reduce(Operator.AND, f1, f2);
  }

  public Formula or(Formula f1, Formula f2) {
    return sfactory.reduce(Operator.OR, f1, f2);
  }

  public Formula not(Formula f) {
    return f.negation();
  }

  public Formula newBoolVar(String relName) {
    return sfactory.newBoolVar(relName);
  }

  public Expression newVar(Sort sort, String relName) {
    return sfactory.newVar(sort, relName);
  }
}
