package nl.cwi.swat.formulacircuit;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieSet;
import nl.cwi.swat.formulacircuit.bool.BooleanAccumulator;
import nl.cwi.swat.formulacircuit.bool.BooleanBinaryGate;
import nl.cwi.swat.formulacircuit.bool.BooleanOperator;
import nl.cwi.swat.formulacircuit.bool.BooleanVariable;
import nl.cwi.swat.formulacircuit.ints.IntBinaryEquationGate;
import nl.cwi.swat.formulacircuit.ints.IntegerAccumulator;
import nl.cwi.swat.formulacircuit.ints.IntegerOperator;
import nl.cwi.swat.translation.data.row.Constraint;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CompactFormulaCircuitFactory implements FormulaFactory {
  private final Cache<GateKey, Formula> formulaCache;
  private final int reductionDepth;

  private final Set.Transient<Term> variables;
  private long label;

  private long intVar;
  private long boolVar;


  public CompactFormulaCircuitFactory(@NonNull Cache<GateKey, Formula> formulaCache, int reductionDepth) {
    this.formulaCache = formulaCache;
    this.reductionDepth = reductionDepth;

    variables = PersistentTrieSet.transientOf();

    label = 0;
    intVar = 0;
    boolVar = 0;
  }

  @Override
  public Formula and(@NonNull Formula f1, @NonNull Formula f2) {
    return assembleFormula(BooleanOperator.AND, f1, f2);
  }

  @Override
  public Formula or(@NonNull Formula f1, @NonNull Formula f2) {
    return assembleFormula(BooleanOperator.OR, f1, f2);
  }

  @Override
  public Formula not(@NonNull Formula f) {
    return f.negation();
  }

  @Override
  public Formula accumulateBools(@NonNull BooleanAccumulator acc) {
    return null;
  }

  @Override
  public Formula boolVar(@NonNull String relName) {
    BooleanVariable newVar = new BooleanVariable(relName + "_" + boolVar++, label++);
    variables.add(newVar);

    return newVar;
  }

  @Override
  public Formula equal(@NonNull Expression e1, @NonNull Expression e2) {
    return assembleEquation(IntegerOperator.EQUAL, e1, e2);
  }

  @Override
  public Formula gt(@NonNull Expression e1, @NonNull Expression e2) {
    return null;
  }

  @Override
  public Formula gte(@NonNull Expression e1, @NonNull Expression e2) {
    return null;
  }

  @Override
  public Formula lt(@NonNull Expression e1, @NonNull Expression e2) {
    return null;
  }

  @Override
  public Formula lte(@NonNull Expression e1, @NonNull Expression e2) {
    return null;
  }

  @Override
  public Expression add(@NonNull Expression e1, @NonNull Expression e2) {
    return null;
  }

  @Override
  public Expression sub(@NonNull Expression e1, @NonNull Expression e2) {
    return null;
  }

  @Override
  public Expression mul(@NonNull Expression e1, @NonNull Expression e2) {
    return null;
  }

  @Override
  public Expression div(@NonNull Expression e1, @NonNull Expression e2) {
    return null;
  }

  @Override
  public Expression mod(@NonNull Expression e1, @NonNull Expression e2) {
    return null;
  }

  @Override
  public Expression accumulateExprs(@NonNull IntegerAccumulator acc) {
    return null;
  }

  @Override
  public Expression intVar(String relName) {
    return null;
  }

  @Override
  public Expression intConst(int val) {
    return null;
  }

  @Override
  public Expression idConst(@NonNull String atom) {
    return null;
  }

  @Override
  public Formula combine(Constraint cons) {
    return assembleFormula(BooleanOperator.AND, cons.exists(), cons.attributeConstraints());
  }

  private Formula assembleFormula(@NonNull Operator op, @NonNull Formula t1, @NonNull Formula t2) {
    Formula low, high;

    if (t1.label() < t2.label()) {
      low = t1;
      high = t2;
    } else {
      low = t2;
      high = t1;
    }

    return cacheFormula(op, low, high);
  }

  private Formula assembleEquation(@NonNull IntegerOperator op, @NonNull Expression e1, @NonNull Expression e2) {
    Expression low, high;

    if (e1.label() < e2.label()) {
      low = e1;
      high = e2;
    } else {
      low = e1;
      high = e2;
    }

    return cacheEquation(op, e1, e2);
  }

  private Formula cacheFormula(@NonNull Operator op, @NonNull Formula low, @NonNull Formula high) {
    GateKey key = new GateKey(op, low, high);
    Formula cachedTerm = formulaCache.getIfPresent(key);

    if (cachedTerm == null) {
      BooleanBinaryGate gate = new BooleanBinaryGate(op, label, low, high);
      formulaCache.put(key, gate);

      return gate;
    } else {
      return cachedTerm;
    }
  }

  private Formula cacheEquation(IntegerOperator op, Expression low, Expression high) {
    GateKey key = new GateKey(op, low, high);
    Formula cachedTerm = formulaCache.getIfPresent(key);

    if (cachedTerm == null) {
      IntBinaryEquationGate gate = new IntBinaryEquationGate(op, low, high, label++);
      formulaCache.put(key, gate);

      return gate;
    } else {
      return cachedTerm;
    }
  }
//
//  public final Assembler<Formula> XoX = (op, t1, t2) -> {
//    if (t1.operator() != t2.operator()) {
//      throw new IllegalArgumentException("To perform an XoX reduction both operators need to be the same");
//    }
//
//    return t1.label() == t2.label() ? t1 : cache(op, t1, t2);
//  };

  public class GateKey {
    private final Operator op;
    private final Term low;
    private final Term high;

    private GateKey(@NonNull Operator op, @NonNull Term low, @NonNull Term high) {
      this.op = op;
      this.low = low;
      this.high = high;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      GateKey that = (GateKey) o;

      if (!op.equals(that.op)) return false;
      if (!low.equals(that.low)) return false;
      return high.equals(that.high);
    }

    @Override
    public int hashCode() {
      int result = op.hashCode();
      result = 31 * result + low.hashCode();
      result = 31 * result + high.hashCode();
      return result;
    }
  }
}
