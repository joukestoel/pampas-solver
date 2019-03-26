package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.row.Constraint;
import nl.cwi.swat.translation.data.row.Tuple;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NaryIdRelation extends IdsOnlyRelation {
  public NaryIdRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows, @NonNull RelationFactory rf, @NonNull FormulaFactory ff, @NonNull Cache<IndexCacheKey, IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }
}
