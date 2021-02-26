package nl.cwi.swat.translation.data.relation.mixed;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.relation.mixed.stable.StableRelation;
import nl.cwi.swat.translation.data.row.Constraint;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.TupleConstraintFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class MultiSortedRelation extends AbstractRelation {

  public MultiSortedRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows, @NonNull RelationFactory rf, @NonNull FormulaFactory ff, @NonNull Cache<IndexCacheKey, IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }

  protected Relation stableIntersection(Relation other) {
    assert other instanceof StableRelation;

    Map.Immutable<Tuple, Constraint> largest;
    Map.Transient<Tuple, Constraint> smallest;

    if (this.nrOfRows() > other.nrOfRows()) {
      largest = rows;
      smallest = other.rows().asTransient();
    } else {
      largest = other.rows();
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

  protected Relation unstableIntersection(Relation other) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  protected Relation stableUnion(Relation other) {
    assert other instanceof StableRelation;

    Map.Transient<Tuple, Constraint> largest;
    Map.Immutable<Tuple, Constraint> smallest;

    if (this.nrOfRows() > other.nrOfRows()) {
      largest = rows.asTransient();
      smallest = other.rows();
    } else {
      largest = other.rows().asTransient();
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

  protected Relation unstableUnion(Relation other) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
