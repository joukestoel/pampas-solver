package nl.cwi.swat.smtlogic;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class NotGate implements Formula {
  private final Formula formula;
  private final long label;

  public NotGate(@NotNull Formula formula) {
    this.formula = formula;
    this.label = -formula.label();
  }

  @Override
  public Operator operator() {
    return Operator.NOT;
  }

  @Override
  public Formula negation() {
    return formula;
  }

  @Override
  public long label() {
    return label;
  }

  @Override
  public int hash(Operator operator) {
    return hashCode();
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Formula input(int pos) {
    if (pos != 0) {
      throw new IndexOutOfBoundsException();
    }

    return negation();
  }

  @Override
  public Iterator<Formula> iterator() {
    return new Iterator<Formula>() {
      boolean hasNext = true;

      @Override
      public boolean hasNext() {
        return hasNext;
      }

      @Override
      public Formula next() {
        if (!hasNext) {
          throw new NoSuchElementException();
        }

        hasNext = false;
        return formula;
      }
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NotGate formulas = (NotGate) o;
    return label == formulas.label &&
            Objects.equals(formula, formulas.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula, label);
  }

  @Override
  public String toString() {
    return operator().toString() + formula.toString();
  }
}
