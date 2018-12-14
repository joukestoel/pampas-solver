package nl.cwi.swat.translation.data;

import nl.cwi.swat.smtlogic.Formula;
import nl.cwi.swat.smtlogic.FormulaFactory;

import java.util.Objects;

public class RowAndConstraint {
  private final FormulaFactory formulaFactory;

  private final Row row;
  private final Formula exists;
  private final Formula attributeConstraints;

  public RowAndConstraint(Row row, Formula exists, Formula attributeConstraints, FormulaFactory formulaFactory) {
    this.row = row;
    this.exists = exists;
    this.attributeConstraints = attributeConstraints;
    this.formulaFactory = formulaFactory;
  }

  public Row getRow() {
    return row;
  }

  public Formula getExists() {
    return exists;
  }

  public Formula getAttributeConstraints() { return attributeConstraints; }

  public Formula getCombinedConstraints() {
    return formulaFactory.and(exists, attributeConstraints);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowAndConstraint that = (RowAndConstraint) o;
    return Objects.equals(row, that.row) &&
            Objects.equals(exists, that.exists);
  }

  @Override
  public int hashCode() {

    return Objects.hash(row, exists);
  }
}
