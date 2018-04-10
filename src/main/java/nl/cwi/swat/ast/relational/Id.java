package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

public final class Id extends Literal<String> {
  public Id(String value) {
    super(value);
  }

  @Override
  public <F,R,L> L accept(TranslationVisitor<F,R,L> visitor) {
    return visitor.visit(this);
  }
}
