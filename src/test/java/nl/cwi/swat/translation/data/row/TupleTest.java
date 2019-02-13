package nl.cwi.swat.translation.data.row;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.IdAtom;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assume.assumeThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitQuickcheck.class)
public class TupleTest {

  @Property
  public void twoTuplesWithSameAttributesAreEqual(@InRange(minInt = 0, maxInt = 50) int arity) {
    assertEquals(create(arity), create(arity));
  }

  @Property
  public void arityIsEqualToNumberOfAttributesInTuple(@InRange(minInt = 0, maxInt = 50) int arity) {
    assertEquals(arity, create(arity).arity());
  }

  @Property
  public void tuplesAreIterable(@InRange(minInt = 0, maxInt = 50) int arity) {
    checkIterable(create(arity), arity);
  }

  @Property
  public void gettingAttributesOutsideBoundsThrowsAnException(@InRange(minInt = 0, maxInt = 50) int arity) {
    assertThrows(IllegalArgumentException.class, () -> create(arity).getAttributeAt(arity+1));
  }

  @Property
  public void tuplesAreOrdered(@InRange(minInt = 0, maxInt = 50) int arity)  {
    checkOrder(create(arity));
  }

  @Property
  public void tuplesOfDifferentArityAreNotEqual(@InRange(minInt = 0, maxInt = 50) int arity1, @InRange(minInt = 0, maxInt = 50) int arity2) {
    assumeThat(arity1, not(arity2));

    assertNotEquals(create(arity1), create(arity2));
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