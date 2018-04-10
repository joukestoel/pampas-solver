package nl.cwi.swat.translation.data;

import nl.cwi.swat.ast.relational.Id;
import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.Literal;

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

  public abstract boolean isStable();

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

  @Override
  public String toString() {
    return value.toString();
  }
}

class IdCell extends Cell<Id> {
  public IdCell(Id id) {
    super(id);
  }

  @Override
  public boolean isStable() {
    return true;
  }
}

class LiteralCell extends Cell<Literal> {
  public LiteralCell(Literal value) {
    super(value);
  }

  @Override
  public boolean isStable() {
    return true;
  }
}

class HoleCell extends Cell<Expression> {
  public HoleCell(Expression value) {
    super(value);
  }

  @Override
  public boolean isStable() {
    return false;
  }
}
