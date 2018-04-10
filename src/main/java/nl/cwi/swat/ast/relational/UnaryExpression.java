package nl.cwi.swat.ast.relational;

import java.util.Objects;

public abstract class UnaryExpression extends Expression {
  private final Expression exp;

  public UnaryExpression(Expression exp) {
    this.exp = exp;
  }

  public Expression getExp() {
    return exp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UnaryExpression that = (UnaryExpression) o;
    return Objects.equals(exp, that.exp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(exp);
  }
}
