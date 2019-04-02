package nl.cwi.swat.solverbackend;

import io.usethesource.capsule.Set;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.SolverVisitor;
import nl.cwi.swat.formulacircuit.Term;
import nl.cwi.swat.formulacircuit.bool.*;
import nl.cwi.swat.formulacircuit.ints.*;
import nl.cwi.swat.formulacircuit.rel.IdConstant;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SmtFileWriter implements SolverVisitor<Void> {
  private enum Phase {
    VAR_DECLARATION, ASSERTIONS;
  }

  private final Path outputFile;
  private Phase phase;

  private BufferedWriter writer;

  public SmtFileWriter() {
    outputFile = Paths.get("./latestSmt.smt");
  }

  public void writeTranslationResult(Formula base, Set<Term> variables) {
    try {
      writer = Files.newBufferedWriter(outputFile);

      phase = Phase.VAR_DECLARATION;
      writeVariableDeclarations(variables);

      phase = Phase.ASSERTIONS;
      writer.append("(assert ");
      base.accept(this);
      writer.append(")");

      writer.flush();
      writer.close();
    } catch (IOException ex) {
      System.err.println("IO exception occurred: " + ex.getMessage());
    }
  }

  private void writeVariableDeclarations(Set<Term> variables) throws IOException {
    for (Term var : variables) {
      var.accept(this);
    }
  }

  @Override
  public Void visit(Operator operator) {
    try {
      if (operator == BooleanOperator.AND) {
        writer.append("and");
      } else if (operator == BooleanOperator.OR) {
        writer.append("or");
      } else {
        System.err.println("Unable to convert operator " + operator + " to SMT");
      }
    } catch (IOException ex) {
      System.err.println("IO exception occurred: " + ex.getMessage());
    }

    return null;
  }

  @Override
  public Void visit(BooleanAccumulator acc) {
    throw new IllegalStateException("There should not be a boolean accumulator left in the translated formula");
  }

  @Override
  public Void visit(BooleanBinaryGate bg) {
    try {
      writer.append(" (");
      bg.operator().accept(this);
      bg.forEach(t -> t.accept(this));
      writer.append(")");
    } catch (IOException ex) {
      System.err.println("IO exception occurred: " + ex.getMessage());
    }

    return null;
  }

  @Override
  public Void visit(BooleanConstant c) {
    throw new IllegalStateException("There should not be a boolean constant left in the translated formula");
  }

  @Override
  public Void visit(BooleanNaryGate ng) {
    try {
      writer.append(" (");
      ng.operator().accept(this);
      ng.forEach(t -> t.accept(this));
      writer.append(")");
    } catch (IOException ex) {
      System.err.println("IO exception occurred: " + ex.getMessage());
    }

    return null;
  }

  @Override
  public Void visit(BooleanVariable bv) {
    try {
      if (phase == Phase.VAR_DECLARATION) {
        writer.append(String.format("(declare-const %s Bool)\n", bv.toString()));
      } else {
        writer.append(" " + bv.toString() + " ");
      }

    } catch (IOException ex) {
      System.err.println("IO exception occurred: " + ex.getMessage());
    }

    return null;
  }

  @Override
  public Void visit(NotGate ng) {
    try {
      writer.append(" (not");
      ng.input(0).accept(this);
      writer.append(")");
    } catch (IOException ex) {
      System.err.println("IO exception occurred: " + ex.getMessage());
    }

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
    try {
      if (phase == Phase.VAR_DECLARATION) {
        writer.append(String.format("(declare-const %s Int)\n", iv.toString()));
      }

    } catch (IOException ex) {
      System.err.println("IO exception occurred: " + ex.getMessage());
    }

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
