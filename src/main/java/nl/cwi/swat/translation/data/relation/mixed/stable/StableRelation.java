package nl.cwi.swat.translation.data.relation.mixed.stable;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.Constraint;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class StableRelation extends AbstractRelation {
  public StableRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows,
                        @NonNull RelationFactory rf, @NonNull FormulaFactory ff,
                        @NonNull Cache<IndexCacheKey, IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }
}
