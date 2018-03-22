package nl.cwi.swat.ast;

public abstract class Literal<T> {
  private final T value;

  public Literal(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }
}
