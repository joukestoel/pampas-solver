package nl.cwi.swat.smtlogic;

import java.util.Objects;

public class BooleanConstant extends Literal implements Formula {
  private final boolean value;

  public static final BooleanConstant TRUE = new BooleanConstant(true);
  public static final BooleanConstant FALSE = new BooleanConstant(false);

  public BooleanConstant(boolean value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BooleanConstant that = (BooleanConstant) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {

    return Objects.hash(value);
  }
}
