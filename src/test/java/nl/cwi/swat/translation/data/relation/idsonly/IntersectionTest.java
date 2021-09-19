package nl.cwi.swat.translation.data.relation.idsonly;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import nl.cwi.swat.translation.data.relation.Relation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntersectionTest extends IdsOnlyRelationTest {
  @Property
  void intersectionIdentity(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 100) int nrOfRows) {
    Relation a = idOnly("A", arity, nrOfRows, false);

    assertEquals(a, a.intersect(a));
  }

  @Property
  void intersectionIsCommutative(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 100) int nrOfRows1,
          @ForAll @IntRange(max = 100) int nrOfRows2) {
    Relation a = idOnly("A", arity, nrOfRows1, true);
    Relation b = idOnly("B", arity, nrOfRows2, true);

    assertEquals(a.intersect(b), b.intersect(a));
  }

  @Property
  void intersectionIsAssociative(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 100) int nrOfRows1,
          @ForAll @IntRange(max = 100) int nrOfRows2,
          @ForAll @IntRange(max = 100) int nrOfRows3) {
    Relation a = idOnly("A", arity, nrOfRows1, false);
    Relation b = idOnly("B", arity, nrOfRows2, true);
    Relation c = idOnly("C", arity, nrOfRows3, true);

    assertEquals((a.intersect(b)).intersect(c), a.intersect(b.intersect(c)));
  }

  @Property
  void intersectionOfTwoRelationsWithoutOverlapResultsInTheEmptyRelation(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 100) int nrOfRows1,
          @ForAll @IntRange(max = 100) int nrOfRows2) {
    Relation a = idOnly("A", arity, nrOfRows1, false, "a");
    Relation b = idOnly("B", arity, nrOfRows2, false, "b");

    assertTrue(a.intersect(b).isEmpty());
  }

  @Property
  void intersectionWithSomeOverlapResultsInARelationWithRows(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 100) int nrOfRows1,
          @ForAll @IntRange(max = 100) int nrOfRows2) {
    Relation a = idOnly("A", arity, nrOfRows1, true);
    Relation b = idOnly("B", arity, nrOfRows2, true);

    assertEquals(a.intersect(b).nrOfRows(), Math.min(nrOfRows1, nrOfRows2));
  }

}

