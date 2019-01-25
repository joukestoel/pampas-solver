package nl.cwi.swat.smtlogic;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimplificationAssemblerTest {
  private SimplificationFactory sfactory;

  @BeforeEach
  public void setUp() throws Exception {
    sfactory = new SimplificationFactory(3, Caffeine.newBuilder().build());
  }

  @Test
  public void constAndConstReductions() {
    assertEquals(BooleanConstant.FALSE, sfactory.reduce(Operator.AND, BooleanConstant.TRUE, BooleanConstant.FALSE));
    assertEquals(BooleanConstant.FALSE, sfactory.reduce(Operator.AND, BooleanConstant.FALSE, BooleanConstant.TRUE));
    assertEquals(BooleanConstant.FALSE, sfactory.reduce(Operator.AND, BooleanConstant.FALSE, BooleanConstant.FALSE));
    assertEquals(BooleanConstant.TRUE, sfactory.reduce(Operator.AND, BooleanConstant.TRUE, BooleanConstant.TRUE));
  }

  @Test
  public void constOrConstReductions() {
    assertEquals(BooleanConstant.TRUE, sfactory.reduce(Operator.OR, BooleanConstant.TRUE, BooleanConstant.FALSE));
    assertEquals(BooleanConstant.TRUE, sfactory.reduce(Operator.OR, BooleanConstant.FALSE, BooleanConstant.TRUE));
    assertEquals(BooleanConstant.TRUE, sfactory.reduce(Operator.OR, BooleanConstant.TRUE, BooleanConstant.TRUE));
    assertEquals(BooleanConstant.FALSE, sfactory.reduce(Operator.OR, BooleanConstant.FALSE, BooleanConstant.FALSE));
  }

  @Test
  public void andAccumulatorReductionsForTwoInputs() {
    Formula var = sfactory.newBoolVar("test");
    FormulaAccumulator acc = FormulaAccumulator.AND();
    acc.add(var);
    acc.add(var.negation());

    assertEquals(BooleanConstant.FALSE, sfactory.reduce(acc));
  }

  @Test
  public void andAccumulatorReductionsForThreeInputs() {
    Formula var = sfactory.newBoolVar("test");
    FormulaAccumulator acc = FormulaAccumulator.AND();
    acc.add(var);
    acc.add(var.negation());
    acc.add(sfactory.newBoolVar("test"));

    assertEquals(BooleanConstant.FALSE, sfactory.reduce(acc));
  }


}