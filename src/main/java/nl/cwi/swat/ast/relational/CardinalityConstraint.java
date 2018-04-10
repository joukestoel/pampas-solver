package nl.cwi.swat.ast.relational;

import java.util.Objects;

public abstract class CardinalityConstraint extends Formula {
  private final Expression expr;

  public CardinalityConstraint(Expression expr) {
    this.expr = expr;
  }

  public Expression getExpr() {
    return expr;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CardinalityConstraint that = (CardinalityConstraint) o;
    return Objects.equals(expr, that.expr);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expr);
  }
}
