package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

public class Hole extends Expression {
  public static final Hole HOLE = new Hole();

  @Override
  public <F, R, L> R accept(TranslationVisitor<F, R, L> visitor) {
    return null;
  }
}
