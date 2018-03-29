package nl.cwi.swat.ast;

import java.util.Objects;

public abstract class Literal<T> extends Expression {
  private final T value;

  public Literal(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Literal<?> literal = (Literal<?>) o;
    return Objects.equals(value, literal.value);
  }

  @Override
  public int hashCode() {

    return Objects.hash(value);
  }
}
