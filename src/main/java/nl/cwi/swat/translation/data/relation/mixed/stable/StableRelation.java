package nl.cwi.swat.translation.data.relation.mixed.stable;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.relation.mixed.MultiSortedRelation;
import nl.cwi.swat.translation.data.row.Constraint;
import nl.cwi.swat.translation.data.row.Tuple;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class StableRelation extends MultiSortedRelation {
  public StableRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows, @NonNull RelationFactory rf, @NonNull FormulaFactory ff, @NonNull Cache<IndexCacheKey, IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }

  @Override
  public boolean isStable() {
    return true;
  }

  @Override
  public Relation intersect(Relation other) {
    checkUnionCompatibility(other);

    return other.isStable() ? stableIntersection(other) : unstableIntersection(other);
  }

  @Override
  public Relation union(Relation other) {
    checkUnionCompatibility(other);

    return other.isStable() ? stableUnion(other) : unstableUnion(other);
  }
}
