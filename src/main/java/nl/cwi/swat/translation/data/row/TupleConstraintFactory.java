package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.BooleanConstant;
import nl.cwi.swat.smtlogic.Formula;
import org.jetbrains.annotations.NotNull;

public class TupleConstraintFactory {
  public static final Constraint ALL_TRUE = new Constraint() {
    @Override
    public Formula exists() {
      return BooleanConstant.TRUE;
    }

    @Override
    public Formula attributeConstraints() {
      return BooleanConstant.TRUE;
    }

    @Override
    public Formula combined() {
      return BooleanConstant.TRUE;
    }
  };

  public static Constraint buildConstraint(@NotNull final Formula exists) {
    return new Constraint.ExistsOnlyConstraint(exists);
  }

  public static Constraint buildConstraint(@NotNull final Formula exists, @NotNull final Formula attributeConstraints) {
    return new Constraint.FullConstraint(exists, attributeConstraints);
  }
}
