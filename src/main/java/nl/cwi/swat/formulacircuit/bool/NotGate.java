package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Gate;
import nl.cwi.swat.formulacircuit.Operator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NotGate extends Gate<Formula> implements Formula<Formula> {
  private final Formula input;

  public NotGate(@NonNull Formula input) {
    super(BooleanOperator.NOT, -input.label());
    this.input = input;
  }

  @Override
  public Operator operator() {
    return BooleanOperator.NOT;
  }

  @Override
  public Formula negation() {
    return input;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Formula input(int pos) {
    if (pos != 0) {
      throw new IllegalArgumentException("NotGate only has 1 input");
    }

    return input;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NotGate notGate = (NotGate) o;

    return input.equals(notGate.input);
  }

  @Override
  public int hashCode() {
    return input.hashCode();
  }

  @NonNull
  @Override
  public Iterator<Formula> iterator() {
    return new Iterator<Formula>() {
      private boolean next = true;

      @Override
      public boolean hasNext() {
        return next;
      }

      @Override
      public Formula next() {
        if (next) {
          next = false;
          return input;
        } else {
          throw new NoSuchElementException();
        }
      }
    };
  }
}
