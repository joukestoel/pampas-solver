package nl.cwi.swat.ast;

import nl.cwi.swat.benchmark.pigeonhole.PigeonHoleTranslatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrettyPrinterTester {
  private PrettyPrinter pp;

  @BeforeEach
  void setup() {
    this.pp = new PrettyPrinter();
  }

  @Test
  void pigeonHoleAsText() {
    String expected = "nest in pigeons x holes\n" +
            "forall p: pigeons | one p |x| nest\n" +
            "forall h: holes | lone h |x| nest\n";

    StringBuilder actual = new StringBuilder();
    PigeonHoleTranslatorTest.constraints().forEach(c -> actual.append(c.accept(pp)).append("\n"));
    assertEquals(expected, actual.toString());
  }
}
