package nl.cwi.swat.smtlogic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ReductionFormulaFactory implements FormulaFactory {
  private final SimplificationFactory sfactory;

  @Inject
  public ReductionFormulaFactory(SimplificationFactory sfactory) {
    this.sfactory = sfactory;
  }

  @Override
  public Formula accumulate(FormulaAccumulator accumulator) {
    return sfactory.reduce(accumulator);
  }

  @Override
  public Formula and(Formula f1, Formula f2) {
    return sfactory.reduce(Operator.AND, f1, f2);
  }

  @Override
  public Formula or(Formula f1, Formula f2) {
    return sfactory.reduce(Operator.OR, f1, f2);
  }

  @Override
  public Formula not(Formula f) {
    return f.negation();
  }

  @Override
  public Formula newBoolVar(String relName) {
    return sfactory.newBoolVar(relName);
  }

  @Override
  public Expression newVar(Sort sort, String relName) {
    return sfactory.newVar(sort, relName);
  }
}
