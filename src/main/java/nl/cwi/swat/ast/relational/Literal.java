package nl.cwi.swat.ast.relational;

import nl.cwi.swat.translation.TranslationVisitor;

import java.util.Objects;

public abstract class Literal<T> extends Node {
  private final T value;

  public Literal(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  @Override
  public abstract <F, R, L> L accept(TranslationVisitor<F, R, L> visitor);

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

  @Override
  public String toString() {
    return value.toString();
  }
}
