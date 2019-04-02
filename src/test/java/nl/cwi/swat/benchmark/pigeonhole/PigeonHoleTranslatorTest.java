package nl.cwi.swat.benchmark.pigeonhole;

import nl.cwi.swat.DaggerSolverSetup;
import nl.cwi.swat.SolverSetup;
import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.relational.*;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import nl.cwi.swat.solverbackend.SmtFileWriter;
import nl.cwi.swat.solverbackend.SolverOutcome;
import nl.cwi.swat.solverbackend.external.ExternalSolver;
import nl.cwi.swat.translation.Environment;
import nl.cwi.swat.translation.Translator;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PigeonHoleTranslatorTest {
  private Translator translator;
  private RelationFactory rf;
  private FormulaFactory ff;
  private SmtFileWriter smtFileWriter;

  @BeforeEach
  void setup() {
    SolverSetup setup = DaggerSolverSetup.builder().build();
    translator = setup.translator();

    rf = setup.relationFactory();
    ff = setup.formulaFactory();

    smtFileWriter = new SmtFileWriter();
  }

  private Environment constructEnv(int nrOfPigeons, int nrOfHoles, boolean optionalNests) {
    RelationFactory.Builder.TupleBuilder pigeonsBuilder = rf.new Builder().create("pigeon").add("pId", Domain.ID).done();
    RelationFactory.Builder.TupleBuilder holesBuilder = rf.new Builder().create("holes").add("hId", Domain.ID).done();
    RelationFactory.Builder.TupleBuilder nestBuilder = rf.new Builder().create("nest").add("pId", Domain.ID).add("hId", Domain.ID).done();

    for (int p = 0; p < nrOfPigeons; p++) {
//      if (optionalNests) {
//        pigeonsBuilder.upper(new Id("p" + p));
//      } else {
        pigeonsBuilder.lower(new Id("p" + p));
//      }

      for (int h = 0; h < nrOfHoles; h++) {
        if (optionalNests) {
          nestBuilder.upper(new Id("p" + p), new Id("h" + h));
        } else {
          nestBuilder.lower(new Id("p" + p), new Id("h" + h));
        }
      }

    }

    for (int h = 0; h < nrOfHoles; h++) {
      holesBuilder.lower(new Id("h"+ h));
    }

    Environment env = Environment.base();
    env.add("pigeons", pigeonsBuilder.done());
    env.add("holes", holesBuilder.done());
    env.add("nest", nestBuilder.done());

    return env;
  }


  private Set<Formula> constraints() {
    Set<Formula> constraints = new HashSet<>();

//    constraints.add(new Subset(new RelVar("nest"), new Product(new RelVar("pigeons"), new RelVar("holes"))));

//    List<Declaration> p = Collections.singletonList(new Declaration("p", new RelVar("pigeons")));
//    constraints.add(new Forall(p, new One(new NaturalJoin(new RelVar("p"), new RelVar("nest")))));

    List<Declaration> h = Collections.singletonList(new Declaration("h", new RelVar("holes")));
    constraints.add(new Forall(h, new Lone(new NaturalJoin(new RelVar("h"), new RelVar("nest")))));

    return constraints;
  }


  void simpleAST() {
    long startTime = System.currentTimeMillis();
    Environment env = constructEnv(2,1, true);
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
    long timeSolving = System.currentTimeMillis() - startTime;

    SolverOutcome outcome = z3.solve();
    System.out.println("Outcome : " + outcome.answer());

    z3.stop();

    System.out.println("Total time of running test: " + (timeCreatingEnv  + timeTranslating + timeSolving) + " ms");
    System.out.println("Time creating environment: " + timeCreatingEnv + " ms");
    System.out.println("Time translating: " + timeTranslating + " ms");
    System.out.println("Time solving SMT in solver: " + timeSolving + " ms");

    assertEquals(BooleanConstant.FALSE, result);
  }
}