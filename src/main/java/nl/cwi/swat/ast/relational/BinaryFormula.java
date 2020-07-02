package nl.cwi.swat.ast.relational;

import java.util.Objects;

public abstract class BinaryFormula extends Formula {
  private final Formula left;
  private final Formula right;

  public BinaryFormula(Formula left, Formula right) {
    this.left = left;
    this.right = right;
  }

  public Formula getLeft() {
    return left;
  }

  public Formula getRight() {
    return right;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BinaryFormula that = (BinaryFormula) o;
    return Objects.equals(left, that.left) &&
            Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }
}
