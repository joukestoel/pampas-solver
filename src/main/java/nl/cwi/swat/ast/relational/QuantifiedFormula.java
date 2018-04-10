package nl.cwi.swat.ast.relational;

import java.util.List;
import java.util.Objects;

public abstract class QuantifiedFormula extends Formula {
  private final List<Declaration> decls;
  private final Formula formula;

  public QuantifiedFormula(List<Declaration> decls, Formula formula) {
    this.decls = decls;
    this.formula = formula;
  }

  public List<Declaration> getDecls() {
    return decls;
  }

  public Formula getFormula() {
    return formula;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    QuantifiedFormula that = (QuantifiedFormula) o;
    return Objects.equals(decls, that.decls) &&
            Objects.equals(formula, that.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(decls, formula);
  }
}
