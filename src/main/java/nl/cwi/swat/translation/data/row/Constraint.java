package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;

public class Constraint {
  private final Formula exists;
  private final Formula attributeConstraints;

  Constraint(Formula exists, Formula attributeConstraints) {
    this.exists = exists;
    this.attributeConstraints = attributeConstraints;
  }

  public Formula exists() {
    return exists;
  }

  public Formula attributeConstraints() {
    return attributeConstraints;
  }

  public String toString() {
    return "[" + exists + "," + attributeConstraints + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Constraint that = (Constraint) o;

    if (!exists.equals(that.exists)) return false;
    return attributeConstraints.equals(that.attributeConstraints);
  }

  @Override
  public int hashCode() {
    int result = exists.hashCode();
    result = 31 * result + attributeConstraints.hashCode();
    return result;
  }
}


