package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.MinimalReducingCircuitFactory;
import nl.cwi.swat.translation.Index;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(JUnitQuickcheck.class)
public class IntersectionTest extends IdsOnlyRelationTest {
  @Property
  public void intersectionIdentity(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 100) int nrOfRows) {
    Relation a = idOnly("A", arity, nrOfRows, false);

    assertEquals(a, a.intersect(a));
  }

  @Property
  public void intersectionIsCommutative(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 100) int nrOfRows1, @InRange(minInt = 0, maxInt = 100) int nrOfRows2) {
    Relation a = idOnly("A", arity, nrOfRows1, true);
    Relation b = idOnly("B", arity, nrOfRows2, true);

    assertEquals(a.intersect(b), b.intersect(a));
  }

  @Property
  public void intersectionIsAssociative(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 100) int nrOfRows1, @InRange(minInt = 0, maxInt = 100) int nrOfRows2, @InRange(minInt = 0, maxInt = 100) int nrOfRows3) {
    Relation a = idOnly("A", arity, nrOfRows1, false);
    Relation b = idOnly("B", arity, nrOfRows2, true);
    Relation c = idOnly("C", arity, nrOfRows3, true);

    assertEquals((a.intersect(b)).intersect(c), a.intersect(b.intersect(c)));
  }

  @Property
  public void intersectionOfTwoRelationsWithoutOverlapResultsInTheEmptyRelation(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 100) int nrOfRows1, @InRange(minInt = 0, maxInt = 100) int nrOfRows2) {
    Relation a = idOnly("A", arity, nrOfRows1, false, "a");
    Relation b = idOnly("B", arity, nrOfRows2, false, "b");

    assertTrue(a.intersect(b).isEmpty());
  }

  @Property
  public void intersectionWithSomeOverlapResultsInARelationWithRows(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 100) int nrOfRows1, @InRange(minInt = 0, maxInt = 100) int nrOfRows2) {
    Relation a = idOnly("A", arity, nrOfRows1, true);
    Relation b = idOnly("B", arity, nrOfRows2, true);

    assertEquals(a.intersect(b).nrOfRows(), Math.min(nrOfRows1, nrOfRows2));
  }

}
