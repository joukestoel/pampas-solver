package nl.cwi.swat.translation.data.row;

import org.jetbrains.annotations.NotNull;

public class RowAndConstraint {
  private final Tuple tuple;
  private final Constraint constraint;

  public RowAndConstraint(@NotNull Tuple tuple, @NotNull Constraint constraint) {
    this.tuple = tuple;
    this.constraint = constraint;
  }

  public Tuple getTuple() {
    return tuple;
  }

  public Constraint getConstraint() {
    return constraint;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RowAndConstraint that = (RowAndConstraint) o;

    if (!tuple.equals(that.tuple)) return false;
    return constraint.equals(that.constraint);
  }

  @Override
  public int hashCode() {
    int result = tuple.hashCode();
    result = 31 * result + constraint.hashCode();
    return result;
  }
}
