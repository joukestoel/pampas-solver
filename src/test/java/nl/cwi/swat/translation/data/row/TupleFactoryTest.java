package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.IdAtom;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static nl.cwi.swat.translation.data.row.TupleFactory.buildTuple;
import static nl.cwi.swat.translation.data.row.TupleFactory.merge;
import static org.junit.jupiter.api.Assertions.*;

class TupleFactoryTest {

  @Test
  void emptyTuple() {
    assertEquals(0, buildTuple().arity());
  }

  @Test
  void buildUnaryTuple() {
    assertEquals(1, buildTuple(atts(1)).arity());
  }

  @Test
  void buildBinaryTuple() {
    assertEquals(2, buildTuple(atts(2)).arity());
  }

  @Test
  void buildTernaryTuple() {
    assertEquals(3, buildTuple(atts(3)).arity());
  }

  @Test
  void buildFourAttributesTuple() {
    assertEquals(4, buildTuple(atts(4)).arity());
  }

  @Test
  void buildFiveAttributesTuple() {
    assertEquals(5, buildTuple(atts(5)).arity());
  }

  @Test
  void buildMoreTHenFiveAttributesTuples() {
    for (int i = 6; i < 20; i++) {
      Tuple tuple = buildTuple(atts(i));
      assertEquals(i, tuple.arity());
      assertTrue(tuple instanceof NaryTuple);
    }
  }

  @Test
  void partialTupleWhenFilteringEverythingLeavesTheEmptyTuple() {
    Tuple tuple = TupleFactory.buildPartialTuple(buildTuple(atts(1)), Collections.emptySet());
    assertEquals(0, tuple.arity());
    assertTrue(tuple instanceof EmptyTuple);
  }

  @Test
  void partialTupleWithIndicesOutsideBoundsThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> TupleFactory.buildPartialTuple(buildTuple(atts(1)), Set.of(1)));
  }

  @Test
  void partialTupleTruncatesOriginalTuple() {
    Tuple tuple = TupleFactory.buildPartialTuple(buildTuple(atts(2)), Set.of(1));
    assertEquals(1, tuple.arity());
    assertEquals(id("1"), tuple.getAttributeAt(0));
  }

  @Test
  void mergingTupleWhenSkippingAllAttributesResultsInBaseTuple() {
    Tuple base = buildTuple(atts(1));
    Tuple other = buildTuple(atts(1));

    Tuple merged = merge(base, other, Set.of(0));
    assertEquals(base,merged);
  }

  @Test
  void mergingTuplesWithoutSkippingAttributesResultsInATupleCombiningAllAttributes() {
    Tuple base = buildTuple(atts(1));
    Tuple other = buildTuple(atts(1));

    Tuple merged = merge(base, other, Collections.emptySet());

    assertEquals(2, merged.arity());
    assertEquals(base.getAttributeAt(0), merged.getAttributeAt(0));
    assertEquals(other.getAttributeAt(0), merged.getAttributeAt(1));
  }

  @Test
  void mergingTuplesWithSkippingFirstAttributeResultsInATupleWithoutThatAttribute() {
    Tuple base = buildTuple(atts(1));
    Tuple other = buildTuple(atts(2));

    Tuple merged = merge(base, other, Set.of(0));

    assertEquals(2, merged.arity());
    assertEquals(base.getAttributeAt(0), merged.getAttributeAt(0));
    assertEquals(other.getAttributeAt(1), merged.getAttributeAt(1));
  }

  @Test
  void mergingAnEmptyBaseTupleWithoutSkippingResultsInTheOtherTuple() {
    Tuple base = buildTuple(atts(0));
    Tuple other = buildTuple(atts(2));

    Tuple merged = merge(base, other, Collections.emptySet());

    assertEquals(other, merged);
  }

  @Test
  void mergingTuplesWithAttributesToSkipIndicesOutsideBoundsResultsInException() {
    Tuple base = buildTuple(atts(1));
    Tuple other = buildTuple(atts(1));

    assertThrows(IllegalArgumentException.class, () -> merge(base, other, Set.of(1)));
  }

  private Expression[] atts(int nr) {
    Expression[] exprs = new Expression[nr];
    for (int i = 0; i < nr; i++) {
      exprs[i] = id("" + i);
    }
    return exprs;
  }

  private Expression id(String val) {
    return new IdAtom(val);
  }
}