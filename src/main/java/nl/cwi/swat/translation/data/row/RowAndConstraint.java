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
}
