package nl.cwi.swat.smtlogic;

public class SimpleFormulaFactory implements FormulaFactory {
  private long label;

  public SimpleFormulaFactory() {
    this.label = 0;
  }

  @Override
  public Formula accumulate(FormulaAccumulator accumulator) {
    Formula result = accumulator.operator.identity();

    for (Formula f : accumulator) {
      result = new BinaryGate(accumulator.operator, label++, result, f);
    }

    return result;
  }

  @Override
  public Formula and(Formula f1, Formula f2) {
    Formula low;
    Formula high;

    if (f1.label() > f2.label()) {
      low = f2;
      high = f1;
    } else {
      low = f1;
      high = f2;
    }

    return new BinaryGate(Operator.AND, label++, low, high);
  }

  @Override
  public Formula or(Formula f1, Formula f2) {
    Formula low;
    Formula high;

    if (f1.label() > f2.label()) {
      low = f2;
      high = f1;
    } else {
      low = f1;
      high = f2;
    }

    return new BinaryGate(Operator.OR, label++, low, high);
  }

  @Override
  public Formula not(Formula f) {
    return f.negation();
  }

  @Override
  public Formula newBoolVar(String relName) {
    return null;
  }

  @Override
  public Expression newVar(Sort sort, String relName) {
    return null;
  }
}
