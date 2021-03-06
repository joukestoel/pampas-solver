package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.row.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

public class UnaryIdRelation extends IdsOnlyRelation {

  public UnaryIdRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows,
                         @NonNull RelationFactory rf, @NonNull FormulaFactory ff,
                         @NonNull Cache<IndexCacheKey,IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);

    if (heading.arity() != 1) {
      throw new IllegalArgumentException("Heading of unary relation can not have a arity greater than 1");
    }
  }

  @Override
  public int arity() {
    return 1;
  }

  @Override
  public Relation naturalJoin(@NonNull Relation other) {
    java.util.Set<String> joiningFieldNames = heading.getIntersectingAttributeNames(other.getHeading());

    if (joiningFieldNames.isEmpty()) {
      throw new IllegalArgumentException("No fields to perform natural join on");
    }
    if (joiningFieldNames.size() != 1) {
      throw new IllegalStateException("Joining an unary relation should only be joined on a single field");
    }

    java.util.Set<Integer> indicesOfJoinedFields = other.getHeading().getAttributeIndices(joiningFieldNames);

    AbstractRelation otherRel = (AbstractRelation) other;
    IndexedRows indexedOtherRows = otherRel.index(joiningFieldNames);

    Map.Transient<Tuple, Constraint> result = PersistentTrieMap.transientOf();

    for (Tuple current : rows.keySet()) {
      Optional<Set.Transient<Row>> joiningRows = indexedOtherRows.get(current);

      if (joiningRows.isPresent()) {
        Constraint rc = rows.get(current);
        Formula exists = rc.exists();
        Formula attCons = rc.attributeConstraints();

        for (Row joiningRow : joiningRows.get()) {
          Tuple joinedTuple = TupleFactory.merge(current, joiningRow.getTuple(), indicesOfJoinedFields);
          Formula newExists = ff.and(exists, joiningRow.getConstraint().exists());
          Formula newAttCons = ff.and(attCons, joiningRow.getConstraint().attributeConstraints());

          result.__put(joinedTuple, TupleConstraintFactory.buildConstraint(newExists, newAttCons));
        }
      }
    }

    return rf.buildRelation(heading.join(other.getHeading()), result.freeze(), isStable() && other.isStable());
  }
}
