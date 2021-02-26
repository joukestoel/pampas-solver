package nl.cwi.swat.translation.data.relation.mixed.stable;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.relation.mixed.MultiSortedRelation;
import nl.cwi.swat.translation.data.row.Constraint;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.TupleConstraintFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class StableRelation extends MultiSortedRelation {
  public StableRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows, @NonNull RelationFactory rf, @NonNull FormulaFactory ff, @NonNull Cache<IndexCacheKey, IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }

  @Override
  public boolean isStable() {
    return true;
  }

  protected Relation stableIntersection(StableRelation other) {
    Map.Immutable<Tuple, Constraint> largest;
    Map.Transient<Tuple, Constraint> smallest;

    if (this.nrOfRows() > other.nrOfRows()) {
      largest = rows;
      smallest = other.rows.asTransient();
    } else {
      largest = other.rows;
      smallest = rows.asTransient();
    }

    for (Tuple tuple: smallest.keySet()) {
      if (largest.containsKey(tuple)) {
        Constraint rc = largest.get(tuple);
        Constraint lc = smallest.get(tuple);

        Formula exists = ff.and(rc.exists(), lc.exists());
        if (!BooleanConstant.FALSE.equals(exists)) {
          smallest.__put(tuple, TupleConstraintFactory.buildConstraint(exists));
        } else {
          smallest.__remove(tuple);
        }
      } else {
        smallest.__remove(tuple);
      }
    }

    return rf.buildRelation(heading, smallest.freeze(), true);
  }

  protected Relation stableUnion(StableRelation other) {
    Map.Transient<Tuple, Constraint> largest;
    Map.Immutable<Tuple, Constraint> smallest;

    if (this.nrOfRows() > other.nrOfRows()) {
      largest = rows.asTransient();
      smallest = other.rows;
    } else {
      largest = other.rows.asTransient();
      smallest = rows;
    }

    for (Tuple tuple : smallest.keySet()) {
      if (largest.containsKey(tuple)) {
        // update exists field
        Constraint rc = largest.remove(tuple);
        Formula result = ff.or(rc.exists(), smallest.get(tuple).exists());
        largest.__put(tuple, TupleConstraintFactory.buildConstraint(result));
      } else {
        // add tuple
        largest.__put(tuple, smallest.get(tuple));
      }
    }

    return rf.buildRelation(heading, largest.freeze(), true);
  }
}
