package nl.cwi.swat.translation.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import nl.cwi.swat.ast.relational.IdDomain;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.smtlogic.SimplificationFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RelationProductTest {
  private FormulaFactory ffactory;
  private Cache<Relation.IndexCacheKey, Relation.IndexedRows> indexCache;

  @Before
  public void setup() {
    this.ffactory = new FormulaFactory(new SimplificationFactory(3, Caffeine.newBuilder().build()));
    this.indexCache = Caffeine.newBuilder().recordStats().build();
  }

  @Test
  public void simpleTest() {
    Relation.RelationBuilder pigeonB = Relation.RelationBuilder.unary("pigeon", "pId", IdDomain.ID, ffactory, indexCache);
    Relation.RelationBuilder holeB = Relation.RelationBuilder.unary("hole", "hId", IdDomain.ID, ffactory, indexCache);

    for (int i = 1; i <= 20; i++) {
      pigeonB.lowerBound("p" + i);
      holeB.lowerBound("h" + i);
    }

    Relation pigeon = pigeonB.build();
    Relation hole = holeB.build();

    Relation nest = pigeon.product(hole);

    assertEquals(400, nest.size());
    System.out.println(nest);
  }
}