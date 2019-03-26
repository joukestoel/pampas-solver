package nl.cwi.swat.formulacircuit;

import nl.cwi.swat.formulacircuit.bool.BooleanAccumulator;
import nl.cwi.swat.formulacircuit.ints.IntegerAccumulator;
import nl.cwi.swat.translation.data.row.Constraint;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface FormulaFactory {
  Formula and(@NonNull Formula f1, @NonNull Formula f2);
  Formula or(@NonNull Formula f1, @NonNull Formula f2);
  Formula not(@NonNull Formula f);

  Formula accumulateBools(@NonNull BooleanAccumulator acc);
  Formula boolVar(@NonNull String relName);

  Formula equal(@NonNull Expression e1, @NonNull Expression e2);
  Formula gt(@NonNull Expression e1, @NonNull Expression e2);
  Formula gte(@NonNull Expression e1, @NonNull Expression e2);
  Formula lt(@NonNull Expression e1, @NonNull Expression e2);
  Formula lte(@NonNull Expression e1, @NonNull Expression e2);

  Expression ite(@NonNull Formula i, @NonNull Expression t, @NonNull Expression e);
  Expression add(@NonNull Expression e1, @NonNull Expression e2);
  Expression sub(@NonNull Expression e1, @NonNull Expression e2);
  Expression mul(@NonNull Expression e1, @NonNull Expression e2);
  Expression div(@NonNull Expression e1, @NonNull Expression e2);
  Expression mod(@NonNull Expression e1, @NonNull Expression e2);

  Expression accumulateExprs(@NonNull IntegerAccumulator acc);

  Expression intVar(@NonNull String relName);
  Expression intConst(int val);

  Expression idConst(@NonNull String atom);

  Formula combine(Constraint cons);
}
