package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.relational.Id;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.MinimalReducingCircuitFactory;
import nl.cwi.swat.translation.Index;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitQuickcheck.class)
public class IdsOnlyRelationTest {
  protected RelationFactory rf;

  @Before
  @BeforeEach
  public void setup() {
    FormulaFactory ff = new MinimalReducingCircuitFactory();
    Index idx = new Index(Caffeine.newBuilder().recordStats().build());

    rf = new RelationFactory(ff, idx);
  }

  protected Relation idOnly(String relName, int arity, int nrOfRows, boolean asUpperBound) {
    return idOnly(relName, arity, nrOfRows, asUpperBound, "");
  }

  protected Relation idOnly(String relName, int arity, int nrOfRows, boolean asUpperBound, String prefix) {
    RelationFactory.Builder.HeaderBuilder headerBuilder = rf.new Builder().create(relName);

    for (int i = 0; i < arity; i++) {
      headerBuilder.add("id_" + i, Domain.ID);
    }

    RelationFactory.Builder.TupleBuilder tupleBuilder = headerBuilder.done();

    for (int i = 0; i < nrOfRows; i++) {
      Id[] lits = new Id[arity];
      for (int j = 0; j < arity; j++) {
        lits[j] = new Id(((!prefix.equals("")) ? prefix + "_" : "") + "idVal_" + i + "_" + j);
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
  public void relationsWithSameHeadingAndRowsAreEqual(@InRange(minInt = 1, maxInt = 10) int arity, @InRange(minInt = 0, maxInt = 100) int nrOfRows) {
    Relation first  = idOnly("rel", arity, nrOfRows, false);
    Relation second = idOnly("rel", arity, nrOfRows, false);

    assertEquals(first,second);
  }
}