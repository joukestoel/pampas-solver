package nl.cwi.swat.smtlogic;

import io.usethesource.capsule.Set;
import nl.cwi.swat.util.XXHashMixer;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class BinaryGate extends MultiGate {
  private final Formula low,high;
  private final int hash;

  public BinaryGate(@NotNull Operator.Nary operator, long label, Formula low, Formula high) {
    super(operator, label);

    if (low.label() > high.label()) {
      throw new IllegalArgumentException("The label of the first Formula (low) must be lower then the formula of the second Formula (high) to maintain the total ordering");
    }

    this.low = low;
    this.high = high;

    this.hash = low.hash(operator) + high.hash(operator);
  }

  @Override
  public Iterator<Formula> iterator() {
    return new Iterator<Formula>() {
      int i = 0;

      @Override
      public boolean hasNext() {
        return i < 2;
      }

      @Override
      public Formula next() {
        i++;

        switch (i) {
          case 1: return low;
          case 2: return high;
          default: throw new NoSuchElementException();
        }
      }
    };
  }

  @Override
  public int hash(Operator operator) {
    return this.operator == operator ? hash : labelHash;
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public Formula input(int pos) {
    switch (pos) {
      case 0: return low;
      case 1: return high;
      default: {
        throw new NoSuchElementException();
      }
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
  public void flatten(Operator op, Set.Transient<Formula> flat, int k) {
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
    if (!super.equals(o)) return false;
    BinaryGate formulas = (BinaryGate) o;
    return Objects.equals(low, formulas.low) &&
            Objects.equals(high, formulas.high);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), low, high);
  }

  @Override
  public String toString() {
    return String.format("%s %s %s", low.toString(), operator.toString(), high.toString());
  }
}
