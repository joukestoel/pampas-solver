package nl.cwi.swat.translation.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import nl.cwi.swat.ast.relational.Hole;
import nl.cwi.swat.ast.relational.IdDomain;
import nl.cwi.swat.ast.ints.IntDomain;
import nl.cwi.swat.smtlogic.BooleanConstant;
import nl.cwi.swat.smtlogic.BooleanVariable;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.smtlogic.SimplificationFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RelationBuilderTest {
  private FormulaFactory ffactory;
  private Cache<Relation.IndexCacheKey, Relation.IndexedRows> indexCache;

  @Before
  public void setup() {
    this.ffactory = new FormulaFactory(new SimplificationFactory(3, Caffeine.newBuilder().build()));
    this.indexCache = Caffeine.newBuilder().build();
  }

  @Test
  public void buildUnaryIdRelation() {
    Relation rel = Relation.RelationBuilder.unary("Unary", "id", IdDomain.ID, this.ffactory, this.indexCache).lowerBound("id1").build();

    assertEquals(1, rel.size());
  }

  @Test
  public void buildUnaryOptionalRelation() {
    Relation rel = Relation.RelationBuilder.unary("Unary", "id", IdDomain.ID, this.ffactory, this.indexCache).upperBound("id1").build();

    assertEquals(1, rel.size());
    assertTrue(rel.getFormula(rel.getRows().iterator().next()) instanceof BooleanVariable);
  }

  @Test
  public void lowerBoundTrumpsUpperBound() {
    Relation relation = Relation.RelationBuilder.unary("Unary", "id", IdDomain.ID, this.ffactory, this.indexCache).lowerBound("id1").upperBound("id1").build();

    assertEquals(1, relation.size());
    assertEquals(BooleanConstant.TRUE, relation.getFormula(relation.getRows().iterator().next()));
  }

  @Test
  public void lowerBoundTrumpsUpperBound_evenWhenTurnedAround() {
    Relation relation = Relation.RelationBuilder.unary("Unary", "id", IdDomain.ID, this.ffactory, this.indexCache).upperBound("id1").lowerBound("id1").build();

    assertEquals(1, relation.size());
    assertEquals(BooleanConstant.TRUE, relation.getFormula(relation.getRows().iterator().next()));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void relationsWithHolesAreNotYetSupported() {
    Relation relation = Relation.RelationBuilder.unary("Unary", "intCol", IntDomain.INT, this.ffactory, this.indexCache).lowerBound(Hole.HOLE).build();
  }

  @Test
  public void buildBinaryMultiDomainRelation() {
    Relation relation = Relation.RelationBuilder.binary("Binary", "id", IdDomain.ID, "int", IntDomain.INT, ffactory, indexCache)
            .lowerBound("id1",1).lowerBound("id2",2).build();

    assertEquals(2, relation.size());
  }

}