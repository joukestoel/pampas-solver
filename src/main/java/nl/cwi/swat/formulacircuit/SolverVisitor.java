package nl.cwi.swat.formulacircuit;

import nl.cwi.swat.formulacircuit.bool.*;
import nl.cwi.swat.formulacircuit.ints.*;
import nl.cwi.swat.formulacircuit.rel.IdConstant;

public interface SolverVisitor <T> {
  T visit(Operator operator);

  T visit(BooleanAccumulator acc);
  T visit(BooleanBinaryGate bg);
  T visit(BooleanConstant c);
  T visit(BooleanNaryGate ng);
  T visit(BooleanVariable bv);
  T visit(NotGate ng);

  T visit(IntegerAccumulator acc);
  T visit(IntegerBinaryGate bg);
  T visit(IntegerConstant ic);
  T visit(IntegerNaryGate ng);
  T visit(IntegerVariable iv);
  T visit(ITEGate ite);
  T visit(NegGate ng);

  T visit(IdConstant idc);
}
