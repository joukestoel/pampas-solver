package nl.cwi.swat.ast;

import nl.cwi.swat.benchmark.pigeonhole.PigeonHoleTranslatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrettyPrinterTester {
  private PrettyPrinter pp;

  @BeforeEach
  void setup() {
    this.pp = new PrettyPrinter();
  }

  @Test
  void pigeonHoleAsText() {
    StringBuilder expected = new StringBuilder();
    expected.append("nest in pigeons x holes\n");
    expected.append("forall p: pigeons | one p |x| nest\n");
    expected.append("forall h: holes | lone h |x| nest\n");

    StringBuilder actual = new StringBuilder();
    PigeonHoleTranslatorTest.constraints().forEach(c -> actual.append(c.accept(pp)+ "\n"));
    assertEquals(expected.toString(), actual.toString());
  }
}
