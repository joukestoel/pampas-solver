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
import java.nio.charset.StandardCharsets;
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

    solverOut = new OutputStreamWriter(smtSolver.getOutputStream(), StandardCharsets.UTF_8);

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

      run(String.format("(declare-const %s %s)", t, sort));
    }
  }

  @Override
  public void addAssert(Formula formula) {
    write("(assert");
    formula.accept(writer);
    write(")");
    pushToSolver();
  }

  @Override
  public void addCommand(Command command) {

  }

  private void setOption(final String option, final String value) {
    run(String.format("(set-option :%s %s)", option, value));
  }

  private void run(String cmd) {
    synchronized (reader) {
      try {
        solverOut.write(cmd + "\n");
        solverOut.flush();

        reader.wait();
      } catch (IOException | InterruptedException e) {
        throw new IllegalStateException("Unable to write command to SMT solver");
      }
    }
  }

  private void write(String partial) {
    try {
      solverOut.write(partial);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to write partial formula to SMT solver");
    }
  }

  private void pushToSolver() {
    run("");
  }

  @Override
  public SolverOutcome solve() {
    run("(check-sat)");

    switch (lastResult) {
      case "sat":     return SolverOutcome.sat(getCurrentAssignedValues());
      case "unsat":   return SolverOutcome.unsat();
      case "unknown": return SolverOutcome.unknown();
      default: throw new IllegalStateException(String.format("Got result %s from solver but I don't know what to do with it", lastResult));
    }
  }

  private Set<Term> getCurrentAssignedValues() {
    write("(get-value (");
    variables.forEach(v -> write(v + " "));
    write("))");
    pushToSolver();

    return PersistentTrieSet.transientOf();
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

  private synchronized void outcomeRead(String lastResult) {
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
    private StringBuilder output;
    private int openBrackets;

    ThreadedInputStreamReader(InputStream is) {
      this.br = new BufferedReader(new InputStreamReader(is));
      finished = false;
      output = new StringBuilder();
      openBrackets = 0;
    }

    @Override
    public void run() {
        while (!finished) {
          try {
            String current;

            if ((current = br.readLine()) != null) {
              if (singularResponse(current)) {
                synchronized (this) {
                  outcomeRead(current);
                  notify();
                }
              } else {
                bookkeepBraces(current);
                output.append(current);

                if (openBrackets == 0) {
                  String completeResponse = output.toString();
                  output = new StringBuilder();

                  synchronized (this) {
                    outcomeRead(completeResponse);
                    notify();
                  }
                }
              }
            }

          } catch (IOException e) {
            throw new IllegalStateException("Unable to read output from solver");
          }

        }
    }

    void finish() {
      this.finished = true;
    }

    private boolean singularResponse(String current) {
      switch(current) {
        case "sat":
        case "unsat":
        case "unknown":
        case "success":
        case "unsupported":
        case "memout":
        case "incomplete":
        case "immediate-exit":
        case "continued-execution": return true;
        default:return false;
      }
    }

    private void bookkeepBraces(String current) {
      current.chars().forEach(c -> {
        if (((char)c) == '(') {
          openBrackets++;
        } else if (((char)c) == ')') {
          openBrackets--;
        }
      });
    }
  }
}
