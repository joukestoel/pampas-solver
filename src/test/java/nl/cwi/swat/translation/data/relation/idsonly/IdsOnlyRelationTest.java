package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.relational.Id;
import nl.cwi.swat.ast.relational.Literal;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.MinimalReducingCircuitFactory;
import nl.cwi.swat.translation.Index;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assume.assumeThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitQuickcheck.class)
public class IdsOnlyRelationTest {
  private FormulaFactory ff;
  private Index idx;
  private RelationFactory rf;

  @Before
  @BeforeEach
  public void setup() {
    ff = new MinimalReducingCircuitFactory();
    idx = new Index(Caffeine.newBuilder().recordStats().build());

    rf = new RelationFactory(ff, idx);
  }

  private Relation idOnly(String relName, int arity, int nrOfRows, boolean asUpperBound) {
    RelationFactory.Builder.HeaderBuilder headerBuilder = rf.new Builder().create(relName);

    for (int i = 0; i < arity; i++) {
      headerBuilder.add("id_" + i, Domain.ID);
    }

    RelationFactory.Builder.TupleBuilder tupleBuilder = headerBuilder.done();

    for (int i = 0; i < nrOfRows; i++) {
      Literal[] lits = new Literal[arity];
      for (int j = 0; j < arity; j++) {
        lits[j] = new Id("idVal_" + i + "_" + j);
      }

      if (asUpperBound) {
        tupleBuilder.upper(lits);
      } else {
        tupleBuilder.lower(lits);
      }
    }

    return tupleBuilder.done();
  }

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

  @Test
  public void renamingANonExistingFieldThrowsAnException() {
    Relation orig = idOnly("rel", 1, 1, true);
    assertThrows(IllegalArgumentException.class, () -> orig.rename(Map.of("non-existing", "will-not-rename")));
  }

  @Property
  public void relationsWithSameHeadingAndRowsAreEqual(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 100) int nrOfRows) {
    Relation first  = idOnly("rel", arity, nrOfRows, false);
    Relation second = idOnly("rel", arity, nrOfRows, false);

    assertEquals(first,second);
  }

  @Property
  public void naturalJoiningARelationWithItselfGivesTheSameRelation(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 100) int nrOfRows) {
    Relation orig = idOnly("rel", arity, nrOfRows, true);

    assertEquals(orig, orig.naturalJoin(orig));
  }

}