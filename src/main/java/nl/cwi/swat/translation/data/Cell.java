package nl.cwi.swat.translation.data;

import java.util.Objects;

public abstract class Cell<T> {
  private T value;

  public Cell(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cell<?> cell = (Cell<?>) o;
    return Objects.equals(value, cell.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
