package nl.cwi.swat.formulacircuit;

import io.usethesource.capsule.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public abstract class NaryGate<T extends Term> extends Gate<T> {
  private final T[] inputs;

  protected NaryGate(@NonNull T[] inputs, @NonNull Operator operator, long label) {
    super(operator, label);

    this.inputs = inputs;
  }

  @Override
  public int size() {
    return inputs.length;
  }

  @Override
  public T input(int pos) {
    if (pos < 0 || pos >= inputs.length) {
      throw new IllegalArgumentException(String.format("Boolean nary gate of size %d does not have formula at index %d", inputs.length, pos));
    }

    return inputs[pos];
  }

  @Override
  public boolean contains(Operator op, long f, int k) {
    if (f == label()) {
      return true;
    } else if (this.operator != op || f > label() || -f > label()) {
      return false;
    } else {
      int low = 0;
      int high = inputs.length - 1;
      int step = 1;

      while (low <= high && step <= k) {
        int mid = (low + high) >>> 1;
        long midVal = inputs[mid].label();

        if (midVal < f) {
          low = mid + 1;
        } else if (midVal > f) {
          high = mid - 1;
        } else {
          return true;
        }

        step++;
      }

      return false;
    }
  }

  @Override
  public void flatten(Operator op, Set.Transient<Term> flat, int k) {
    if (k <= 0) {
      throw new IllegalArgumentException("The supplied k is not positive: " + k);
    }

    if (this.operator == op && k >= inputs.length) {
      int diff = k - inputs.length;

      for(T f: inputs) {
        int oldsize = flat.size();
        f.flatten(op, flat, StrictMath.max(1, diff));
        diff -= (flat.size() - oldsize);
      }
    } else {
      flat.add(this);
    }
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int i = 0;

      @Override
      public boolean hasNext() {
        return i < inputs.length;
      }

      @Override
      public T next() {
        return inputs[i++];
      }
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NaryGate<?> naryGate = (NaryGate<?>) o;

    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(inputs, naryGate.inputs);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(inputs);
  }
}
