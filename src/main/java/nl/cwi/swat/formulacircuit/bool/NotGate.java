package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Gate;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NotGate extends Gate implements Formula {
  private final Term input;

  public NotGate(@NonNull Term input) {
    super(BooleanOperator.NOT, -input.label());
    this.input = input;
  }

  @Override
  public Operator operator() {
    return BooleanOperator.NOT;
  }

  @Override
  public Term negation() {
    return input;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Term input(int pos) {
    if (pos != 0) {
      throw new IllegalArgumentException("NotGate only has 1 input");
    }

    return input;
  }

  @Override
  public String toString() {
    return "!" + input;
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
  public Iterator<Term> iterator() {
    return new Iterator<>() {
      private boolean next = true;

      @Override
      public boolean hasNext() {
        return next;
      }

      @Override
      public Term next() {
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
