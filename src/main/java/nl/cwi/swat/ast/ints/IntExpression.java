package nl.cwi.swat.ast.ints;

import nl.cwi.swat.ast.relational.Expression;
import nl.cwi.swat.ast.TranslationVisitor;

public class IntExpression extends Expression {
  @Override
  public <F, R, L> R accept(TranslationVisitor<F, R, L> visitor) {
    return null;
  }
}
