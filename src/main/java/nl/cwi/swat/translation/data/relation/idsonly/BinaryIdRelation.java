package nl.cwi.swat.translation.data.relation.idsonly;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.translation.data.relation.Heading;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.relation.RelationFactory;
import nl.cwi.swat.translation.data.row.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class BinaryIdRelation extends IdsOnlyRelation {
  public BinaryIdRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows,
                          @NonNull RelationFactory rf, @NonNull FormulaFactory ff,
                          @NonNull Cache<IndexCacheKey,IndexedRows> indexCache) {
    super(heading, rows, rf, ff, indexCache);

    if (heading.arity() != 2) {
      throw new IllegalArgumentException("Heading of binary relation can not have a arity other than 2");
    }
  }

  @Override
  public int arity() {
    return 2;
  }

  @Override
  public Relation transitiveClosure() {
    // Could we use Warshall's Algorithm?
    Set<String> from = Collections.singleton(heading.getAttributeNameAt(0));
    Set<String> to   = Collections.singleton(heading.getAttributeNameAt(1));

    IndexedRows base = index(to);
    Map.Transient<Tuple, Constraint> result = rows.asTransient();
    BinaryIdRelation relFromPrevIt = (BinaryIdRelation) shallowClone();

    boolean changed = true;

    while (changed) {
      changed = false;

      IndexedRows indexedFrom = relFromPrevIt.index(from);
      Map.Transient<Tuple, Constraint> currentIt = PersistentTrieMap.transientOf();

      for (Tuple key : base) {
        Optional<io.usethesource.capsule.Set.Transient<Row>> ownRacs = base.get(key);

        if (ownRacs.isPresent()) {
          Optional<io.usethesource.capsule.Set.Transient<Row>> otherRacs = indexedFrom.get(key);

          if (otherRacs.isPresent()) {

            for (Row ownRac: ownRacs.get()) {
              for (Row otherRac: otherRacs.get()) {
                Tuple joinedTuple = TupleFactory.merge(ownRac.getTuple(), otherRac.getTuple(), Set.of(0));

                if (!rows.containsKey(joinedTuple)) { // already in base relation, no need to add
                  Formula exists = ff.and(ownRac.getConstraint().exists(), otherRac.getConstraint().exists());

                  if (currentIt.containsKey(joinedTuple)) {
                    exists = ff.or(currentIt.get(joinedTuple).exists(), exists);
                  }

                  currentIt.__put(joinedTuple, TupleConstraintFactory.buildConstraint(exists));
                  changed = true;
                }

              }
            }
          }
        }
      }

      if (changed) {
        relFromPrevIt = (BinaryIdRelation) rf.buildRelation(heading, currentIt.freeze(), true);
        result.__putAll(currentIt);
      }
    }

    return rf.buildRelation(heading, result.freeze(), true);

  }

  private Relation shallowClone() {
    return rf.buildRelation(heading, rows, isStable());
  }
}
