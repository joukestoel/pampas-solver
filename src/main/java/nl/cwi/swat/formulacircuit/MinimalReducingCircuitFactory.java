package nl.cwi.swat.formulacircuit;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieMap;
import io.usethesource.capsule.core.PersistentTrieSet;
import nl.cwi.swat.formulacircuit.bool.*;
import nl.cwi.swat.formulacircuit.ints.*;
import nl.cwi.swat.formulacircuit.rel.IdConstant;
import nl.cwi.swat.translation.data.row.Constraint;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class MinimalReducingCircuitFactory implements FormulaFactory {
  private long label;
  private long intLabel;
  private long boolLabel;

  private final Set.Transient<Term> variables;

  private final Map.Transient<Integer, Expression> intConstants;
  private final Map.Transient<String, Expression> idConstants;

  public MinimalReducingCircuitFactory() {
    this.label = 0;
    this.intLabel = 0;
    this.boolLabel = 0;

    this.variables = PersistentTrieSet.transientOf();
    this.intConstants = PersistentTrieMap.transientOf();
    this.idConstants = PersistentTrieMap.transientOf();
  }

  @Override
  public Formula and(@NonNull Formula f1, @NonNull Formula f2) {
    return assemble(BooleanOperator.AND, f1, f2);
  }

  @Override
  public Formula or(@NonNull Formula f1, @NonNull Formula f2) {
    return assemble(BooleanOperator.OR, f1, f2);
  }

  @NotNull
  private Formula assemble(BooleanOperator.Nary op, @NonNull Formula f1, @NonNull Formula f2) {
    Formula low, high;

    if (f1.operator().ordinal() < f2.operator().ordinal()) {
      low = f1;
      high = f2;
    } else {
      low = f2;
      high = f1;
    }

    if (high.operator() == BooleanOperator.BOOLEAN_CONST) {
      return high == op.identity() ? high : low;
    }

    return new BooleanBinaryGate(op, label++, low, high);
  }

  @Override
  public Formula not(@NonNull Formula f) {
    return f.negation();
  }

  @Override
  public Formula accumulateBools(@NonNull BooleanAccumulator acc) {
    return new BooleanNaryGate(acc, label++);
  }

  @Override
  public Formula boolVar(@NonNull String relName) {
    BooleanVariable newVar = new BooleanVariable(relName + "_" + boolLabel++, label++);
    variables.add(newVar);

    return newVar;
  }

  @Override
  public Formula equal(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return BooleanConstant.byVal(((IntegerConstant)e1).getVal() == ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryEquationGate(IntegerOperator.EQUAL, e1, e2, label++);
  }

  @Override
  public Formula gt(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return BooleanConstant.byVal(((IntegerConstant)e1).getVal() > ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryEquationGate(IntegerOperator.GT, e1, e2, label++);
  }

  @Override
  public Formula gte(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return BooleanConstant.byVal(((IntegerConstant)e1).getVal() >= ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryEquationGate(IntegerOperator.GTE, e1, e2, label++);

  }

  @Override
  public Formula lt(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return BooleanConstant.byVal(((IntegerConstant)e1).getVal() < ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryEquationGate(IntegerOperator.LT, e1, e2, label++);

  }

  @Override
  public Formula lte(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return BooleanConstant.byVal(((IntegerConstant)e1).getVal() <= ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryEquationGate(IntegerOperator.LTE, e1, e2, label++);

  }

  @Override
  public Expression add(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return new IntegerConstant(label++, ((IntegerConstant)e1).getVal() + ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryExpressionGate(IntegerOperator.ADD, e1, e2, label++);
  }

  @Override
  public Expression sub(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return new IntegerConstant(label++, ((IntegerConstant)e1).getVal() - ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryExpressionGate(IntegerOperator.SUB, e1, e2, label++);
  }

  @Override
  public Expression mul(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return new IntegerConstant(label++, ((IntegerConstant)e1).getVal() * ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryExpressionGate(IntegerOperator.MUL, e1, e2, label++);

  }

  @Override
  public Expression div(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return new IntegerConstant(label++, ((IntegerConstant)e1).getVal() / ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryExpressionGate(IntegerOperator.DIV, e1, e2, label++);

  }

  @Override
  public Expression mod(@NonNull Expression e1, @NonNull Expression e2) {
    if (e1.operator() == IntegerOperator.INT_CONST && e2.operator() == IntegerOperator.INT_CONST) {
      return new IntegerConstant(label++, ((IntegerConstant)e1).getVal() % ((IntegerConstant)e2).getVal());
    }

    return new IntBinaryExpressionGate(IntegerOperator.MOD, e1, e2, label++);
  }

  @Override
  public Expression accumulateExprs(@NonNull IntegerAccumulator acc) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @NonNull
  @Override
  public Expression intVar(@NonNull String relName) {
    IntegerVariable newVar = new IntegerVariable(relName + "_" + intLabel++, label++);
    variables.add(newVar);

    return newVar;
  }

  @NonNull
  @Override
  public Expression intConst(int val) {
    if (intConstants.containsKey(val)) {
      return intConstants.get(val);
    }

    IntegerConstant c = new IntegerConstant(label++, val);
    intConstants.put(val, c);

    return c;
  }

  @Override
  public Expression idConst(@NonNull String atom) {
    if (idConstants.containsKey(atom)) {
      return idConstants.get(atom);
    }

    IdConstant c = new IdConstant(label++, atom);
    idConstants.put(atom, c);

    return c;
  }

  @Override
  public Formula combine(Constraint cons) {
    return assemble(BooleanOperator.AND, cons.exists(), cons.attributeConstraints());
  }
}
