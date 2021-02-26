package nl.cwi.swat.translation.data.relation.idsonly;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.translation.data.relation.Relation;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assume.assumeThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitQuickcheck.class)
public class RenamingTest extends IdsOnlyRelationTest {
  @Property
  public void renamingDoesNotChangeNrOfRowsAndArity(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 1, maxInt = 10) int nrOfRows, @InRange(minInt = 0, maxInt = 9) int renameField, String newName) {
    assumeThat(renameField, lessThan(arity));

    Relation orig = idOnly("rel", arity, nrOfRows, true);
    Relation renamed = orig.rename(Map.of("id_" + renameField, newName));

    assertEquals(arity, renamed.arity());
    assertEquals(nrOfRows, renamed.nrOfRows());
    assertEquals(newName, renamed.getHeading().getAttributeNameAt(renameField));
  }

  @Property
  public void renamingOnlyChangesFieldNameOfRenamedFields(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 9) int renameField, String newName) {
    assumeThat(renameField, lessThan(arity));

    Relation orig = idOnly("rel", arity, 10, true);
    Relation renamed = orig.rename(Map.of("id_" + renameField, newName));

    for (int i = 0; i < arity; i++) {
      if (i == renameField) {
        assertEquals(newName, renamed.getHeading().getAttributeNameAt(renameField));
      } else {
        assertEquals("id_" + i, renamed.getHeading().getAttributeNameAt(i));
      }
    }
  }

  @Property
  public void renamingWithSameNameResultsInTheSameRelation(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 9) int renameField) {
    assumeThat(renameField, lessThan(arity));

    Relation orig = idOnly("rel", arity, 10, true);

    String renaming = "id_" + renameField;
    assertEquals(orig, orig.rename(Map.of(renaming, renaming)));
  }

  @Property
  public void renamingWithDifferentNameResultsInDifferentRelation(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 9) int renameField) {
    assumeThat(renameField, lessThan(arity));

    Relation orig = idOnly("rel", arity, 10, true);

    String renaming = "id_" + renameField;
    String newName = renaming + "_new";
    assertNotEquals(orig, orig.rename(Map.of(renaming, newName)));
  }

  @Test
  public void renamingANonExistingFieldThrowsAnException() {
    Relation orig = idOnly("rel", 1, 1, true);
    assertThrows(IllegalArgumentException.class, () -> orig.rename(Map.of("non-existing", "will-not-rename")));
  }

}
