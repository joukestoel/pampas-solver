package nl.cwi.swat.solverbackend.external;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.util.List;

@Ignore
class ExternalSolverTest {

  @Test
  void canStartAndStopExternalSolverWithoutException() {
    ExternalSolver z3 = new ExternalSolver("z3", List.of("-smt2", "-in"));
    z3.stop();
  }
}