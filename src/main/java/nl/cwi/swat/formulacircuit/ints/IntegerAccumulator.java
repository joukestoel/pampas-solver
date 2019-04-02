package nl.cwi.swat.formulacircuit.ints;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieSet;
import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.SolverVisitor;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class IntegerAccumulator implements Expression {
  private final long label;

  private final IntegerOperator operator;
  private final Set.Transient<Term> inputs;

  public IntegerAccumulator(@NonNull IntegerOperator operator, long label) {
    this.label = label;
    this.operator = operator;

    inputs = PersistentTrieSet.transientOf();
  }

  public Term add(@NonNull Term e) {
    inputs.add(e);

    return this;
  }

  @Override
  public long label() {
    return label;
  }

  @Override
  public int size() {
    return inputs.size();
  }

  @Override
  public Term input(int pos) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Operator operator() {
    return operator;
  }

  @Override
  public Term negation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T accept(SolverVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    IntegerAccumulator that = (IntegerAccumulator) o;

    if (label != that.label) return false;
    if (!operator.equals(that.operator)) return false;
    return inputs.equals(that.inputs);
  }

  @Override
  public int hashCode() {
    int result = (int) (label ^ (label >>> 32));
    result = 31 * result + operator.hashCode();
    result = 31 * result + inputs.hashCode();
    return result;
  }

  @NotNull
  @Override
  public Iterator<Term> iterator() {
    return inputs.iterator();
  }
}
