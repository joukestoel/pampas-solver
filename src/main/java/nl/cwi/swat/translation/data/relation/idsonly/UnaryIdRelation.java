package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.smtlogic.Formula;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.translation.data.relation.AbstractRelation;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.row.Row;
import nl.cwi.swat.translation.data.row.RowAndConstraint;
import nl.cwi.swat.translation.data.row.RowConstraint;
import nl.cwi.swat.translation.data.row.RowFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class UnaryIdRelation extends IdsOnlyRelation {

  public UnaryIdRelation(@NotNull Heading heading, @NotNull Map.Immutable<Row, RowConstraint> rows,
                         @NotNull RelationFactory rf, @NotNull FormulaFactory ff,
                         @NotNull Cache<IndexCacheKey,IndexedRows> indexCache) {
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
  public Relation project(@NotNull java.util.Set<String> projectedAttributes) {
    throw new UnsupportedOperationException("Projection of an unary relation is pointless");
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
    IndexedRows indexedOtherRows = otherRel.index(joiningFieldNames);

    Map.Transient<Row,RowConstraint> result = PersistentTrieMap.transientOf();

    for (Row current : rows.keySet()) {
      Optional<Set.Transient<RowAndConstraint>> joiningRows = indexedOtherRows.get(current);

      if (joiningRows.isPresent()) {
        RowConstraint rc = rows.get(current);
        Formula exists = rc.exists();
        Formula attCons = rc.attributeConstraints();

        for (RowAndConstraint joiningRow : joiningRows.get()) {
          Row joinedRow = RowFactory.merge(current, joiningRow.getRow(), indicesOfJoinedFields);
          exists = ff.and(exists, joiningRow.getConstraint().exists());
          attCons = ff.and(attCons, joiningRow.getConstraint().attributeConstraints());

          result.put(joinedRow, RowFactory.buildRowConstraint(exists,attCons));
        }
      }
    }

    return rf.buildRelation(heading.join(other.getHeading()), result.freeze(), isStable() && other.isStable());
  }
}
