package nl.cwi.swat.ast.relational;

import nl.cwi.swat.translation.TranslationVisitor;

public class Subset extends ComparisonFormula {

  public Subset(Expression left, Expression right) {
    super(left, right);
  }

  @Override
  public <F, R, L> F accept(TranslationVisitor<F, R, L> visitor) {
    return visitor.visit(this);
  }
}
