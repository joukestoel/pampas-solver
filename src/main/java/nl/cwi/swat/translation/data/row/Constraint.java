package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.BooleanConstant;
import nl.cwi.swat.smtlogic.Formula;

public interface Constraint {
  Formula exists();
  Formula attributeConstraints();
  Formula combined();

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
    public Formula combined() {
      return null;
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
    public Formula combined() {
      return exists;
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

