package nl.cwi.swat.translation.data.relation.idsonly;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import nl.cwi.swat.translation.data.relation.Relation;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NaturalJoinTest extends IdsOnlyRelationTest {

  @Property
  void naturalJoinIdentity(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 1000) int nrOfRows) {
    Relation orig = idOnly("rel", arity, nrOfRows, true);

    assertEquals(orig, orig.naturalJoin(orig));
  }

  @Property
  void naturalJoinIsCommutative(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 1000) int nrOfRowsRelA,
          @ForAll @IntRange(max = 1000) int nrOfRowsRelB) {
    Relation a = idOnly("rel", arity, nrOfRowsRelA, true, "a");
    Relation b = idOnly("rel", arity, nrOfRowsRelB, true, "b");

    assertEquals(a.naturalJoin(b), b.naturalJoin(a));
  }

  @Property
  void naturalJoinIsAssociative(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 1000) int nrOfRowsRelA,
          @ForAll @IntRange(max = 1000) int nrOfRowsRelB,
          @ForAll @IntRange(max = 1000) int nrOfRowsRelC) {
    Relation a = idOnly("rel", arity, nrOfRowsRelA, true, "a");
    Relation b = idOnly("rel", arity, nrOfRowsRelB, true, "b");
    Relation c = idOnly("rel", arity, nrOfRowsRelB, true, "c");

    assertEquals(a.naturalJoin(b.naturalJoin(c)), (a.naturalJoin(b)).naturalJoin(c));
  }

}
