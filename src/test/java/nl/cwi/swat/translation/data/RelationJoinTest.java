package nl.cwi.swat.translation.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import nl.cwi.swat.ast.IdDomain;
import nl.cwi.swat.smtlogic.FormulaFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RelationJoinTest {
  private FormulaFactory ffactory;
  private Cache<Relation.IndexCacheKey, Relation.IndexedRows> indexCache;

  @Before
  public void setup() {
    this.ffactory = new FormulaFactory();
    this.indexCache = Caffeine.newBuilder().build();
  }

  @Test
  public void joinShouldHaveThreeNonOptionalRows() {
    Relation pigeon = Relation.RelationBuilder.unary("pigeon", "pId", IdDomain.ID, ffactory, indexCache)
            .upperBound("p1").build();

    Relation nest = Relation.RelationBuilder.binary("nest", "pId", IdDomain.ID, "hId", IdDomain.ID, ffactory, indexCache)
            .lowerBound("p1","h1").lowerBound("p1","h2").lowerBound("p1","h3")
            .lowerBound("p2","h1").lowerBound("p2","h2").lowerBound("p2","h3")
            .lowerBound("p3","h1").lowerBound("p3","h2").lowerBound("p3","h3").build();

    Relation result = pigeon.join(nest);

    assertEquals(3, result.size());
  }
}