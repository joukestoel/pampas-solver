package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

public class Some extends CardinalityConstraint {
  public Some(Expression expr) {
    super(expr);
  }

  @Override
  public <F, R, L> F accept(TranslationVisitor<F, R, L> visitor) {
    return visitor.visit(this);
  }
}