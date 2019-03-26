package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Gate;
import nl.cwi.swat.formulacircuit.Term;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NegGate extends Gate implements Expression {
  private final Term input;

  public NegGate(long label, @NonNull Term input) {
    super(IntegerOperator.NEG, label);
    this.input = input;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Term input(int pos) {
    if (pos != 0) {
      throw new IllegalArgumentException("NegGate only has 1 input");
    }

    return input;
  }

  @Override
  public Term negation() {
    throw new UnsupportedOperationException();
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NegGate terms = (NegGate) o;

    return input.equals(terms.input);
  }

  @Override
  public int hashCode() {
    return input.hashCode();
  }
}
