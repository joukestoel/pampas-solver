package nl.cwi.swat.translation.data.relation.unstable;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.Constraint;
import org.jetbrains.annotations.NotNull;

public abstract class UnstableRelation extends AbstractRelation {
  public UnstableRelation(@NotNull Heading heading, @NotNull Map.Immutable<Tuple, Constraint> rows,
                          @NotNull RelationFactory rf, @NotNull FormulaFactory ff,
                          @NotNull Cache<IndexCacheKey, IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }
}
