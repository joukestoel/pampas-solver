package nl.cwi.swat.smtlogic;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieMap;
import io.usethesource.capsule.core.PersistentTrieSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class FormulaAccumulator implements Formula {
  final Operator.Nary operator;
//  private final Set.Transient<Formula> inputs;
  private Map.Transient<Long,Formula> inputs;

  private FormulaAccumulator(Operator.Nary operator) {
    this.operator = operator;

//    this.inputs = PersistentTrieSet.transientOf();
    this.inputs = PersistentTrieMap.transientOf();
  }

  public static FormulaAccumulator OR() {
    return new FormulaAccumulator(Operator.OR);
  }

  public static FormulaAccumulator AND() {
    return new FormulaAccumulator(Operator.AND);
  }

  public Formula add(Formula f) {
    if (isShortCircuited()) {
      return operator.shortCircuit();
    } else if (f.equals(operator.shortCircuit()) || inputs.containsKey(-f.label())) {
      inputs = PersistentTrieMap.transientOf(operator.shortCircuit().label(), operator.shortCircuit());
      return operator.shortCircuit();
    } else if (!f.equals(operator.identity()) && !inputs.containsKey(f.label())) {
      inputs.__put(f.label(), f);
    }

    return this;
  }

  public boolean isShortCircuited() {
    if (inputs.size() == 1) {
      return inputs.valueIterator().next() == operator.shortCircuit();
    }

    return false;
  }

  @Override
  public Iterator<Formula> iterator() {
    return inputs.valueIterator();
  }

  @Override
  public Operator operator() {
    return this.operator;
  }

  @Override
  public Formula negation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long label() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hash(Operator operator) {
    throw new UnsupportedOperationException();
  }

  public int size() {
    return inputs.size();
  }

  @Override
  public Formula input(int pos) {
    throw new UnsupportedOperationException();
  }

  public Operator.Nary getOperator() {
    return operator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FormulaAccumulator formulas = (FormulaAccumulator) o;
    return Objects.equals(operator, formulas.operator) &&
            Objects.equals(inputs, formulas.inputs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operator, inputs);
  }
}
