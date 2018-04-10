package nl.cwi.swat.ast.relational;

import java.util.Objects;

public abstract class ComparisonFormula extends Formula {
  private final Expression left;
  private final Expression right;

  public ComparisonFormula(Expression left, Expression right) {
    this.left = left;
    this.right = right;
  }

  public Expression getLeft() {
    return left;
  }

  public Expression getRight() {
    return right;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ComparisonFormula that = (ComparisonFormula) o;
    return Objects.equals(left, that.left) &&
            Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {

    return Objects.hash(left, right);
  }
}
