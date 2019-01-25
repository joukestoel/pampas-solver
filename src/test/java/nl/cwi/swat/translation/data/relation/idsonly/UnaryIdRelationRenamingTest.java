package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Caffeine;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.smtlogic.SimpleFormulaFactory;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import org.junit.jupiter.api.BeforeAll;

class UnaryIdRelationRenamingTest {
  private RelationFactory rf;
  private FormulaFactory ff;

  @BeforeAll
  public void initialize() {
    ff = new SimpleFormulaFactory();
    rf = new RelationFactory(ff, Caffeine.newBuilder().recordStats().build());
  }


}