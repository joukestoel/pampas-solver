package nl.cwi.swat.translation;

import com.github.benmanes.caffeine.cache.Caffeine;
import nl.cwi.swat.DaggerSolverSetup;
import nl.cwi.swat.SolverSetup;
import nl.cwi.swat.ast.relational.*;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.translation.data.Relation;
import nl.cwi.swat.util.MicroBenchmark;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PigeonHoleBenchmark extends MicroBenchmark {
  private Translator translator;
  private TranslationCache translationCache;
  private FormulaFactory ffactory;
  private Set<Formula> constraints;
  private Environment env;

  private final int nrOfPigeons;
  private final int nrOfHoles;

  public PigeonHoleBenchmark(int nrOfPigeons, int nrOfHoles) {
    this.nrOfPigeons = nrOfPigeons;
    this.nrOfHoles = nrOfHoles;
  }

  @Override
  protected void setup() {
    SolverSetup setup = DaggerSolverSetup.builder().build();
    translator = setup.translator();
    translationCache = setup.translationCache();
    ffactory = setup.formulaFactory();

    env = constructEnv(true);
    constraints = constraints();
  }

  private Environment constructEnv(boolean optional) {
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

  @Override
  protected void beforeRun() {
    reset();
  }

  private void reset() {
    env.invalidateIndexCaches();
    translator.setBaseEnvironment(env);
    translationCache.invalidate();
  }

  @Override
  protected void singleRun(int currentRunNr) {
    translator.translate(constraints);
  }

  @Override
  protected void beforeWarmup() {
    reset();
  }

  @Override
  protected void warmup() {
    translator.translate(constraints);
  }

  public static void main(String... args) {
    PigeonHoleBenchmark test = new PigeonHoleBenchmark(100,99);
    List<Long> times = test.runBenchmark(50, 100);

    System.out.println("-----------------------");
    System.out.printf("Mean running time: %d \n", mean(times));
    System.out.printf("Average running time: %d \n", average(times));
    System.out.printf("90p running time: %d \n", ninetyPercent(times));
    System.out.printf("Min running time: %d \n", min(times));
    System.out.printf("Max running time: %d \n", max(times));
    System.out.println(times);
  }

}
