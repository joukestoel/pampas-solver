package nl.cwi.swat.formulacircuit.bool;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.Term;

import static org.junit.jupiter.api.Assertions.*;

class BooleanBinaryGateTest {

  private Formula createForm(long label) {
    return new BooleanVariable("name", label);
  }

  @Property
  void binaryGateIsOrdered(
          @ForAll long first,
          @ForAll long second) {
    Assume.that(first != second);

    Formula f = createForm(first);
    Formula s = createForm(second);

    BooleanBinaryGate form = new BooleanBinaryGate(BooleanOperator.AND, 1, f, s);
    assertEquals(2, form.size());
    assertTrue(form.input(0).label() < form.input(1).label());
  }

  @Property
  public void binaryGateIsIterable(
          @ForAll long first,
          @ForAll long second) {
    Assume.that(first != second);

    Formula low = createForm(first);
    Formula high = createForm(second);

    if (second < first) {
      low = createForm(second);
      high = createForm(first);
    }

    BooleanBinaryGate form = new BooleanBinaryGate(BooleanOperator.AND, 1, low, high);

    int count = 0;
    long lastLabel = Long.MIN_VALUE;
    for (Term f : form) {
      if (f.label() < lastLabel) {
        fail("Terms should have increasing labels");
      }
      lastLabel = f.label();
      count += 1;
    }

    assertEquals(2, count);
  }
}