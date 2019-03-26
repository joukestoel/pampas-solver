package nl.cwi.swat.formulacircuit.bool;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;

public class BooleanAccumulator implements Formula {
  private Map.Transient<Long, Term> inputs;

  private final BooleanOperator.Nary operator;

  private BooleanAccumulator(BooleanOperator.Nary operator) {
    this.operator = operator;

    inputs = PersistentTrieMap.transientOf();
  }

  public static BooleanAccumulator AND() {
    return new BooleanAccumulator(BooleanOperator.AND);
  }

  public static BooleanAccumulator OR() {
    return new BooleanAccumulator(BooleanOperator.OR);
  }


  public Term add(@NonNull Term f) {
    if (isShortCircuited()) {
      return operator.shortCircuit();
    } else {
      final long lit = f.label();

      if (f == operator.shortCircuit() || inputs.containsKey(-lit)) {
        inputs = PersistentTrieMap.transientOf();
        inputs.__put(operator.shortCircuit().label(), operator.shortCircuit());

        return operator.shortCircuit();
      }

      if (f != operator.identity() && !inputs.containsKey(lit)) {
        inputs.put(lit, f);
      }

      return this;
    }
  }

  public boolean isShortCircuited() {
    if (inputs.size() == 1) {
      for (Term t : inputs.values()) {
        return t == operator.shortCircuit();
      }
    }

    return false;
  }

  @Override
  public Operator operator() {
    return operator;
  }

  @Override
  public Formula negation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long label() {
    return 0;
  }

  @Override
  public int size() {
    return inputs.size();
  }

  @Override
  public Formula input(int pos) {
    throw new UnsupportedOperationException();
  }

  @NonNull
  @Override
  public Iterator<Term> iterator() {
    return inputs.valueIterator();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BooleanAccumulator formulas = (BooleanAccumulator) o;

    if (!inputs.equals(formulas.inputs)) return false;
    return operator.equals(formulas.operator);
  }

  @Override
  public int hashCode() {
    int result = inputs.hashCode();
    result = 31 * result + operator.hashCode();
    return result;
  }
}
