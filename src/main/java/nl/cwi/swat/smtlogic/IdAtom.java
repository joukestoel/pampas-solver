package nl.cwi.swat.smtlogic;

import org.jetbrains.annotations.NotNull;

public class IdAtom extends Literal {
  private final String value;

  public IdAtom(@NotNull String value) {
    this.value = value;
  }

  @Override
  public Operator operator() {
    return Operator.CONST;
  }

  @Override
  public int hash(Operator op) {
    return 0;
  }

  @Override
  public long label() {
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    IdAtom idAtom = (IdAtom) o;

    return value.equals(idAtom.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}
