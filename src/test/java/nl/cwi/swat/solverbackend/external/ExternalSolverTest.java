package nl.cwi.swat.solverbackend.external;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

//@Tag("SolverIntegration")
class ExternalSolverTest {

  @Test
  void canStartAndStopExternalSolverWithoutExceptionTest() {
    ExternalSolver z3 = new ExternalSolver("z3", List.of("-smt2", "-in"));

    z3.start();
    z3.stop();
  }
}