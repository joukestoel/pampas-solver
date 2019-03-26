package nl.cwi.swat.translation.data.relation;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.relational.Id;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.MinimalReducingCircuitFactory;
import nl.cwi.swat.translation.Index;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitQuickcheck.class)
public class RelationBuilderTest {
  private FormulaFactory ff;
  private Index idx;
  private RelationFactory rf;

  @Before
  public void setup() {
    ff = new MinimalReducingCircuitFactory();
    idx = new Index(Caffeine.newBuilder().recordStats().build());

    rf = new RelationFactory(ff, idx);
  }

  @Property
  public void unaryIdRelationWithNTupleCreatedHasSameSize(@InRange(minInt = 0, maxInt = 100) int size) {
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
  public void addingSameTupleToUnaryIdRelationDoesNotIncreaseSize(@InRange(minInt = 0, maxInt = 10) int times) {
    RelationFactory.Builder.TupleBuilder tupleBuilder = rf.new Builder().create("simple")
            .add("id", Domain.ID).done();

    for (int i = 0; i < times; i++) {
      tupleBuilder.upper(new Id("sameId"));
    }

    Relation r = tupleBuilder.done();

    assertEquals(times > 0 ? 1 : 0, r.nrOfRows());
  }
}