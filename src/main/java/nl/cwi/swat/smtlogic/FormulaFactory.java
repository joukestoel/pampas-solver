package nl.cwi.swat.smtlogic;

import java.util.HashMap;
import java.util.Map;

import static nl.cwi.swat.smtlogic.BooleanSort.BOOLEAN;

public class FormulaFactory {
  private final Map<Sort, String> variables;
  private int varNr;

  public FormulaFactory() {
    this.variables = new HashMap<>();
    varNr = 0;
  }

  public Formula and(Formula f1, Formula f2) {
    return NaryGate.and(f1,f2);
  }

  public Formula or(Formula f1, Formula f2) {
    return NaryGate.or(f1,f2);
  }

  private String newVarName(Sort sort, String prefix) {
    String varName = prefix + "_" + ++varNr;
    variables.put(sort, varName);

    return varName;
  }

  public Formula newBoolVar(String relName) {
    return new BooleanVariable(newVarName(BOOLEAN, relName));
  }

  public Expression newVar(Sort sort, String prefix) {
    return sort.newVar(newVarName(sort, prefix));
  }

}
