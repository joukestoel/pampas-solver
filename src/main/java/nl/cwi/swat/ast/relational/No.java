package nl.cwi.swat.ast.relational;

import nl.cwi.swat.translation.TranslationVisitor;

public class No extends CardinalityFormula {

  public No(Expression expr) {
    super(expr);
  }

  @Override
  public <F, R, L> F accept(TranslationVisitor<F, R, L> visitor) {
    return visitor.visit(this);
  }
}
