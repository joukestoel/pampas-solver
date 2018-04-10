package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

public class Product extends BinaryExpression {
  public Product(Expression left, Expression right) {
    super(left, right);
  }

  @Override
  public <F, R, L> R accept(TranslationVisitor<F, R, L> visitor) {
    return visitor.visit(this);
  }
}
