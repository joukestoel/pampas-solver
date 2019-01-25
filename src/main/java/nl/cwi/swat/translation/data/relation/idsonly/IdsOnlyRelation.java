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
import nl.cwi.swat.translation.data.row.Row;
import nl.cwi.swat.translation.data.row.RowAndConstraint;
import nl.cwi.swat.translation.data.row.RowConstraint;
import nl.cwi.swat.translation.data.row.RowFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class IdsOnlyRelation extends AbstractRelation {

  IdsOnlyRelation(@NotNull Heading heading, @NotNull Map.Immutable<Row, RowConstraint> rows,
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

    Map.Transient<Row,RowConstraint> largest;
    Map.Immutable<Row,RowConstraint> smallest;

    if (this.nrOfRows() > otherRel.nrOfRows()) {
      largest = rows.asTransient();
      smallest = otherRel.rows;
    } else {
      largest = otherRel.rows.asTransient();
      smallest = rows;
    }

    for (Row row: smallest.keySet()) {
      if (largest.containsKey(row)) {
        // update exists field
        RowConstraint rc = largest.remove(row);
        Formula result = ff.or(rc.exists(), smallest.get(row).exists());

        if (!BooleanConstant.FALSE.equals(result)) {
          largest.put(row, RowFactory.buildRowConstraint(result));
        }
      } else {
        // add row
        largest.put(row, smallest.get(row));
      }
    }

    return rf.buildRelation(heading, largest.freeze(), true);
  }


  @Override
  public Relation intersect(@NotNull Relation other) {
    checkUnionCompatibility(other);
    checkType(other);

    IdsOnlyRelation otherRel = (IdsOnlyRelation) other;

    Map.Immutable<Row, RowConstraint> largest;
    Map.Immutable<Row, RowConstraint> smallest;

    if (this.nrOfRows() < otherRel.nrOfRows()) {
      largest = rows;
      smallest = otherRel.rows;
    } else {
      largest = otherRel.rows;
      smallest = rows;
    }

    Map.Transient<Row, RowConstraint> result = PersistentTrieMap.transientOf();

    for (Row row : smallest.keySet()) {
      if (largest.containsKey(row)) {
        Formula conjoined = ff.and(largest.get(row).exists(), smallest.get(row).exists());

        if (!BooleanConstant.FALSE.equals(conjoined)) {
          result.put(row, RowFactory.buildRowConstraint(conjoined));
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

    Map.Transient<Row,RowConstraint> left = rows.asTransient();
    Map.Immutable<Row,RowConstraint> right = otherRel.rows;

    for (Row row: left.keySet()) {
      if (right.containsKey(row)) {
        // update exists field
        RowConstraint rc = left.remove(row);
        Formula result = ff.and(rc.exists(), right.get(row).exists().negation());

        if (!BooleanConstant.FALSE.equals(result)) {
          left.put(row, RowFactory.buildRowConstraint(result));
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

    Iterator<Row> rowIt = rows.keyIterator();
    while (rowIt.hasNext() && !acc.isShortCircuited()) {
      Row current = rowIt.next();

      RowConstraint oc = otherRel.rows.get(current);
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

    Map.Transient<Row,RowConstraint> result = PersistentTrieMap.transientOf();

    for (Row key : indexedRows) {
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

    Map.Transient<Row,RowConstraint> result = PersistentTrieMap.transientOf();

    for (Row current : indexedOwnRows) {
      Optional<Set.Transient<RowAndConstraint>> ownRows = indexedOwnRows.get(current);
      Optional<Set.Transient<RowAndConstraint>> otherRows = indexedOtherRows.get(current);

      if (ownRows.isPresent() && otherRows.isPresent()) {
        for (RowAndConstraint ownRow : ownRows.get()) {
          RowConstraint rc = rows.get(current);
          Formula exists = rc.exists();
          Formula attCons = rc.attributeConstraints();

          for (RowAndConstraint otherRow : otherRows.get()) {
            Row joinedRow = RowFactory.merge(ownRow.getRow(), otherRow.getRow(), indicesOfJoinedFields);
            exists = ff.and(exists, otherRow.getConstraint().exists());
            attCons = ff.and(attCons, otherRow.getConstraint().attributeConstraints());

            result.put(joinedRow, RowFactory.buildRowConstraint(exists,attCons));
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
  public Relation asSingleton(@NotNull Row row) {
    return rf.buildRelation(heading, PersistentTrieMap.of(row, RowFactory.ALL_TRUE), true);
  }

}
