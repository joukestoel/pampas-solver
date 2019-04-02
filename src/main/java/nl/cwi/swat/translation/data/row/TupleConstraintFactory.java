package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TupleConstraintFactory {
  public static final Constraint ALL_TRUE = new Constraint(BooleanConstant.TRUE, BooleanConstant.TRUE);

  public static Constraint buildConstraint(@NonNull final Formula exists) {
    return new Constraint(exists, BooleanConstant.TRUE);
  }

  public static Constraint buildConstraint(@NonNull final Formula exists, @NonNull final Formula attributeConstraints) {
    return new Constraint(exists, attributeConstraints);
  }
}
