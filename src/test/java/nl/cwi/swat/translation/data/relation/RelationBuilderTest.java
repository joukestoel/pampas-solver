package nl.cwi.swat.translation.data.relation;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelationBuilderTest {
  private RelationFactory rf;

  @BeforeProperty
  void setup() {
    FormulaFactory ff = new MinimalReducingCircuitFactory();
    Index idx = new Index(Caffeine.newBuilder().recordStats().build());

    rf = new RelationFactory(ff, idx);
  }

  @Property
  void unaryIdRelationWithNTupleCreatedHasSameSize(
          @ForAll  @IntRange(max = 100) int size) {
    RelationFactory.Builder.TupleBuilder tupleBuilder = rf.new Builder().create("simple")
            .add("id", Domain.ID).done();

    for (int i = 0; i < size; i++) {
      tupleBuilder.upper(new Id("id_" + i));
    }

    Relation r = tupleBuilder.done();

    assertEquals(size, r.nrOfRows());
    assertEquals(1, r.arity());
  }

  @Property
  void addingSameTupleToUnaryIdRelationDoesNotIncreaseSize(
          @ForAll @IntRange(max = 10) int times) {
    RelationFactory.Builder.TupleBuilder tupleBuilder = rf.new Builder().create("simple")
            .add("id", Domain.ID).done();

    for (int i = 0; i < times; i++) {
      tupleBuilder.upper(new Id("sameId"));
    }

    Relation r = tupleBuilder.done();

    assertEquals(times > 0 ? 1 : 0, r.nrOfRows());
  }
}