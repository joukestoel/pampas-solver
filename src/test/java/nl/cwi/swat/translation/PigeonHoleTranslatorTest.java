package nl.cwi.swat.translation;

import com.github.benmanes.caffeine.cache.Caffeine;
import nl.cwi.swat.DaggerSolverSetup;
import nl.cwi.swat.SolverSetup;
import nl.cwi.swat.ast.relational.*;
import nl.cwi.swat.smtlogic.BooleanConstant;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.translation.data.Relation;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class PigeonHoleTranslatorTest {
  private Translator translator;
  private FormulaFactory ffactory;

  @Before
  public void setup() {
    SolverSetup setup = DaggerSolverSetup.builder().build();
    translator = setup.translator();
    ffactory = setup.formulaFactory();
  }

  private Environment constructEnv(int nrOfPigeons, int nrOfHoles, boolean optional) {
    Relation.RelationBuilder pigeonsBuilder = Relation.RelationBuilder.unary("pigeon", "pId", IdDomain.ID, ffactory, Caffeine.newBuilder().build());
    Relation.RelationBuilder holesBuilder = Relation.RelationBuilder.unary("holes", "hId", IdDomain.ID, ffactory, Caffeine.newBuilder().build());
    Relation.RelationBuilder nestBuilder = Relation.RelationBuilder.binary("nest", "pId", IdDomain.ID, "hId", IdDomain.ID, ffactory, Caffeine.newBuilder().build());

    for (int p = 0; p < nrOfPigeons; p++) {
      pigeonsBuilder.lowerBound("p" + p);
      for (int h = 0; h < nrOfHoles; h++) {
        nestBuilder.add(optional, "p" + p, "h" + h);
      }

    }

    for (int h = 0; h < nrOfHoles; h++) {
      holesBuilder.lowerBound("h"+ h);
    }

    Environment env = Environment.base();
    env.add("pigeons", pigeonsBuilder.build());
    env.add("holes", holesBuilder.build());
    env.add("nest", nestBuilder.build());

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
  public void simpleAST() {
    Environment env = constructEnv(100,99, true);
    translator.setBaseEnvironment(env);

    nl.cwi.swat.smtlogic.Formula result = translator.translate(constraints());

    assertEquals(BooleanConstant.FALSE, result);
  }
}