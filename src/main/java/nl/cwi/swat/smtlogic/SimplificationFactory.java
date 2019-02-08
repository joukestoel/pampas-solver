package nl.cwi.swat.smtlogic;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieMap;
import io.usethesource.capsule.core.PersistentTrieSet;

import javax.inject.Singleton;
import java.util.Iterator;

@Singleton
public class SimplificationFactory {
  private int reductionDepth;
  private long label;

  private final Map.Transient<Sort, Set.Transient<String>> variables;

  private final Cache<FormulaCacheKey, Set.Transient<Formula>> formulaCache;

  private Set.Transient<Formula> scratch1 = PersistentTrieSet.transientOf();
  private Set.Transient<Formula> scratch2 = PersistentTrieSet.transientOf();

  public SimplificationFactory(int reductionDepth, Cache<FormulaCacheKey, Set.Transient<Formula>> formulaCache) {
    this.reductionDepth = reductionDepth;
    this.label = 0;
    this.formulaCache = formulaCache;

    variables = PersistentTrieMap.transientOf();
  }

  public Formula newBoolVar(String relName) {
    label++;
    String varName = relName + "_" + label;
    registerVariable(BooleanSort.BOOLEAN, varName);
    return new BooleanVariable(varName, label);
  }

  public Expression newVar(Sort sort, String relName) {
    label++;
    String varName = relName + "_" + label;
    registerVariable(sort, varName);
    return sort.newVar(varName, label);
  }

  private void registerVariable(Sort sort, String varName) {
    Set.Transient<String> varsOfSort = variables.containsKey(sort) ? variables.get(sort) : PersistentTrieSet.transientOf();

    varsOfSort.__insert(varName);
    variables.__put(sort, varsOfSort);
  }

  public Formula accumulate(FormulaAccumulator accumulator) {
    return reduce(accumulator);
  }

  public Formula and(Formula f1, Formula f2) {
    return reduce(Operator.AND, f1, f2);
  }

  public Formula or(Formula f1, Formula f2) {
    return reduce(Operator.OR, f1, f2);
  }


  public Formula reduce(FormulaAccumulator accumulator) {
    Operator.Nary op = accumulator.getOperator();
    Iterator<Formula> it = accumulator.iterator();

    switch (accumulator.size()) {
      case 0:
        return op.identity();
      case 1:
        return it.next();
      case 2:
        return reduce(op, it.next(), it.next());
      default: {
        FormulaCacheKey key = new FormulaCacheKey(accumulator.operator(), accumulator);

        //if (accumulator.size() > reductionDepth)
        //TODO; should check if there is a match in the cache
        return new NaryGate(accumulator, label++);
      }
    }
  }

  public Formula reduce(Operator.Nary operator, Formula left, Formula right) {
    final Formula low, high;

    if (left.operator().ordinal < right.operator().ordinal) {
      low = left;
      high = right;
    } else {
      low = right;
      high = left;
    }

    if (Operator.CONST == high.operator()) {
      return high == operator.identity() ? low : high;
    }

    return assembler(low.operator(), high.operator()).assemble(operator, low, high);
  }

  private Formula cache(Operator.Nary operator, Formula f0, Formula f1) {
    final Formula low, high;
    if (f0.label() < f1.label()) {
      low = f0;
      high = f1;
    } else {
      low = f1;
      high = f0;
    }

    FormulaCacheKey key = new FormulaCacheKey(operator, low, high);

    Set.Transient<Formula> cached = formulaCache.getIfPresent(key);
    if (cached != null) {
      if (low.operator() == operator || high.operator() == operator) {
        scratch1 = PersistentTrieSet.transientOf();

        low.flatten(operator, scratch1, reductionDepth - 1);
        high.flatten(operator, scratch1, reductionDepth - 1);

        for (Formula gate : cached) {
          if (gate.size() == 2 && gate.input(0) == low && gate.input(1) == high) {
            return gate;
          }

          scratch2 = PersistentTrieSet.transientOf();
          gate.flatten(operator, scratch2, reductionDepth);
          if (scratch1.equals(scratch2)) {
            return gate;
          }
        }
      } else {
        for (Formula gate : cached) {
          if (gate.size() == 2 && gate.input(0) == low && gate.input(1) == high) {
            return gate;
          }
        }
      }
      // nothing found, update cache
    } else {
      cached = PersistentTrieSet.transientOf();
    }

    final BinaryGate result = new BinaryGate(operator, label++, low, high);
    cached.__insert(result);

    formulaCache.put(key, cached);

    return result;
  }

  public static class FormulaCacheKey {
    final Operator operator;
    final int hashOfSubformulas;

    FormulaCacheKey(Operator operator, Formula f0, Formula f1) {
      this.operator = operator;
      this.hashOfSubformulas = f0.hash(operator) + f1.hash(operator);
    }

    FormulaCacheKey(Operator operator, Iterable<Formula> formulas) {
      this.operator = operator;

      int sum = 0;
      for (Formula f : formulas) {
        sum += f.hash(operator);
      }

      this.hashOfSubformulas = sum;
    }
  }

  public Assembler assembler(Operator low, Operator high) {
//    return ASSEMBLERS[((low.ordinal << 2) + high.ordinal) - ((low.ordinal * (low.ordinal - 1)) >> 1)];
    return ASSEMBLERS[((low.ordinal * 4) + high.ordinal) - ((low.ordinal * (low.ordinal - 1)) / 2)];
  }

  private interface Assembler {
    Formula assemble(Operator.Nary operator, Formula left, Formula right);
  }

  private static void checkOrder(Formula low, Formula high) {
    if (low.label() > high.label()) {
      throw new IllegalArgumentException("Low is higher then high. Total order is corrupted");
    }
  }

  /**
   * Performs common simplifications on circuits of the form AND op X or OR op X,
   * where X can be any operator other than CONST (J stands for 'junction').
   */
  private final Assembler JoX = (operator, left, right) -> {
    if (left.operator().ordinal > 1) {
      throw new IllegalArgumentException("JoX can only be applied to AND or OR");
    }

    final long label = right.label();
    if (left.contains(left.operator(), label, reductionDepth)) {
      return operator == left.operator() ? left : right;
    } else if (operator == left.operator() && left.contains(operator, -label, reductionDepth)) {
      return operator.shortCircuit();
    } else {
      return cache(operator, left, right);
    }
  };

  /**
   * Performs common simplifications on circuits of the form AND op OR.
   */
  private final Assembler AoO = (operator, left, right) -> {
    /**
     * Performs the following reductions, if possible, along with JoX reductions.
     * (aj & ... & ak) & (a1 | ... | an) = (aj & ... & ak) where 1 <= j <= k <= n
     * (a1 & ... & an) | (aj | ... | ak) = (aj | ... | ak) where 1 <= j <= k <= n
     * @requires f0.op = AND && f1.op = OR
     */
    if (left.operator() != Operator.AND || right.operator() != Operator.OR) {
      throw new IllegalArgumentException("AoR can only be applied if the first operator is AND and the second is OR");
    }

    scratch1 = PersistentTrieSet.transientOf();
    scratch2 = PersistentTrieSet.transientOf();

    left.flatten(left.operator(), scratch1, reductionDepth);
    right.flatten(right.operator(), scratch2, reductionDepth);

    for (Formula f : scratch2) {
      if (scratch1.contains(f)) {
        return operator == Operator.AND ? left : right;
      }
    }

    return left.label() < right.label() ? JoX.assemble(operator, right, left) : JoX.assemble(operator, left, right);
  };

  /**
   * Performs common simplifications on circuits of the form AND op AND or OR op OR.
   */
  private final Assembler JoJ = (operator, left, right) -> {
    /**
     * Performs the following reductions, if possible, along with the JoX reductions.
     * (a1 & ... & an) & (aj & ... & ak) = (a1 & ... & an) where 1 <= j <= k <= n
     * (a1 & ... & an) | (aj & ... & ak) = (aj & ... & ak) where 1 <= j <= k <= n
     * (a1 | ... | an) | (aj | ... | ak) = (a1 | ... | an) where 1 <= j <= k <= n
     * (a1 | ... | an) & (aj | ... | ak) = (aj | ... | ak) where 1 <= j <= k <= n
     * @requires f0.op = f1.op && (f0+f1).op in (AND + OR)
     */
    if (left.operator() != right.operator()) {
      throw new IllegalArgumentException("JoJ can only be applies to two of the same operators");
    }

    if (left == right) {
      return left;
    }

    scratch1 = PersistentTrieSet.transientOf();
    scratch2 = PersistentTrieSet.transientOf();

    left.flatten(left.operator(), scratch1, reductionDepth);
    left.flatten(left.operator(), scratch2, reductionDepth);

    if (scratch1.size() < scratch2.size() && scratch2.containsAll(scratch1)) {
      return operator == left.operator() ? right : left;
    } else if (scratch1.size() >= scratch2.size() && scratch1.containsAll(scratch2)) {
      return operator == left.operator() ? left : right;
    } else if (left.label() < right.label()) {
      return JoX.assemble(operator, right, left);
    } else {
      return JoX.assemble(operator, left, right);
    }
  };

  /**
   * Performs common simplifications on circuits of the form AND op ITE or OR op ITE.
   */
  private final Assembler JoI = (operator, left, right) -> {
    /**
     * Combines JoX and IoX reductions.
     * @requires f0.op in (AND + OR) && f1.op = ITE
     */
    if (left.operator().ordinal > 1 || right.operator() != Operator.ITE) {
      throw new IllegalArgumentException("JoI can only be applied on AND/OR op ITE");
    }

    return left.label() < right.label() ? cache(operator, right, left) : JoX.assemble(operator, left, right);
  };

  /**
   * Performs common simplifications on circuits of the form NOT op X, where X can be any operator other than CONST.
   */
  private final Assembler NoX = (operator, left, right) -> {
    /**
     * Performs the following reductions, if possible.  Note that
     * these reductions will be possible only if f0 was created after f1 (i.e.  |f0.label| > |f1.label|).
     * !(a | b) & a = F	!(a | b) & !a = !(a | b)
     * !(a & b) | a = T	!(a & b) | !a = !(a & b)
     * @requires f0.op = NOT
     */
    if (left.operator() != Operator.NOT) {
      throw new IllegalArgumentException("NoX can only be applied to NOT");
    }

    if (left.input(0).contains(operator.complement(), right.label(), reductionDepth)) {
      return operator.shortCircuit();
    } else if (left.input(0).contains(operator.complement(), -right.label(), reductionDepth)) {
      return left;
    } else {
      return cache(operator, left, right);
    }
  };

  /**
   * Performs common simplifications on circuits of the form AND op NOT or OR op NOT.
   */
  private final Assembler JoN = (operator, left, right) -> {
    /**
     * Performs the following reductions, if possible, along with the JoX/NoX reductions.
     * a & !a = F	a | !a = T
     * @requires f0.op in (AND + OR) && f1.op = NOT
     */
    if (left.operator().ordinal > 1 || right.operator() != Operator.NOT) {
      throw new IllegalArgumentException("JoN can only be applied on AND/OR op NOT");
    }

    if (left.label() == -right.label()) {
      return operator.shortCircuit();
    } else if (left.label() < Math.abs(right.label())) {
      return NoX.assemble(operator, right, left);
    } else {
      return JoX.assemble(operator, left, right);
    }
  };

  /**
   * Performs common simplifications on circuits of the form ITE op VAR.
   */
  private final Assembler IoV = (operator, left, right) -> {
    /**
     * Returns cache(op, f0, f1)
     * @requires f0.op = ITE && f1.op = VAR
     */
    if (left.operator() != Operator.ITE || right.operator() != Operator.VAR) {
      throw new IllegalArgumentException("IoV can only be applied to an ITE and a VAR");
    }

    return cache(operator, left, right);
  };


  /**
   * Performs common simplifications on circuits of the form ITE op NOT.
   */
  private final Assembler IoN = (operator, left, right) -> {
    /**
     * Performs the following reductions, if possible, along with IoX/NoX reductions.
     * (a ? b : c) & !(a ? b : c) = F
     * (a ? b : c) | !(a ? b : c) = T
     * @requires f0.op = ITE && f1.op = NOT
     */
    if (left.operator() != Operator.ITE || right.operator() != Operator.NOT) {
      throw new IllegalArgumentException("IoN can only be applied to an ITE and a NOT");
    }

    if (left.label() == -right.label()) {
      return operator.shortCircuit();
    } else if (left.label() < Math.abs(right.label())) {
      return NoX.assemble(operator, right, left);
    } else {
      return cache(operator, left, right);
    }
  };


  /**
   * Performs common simplifications on circuits of the form NOT op NOT.
   */
  private final Assembler NoN = (operator, left, right) -> {
    /**
     * Performs the following reductions, if possible, along with NoX reductions.
     * !a & !a = !a		!a | !a = !a
     * @requires f1.op + f0.op = NOT
     */
    if (left.operator() != Operator.NOT || right.operator() != Operator.NOT) {
      throw new IllegalArgumentException("NoN can only be applied to two NOT formulas");
    }

    if (left == right) {
      return left;
    } else if (left.label() < right.label()) {
      return NoX.assemble(operator, left, right);
    } else {
      return NoX.assemble(operator, right, left);
    }
  };


  /**
   * Performs common simplifications on circuits of the form NOT op VAR.
   */
  private final Assembler NoV = (operator, left, right) -> {
    /**
     * Performs the following reductions, if possible, along with NoX reductions.
     * !a & a = F		!a | a = T
     * @requires f1.op = NOT && f1.op = VAR
     */
    if (left.operator() != Operator.NOT || right.operator() != Operator.VAR) {
      throw new IllegalArgumentException("NoV can only be applied to a NOT and a VAR formula");
    }

    if (left.label() == -right.label()) {
      return operator.shortCircuit();
    } else {
      return NoX.assemble(operator, left, right);
    }
  };


  /**
   * Performs common simplifications on circuits of the form X op X.
   */
  private final Assembler XoX = (operator, left, right) -> {
    /**
     * Performs the following reductions, if possible.
     * a & a = a	a | a = a
     * @requires f0.op = f1.op
     */
    if (left.operator() != right.operator()) {
      throw new IllegalArgumentException("XoX can only be applied to formulas with the same operator");
    }

    return (left == right) ? left : cache(operator, left, right);
  };


  private final Assembler[] ASSEMBLERS = {
          JoJ,    /* AND op AND */
          AoO,    /* AND op OR */
          JoI,    /* AND op ITE */
          JoN,    /* AND op NOT */
          JoX,    /* AND op VAR */
          JoJ,    /* OR op OR */
          JoI,    /* OR op ITE */
          JoN,    /* OR op NOT */
          JoX,    /* OR op VAR */
          XoX,    /* ITE op ITE */
          IoN,    /* ITE op NOT */
          IoV,    /* ITE op VAR */
          NoN,    /* NOT op NOT */
          NoV,    /* NOT op VAR */
          XoX      /* VAR op VAR */
  };
}


