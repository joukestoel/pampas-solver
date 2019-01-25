package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.BooleanConstant;
import nl.cwi.swat.smtlogic.Formula;

public interface RowConstraint {
  Formula exists();
  Formula attributeConstraints();
  Formula combined();

  class FullRowConstraint implements RowConstraint {
    private final Formula exists;
    private final Formula attributeConstraints;

    FullRowConstraint(Formula exists, Formula attributeConstraints) {
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
  }

  class ExistsOnlyRowConstaint implements RowConstraint {
    private final Formula exists;

    ExistsOnlyRowConstaint(Formula exists) {
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
  }
}

