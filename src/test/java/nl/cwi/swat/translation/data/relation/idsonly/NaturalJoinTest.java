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
          @ForAll @IntRange(max = 100) int nrOfRows) {
    Relation orig = idOnly("rel", arity, nrOfRows, true);

    assertEquals(orig, orig.naturalJoin(orig));
  }


}
