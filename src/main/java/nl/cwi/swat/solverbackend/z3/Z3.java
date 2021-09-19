//package nl.cwi.swat.solverbackend.z3;
//
//import com.microsoft.z3.Context;
//import io.usethesource.capsule.Set;
//import nl.cwi.swat.formulacircuit.*;
//import nl.cwi.swat.formulacircuit.bool.*;
//import nl.cwi.swat.formulacircuit.ints.*;
//import nl.cwi.swat.formulacircuit.rel.IdConstant;
//import nl.cwi.swat.solverbackend.SmtSolver;
//import nl.cwi.swat.solverbackend.SolverOutcome;
//
//public class Z3 implements SmtSolver {
//  private Context ctx;
//
//  public Z3() {}
//
//  @Override
//  public void start() {
//    ctx = new Context();
//  }
//
//  @Override
//  public void addVariables(Set<Term> variables) {
//  }
//
//  @Override
//  public void addAssert(Formula formula) {
//
//  }
//
//  @Override
//  public void addCommand(Command command) {
//
//  }
//
//  @Override
//  public SolverOutcome solve() {
//    return null;
//  }
//
//  @Override
//  public SolverOutcome next(Set<Term> currentModel) {
//    return null;
//  }
//
//  @Override
//  public void stop() {
//
//  }
//
//  class Z3Visitor implements SolverVisitor<Void> {
//    @Override
//    public Void visit(Operator operator) {
//      return null;
//    }
//
//    @Override
//    public Void visit(BooleanAccumulator acc) {
//      return null;
//    }
//
//    @Override
//    public Void visit(BooleanBinaryGate bg) {
//      return null;
//    }
//
//    @Override
//    public Void visit(BooleanConstant c) {
//      return null;
//    }
//
//    @Override
//    public Void visit(BooleanNaryGate ng) {
//      return null;
//    }
//
//    @Override
//    public Void visit(BooleanVariable bv) {
//      return null;
//    }
//
//    @Override
//    public Void visit(NotGate ng) {
//      return null;
//    }
//
//    @Override
//    public Void visit(IntegerAccumulator acc) {
//      return null;
//    }
//
//    @Override
//    public Void visit(IntegerBinaryGate bg) {
//      return null;
//    }
//
//    @Override
//    public Void visit(IntegerConstant ic) {
//      return null;
//    }
//
//    @Override
//    public Void visit(IntegerNaryGate ng) {
//      return null;
//    }
//
//    @Override
//    public Void visit(IntegerVariable iv) {
//      return null;
//    }
//
//    @Override
//    public Void visit(ITEGate ite) {
//      return null;
//    }
//
//    @Override
//    public Void visit(NegGate ng) {
//      return null;
//    }
//
//    @Override
//    public Void visit(IdConstant idc) {
//      return null;
//    }
//  }
//}
