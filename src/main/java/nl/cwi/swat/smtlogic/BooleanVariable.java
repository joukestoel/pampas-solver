package nl.cwi.swat.smtlogic;

import java.util.Objects;

public class BooleanVariable extends Expression implements Formula {
  private final String name;

  public BooleanVariable(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BooleanVariable that = (BooleanVariable) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {

    return Objects.hash(name);
  }
}
