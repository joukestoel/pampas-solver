package nl.cwi.swat.solverbackend;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieSet;
import nl.cwi.swat.formulacircuit.Term;

public class SolverOutcome {
  private final SolverAnswer answer;
  private final Set<Term> model;

  private SolverOutcome(SolverAnswer answer, Set<Term> model) {
    this.answer = answer;
    this.model = model;
  }

  public static SolverOutcome sat(Set<Term> model) {
    return new SolverOutcome(SolverAnswer.SAT, model);
  }

  public static SolverOutcome unsat() {
    return new SolverOutcome(SolverAnswer.UNSAT, PersistentTrieSet.of());
  }

  public static SolverOutcome unknown() {
    return new SolverOutcome(SolverAnswer.UNKNOWN, PersistentTrieSet.of());
  }

  public static SolverOutcome timeout() {
    return new SolverOutcome(SolverAnswer.TIMEOUT, PersistentTrieSet.of());
  }

  public SolverAnswer answer() {
    return answer;
  }

  public Set<Term> model() {
    return model;
  }
}
