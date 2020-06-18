package nl.cwi.swat.translation.data.row;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Row {
  private final Tuple tuple;
  private final Constraint constraint;

  public Row(@NonNull Tuple tuple, @NonNull Constraint constraint) {
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

    Row that = (Row) o;

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
