package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.IdAtom;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleTest {

  @Test
  void zeroAttributesTupleHasZeroLength() {
    assertEquals(0, create(0).arity());
  }

  @Test
  void zeroAttributesTupleCanBeIterated() {
    checkIterable(create(0), 0);
  }

  @Test
  void cantGetAttributesOfZeroAttributesTuple() {
    assertThrows(IllegalArgumentException.class, () -> create(0).getAttributeAt(0));
  }

  @Test
  void twoZeroTuplesMustBeEqual() {
    assertEquals(create(0),create(0));
  }

  @Test
  void unaryTupleIsOfSizeOne() {
    assertEquals(1, create(1).arity());
  }

  @Test
  void unaryTupleCanBeIterated() {
    checkIterable(create(1),1);
  }

  @Test
  void canGetFirstAttributeOfAUnaryTuple() {
    assertEquals(id("0"), create(1).getAttributeAt(0));
  }

  @Test
  void noSecondAttributeInAUnaryTuple() {
    assertThrows(IllegalArgumentException.class, () -> create(1).getAttributeAt(1));
  }

  @Test
  void twoUnaryTuplesContainingSameDataAreEqual() {
    assertEquals(create(1),create(1));
  }

  @Test
  void twoUnaryTuplesContainingDifferentDataAreNotEqual() {
    assertNotEquals(create(1),create(id("a'")));
  }

  @Test
  void tuplessOfDifferentSizesAreNotEqual() {
    for (int i = 0; i < 6; i++) {
      assertNotEquals(create(i), create(i + 1));
    }
  }

  @Test
  void binaryTupleIsOfSizeTwo() {
    assertEquals(2, create(2).arity());
  }

  @Test
  void binaryTupleContainsTwoAttributes() {
    checkIterable(create(2),2);
  }

  @Test
  void binaryTupleIsOrdered() {
    checkOrder(create(2));
  }

  @Test
  void noMoreAttributesInBinaryTuple() {
    assertThrows(IllegalArgumentException.class, () -> create(2).getAttributeAt(2));
  }

  @Test
  void ternaryTupleIsOfSizeThree() {
    assertEquals(3, create(3).arity());
  }

  @Test
  void ternaryTupleContainsThreeAttributes() {
    checkIterable(create(3),3);
  }

  @Test
  void ternaryTupleIsOrdered() {
    checkOrder(create(3));
  }

  @Test
  void noMoreAttributesInTernaryTuple() {
    assertThrows(IllegalArgumentException.class, () -> create(3).getAttributeAt(3));
  }

  @Test
  void fourAttributeTupleIsOfSizeFour() {
    assertEquals(4, create(4).arity());
  }

  @Test
  void fourAttributeTupleContainsFourAttributes() {
    checkIterable(create(4),4);
  }

  @Test
  void fourAttributeTupleeIsOrdered() {
    checkOrder(create(4));
  }

  @Test
  void noMoreAttributesInFourAttributesTuple() {
    assertThrows(IllegalArgumentException.class, () -> create(4).getAttributeAt(4));
  }

  @Test
  void fiveAttributeTupleIsOfSizeFive() {
    assertEquals(5, create(5).arity());
  }

  @Test
  void fiveAttributeTupleContainsFiveAttributes() {
    checkIterable(create(5),5);
  }

  @Test
  void fiveAttributesTupleIsOrdered() {
    checkOrder(create(5));
  }

  @Test
  void noMoreAttributesInFiveAttributesTuple() {
    assertThrows(IllegalArgumentException.class, () -> create(5).getAttributeAt(5));
  }

  @Test
  void naryTupleCanBeOfSizeTwenty() {
    assertEquals(20, create(20).arity());
  }

  @Test
  void naryTupleCanContainTwentyAttributes() {
    checkIterable(create(20),20);
  }

  @Test
  void naryTupleIsOrdered() {
    checkOrder(create(20));
  }

  @Test
  void noMoreAttributesInNaryTuple() {
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
      default:return new NaryTuple(atts);
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