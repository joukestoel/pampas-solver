package nl.cwi.swat.translation.data.row;

import org.jetbrains.annotations.NotNull;

public class RowAndConstraint {
  private final Row row;
  private final RowConstraint constraint;

  public RowAndConstraint(@NotNull Row row, @NotNull RowConstraint constraint) {
    this.row = row;
    this.constraint = constraint;
  }

  public Row getRow() {
    return row;
  }

  public RowConstraint getConstraint() {
    return constraint;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RowAndConstraint that = (RowAndConstraint) o;

    if (!row.equals(that.row)) return false;
    return constraint.equals(that.constraint);
  }

  @Override
  public int hashCode() {
    int result = row.hashCode();
    result = 31 * result + constraint.hashCode();
    return result;
  }
}
