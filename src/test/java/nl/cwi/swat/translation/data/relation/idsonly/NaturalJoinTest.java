package nl.cwi.swat.translation.data.relation.idsonly;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.translation.data.relation.Relation;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitQuickcheck.class)
public class NaturalJoinTest extends IdsOnlyRelationTest {

  @Property
  public void naturalJoinIdentity(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 100) int nrOfRows) {
    Relation orig = idOnly("rel", arity, nrOfRows, true);

    assertEquals(orig, orig.naturalJoin(orig));
  }


}
