package nl.cwi.swat.translation.data.row;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.rel.IdConstant;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Set;

import static nl.cwi.swat.translation.data.row.TupleFactory.buildTuple;
import static nl.cwi.swat.translation.data.row.TupleFactory.merge;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitQuickcheck.class)
public class TupleFactoryTest {

  @Property
  public void tuplesWithArityUpToFiveAreOfSpecialisedType(@InRange(minInt = 0, maxInt = 50) int arity) {
    Tuple tuple = buildTuple(atts(arity));
    switch (arity) {
      case 0: assertTrue(tuple instanceof EmptyTuple); break;
      case 1: assertTrue(tuple instanceof UnaryTuple); break;
      case 2: assertTrue(tuple instanceof BinaryTuple); break;
      case 3: assertTrue(tuple instanceof TernaryTuple); break;
      case 4: assertTrue(tuple instanceof FourAttributesTuple); break;
      case 5: assertTrue(tuple instanceof FiveAttributesTuple); break;
      default: assertTrue(tuple instanceof NaryTuple);
    }
  }

  @Property
  public void arityIsEqualToNumberOfAttributesInTuple(@InRange(minInt = 0, maxInt = 50) int arity) {
    assertEquals(arity, buildTuple(atts(arity)).arity());
  }

  @Property
  public void buildingAPartialTupleWhenFilteringEverythingLeavesTheEmptyTuple(@InRange(minInt = 0, maxInt = 50) int arity) {
    Tuple tuple = TupleFactory.buildPartialTuple(buildTuple(atts(arity)), Collections.emptySet());
    assertEquals(0, tuple.arity());
    assertTrue(tuple instanceof EmptyTuple);
  }

  @Property
  public void buildingAPartialTupleWithIndicesOutsideBoundsThrowsException(@InRange(minInt = 0, maxInt = 50) int arity, Set<@InRange(minInt = 0, maxInt = 70) Integer> indices) {
    assumeThat(indices, hasItem(arity + 1));
//    assumeThat(indices, hasSize(arity));

    assertThrows(IllegalArgumentException.class, () -> TupleFactory.buildPartialTuple(buildTuple(atts(arity)), indices));
  }

//  @Property
//  public void buildingAPartialTupleWith()


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
    return new IdConstant(0, val);
  }
}