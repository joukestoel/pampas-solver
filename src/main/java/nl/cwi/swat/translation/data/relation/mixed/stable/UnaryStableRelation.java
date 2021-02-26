package nl.cwi.swat.translation.data.relation.mixed.stable;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.relation.idsonly.IdsOnlyRelation;
import nl.cwi.swat.translation.data.relation.mixed.MultiSortedRelation;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.Constraint;
import nl.cwi.swat.translation.data.row.TupleConstraintFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.com.google.common.collect.Sets;

import java.util.Set;

public class UnaryStableRelation extends StableRelation {
  public UnaryStableRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows,
                             @NonNull RelationFactory rf, @NonNull FormulaFactory ff,
                             @NonNull Cache<IndexCacheKey, IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);

    assert(heading.arity() == 1);
  }

  @Override
  public boolean isStable() {
    return true;
  }

  @Override
  public Relation project(Set<String> projectedAttributes) {
    return null;
  }

  @Override
  public Relation restrict() {
    return null;
  }

  @Override
  public Relation difference(Relation other) {
    return null;
  }

  @Override
  public Formula subset(Relation other) {
    return null;
  }

  @Override
  public Relation naturalJoin(Relation other) {
    return intersect(other);
  }

  @Override
  public Relation aggregate() {
    return null;
  }

  @Override
  public Relation asSingleton(@NonNull Tuple tuple) {
    return rf.buildRelation(heading, PersistentTrieMap.of(tuple, TupleConstraintFactory.ALL_TRUE), true);
  }

}
