package nl.cwi.swat.formulacircuit.bool;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Term;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitQuickcheck.class)
public class BooleanBinaryGateTest {

  private Formula createForm(long label) {
    return new BooleanVariable("name", label);
  }

  @Property
  public void binaryGateIsOrdered(long first, long second) {
    Formula f = createForm(first);
    Formula s = createForm(second);

    BooleanBinaryGate form = new BooleanBinaryGate(BooleanOperator.AND, 1, f, s);
    assertEquals(2, form.size());
    assertTrue(form.input(0).label() < form.input(1).label());
  }

  @Test
  public void binaryGateIsIterable() {
    Formula low = createForm(2);
    Formula high = createForm(3);

    BooleanBinaryGate form = new BooleanBinaryGate(BooleanOperator.AND, 1, low, high);

    int count = 0;
    int lastLabel = 0;
    for (Term f : form) {
      if (f.label() <= lastLabel) {
        fail("Terms should have increasing labels");
      }
      count += 1;
    }

    assertEquals(2, count);
  }
}