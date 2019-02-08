package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.smtlogic.*;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.RowAndConstraint;
import nl.cwi.swat.translation.data.row.Constraint;
import nl.cwi.swat.translation.data.row.RowFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class IdsOnlyRelation extends AbstractRelation {

  IdsOnlyRelation(@NotNull Heading heading, @NotNull Map.Immutable<Tuple, Constraint> rows,
                  @NotNull RelationFactory rf, @NotNull FormulaFactory ff,
                  @NotNull Cache<IndexCacheKey,IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);
  }

  @Override
  public boolean isStable() {
    return true;
  }

  @Override
  public Relation rename(@NotNull java.util.Map<String, String> renamings) {
    return rf.buildRelation(heading.rename(renamings), rows, true);
  }

  private void checkUnionCompatibility(@NotNull Relation other) {
    if (!unionCompatible(other)) {
      throw new IllegalArgumentException("Other relation is not union compatible with this relation");
    }
  }

  private void checkType(@NotNull Relation other) {
    if (!(other instanceof IdsOnlyRelation)) {
      throw new IllegalArgumentException("Can only union other id only relations");
    }
  }

  @Override
  public Relation union(@NotNull Relation other) {
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
          largest.put(tuple, RowFactory.buildRowConstraint(result));
        }
      } else {
        // add tuple
        largest.put(tuple, smallest.get(tuple));
      }
    }

    return rf.buildRelation(heading, largest.freeze(), true);
  }


  @Override
  public Relation intersect(@NotNull Relation other) {
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
          result.put(tuple, RowFactory.buildRowConstraint(conjoined));
        }
      }
    }

    return rf.buildRelation(heading, result.freeze(), true);
  }

  @Override
  public Relation difference(@NotNull Relation other) {
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
          left.put(tuple, RowFactory.buildRowConstraint(result));
        }
      }
    }

    return rf.buildRelation(heading, left.freeze(), true);
  }

  @Override
  public Formula subset(@NotNull Relation other) {
    checkUnionCompatibility(other);
    checkType(other);

    IdsOnlyRelation otherRel = (IdsOnlyRelation)other;

    FormulaAccumulator acc = FormulaAccumulator.AND();

    Iterator<Tuple> rowIt = rows.keyIterator();
    while (rowIt.hasNext() && !acc.isShortCircuited()) {
      Tuple current = rowIt.next();

      Constraint oc = otherRel.rows.get(current);
      if (oc != null) {
        acc.add(ff.or(rows.get(current).combined().negation(), oc.combined()));
      } else {
        acc.add(rows.get(current).combined().negation());
      }
    }

    return ff.accumulate(acc);
  }

  @Override
  public Relation project(@NotNull java.util.Set<String> projectedAttributes) {
    Heading projectedHeading = heading.project(projectedAttributes);

    IndexedRows indexedRows = index(projectedAttributes);

    Map.Transient<Tuple, Constraint> result = PersistentTrieMap.transientOf();

    for (Tuple key : indexedRows) {
      Optional<Set.Transient<RowAndConstraint>> rac = indexedRows.get(key);

      FormulaAccumulator acc = FormulaAccumulator.OR();

      if (rac.isPresent()) {
        for (RowAndConstraint rc : rac.get()) {
          acc.add(rc.getConstraint().exists());
        }
      }

      result.put(key, RowFactory.buildRowConstraint(ff.accumulate(acc)));
    }

    return rf.buildRelation(projectedHeading, result.freeze(), true);
  }

  @Override
  public Relation naturalJoin(@NotNull Relation other) {
    java.util.Set<String> joiningFieldNames = heading.intersect(other.getHeading());
    List<Integer> indicesOfJoinedFields = other.getHeading().getAttributeIndices(joiningFieldNames);

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
      Optional<Set.Transient<RowAndConstraint>> ownRows = indexedOwnRows.get(current);
      Optional<Set.Transient<RowAndConstraint>> otherRows = indexedOtherRows.get(current);

      if (ownRows.isPresent() && otherRows.isPresent()) {
        for (RowAndConstraint ownRow : ownRows.get()) {
          Constraint rc = rows.get(current);
          Formula exists = rc.exists();
          Formula attCons = rc.attributeConstraints();

          for (RowAndConstraint otherRow : otherRows.get()) {
            Tuple joinedTuple = RowFactory.merge(ownRow.getTuple(), otherRow.getTuple(), indicesOfJoinedFields);
            exists = ff.and(exists, otherRow.getConstraint().exists());
            attCons = ff.and(attCons, otherRow.getConstraint().attributeConstraints());

            result.put(joinedTuple, RowFactory.buildRowConstraint(exists,attCons));
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
  public Relation asSingleton(@NotNull Tuple tuple) {
    return rf.buildRelation(heading, PersistentTrieMap.of(tuple, RowFactory.ALL_TRUE), true);
  }

}
