package nl.cwi.swat.translation.data;

import nl.cwi.swat.smtlogic.Formula;

import java.util.Objects;

public class RowAndConstraint {
  private final Row row;
  private final Formula constraint;

  public RowAndConstraint(Row row, Formula constraint) {
    this.row = row;
    this.constraint = constraint;
  }

  public Row getRow() {
    return row;
  }

  public Formula getConstraint() {
    return constraint;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowAndConstraint that = (RowAndConstraint) o;
    return Objects.equals(row, that.row) &&
            Objects.equals(constraint, that.constraint);
  }

  @Override
  public int hashCode() {

    return Objects.hash(row, constraint);
  }
}
