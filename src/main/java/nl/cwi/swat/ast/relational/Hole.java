package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

public class Hole extends Literal<String> {
  public static final Hole HOLE = new Hole();

  Hole() {
    super("?");
  }

  @Override
  public <F,R,L> L accept(TranslationVisitor<F,R,L> visitor) {
    return visitor.visit(this);
  }
}
