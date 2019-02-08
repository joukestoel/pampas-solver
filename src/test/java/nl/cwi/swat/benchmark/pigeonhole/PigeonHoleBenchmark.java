package nl.cwi.swat.benchmark.pigeonhole;

import nl.cwi.swat.benchmark.MicroBenchmark;

public class PigeonHoleBenchmark extends MicroBenchmark {
  @Override
  protected void singleRun(int currentRunNr) {

  }

  @Override
  protected void warmup() {

  }
//  private Translator translator;
//  private TranslationCache translationCache;
//  private ReductionFormulaFactory ffactory;
//  private Set<Formula> constraints;
//  private Environment env;
//
//  private final int nrOfPigeons;
//  private final int nrOfHoles;
//
//  public PigeonHoleBenchmark(int nrOfPigeons, int nrOfHoles) {
//    this.nrOfPigeons = nrOfPigeons;
//    this.nrOfHoles = nrOfHoles;
//  }
//
//  @Override
//  protected void setup() {
//    SolverSetup setup = DaggerSolverSetup.builder().build();
//    translator = setup.translator();
//    translationCache = setup.translationCache();
//    ffactory = setup.formulaFactory();
//
//    env = constructEnv(true);
//    constraints = constraints();
//  }
//
//  private Environment constructEnv(boolean optional) {
//    RelationOld.RelationBuilder pigeonsBuilder = RelationOld.RelationBuilder.unary("pigeon", "pId", IdDomain.ID, ffactory, Caffeine.newBuilder().build());
//    RelationOld.RelationBuilder holesBuilder = RelationOld.RelationBuilder.unary("holes", "hId", IdDomain.ID, ffactory, Caffeine.newBuilder().build());
//    RelationOld.RelationBuilder nestBuilder = RelationOld.RelationBuilder.binary("nest", "pId", IdDomain.ID, "hId", IdDomain.ID, ffactory, Caffeine.newBuilder().build());
//
//    for (int p = 0; p < nrOfPigeons; p++) {
//      pigeonsBuilder.lowerBound("p" + p);
//      for (int h = 0; h < nrOfHoles; h++) {
//        nestBuilder.add(optional, "p" + p, "h" + h);
//      }
//
//    }
//
//    for (int h = 0; h < nrOfHoles; h++) {
//      holesBuilder.lowerBound("h" + h);
//    }
//
//    Environment env = Environment.base();
//    env.add("pigeons", pigeonsBuilder.build());
//    env.add("holes", holesBuilder.build());
//    env.add("nest", nestBuilder.build());
//
//    return env;
//  }
//
//
//  private Set<Formula> constraints() {
//    Set<Formula> constraints = new HashSet<>();
//
//    constraints.add(new Subset(new RelVar("nest"), new Product(new RelVar("pigeons"), new RelVar("holes"))));
//
//    List<Declaration> p = Collections.singletonList(new Declaration("p", new RelVar("pigeons")));
//    constraints.add(new Forall(p, new One(new NaturalJoin(new RelVar("p"), new RelVar("nest")))));
//
//    List<Declaration> h = Collections.singletonList(new Declaration("h", new RelVar("holes")));
//    constraints.add(new Forall(h, new Lone(new NaturalJoin(new RelVar("h"), new RelVar("nest")))));
//
//    return constraints;
//  }
//
//  @Override
//  protected void beforeRun() {
//    reset();
//  }
//
//  private void reset() {
//    env.invalidateIndexCaches();
//    translationCache.invalidate();
//  }
//
//  @Override
//  protected void singleRun(int currentRunNr) {
//    env = constructEnv(true);
//    translator.translate(env, constraints);
//  }
//
//  @Override
//  protected void beforeWarmup() {
//    reset();
//  }
//
//  @Override
//  protected void warmup() {
//    translator.translate(env, constraints);
//  }
//
//  public static void main(String... args) {
//    PigeonHoleBenchmark test = new PigeonHoleBenchmark(100,99);
//    List<Long> times = test.runBenchmark(10, 30);
//
//    System.out.println("-----------------------");
//    System.out.printf("Mean running time: %d \n", mean(times));
//    System.out.printf("Average running time: %d \n", average(times));
//    System.out.printf("90p running time: %d \n", ninetyPercent(times));
//    System.out.printf("Min running time: %d \n", min(times));
//    System.out.printf("Max running time: %d \n", max(times));
//    System.out.printf("First quartile time: %d \n", firstQuartile(times));
//    System.out.printf("Third quartile time: %d \n", thirdQuartile(times));
//    System.out.println(times);
//  }
//
////  public static void main(String... args) {
////    HashMap<Integer, Long> meanTimesPerConfig = new HashMap<>();
////
////    for (int i = 10; i < 100; i++) {
////      System.out.printf("Start benchmark run for %d pigeons and %d holes\n",i, i-1);
////      PigeonHoleBenchmark currentRun = new PigeonHoleBenchmark(i, i - 1);
////      List<Long> times = currentRun.runBenchmark(20, 50);
////
////      meanTimesPerConfig.put(i, mean(times));
////    }
////
////    saveToCSV("/Users/jouke/workspace/allealle-benchmark/benchmark/results/kodkod-comparison/pampas-results.csv", meanTimesPerConfig);
////  }
//
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
