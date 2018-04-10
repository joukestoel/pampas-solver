package nl.cwi.swat.translation.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import nl.cwi.swat.ast.relational.IdDomain;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.smtlogic.SimplificationFactory;
import nl.cwi.swat.util.MicroBenchmark;

import java.util.List;

public class JoinBenchmarkTest extends MicroBenchmark {

  private final int nrOfPigeons;
  private final int nrOfHoles;

  private final FormulaFactory ffactory;
  private final Cache<Relation.IndexCacheKey, Relation.IndexedRows> indexCache;

  private Relation.RelationBuilder nestBuilder;

  private Relation nest;
  private Relation[] pigeons;
  private Relation[] holes;

  public JoinBenchmarkTest(int nrOfPigeons, int nrOfHoles, Cache<Relation.IndexCacheKey, Relation.IndexedRows> indexCache) {
    this.nrOfPigeons = nrOfPigeons;
    this.nrOfHoles = nrOfHoles;
    this.ffactory = new FormulaFactory(new SimplificationFactory(3, Caffeine.newBuilder().build()));
    this.indexCache = indexCache;
  }

  @Override
  protected void setup() {
    nestBuilder = Relation.RelationBuilder.binary("nest", "pId", IdDomain.ID, "hId", IdDomain.ID, ffactory, indexCache);
    pigeons = new Relation[nrOfPigeons];
    holes = new Relation[nrOfHoles];
  }

  @Override
  protected void beforeRun() {
    indexCache.invalidateAll();
    indexCache.cleanUp();
  }

  @Override
  protected void singleRun(int currentRunNr) {
    for (int p = 0; p < nrOfPigeons; p++) {
      pigeons[p].join(nest);
    }
    for (int h = 0; h < nrOfHoles; h++) {
      holes[h].join(nest);
    }
  }

  @Override
  protected void afterWarmup() {
    indexCache.invalidateAll();
    indexCache.cleanUp();
  }

  @Override
  protected void beforeWarmup() {
    for (int p = 0; p < nrOfPigeons; p++) {
      for (int h = 0; h < nrOfHoles; h++) {
        nestBuilder.upperBound("p" + p, "h" + h);
      }

      pigeons[p] = Relation.RelationBuilder.unary("pigeon", "pId", IdDomain.ID, ffactory, indexCache).upperBound("p" + p).build();
    }

    nest = nestBuilder.build();

    for (int h = 0; h < nrOfHoles; h++) {
      holes[h] = Relation.RelationBuilder.unary("holes", "hId", IdDomain.ID, ffactory, indexCache).upperBound("h" + h).build();
    }
 }

  @Override
  protected void warmup() {
    for (int p = 0; p < nrOfPigeons; p++) {
      pigeons[p].join(nest);
    }
    for (int h = 0; h < nrOfHoles; h++) {
      holes[h].join(nest);
    }
  }

  public static void main(String... args) {
    Cache<Relation.IndexCacheKey, Relation.IndexedRows> indexCache = Caffeine.newBuilder().recordStats().build();
    JoinBenchmarkTest test = new JoinBenchmarkTest(100, 99, indexCache);
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
