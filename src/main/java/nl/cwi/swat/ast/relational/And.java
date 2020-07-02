package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

public final class And extends BinaryFormula {

  public And(Formula left, Formula right) {
    super(left, right);
  }

  @Override
  public <F, R, L> F accept(TranslationVisitor<F, R, L> visitor) {
    return visitor.visit(this);
  }
}
