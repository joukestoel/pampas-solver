package nl.cwi.swat.solverbackend.external;

import io.usethesource.capsule.core.PersistentTrieSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExternalSolverTest {

  @Test
  void canStartAndStopExternalSolverWithoutException() {
    ExternalSolver z3 = new ExternalSolver("z3", List.of("-smt2", "-in"));

    z3.solve();

    z3.stop();
  }
}