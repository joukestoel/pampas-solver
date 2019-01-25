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
}
