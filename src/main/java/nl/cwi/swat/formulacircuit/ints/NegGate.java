package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Operator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NegGate implements Expression {
  private final long label;
  private final Expression input;

  public NegGate(@NonNull Expression input, long label) {
    this.input = input;
    this.label = label;
  }

  @Override
  public long label() {
    return label;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Expression input(int pos) {
    if (pos != 0) {
      throw new IllegalArgumentException("NegGate only has 1 input");
    }

    return input;
  }

  @Override
  public Operator operator() {
    return IntegerOperator.NEG;
  }

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<>() {
      private boolean next = true;

      @Override
      public boolean hasNext() {
        return next;
      }

      @Override
      public Expression next() {
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

    NegGate that = (NegGate) o;

    if (label != that.label) return false;
    return input.equals(that.input);
  }

  @Override
  public int hashCode() {
    int result = (int) (label ^ (label >>> 32));
    result = 31 * result + input.hashCode();
    return result;
  }
}
