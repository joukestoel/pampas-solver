package nl.cwi.swat.smtlogic;

public interface FormulaFactory {
  Formula accumulate(FormulaAccumulator accumulator);

  Formula and(Formula f1, Formula f2);

  Formula or(Formula f1, Formula f2);

  Formula not(Formula f);

  Formula newBoolVar(String relName);

  Expression newVar(Sort sort, String relName);
}
