package nl.cwi.swat.formulacircuit;

import nl.cwi.swat.formulacircuit.bool.*;
import nl.cwi.swat.formulacircuit.ints.*;
import nl.cwi.swat.formulacircuit.rel.IdConstant;

public class CnfConverter implements SolverVisitor<Term> {




  @Override
  public Term visit(Operator operator) {
    return null;
  }

  @Override
  public Term visit(BooleanAccumulator acc) {
    return null;
  }

  @Override
  public Term visit(BooleanBinaryGate bg) {
    return null;
  }

  @Override
  public Term visit(BooleanConstant c) {
    return null;
  }

  @Override
  public Term visit(BooleanNaryGate ng) {
    return null;
  }

  @Override
  public Term visit(BooleanVariable bv) {
    return null;
  }

  @Override
  public Term visit(NotGate ng) {
    return null;
  }

  @Override
  public Term visit(IntegerAccumulator acc) {
    return null;
  }

  @Override
  public Term visit(IntegerBinaryGate bg) {
    return null;
  }

  @Override
  public Term visit(IntegerConstant ic) {
    return null;
  }

  @Override
  public Term visit(IntegerNaryGate ng) {
    return null;
  }

  @Override
  public Term visit(IntegerVariable iv) {
    return null;
  }

  @Override
  public Term visit(ITEGate ite) {
    return null;
  }

  @Override
  public Term visit(NegGate ng) {
    return null;
  }

  @Override
  public Term visit(IdConstant idc) {
    return null;
  }

  private class PolarityDetector implements SolverVisitor<Void> {

    @Override
    public Void visit(Operator operator) {
      return null;
    }

    @Override
    public Void visit(BooleanAccumulator acc) {
      return null;
    }

    @Override
    public Void visit(BooleanBinaryGate bg) {
      return null;
    }

    @Override
    public Void visit(BooleanConstant c) {
      return null;
    }

    @Override
    public Void visit(BooleanNaryGate ng) {
      return null;
    }

    @Override
    public Void visit(BooleanVariable bv) {
      return null;
    }

    @Override
    public Void visit(NotGate ng) {
      return null;
    }

    @Override
    public Void visit(IntegerAccumulator acc) {
      return null;
    }

    @Override
    public Void visit(IntegerBinaryGate bg) {
      return null;
    }

    @Override
    public Void visit(IntegerConstant ic) {
      return null;
    }

    @Override
    public Void visit(IntegerNaryGate ng) {
      return null;
    }

    @Override
    public Void visit(IntegerVariable iv) {
      return null;
    }

    @Override
    public Void visit(ITEGate ite) {
      return null;
    }

    @Override
    public Void visit(NegGate ng) {
      return null;
    }

    @Override
    public Void visit(IdConstant idc) {
      return null;
    }
  }
}
