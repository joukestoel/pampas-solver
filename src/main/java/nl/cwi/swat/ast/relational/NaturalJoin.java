package nl.cwi.swat.ast.relational;

import nl.cwi.swat.translation.TranslationVisitor;

public class NaturalJoin extends BinaryExpression {
  public NaturalJoin(Expression left, Expression right) {
    super(left, right);
  }

  @Override
  public <F, R, L> R accept(TranslationVisitor<F, R, L> visitor) {
    return visitor.visit(this);
  }
}
