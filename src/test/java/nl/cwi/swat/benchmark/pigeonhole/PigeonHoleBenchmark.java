package nl.cwi.swat.benchmark.pigeonhole;

import nl.cwi.swat.DaggerSolverSetup;
import nl.cwi.swat.SolverSetup;
import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.relational.*;
import nl.cwi.swat.benchmark.MicroBenchmark;
import nl.cwi.swat.translation.Environment;
import nl.cwi.swat.translation.Index;
import nl.cwi.swat.translation.TranslationCache;
import nl.cwi.swat.translation.Translator;
import nl.cwi.swat.translation.data.relation.RelationFactory;

import java.util.*;

public class PigeonHoleBenchmark extends MicroBenchmark {
  private Translator translator;
  private TranslationCache translationCache;
  private RelationFactory rf;
  private Index idx;
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
    rf = setup.relationFactory();
    idx = setup.index();

    env = constructEnv(true);
    constraints = constraints();
  }

  private Environment constructEnv(boolean optional) {
    RelationFactory.Builder.TupleBuilder pigeonsBuilder = rf.new Builder().create("pigeon").add("pId", Domain.ID).done();
    RelationFactory.Builder.TupleBuilder holesBuilder = rf.new Builder().create("holes").add("hId", Domain.ID).done();
    RelationFactory.Builder.TupleBuilder nestBuilder = rf.new Builder().create("nest").add("pId", Domain.ID).add("hId", Domain.ID).done();

    for (int p = 0; p < nrOfPigeons; p++) {
      pigeonsBuilder.lower(new Id("p" + p));

      for (int h = 0; h < nrOfHoles; h++) {
        if (optional) {
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
    translationCache.invalidate();
    idx.invalidate();
  }

  @Override
  protected void singleRun(int currentRunNr) {
    env = constructEnv(true);
    nl.cwi.swat.formulacircuit.Formula f = translator.translate(env, constraints);
  }

  @Override
  protected void beforeWarmup() {
    reset();
  }

  @Override
  protected void warmup() {
    translator.translate(env, constraints);
  }

  public static void main(String... args) {
    PigeonHoleBenchmark test = new PigeonHoleBenchmark(50,49);

    List<Long> times = test.runBenchmarkAfterEnter(10, 30);

    System.out.println("-----------------------");
    System.out.printf("Mean running time: %d \n", mean(times));
    System.out.printf("Average running time: %d \n", average(times));
    System.out.printf("90p running time: %d \n", ninetyPercent(times));
    System.out.printf("Min running time: %d \n", min(times));
    System.out.printf("Max running time: %d \n", max(times));
    System.out.printf("First quartile time: %d \n", firstQuartile(times));
    System.out.printf("Third quartile time: %d \n", thirdQuartile(times));
    System.out.println(times);
  }

  //  public static void main(String... args) {
//    HashMap<Integer, Long> meanTimesPerConfig = new HashMap<>();
//
//    for (int i = 10; i < 100; i++) {
//      System.out.printf("Start benchmark run for %d pigeons and %d holes\n",i, i-1);
//      PigeonHoleBenchmark currentRun = new PigeonHoleBenchmark(i, i - 1);
//      List<Long> times = currentRun.runBenchmark(20, 50);
//
//      meanTimesPerConfig.put(i, mean(times));
//    }
//
//    saveToCSV("/Users/jouke/workspace/allealle-benchmark/benchmark/results/kodkod-comparison/pampas-results.csv", meanTimesPerConfig);
//  }

//  private static void saveToCSV(String csvLocation, Map<Integer, Long> meanTimesPerConfig) {
//    File csv = new File(csvLocation);
//
//    if (!csv.exists()) {
//      try {
//        csv.createNewFile();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//
//    try (PrintWriter pw = new PrintWriter(new FileWriter(csv))) {
//      pw.println("#Config,MeanTranslationTime");
//      List<Integer> sortedConfigs = new ArrayList<>(meanTimesPerConfig.keySet());
//      sortedConfigs.sort(Comparator.comparingInt(a -> a));
//
//      for (Integer cur : sortedConfigs) {
//        pw.printf("%d,%d\n", cur, meanTimesPerConfig.get(cur));
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//  }

}
