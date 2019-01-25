package nl.cwi.swat.translation.data.relation.stable;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.row.Row;
import nl.cwi.swat.translation.data.row.RowConstraint;
import org.jetbrains.annotations.NotNull;

public abstract class StableRelation extends AbstractRelation {
  public StableRelation(@NotNull Heading heading, @NotNull Map.Immutable<Row, RowConstraint> rows,
                        @NotNull RelationFactory rf, @NotNull FormulaFactory ff,
                        @NotNull Cache<IndexCacheKey, IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }
}
