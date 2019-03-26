package nl.cwi.swat.formulacircuit.bool;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Term;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(JUnitQuickcheck.class)
public class BooleanBinaryGateTest {

  private Formula createForm(long label) {
    return new BooleanVariable("name", label);
  }

  @Property
  public void binaryGateIsOrdered(long first, long second) {
    Formula low = createForm(first);
    Formula high = createForm(second);

    if (first > second) {
      assertThrows(IllegalArgumentException.class, () -> new BooleanBinaryGate(BooleanOperator.AND, 1, low, high));
    } else {
      BooleanBinaryGate form = new BooleanBinaryGate(BooleanOperator.AND, 1, low, high);
      assertEquals(2, form.size());
      assertEquals(low, form.input(0));
      assertEquals(high, form.input(1));
    }
  }

  @Test
  public void binaryGateIsIterable() {
    Formula low = createForm(2);
    Formula high = createForm(3);

    BooleanBinaryGate form = new BooleanBinaryGate(BooleanOperator.AND, 1, low, high);

    int count = 0;
    for (Term f : form) {
      count += 1;
    }

    assertEquals(2, count);
  }
}