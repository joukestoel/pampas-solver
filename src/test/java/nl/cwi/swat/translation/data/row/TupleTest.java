package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.IdAtom;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleTest {

  @Test
  void zeroAttributesRowHasZeroLength() {
    assertEquals(0, create(0).arity());
  }

  @Test
  void zeroAttributesRowCanBeIterated() {
    checkIterable(create(0), 0);
  }

  @Test
  void cantGetAttributesOfZeroAttributesRow() {
    assertThrows(IllegalArgumentException.class, () -> create(0).getAttributeAt(0));
  }

  @Test
  void twoZeroRowsMustBeEqual() {
    assertEquals(create(0),create(0));
  }

  @Test
  void oneAttributeRowIsOfSizeOne() {
    assertEquals(1, create(1).arity());
  }

  @Test
  void oneAttributeRowCanBeIterated() {
    checkIterable(create(1),1);
  }

  @Test
  void canGetFirstAttributeOfAOneAttributeRow() {
    assertEquals(id("0"), create(1).getAttributeAt(0));
  }

  @Test
  void noSecondAttributeInAOneAttributeRow() {
    assertThrows(IllegalArgumentException.class, () -> create(1).getAttributeAt(1));
  }

  @Test
  void twoOneAttributeRowsContainingSameDataAreEqual() {
    assertEquals(create(1),create(1));
  }

  @Test
  void twoOneAttributeRowsContainingDifferentDataAreNotEqual() {
    assertNotEquals(create(1),create(id("a'")));
  }

  @Test
  void rowsOfDifferentSizesAreNotEqual() {
    for (int i = 0; i < 6; i++) {
      assertNotEquals(create(i), create(i + 1));
    }
  }

  @Test
  void twoAttributeRowIsOfSizeTwo() {
    assertEquals(2, create(2).arity());
  }

  @Test
  void twoAttributeRowContainsTwoAttributes() {
    checkIterable(create(2),2);
  }

  @Test
  void twoAttributesRowKeepsOrder() {
    checkOrder(create(2));
  }

  @Test
  void noMoreAttributesInTwoAttributesRow() {
    assertThrows(IllegalArgumentException.class, () -> create(2).getAttributeAt(2));
  }

  @Test
  void threeAttributeRowIsOfSizeThree() {
    assertEquals(3, create(3).arity());
  }

  @Test
  void threeAttributeRowContainsThreeAttributes() {
    checkIterable(create(3),3);
  }

  @Test
  void threeAttributesRowKeepsOrder() {
    checkOrder(create(3));
  }

  @Test
  void noMoreAttributesInThreeAttributesRow() {
    assertThrows(IllegalArgumentException.class, () -> create(3).getAttributeAt(3));
  }

  @Test
  void fourAttributeRowIsOfSizeThree() {
    assertEquals(4, create(4).arity());
  }

  @Test
  void fourAttributeRowContainsThreeAttributes() {
    checkIterable(create(4),4);
  }

  @Test
  void fourAttributesRowKeepsOrder() {
    checkOrder(create(4));
  }

  @Test
  void noMoreAttributesInFourAttributesRow() {
    assertThrows(IllegalArgumentException.class, () -> create(4).getAttributeAt(4));
  }

  @Test
  void fiveAttributeRowIsOfSizeThree() {
    assertEquals(5, create(5).arity());
  }

  @Test
  void fiveAttributeRowContainsThreeAttributes() {
    checkIterable(create(5),5);
  }

  @Test
  void fiveAttributesRowKeepsOrder() {
    checkOrder(create(5));
  }

  @Test
  void noMoreAttributesInFiveAttributesRow() {
    assertThrows(IllegalArgumentException.class, () -> create(5).getAttributeAt(5));
  }

  @Test
  void twentyAttributeRowIsOfSizeTwenty() {
    assertEquals(20, create(20).arity());
  }

  @Test
  void twentyAttributeRowContainsTwentyAttributes() {
    checkIterable(create(20),20);
  }

  @Test
  void twentyAttributesRowKeepsOrder() {
    checkOrder(create(20));
  }

  @Test
  void noMoreAttributesInTwentyAttributesRow() {
    assertThrows(IllegalArgumentException.class, () -> create(20).getAttributeAt(20));
  }

  //// Helper methods below

  private Expression id(String val) {
    return new IdAtom(val);
  }

  private Tuple create(int size) {
    Expression[] atts = new Expression[size];
    for (int i = 0; i < size; i++) {
      atts[i] = id("" + i);
    }

    return create(atts);
  }

  private Tuple create(Expression... atts) {
    switch (atts.length) {
      case 0: return EmptyTuple.EMPTY;
      case 1: return new UnaryTuple(atts[0]);
      case 2: return new BinaryTuple(atts[0],atts[1]);
      case 3: return new TernaryTuple(atts[0],atts[1],atts[2]);
      case 4: return new FourAttributesTuple(atts[0],atts[1],atts[2],atts[3]);
      case 5: return new FiveAttributesTuple(atts[0],atts[1],atts[2],atts[3],atts[4]);
      default:return new NAttributeTuple(atts);
    }
  }

  private void checkIterable(Tuple tuple, int expectedCount) {
    int count = 0;
    for (Expression ignored : tuple) {
      count++;
    }

    assertEquals(expectedCount,count);
  }

  private void checkOrder(Tuple tuple) {
    for (int i = 0; i < tuple.arity(); i++) {
      assertEquals(id("" + i), tuple.getAttributeAt(i));
    }
  }

}