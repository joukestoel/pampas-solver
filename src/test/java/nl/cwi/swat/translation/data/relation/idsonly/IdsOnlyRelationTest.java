package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Caffeine;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.lifecycle.BeforeProperty;
import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.relational.Id;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.MinimalReducingCircuitFactory;
import nl.cwi.swat.translation.Index;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdsOnlyRelationTest {
  protected RelationFactory rf;

  @BeforeProperty
  @BeforeEach
  void setup() {
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
  void relationsWithSameHeadingAndRowsAreEqual(
          @ForAll @IntRange(min = 1, max = 10) int arity,
          @ForAll @IntRange(min = 0, max = 100) int nrOfRows) {
    Relation first  = idOnly("rel", arity, nrOfRows, false);
    Relation second = idOnly("rel", arity, nrOfRows, false);

    assertEquals(first,second);
  }
}