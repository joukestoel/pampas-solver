package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

public class No extends CardinalityConstraint {

  public No(Expression expr) {
    super(expr);
  }

  @Override
  public <F, R, L> F accept(TranslationVisitor<F, R, L> visitor) {
    return visitor.visit(this);
  }
}
