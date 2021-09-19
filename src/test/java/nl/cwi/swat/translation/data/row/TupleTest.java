package nl.cwi.swat.translation.data.row;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.rel.IdConstant;

import static org.junit.jupiter.api.Assertions.*;

class TupleTest {

  @Property
  void twoTuplesWithSameAttributesAreEqual(
          @ForAll @IntRange(max = 50) int arity) {
    assertEquals(create(arity), create(arity));
  }

  @Property
  void arityIsEqualToNumberOfAttributesInTuple(
          @ForAll @IntRange(max = 50) int arity) {
    assertEquals(arity, create(arity).arity());
  }

  @Property
  void tuplesAreIterable(
          @ForAll @IntRange(max = 50) int arity) {
    checkIterable(create(arity), arity);
  }

  @Property
  void gettingAttributesOutsideBoundsThrowsAnException(
          @ForAll @IntRange(max = 50) int arity) {
    assertThrows(IllegalArgumentException.class, () -> create(arity).getAttributeAt(arity + 1));
  }

  @Property
  void tuplesAreOrdered(
          @ForAll @IntRange(max = 50) int arity) {
    checkOrder(create(arity));
  }

  @Property
  void tuplesOfDifferentArityAreNotEqual(
          @ForAll @IntRange(max = 50) int arity1,
          @ForAll @IntRange(max = 50) int arity2) {
    Assume.that(arity1 != arity2);

    assertNotEquals(create(arity1), create(arity2));
  }

  //// Helper methods below

  private Expression id(String val) {
    return new IdConstant(0, val);
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
      case 0:
        return EmptyTuple.EMPTY;
      case 1:
        return new UnaryTuple(atts[0]);
      case 2:
        return new BinaryTuple(atts[0], atts[1]);
      case 3:
        return new TernaryTuple(atts[0], atts[1], atts[2]);
      case 4:
        return new FourAttributesTuple(atts[0], atts[1], atts[2], atts[3]);
      case 5:
        return new FiveAttributesTuple(atts[0], atts[1], atts[2], atts[3], atts[4]);
      default:
        return new NaryTuple(atts);
    }
  }

  private void checkIterable(Tuple tuple, int expectedCount) {
    int count = 0;
    for (Expression ignored : tuple) {
      count++;
    }

    assertEquals(expectedCount, count);
  }

  private void checkOrder(Tuple tuple) {
    for (int i = 0; i < tuple.arity(); i++) {
      assertEquals(id("" + i), tuple.getAttributeAt(i));
    }
  }

}