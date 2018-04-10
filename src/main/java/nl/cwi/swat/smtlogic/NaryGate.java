package nl.cwi.swat.smtlogic;

import io.usethesource.capsule.Set;

import java.util.Arrays;
import java.util.Iterator;

public class NaryGate extends MultiGate implements Formula {
  private final Formula[] inputs;
  private final int hash;

  public NaryGate(FormulaAccumulator accumulator, long label) {
    super(accumulator.operator, label);
    this.inputs = new Formula[accumulator.size()];

    int i = 0;
    int sum = 0;
    Iterator<Formula> iterator = accumulator.iterator();
    while (iterator.hasNext()) {
      Formula next = iterator.next();

      this.inputs[i] = next;
      sum += next.hash(operator);

      i++;
    }

    this.hash = sum;
  }

  @Override
  public Iterator<Formula> iterator() {
    return new Iterator<Formula>() {
      private int index = 0;
      private int size = inputs.length;

      @Override
      public boolean hasNext() {
        return index < size;
      }

      @Override
      public Formula next() {
        return inputs[index++];
      }
    };
  }

  @Override
  public int hash(Operator operator) {
    return operator == this.operator ? hash : labelHash;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Formula input(int pos) {
    return null;
  }

  @Override
  public boolean contains(Operator op, long f, int k) {
    if (f == label()) {
      return true;
    } else if (this.operator != op || f > label() || -f > label()) {
      return false;
    } else {
      int low = 0;
      int high = inputs.length-1;
      int step = 1;

      while (low <= high && step <= k) {
        int mid = (low + high) / 2;
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
  public void flatten(Operator op, Set.Transient<Formula> flat, int k) {
    if (this.operator == op && k >= inputs.length) {
      for (Formula f : inputs) {
        f.flatten(op, flat, k-1);
      }
    } else {
      flat.add(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    NaryGate formulas = (NaryGate) o;
    return Arrays.equals(inputs, formulas.inputs);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode(inputs);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < inputs.length-1; i++) {
      builder.append(String.format("%s %s", inputs[i], operator.toString()));
    }
    builder.append(inputs[inputs.length-1].toString());

    return builder.toString();
  }
}
