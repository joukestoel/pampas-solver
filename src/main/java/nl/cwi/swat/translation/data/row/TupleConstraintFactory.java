package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.formulacircuit.Term;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import nl.cwi.swat.formulacircuit.Formula;
import org.checkerframework.checker.nullness.qual.NonNull;

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
  };

  public static Constraint buildConstraint(@NonNull final Formula exists) {
    return new Constraint.ExistsOnlyConstraint(exists);
  }

  public static Constraint buildConstraint(@NonNull final Formula exists, @NonNull final Formula attributeConstraints) {
    return new Constraint.FullConstraint(exists, attributeConstraints);
  }
}
