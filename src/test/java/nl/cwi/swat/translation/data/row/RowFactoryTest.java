package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.IdAtom;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static nl.cwi.swat.translation.data.row.RowFactory.buildRow;
import static nl.cwi.swat.translation.data.row.RowFactory.merge;
import static org.junit.jupiter.api.Assertions.*;

class RowFactoryTest {

  @Test
  void buildZeroArityRow() {
    assertEquals(0, buildRow().arity());
  }

  @Test
  void buildUnaryRow() {
    assertEquals(1, buildRow(atts(1)).arity());
  }

  @Test
  void buildBinaryRow() {
    assertEquals(2, buildRow(atts(2)).arity());
  }

  @Test
  void buildTenaryRow() {
    assertEquals(3, buildRow(atts(3)).arity());
  }

  @Test
  void buildFourAttributeRow() {
    assertEquals(4, buildRow(atts(4)).arity());
  }

  @Test
  void buildFiveAttributeRow() {
    assertEquals(5, buildRow(atts(5)).arity());
  }

  @Test
  void buildMoreTHenFiveAttributeRows() {
    for (int i = 6; i < 20; i++) {
      Row row = buildRow(atts(i));
      assertEquals(i, row.arity());
      assertTrue(row instanceof NAttributeRow);
    }
  }

  @Test
  void buildingPartialRowWhenFilteringEverythingLeavesTheEmptyRow() {
    Row row = RowFactory.buildPartialRow(buildRow(atts(1)), Collections.emptyList());
    assertEquals(0, row.arity());
    assertTrue(row instanceof EmptyRow);
  }

  @Test
  void buildingPartialRowWithIndicesOutsideBoundsThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> RowFactory.buildPartialRow(buildRow(atts(1)), List.of(1)));
  }

  @Test
  void buildingPartialRowTruncatesOriginalRow() {
    Row row = RowFactory.buildPartialRow(buildRow(atts(2)), List.of(1));
    assertEquals(1, row.arity());
    assertEquals(id("1"), row.getAttributeAt(0));
  }

  @Test
  void mergingRowsWhenSkippingAllAttributesResultsInBaseRow() {
    Row base = buildRow(atts(1));
    Row other = buildRow(atts(1));

    Row merged = merge(base, other, List.of(0));
    assertEquals(base,merged);
  }

  @Test
  void mergingRowsWithoutSkippingAttributesResultsInARowCombiningAllAttributes() {
    Row base = buildRow(atts(1));
    Row other = buildRow(atts(1));

    Row merged = merge(base, other, Collections.emptyList());

    assertEquals(2, merged.arity());
    assertEquals(base.getAttributeAt(0), merged.getAttributeAt(0));
    assertEquals(other.getAttributeAt(0), merged.getAttributeAt(1));
  }

  @Test
  void mergingRowsWithSkippingFirstAttributeResultsInARowWithoutThatAttribute() {
    Row base = buildRow(atts(1));
    Row other = buildRow(atts(2));

    Row merged = merge(base, other, List.of(0));

    assertEquals(2, merged.arity());
    assertEquals(base.getAttributeAt(0), merged.getAttributeAt(0));
    assertEquals(other.getAttributeAt(1), merged.getAttributeAt(1));
  }

  @Test
  void mergingAnEmptyBaseRowWithoutSkippingResultsInOther() {
    Row base = buildRow(atts(0));
    Row other = buildRow(atts(2));

    Row merged = merge(base, other, Collections.emptyList());

    assertEquals(other, merged);
  }

  @Test
  void mergingRowsWithAttributesToSkipIndicesOutsideBoundsResultsInException() {
    Row base = buildRow(atts(1));
    Row other = buildRow(atts(1));

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