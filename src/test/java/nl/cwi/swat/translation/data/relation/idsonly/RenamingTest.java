package nl.cwi.swat.translation.data.relation.idsonly;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import nl.cwi.swat.translation.data.relation.Relation;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RenamingTest extends IdsOnlyRelationTest {

  @Property
  void renamingDoesNotChangeNrOfRowsAndArity(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(min = 1, max = 10) int nrOfRows,
          @ForAll @IntRange(max = 9) int renameField,
          @ForAll String newName) {
    Assume.that(renameField < arity);

    Relation orig = idOnly("rel", arity, nrOfRows, true);
    Relation renamed = orig.rename(Map.of("id_" + renameField, newName));

    assertEquals(arity, renamed.arity());
    assertEquals(nrOfRows, renamed.nrOfRows());
    assertEquals(newName, renamed.getHeading().getAttributeNameAt(renameField));
  }

  @Property
  void renamingOnlyChangesFieldNameOfRenamedFields(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 9) int renameField,
          @ForAll String newName) {
    Assume.that(renameField < arity);

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
  void renamingWithSameNameResultsInTheSameRelation(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 9) int renameField) {
    Assume.that(renameField < arity);

    Relation orig = idOnly("rel", arity, 10, true);

    String renaming = "id_" + renameField;
    assertEquals(orig, orig.rename(Map.of(renaming, renaming)));
  }

  @Property
  void renamingWithDifferentNameResultsInDifferentRelation(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(max = 9) int renameField) {
    Assume.that(renameField < arity);

    Relation orig = idOnly("rel", arity, 10, true);

    String renaming = "id_" + renameField;
    String newName = renaming + "_new";
    assertNotEquals(orig, orig.rename(Map.of(renaming, newName)));
  }

  @Test
  void renamingANonExistingFieldThrowsAnException() {
    Relation orig = idOnly("rel", 1, 1, true);
    assertThrows(IllegalArgumentException.class, () -> orig.rename(Map.of("non-existing", "will-not-rename")));
  }

}
