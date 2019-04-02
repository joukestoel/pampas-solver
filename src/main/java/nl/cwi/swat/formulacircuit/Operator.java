package nl.cwi.swat.formulacircuit;

public abstract class Operator implements Comparable<Operator> {
  private final int ordinal;

  public Operator(int ordinal) {
    this.ordinal = ordinal;
  }

  /**
   * Returns the ordinal of this operator constant.
   * @return the ordinal of this operator constant.
   */
  public final int ordinal() {
    return ordinal;
  }

  /**
   * Returns an integer i such that i < 0 if this.ordinal < op.ordinal,
   * i = 0 when this.ordinal = op.ordinal, and i > 0 when this.ordinal > op.ordinal.
   * @return i: int | this.ordinal < op.ordinal => i < 0,
   *         this.ordinal = op.ordinal => i = 0, i > 0
   * @throws NullPointerException  op = null
   */
  public int compareTo(Operator op) {
    return ordinal - op.ordinal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Operator operator = (Operator) o;

    return ordinal == operator.ordinal;
  }

  @Override
  public int hashCode() {
    return ordinal;
  }

  public <T> T accept(SolverVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
