package nl.cwi.swat.solverbackend;

import io.usethesource.capsule.Set;
import nl.cwi.swat.formulacircuit.Command;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Term;

public interface SmtSolver {

  void addVariables(Set<Term> variables);

  void addAssert(Formula formula);

  void addCommand(Command command);

  SolverOutcome solve();

  SolverOutcome next(Set<Term> currentModel);

  void stop();
}
