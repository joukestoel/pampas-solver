package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.bool.BooleanAccumulator;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.row.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.Optional;

public abstract class IdsOnlyRelation extends AbstractRelation {

  IdsOnlyRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows,
                  @NonNull RelationFactory rf, @NonNull FormulaFactory ff,
                  @NonNull Cache<IndexCacheKey,IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }

  @Override
  public boolean isStable() {
    return true;
  }

  @Override
  public Relation rename(java.util.Map<String, String> renamings) {
    return rf.buildRelation(heading.rename(renamings), rows, true);
  }

  private void checkUnionCompatibility(@NonNull Relation other) {
    if (!unionCompatible(other)) {
      throw new IllegalArgumentException("Other relation is not union compatible with this relation");
    }
  }

  private void checkType(@NonNull Relation other) {
    if (!(other instanceof IdsOnlyRelation)) {
      throw new IllegalArgumentException("Can only union other id only relations");
    }
  }

  @Override
  public int arity() {
    return heading.arity();
  }

  @Override
  public Relation union(@NonNull Relation other) {
    checkUnionCompatibility(other);
    checkType(other);

    IdsOnlyRelation otherRel = (IdsOnlyRelation)other;

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


  @Override
  public Relation intersect(@NonNull Relation other) {
    checkUnionCompatibility(other);
    checkType(other);

    IdsOnlyRelation otherRel = (IdsOnlyRelation) other;

    Map.Immutable<Tuple, Constraint> largest;
    Map.Immutable<Tuple, Constraint> smallest;

    if (this.nrOfRows() < otherRel.nrOfRows()) {
      largest = rows;
      smallest = otherRel.rows;
    } else {
      largest = otherRel.rows;
      smallest = rows;
    }

    Map.Transient<Tuple, Constraint> result = PersistentTrieMap.transientOf();

    for (Tuple tuple : smallest.keySet()) {
      if (largest.containsKey(tuple)) {
        Formula conjoined = ff.and(largest.get(tuple).exists(), smallest.get(tuple).exists());

        if (!BooleanConstant.FALSE.equals(conjoined)) {
          result.__put(tuple, TupleConstraintFactory.buildConstraint(conjoined));
        }
      }
    }

    return rf.buildRelation(heading, result.freeze(), true);
  }

  @Override
  public Relation difference(@NonNull Relation other) {
    checkUnionCompatibility(other);
    checkType(other);

    IdsOnlyRelation otherRel = (IdsOnlyRelation)other;

    Map.Transient<Tuple, Constraint> left = rows.asTransient();
    Map.Immutable<Tuple, Constraint> right = otherRel.rows;

    for (Tuple tuple : left.keySet()) {
      if (right.containsKey(tuple)) {
        // update exists field
        Constraint rc = left.remove(tuple);
        Formula result = ff.and(rc.exists(), right.get(tuple).exists().negation());

        if (!BooleanConstant.FALSE.equals(result)) {
          left.__put(tuple, TupleConstraintFactory.buildConstraint(result));
        }
      }
    }

    return rf.buildRelation(heading, left.freeze(), true);
  }

  @Override
  public Formula subset(@NonNull Relation other) {
    checkUnionCompatibility(other);
    checkType(other);

    IdsOnlyRelation otherRel = (IdsOnlyRelation)other;

    BooleanAccumulator acc = BooleanAccumulator.AND();

    Iterator<Tuple> rowIt = rows.keyIterator();
    while (rowIt.hasNext() && !acc.isShortCircuited()) {
      Tuple current = rowIt.next();

      Constraint oc = otherRel.rows.get(current);
      if (oc != null) {
        acc.add(ff.or(ff.combine(rows.get(current)).negation(), ff.combine(oc)));
      } else {
        acc.add(ff.combine(rows.get(current)).negation());
      }
    }

    return ff.accumulateBools(acc);
  }

  @Override
  public Relation project(java.util.Set<String> projectedAttributes) {
    Heading projectedHeading = heading.project(projectedAttributes);

    IndexedRows indexedRows = index(projectedAttributes);

    Map.Transient<Tuple, Constraint> result = PersistentTrieMap.transientOf();

    for (Tuple key : indexedRows) {
      Optional<Set.Transient<TupleAndConstraint>> rac = indexedRows.get(key);

      BooleanAccumulator acc = BooleanAccumulator.OR();

      if (rac.isPresent()) {
        for (TupleAndConstraint rc : rac.get()) {
          acc.add(rc.getConstraint().exists());
        }
      }

      result.__put(key, TupleConstraintFactory.buildConstraint(ff.accumulateBools(acc)));
    }

    return rf.buildRelation(projectedHeading, result.freeze(), true);
  }

  @Override
  public Relation naturalJoin(@NonNull Relation other) {
    java.util.Set<String> joiningFieldNames = heading.getIntersectingAttributeNames(other.getHeading());
    java.util.Set<Integer> indicesOfJoinedFields = other.getHeading().getAttributeIndices(joiningFieldNames);

    if (joiningFieldNames.isEmpty()) {
      throw new IllegalArgumentException("No fields to perform natural join on");
    }
    if (joiningFieldNames.size() != 1) {
      throw new IllegalStateException("Joining an unary relation should only be joined on a single field");
    }

    AbstractRelation otherRel = (AbstractRelation) other;

    IndexedRows indexedOwnRows = index(joiningFieldNames);
    IndexedRows indexedOtherRows = otherRel.index(joiningFieldNames);

    Map.Transient<Tuple, Constraint> result = PersistentTrieMap.transientOf();

    for (Tuple current : indexedOwnRows) {
      Optional<Set.Transient<TupleAndConstraint>> ownRows = indexedOwnRows.get(current);
      Optional<Set.Transient<TupleAndConstraint>> otherRows = indexedOtherRows.get(current);

      if (ownRows.isPresent() && otherRows.isPresent()) {
        for (TupleAndConstraint ownRow : ownRows.get()) {
          Constraint rc = rows.get(current);
          Formula exists = rc.exists();
          Formula attCons = rc.attributeConstraints();

          for (TupleAndConstraint otherRow : otherRows.get()) {
            Tuple joinedTuple = TupleFactory.merge(ownRow.getTuple(), otherRow.getTuple(), indicesOfJoinedFields);
            exists = ff.and(exists, otherRow.getConstraint().exists());
            attCons = ff.and(attCons, otherRow.getConstraint().attributeConstraints());

            result.__put(joinedTuple, TupleConstraintFactory.buildConstraint(exists,attCons));
          }
        }
      }

    }

    return rf.buildRelation(heading.join(other.getHeading()), result.freeze(), isStable() && other.isStable());
  }

  @Override
  public Relation restrict() {
    throw new UnsupportedOperationException("Can not restrict an id only relation");
  }

  @Override
  public Relation aggregate() {
    throw new UnsupportedOperationException("TODO!!");
  }

  @Override
  public Relation asSingleton(@NonNull Tuple tuple) {
    return rf.buildRelation(heading, PersistentTrieMap.of(tuple, TupleConstraintFactory.ALL_TRUE), true);
  }

}
