package nl.cwi.swat.smtlogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NaryGate implements Formula {
  private List<Formula> inputs;
  private final Operator op;

  private NaryGate(final Operator op) {
    this.op = op;
    this.inputs = new ArrayList<>();
  }

  public static NaryGate and(Formula left, Formula right) {
    NaryGate andGate = new NaryGate(Operator.AND);
    andGate.inputs.add(left);
    andGate.inputs.add(right);

    return andGate;
  }

  public static NaryGate or(Formula left, Formula right) {
    NaryGate orGate = new NaryGate(Operator.OR);
    orGate.inputs.add(left);
    orGate.inputs.add(right);

    return orGate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NaryGate naryGate = (NaryGate) o;
    return Objects.equals(inputs, naryGate.inputs) &&
            Objects.equals(op, naryGate.op);
  }

  @Override
  public int hashCode() {

    return Objects.hash(inputs, op);
  }
}
