package nl.cwi.swat.solverbackend.external;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieSet;
import nl.cwi.swat.formulacircuit.*;
import nl.cwi.swat.formulacircuit.bool.*;
import nl.cwi.swat.formulacircuit.ints.*;
import nl.cwi.swat.formulacircuit.rel.IdConstant;
import nl.cwi.swat.solverbackend.SmtSolver;
import nl.cwi.swat.solverbackend.SolverOutcome;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExternalSolver implements SmtSolver {
  private final Process smtSolver;

  private final ThreadedInputStreamReader reader;
  private final OutputStreamWriter solverOut;

  private String lastResult;

  private Set<Term> variables;
  private SolverWriter writer;

  public ExternalSolver(String executable, List<String> options) {
    List<String> commands = new ArrayList<>(options);
    commands.add(0, executable);

    smtSolver = startSolver(commands);

    writer = new SolverWriter();

    reader = new ThreadedInputStreamReader(smtSolver.getInputStream());
    reader.start();

    try {
      solverOut = new OutputStreamWriter(smtSolver.getOutputStream(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Unable to create outputstream with charset UTF-8");
    }

    setOption("print-success", "true");
  }

  private Process startSolver(List<String> commands) {
    ProcessBuilder pb = new ProcessBuilder(commands)
            .redirectErrorStream(true);

    try {
      return pb.start();
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to start external solver, reason: " + e.getMessage());
    }
  }

  @Override
  public void addVariables(Set<Term> variables) {
    this.variables = variables;

    for (Term t : variables) {
      String sort = "";
      if (t instanceof BooleanVariable) {
        sort = "Bool";
      } else if (t instanceof IntegerVariable) {
        sort = "Int";
      }

      run(String.format("(declare-const %s %s)", t.toString(), sort));
    }
  }

  @Override
  public void addAssert(Formula formula) {
    write("(assert");
    formula.accept(writer);
    write(")");
    pushFormula();
  }

  @Override
  public void addCommand(Command command) {

  }

  public void setOption(String option, String value) {
    run(String.format("(set-option :%s %s)", option, value));
  }

  private void run(String cmd) {
    synchronized (reader) {
      try {
        System.out.println(cmd);
        solverOut.write(cmd + "\n");
        solverOut.flush();

        reader.wait();
      } catch (IOException | InterruptedException e) {
        throw new IllegalStateException("Unable to write command to SMT solver");
      }
    }
  }

  public void write(String partial) {
    try {
      System.out.print(partial);
      solverOut.write(partial);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to write partial formula to SMT solver");
    }
  }

  private void pushFormula() {
    run("");
  }

  @Override
  public SolverOutcome solve() {
    run("(check-sat)");

    switch (lastResult) {
      case "sat":     return SolverOutcome.sat(PersistentTrieSet.transientOf());
      case "unsat":   return SolverOutcome.unsat();
      case "unknown": return SolverOutcome.unknown();
      default: throw new IllegalStateException(String.format("Got result %s from solver but I don't know what to do with it", lastResult));
    }

  }

  @Override
  public SolverOutcome next(Set<Term> currentModel) {
    return null;
  }

  @Override
  public void stop() {
    reader.finish();

    if (smtSolver.isAlive()) {
      smtSolver.destroy();
    }
  }

  synchronized void outcomeRead(String lastResult) {
    this.lastResult = lastResult;
  }

  class SolverWriter implements SolverVisitor<Void> {
    @Override
    public Void visit(Operator operator) {
      if (operator == BooleanOperator.AND) {
        ExternalSolver.this.write("and");
      } else if (operator == BooleanOperator.OR) {
        ExternalSolver.this.write("or");
      } else {
        throw new IllegalStateException("Writing operator " + operator + " to the solver not yet implemented");
      }

      return null;
    }

    @Override
    public Void visit(BooleanAccumulator acc) {
      throw new IllegalStateException("There should not be a boolean accumulator left in the translated formula");
    }

    @Override
    public Void visit(BooleanBinaryGate bg) {
      ExternalSolver.this.write(" (");

      bg.operator().accept(this);
      bg.forEach(t -> t.accept(this));

      ExternalSolver.this.write(")");

      return null;
    }

    @Override
    public Void visit(BooleanConstant c) {
      throw new IllegalStateException("There should not be a boolean constant left in the translated formula");
    }

    @Override
    public Void visit(BooleanNaryGate ng) {
      ExternalSolver.this.write(" (");

      ng.operator().accept(this);
      ng.forEach(t -> t.accept(this));

      ExternalSolver.this.write(")");

      return null;
    }

    @Override
    public Void visit(BooleanVariable bv) {
      ExternalSolver.this.write(" " + bv.toString());

      return null;
    }

    @Override
    public Void visit(NotGate ng) {
      ExternalSolver.this.write(" (not");
      ng.input(0).accept(this);
      ExternalSolver.this.write(")");

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

  class ThreadedInputStreamReader extends Thread {
    private final BufferedReader br;

    private boolean finished;

    public ThreadedInputStreamReader(InputStream is) {
      this.br = new BufferedReader(new InputStreamReader(is));
      finished = false;

    }

    @Override
    public void run() {
        while (!finished) {
          try {
            String current;

            if ((current = br.readLine()) != null) {
                synchronized (this) {
                  outcomeRead(current);
                  notify();
              }

            }

          } catch (IOException e) {
            throw new IllegalStateException("Unable to read output from solver");
          }

        }
    }

    public void finish() {
      this.finished = true;
    }


  }
}
