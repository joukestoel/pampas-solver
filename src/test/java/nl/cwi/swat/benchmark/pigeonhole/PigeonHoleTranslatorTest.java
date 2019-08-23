package nl.cwi.swat.benchmark.pigeonhole;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.DaggerSolverSetup;
import nl.cwi.swat.SolverSetup;
import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.relational.*;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import nl.cwi.swat.solverbackend.SolverAnswer;
import nl.cwi.swat.solverbackend.SolverOutcome;
import nl.cwi.swat.solverbackend.external.ExternalSolver;
import nl.cwi.swat.translation.Environment;
import nl.cwi.swat.translation.Translator;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assume.assumeThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Ignore
@RunWith(JUnitQuickcheck.class)
public class PigeonHoleTranslatorTest {
  private Translator translator;
  private RelationFactory rf;
  private FormulaFactory ff;

  @Before
  @BeforeEach
  public void setup() {
    SolverSetup setup = DaggerSolverSetup.builder().build();
    translator = setup.translator();

    rf = setup.relationFactory();
    ff = setup.formulaFactory();
  }

  private Environment constructEnv(int nrOfPigeons, int nrOfHoles, boolean optionalNests) {
    RelationFactory.Builder.TupleBuilder pigeonsBuilder = rf.new Builder().create("pigeon").add("pId", Domain.ID).done();
    RelationFactory.Builder.TupleBuilder holesBuilder = rf.new Builder().create("holes").add("hId", Domain.ID).done();
    RelationFactory.Builder.TupleBuilder nestBuilder = rf.new Builder().create("nest").add("pId", Domain.ID).add("hId", Domain.ID).done();

    for (int p = 0; p < nrOfPigeons; p++) {
      pigeonsBuilder.lower(new Id("p" + p));

      for (int h = 0; h < nrOfHoles; h++) {
        if (optionalNests) {
          nestBuilder.upper(new Id("p" + p), new Id("h" + h));
        } else {
          nestBuilder.lower(new Id("p" + p), new Id("h" + h));
        }
      }

    }

    for (int h = 0; h < nrOfHoles; h++) {
      holesBuilder.lower(new Id("h" + h));
    }

    Environment env = Environment.base();
    env.add("pigeons", pigeonsBuilder.done());
    env.add("holes", holesBuilder.done());
    env.add("nest", nestBuilder.done());

    return env;
  }


  private Set<Formula> constraints() {
    Set<Formula> constraints = new HashSet<>();

    constraints.add(new Subset(new RelVar("nest"), new Product(new RelVar("pigeons"), new RelVar("holes"))));

    List<Declaration> p = Collections.singletonList(new Declaration("p", new RelVar("pigeons")));
    constraints.add(new Forall(p, new One(new NaturalJoin(new RelVar("p"), new RelVar("nest")))));

    List<Declaration> h = Collections.singletonList(new Declaration("h", new RelVar("holes")));
    constraints.add(new Forall(h, new Lone(new NaturalJoin(new RelVar("h"), new RelVar("nest")))));

    return constraints;
  }

  @Test
  public void pigeonHoleProblemIsUnsatSingularTest() {
    long startTime = System.currentTimeMillis();
    Environment env = constructEnv(5, 4, true);
    long timeCreatingEnv = System.currentTimeMillis() - startTime;
    System.out.println("Done building env");

    startTime = System.currentTimeMillis();
    nl.cwi.swat.formulacircuit.Formula result = translator.translate(env, constraints());
    System.out.println("Done translating");
    long timeTranslating = System.currentTimeMillis() - startTime;

    startTime = System.currentTimeMillis();
    ExternalSolver z3 = new ExternalSolver("z3", List.of("-smt2", "-in"));
    z3.addVariables(ff.getVariables());
    z3.addAssert(result);
    SolverOutcome outcome = z3.solve();
    long timeSolving = System.currentTimeMillis() - startTime;

    System.out.println("Outcome : " + outcome.answer());

    z3.stop();

    System.out.println("Total time of running test: " + (timeCreatingEnv + timeTranslating + timeSolving) + " ms");
    System.out.println("Time creating environment: " + timeCreatingEnv + " ms");
    System.out.println("Time translating: " + timeTranslating + " ms");
    System.out.println("Time solving SMT in solver: " + timeSolving + " ms");

    assertEquals(SolverAnswer.UNSAT, outcome.answer());
  }

  @Property
  public void pigeonHoleProblemIsUnsat(@InRange(minInt = 1, maxInt = 10) int pigeons, @InRange(minInt = 1, maxInt = 10) int holes, boolean optional) {
    assumeThat(holes, lessThan(pigeons));

    System.out.println("");
    System.out.println("Configuration: " + pigeons + " pigeons, " + holes + " holes.");

    long startTime = System.currentTimeMillis();
    Environment env = constructEnv(pigeons, holes, optional);
    long timeCreatingEnv = System.currentTimeMillis() - startTime;

    startTime = System.currentTimeMillis();
    nl.cwi.swat.formulacircuit.Formula result = translator.translate(env, constraints());
    long timeTranslating = System.currentTimeMillis() - startTime;

    if (!optional) {
      assertEquals(BooleanConstant.FALSE, result);
    } else {
      startTime = System.currentTimeMillis();
      ExternalSolver z3 = new ExternalSolver("z3", List.of("-smt2", "-in"));
      z3.addVariables(ff.getVariables());
      z3.addAssert(result);
      SolverOutcome outcome = z3.solve();
      long timeSolving = System.currentTimeMillis() - startTime;

      System.out.println("Outcome : " + outcome.answer());

      z3.stop();

      System.out.println("Time creating environment: " + timeCreatingEnv + " ms");
      System.out.println("Time translating: " + timeTranslating + " ms");
      System.out.println("Time solving SMT in solver: " + timeSolving + " ms");
      System.out.println("Total time of running test: " + (timeCreatingEnv + timeTranslating + timeSolving) + " ms");

      assertEquals(SolverAnswer.UNSAT, outcome.answer());
    }
  }
}