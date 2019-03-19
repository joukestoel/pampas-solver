package nl.cwi.swat.formulacircuit;

import io.usethesource.capsule.Set;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BinaryGate<T extends Term> extends Gate<T> {
  private final T low;
  private final T high;

  public BinaryGate(@NonNull T low, @NonNull T high, @NonNull Operator operator, long label) {
    super(operator, label);

    if (low.label() > high.label()) {
      throw new IllegalArgumentException("Label of the first formula (low) must be lower than the label of the second formula (high)");
    }

    this.low = low;
    this.high = high;
  }

  @NonNull
  @Override
  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int cur = 0;

      @Override
      public boolean hasNext() {
        return cur < 2;
      }

      @Override
      public T next() {
        cur += 1;

        if (cur == 1) {
          return low;
        } else if (cur == 2) {
          return high;
        } else {
          throw new NoSuchElementException();
        }
      }
    };
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public T input(int pos) {
    if (pos == 0) {
      return low;
    } else if (pos == 1) {
      return high;
    } else {
      throw new IllegalArgumentException("BinaryGate has 2 inputs");
    }
  }

  @Override
  public boolean contains(Operator op, long f, int k) {
    if (label() ==  f) {
      return true;
    } else if (this.operator != op || f > label() || -f > label()) {
      return false;
    } else if (low.contains(op, f, k-1)) {
      return true;
    } else {
      return high.contains(op, f, k-1);
    }
  }

  @Override
  public void flatten(Operator op, Set.Transient<Term> flat, int k) {
    if (op == operator && k > 1) {
      low.flatten(op, flat, k-1);
      high.flatten(op, flat, k-1);
    } else {
      flat.add(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BinaryGate<?> that = (BinaryGate<?>) o;

    if (!low.equals(that.low)) return false;
    return high.equals(that.high);
  }

  @Override
  public int hashCode() {
    int result = low.hashCode();
    result = 31 * result + high.hashCode();
    return result;
  }
}
