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
import nl.cwi.swat.translation.data.row.Constraint;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.TupleConstraintFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class MultiSortedRelation extends AbstractRelation {

  public MultiSortedRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows, @NonNull RelationFactory rf, @NonNull FormulaFactory ff, @NonNull Cache<IndexCacheKey, IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }

  @Override
  public Relation union(Relation other) {
    checkUnionCompatibility(other);

    MultiSortedRelation otherRel = (MultiSortedRelation) other;

    Map.Transient<Tuple, Constraint> largest;
    Map.Immutable<Tuple, Constraint> smallest;

    if (this.nrOfRows() > otherRel.nrOfRows()) {
      largest = rows.asTransient();
      smallest = otherRel.rows;
    } else {
      largest = otherRel.rows.asTransient();
      smallest = rows;
    }

    for (Tuple tuple : smallest.keySet()) {
      if (largest.containsKey(tuple)) {
        // update exists field
        Constraint rc = largest.remove(tuple);
        Formula result = ff.or(rc.exists(), smallest.get(tuple).exists());

        if (!BooleanConstant.FALSE.equals(result)) {
          largest.__put(tuple, TupleConstraintFactory.buildConstraint(result));
        }
      } else {
        // add tuple
        largest.__put(tuple, smallest.get(tuple));
      }
    }

    return rf.buildRelation(heading, largest.freeze(), true);
  }

}
