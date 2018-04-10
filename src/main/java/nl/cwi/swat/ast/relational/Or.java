package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

public class Or extends BinaryFormula {
  public Or(Formula left, Formula right) {
    super(left, right);
  }

  @Override
  public <F, R, L> F accept(TranslationVisitor<F, R, L> visitor) {
    return null;
  }
}
