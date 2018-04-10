package nl.cwi.swat.ast.ints;

import nl.cwi.swat.ast.relational.Literal;
import nl.cwi.swat.ast.TranslationVisitor;

public class IntLiteral extends Literal<Integer> {
  public IntLiteral(Integer value) {
    super(value);
  }

  @Override
  public <F, R, L> L accept(TranslationVisitor<F, R, L> visitor) {
//    return visitor.visit(this);
    return null;
  }
}
