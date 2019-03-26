package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;

public interface Constraint {
  Formula exists();
  Formula attributeConstraints();

  class FullConstraint implements Constraint {
    private final Formula exists;
    private final Formula attributeConstraints;

    FullConstraint(Formula exists, Formula attributeConstraints) {
      this.exists = exists;
      this.attributeConstraints = attributeConstraints;
    }

    @Override
    public Formula exists() {
      return exists;
    }

    @Override
    public Formula attributeConstraints() {
      return attributeConstraints;
    }

    @Override
    public String toString() {
      return "[" + exists + "," + attributeConstraints + "]";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      FullConstraint that = (FullConstraint) o;

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

  class ExistsOnlyConstraint implements Constraint {
    private final Formula exists;

    ExistsOnlyConstraint(Formula exists) {
      this.exists = exists;
    }

    @Override
    public Formula exists() {
      return exists;
    }

    @Override
    public Formula attributeConstraints() {
      return BooleanConstant.TRUE;
    }

    @Override
    public String toString() {
      return "[" + exists + "," + BooleanConstant.TRUE + "]";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ExistsOnlyConstraint that = (ExistsOnlyConstraint) o;

      return exists.equals(that.exists);
    }

    @Override
    public int hashCode() {
      return exists.hashCode();
    }
  }
}

