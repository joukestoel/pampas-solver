package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.IdAtom;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static nl.cwi.swat.translation.data.row.RowFactory.buildTuple;
import static nl.cwi.swat.translation.data.row.RowFactory.merge;
import static org.junit.jupiter.api.Assertions.*;

class TupleFactoryTest {

  @Test
  void buildZeroArityRow() {
    assertEquals(0, buildTuple().arity());
  }

  @Test
  void buildUnaryRow() {
    assertEquals(1, buildTuple(atts(1)).arity());
  }

  @Test
  void buildBinaryRow() {
    assertEquals(2, buildTuple(atts(2)).arity());
  }

  @Test
  void buildTenaryRow() {
    assertEquals(3, buildTuple(atts(3)).arity());
  }

  @Test
  void buildFourAttributeRow() {
    assertEquals(4, buildTuple(atts(4)).arity());
  }

  @Test
  void buildFiveAttributeRow() {
    assertEquals(5, buildTuple(atts(5)).arity());
  }

  @Test
  void buildMoreTHenFiveAttributeRows() {
    for (int i = 6; i < 20; i++) {
      Tuple tuple = buildTuple(atts(i));
      assertEquals(i, tuple.arity());
      assertTrue(tuple instanceof NAttributeTuple);
    }
  }

  @Test
  void buildingPartialRowWhenFilteringEverythingLeavesTheEmptyRow() {
    Tuple tuple = RowFactory.buildPartialTuple(buildTuple(atts(1)), Collections.emptyList());
    assertEquals(0, tuple.arity());
    assertTrue(tuple instanceof EmptyTuple);
  }

  @Test
  void buildingPartialRowWithIndicesOutsideBoundsThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> RowFactory.buildPartialTuple(buildTuple(atts(1)), List.of(1)));
  }

  @Test
  void buildingPartialRowTruncatesOriginalRow() {
    Tuple tuple = RowFactory.buildPartialTuple(buildTuple(atts(2)), List.of(1));
    assertEquals(1, tuple.arity());
    assertEquals(id("1"), tuple.getAttributeAt(0));
  }

  @Test
  void mergingRowsWhenSkippingAllAttributesResultsInBaseRow() {
    Tuple base = buildTuple(atts(1));
    Tuple other = buildTuple(atts(1));

    Tuple merged = merge(base, other, List.of(0));
    assertEquals(base,merged);
  }

  @Test
  void mergingRowsWithoutSkippingAttributesResultsInARowCombiningAllAttributes() {
    Tuple base = buildTuple(atts(1));
    Tuple other = buildTuple(atts(1));

    Tuple merged = merge(base, other, Collections.emptyList());

    assertEquals(2, merged.arity());
    assertEquals(base.getAttributeAt(0), merged.getAttributeAt(0));
    assertEquals(other.getAttributeAt(0), merged.getAttributeAt(1));
  }

  @Test
  void mergingRowsWithSkippingFirstAttributeResultsInARowWithoutThatAttribute() {
    Tuple base = buildTuple(atts(1));
    Tuple other = buildTuple(atts(2));

    Tuple merged = merge(base, other, List.of(0));

    assertEquals(2, merged.arity());
    assertEquals(base.getAttributeAt(0), merged.getAttributeAt(0));
    assertEquals(other.getAttributeAt(1), merged.getAttributeAt(1));
  }

  @Test
  void mergingAnEmptyBaseRowWithoutSkippingResultsInOther() {
    Tuple base = buildTuple(atts(0));
    Tuple other = buildTuple(atts(2));

    Tuple merged = merge(base, other, Collections.emptyList());

    assertEquals(other, merged);
  }

  @Test
  void mergingRowsWithAttributesToSkipIndicesOutsideBoundsResultsInException() {
    Tuple base = buildTuple(atts(1));
    Tuple other = buildTuple(atts(1));

    assertThrows(IllegalArgumentException.class, () -> merge(base, other, List.of(1)));
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