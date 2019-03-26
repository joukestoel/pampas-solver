package nl.cwi.swat.translation.data.relation;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieMap;
import io.usethesource.capsule.core.PersistentTrieSet;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.translation.data.row.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

public abstract class AbstractRelation implements Relation {
  protected final Map.Immutable<Tuple, Constraint> rows;
  private final Cache<IndexCacheKey,IndexedRows> indexCache;

  protected final Heading heading;
  protected final FormulaFactory ff;
  protected final RelationFactory rf;

  public AbstractRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows,
                          @NonNull RelationFactory rf, @NonNull FormulaFactory ff,
                          @NonNull Cache<IndexCacheKey,IndexedRows> indexCache) {
    this.heading = heading;
    this.rows = rows;
    this.rf = rf;
    this.ff = ff;
    this.indexCache = indexCache;
  }

  @Override
  public Heading getHeading() {
    return heading;
  }

  @Override
  public boolean unionCompatible(@NonNull Relation other) {
    return heading.isUnionCompatible(other.getHeading());
  }

  @Override
  public Formula equal(Relation other) {
    return ff.and(subset(other), other.subset(this));
  }

  @Override
  public int nrOfRows() {
    return rows.size();
  }

  @Override
  public boolean isEmpty() {
    return rows.isEmpty();
  }

  @Override
  public Formula getCombinedConstraints(@NonNull Tuple tuple) {
    if (!rows.containsKey(tuple)) {
      throw new IllegalArgumentException("Tuple is not part of this relation");
    }

    return ff.combine(rows.get(tuple));
  }

  @Override
  public Constraint getRowConstraint(@NonNull Tuple tuple) {
    if (!rows.containsKey(tuple)) {
      throw new IllegalArgumentException("Tuple is not part of this relation");
    }

    return rows.get(tuple);
  }

  @NonNull
  @Override
  public Iterator<Tuple> iterator() {
    return rows.keyIterator();
  }

  public IndexedRows index(java.util.Set<String> indexOn) {
    // check if this index is already in the cache
    IndexedRows indexed = indexCache.getIfPresent(IndexCacheKey.toKey(this.rows, indexOn));
    if (indexed != null) {
      return indexed;
    }

    indexed = new IndexedRows();

    // find positions of attributes to index on
    java.util.Set<Integer> attIndices = heading.getAttributeIndices(indexOn);

    for (java.util.Map.Entry<Tuple, Constraint> rac : rows.entrySet()) {
      Tuple key = TupleFactory.buildPartialTuple(rac.getKey(), attIndices);
      indexed.add(key, rac.getKey(), rac.getValue());
    }

    indexCache.put(IndexCacheKey.toKey(this.rows, indexOn), indexed);

    return indexed;
  }

  @Override
  public Relation transitiveClosure() {
    throw new UnsupportedOperationException("Can only calculate a transitive closure of a binary relation with two id fields");
  }

  @Override
  public Relation product(@NonNull Relation other) {
    if (! heading.getIntersectingAttributeNames(other.getHeading()).isEmpty()) {
      throw new IllegalArgumentException("There are overlapping fields. Can not perform cross product");
    }

    Map.Transient<Tuple, Constraint> result = PersistentTrieMap.transientOf();

    for (Tuple lhs : this) {
      Constraint lhsCons = rows.get(lhs);

      for (Tuple rhs : other) {
        Constraint rhsCons = other.getRowConstraint(rhs);

        Tuple joinedTuple = TupleFactory.merge(lhs, rhs, Collections.emptySet());
        Formula exists = ff.and(lhsCons.exists(), rhsCons.exists());
        Formula attCons = ff.and(lhsCons.attributeConstraints(), rhsCons.attributeConstraints());

        result.__put(joinedTuple, TupleConstraintFactory.buildConstraint(exists, attCons));
      }
    }

    return rf.buildRelation(heading.join(other.getHeading()), result.freeze(), isStable() && other.isStable());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AbstractRelation rows = (AbstractRelation) o;

    if (!this.rows.equals(rows.rows)) return false;
    if (!heading.equals(rows.heading)) return false;
    if (!ff.equals(rows.ff)) return false;
    return rf.equals(rows.rf);
  }

  @Override
  public int hashCode() {
    int result = rows.hashCode();
    result = 31 * result + heading.hashCode();
    result = 31 * result + ff.hashCode();
    result = 31 * result + rf.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "TODO";
  }

  public class IndexedRows implements Iterable<Tuple> {
    private final Map.Transient<Tuple, Set.Transient<TupleAndConstraint>> indexedRows;

    IndexedRows() {
      this.indexedRows = PersistentTrieMap.transientOf();
    }

    public void add(@NonNull Tuple key, @NonNull Tuple whole, @NonNull Constraint rc) {
      Set.Transient<TupleAndConstraint> currentVal = indexedRows.get(key);

      if (currentVal != null) {
        indexedRows.__remove(key);
      } else {
        currentVal = PersistentTrieSet.transientOf();
      }

      currentVal.__insert(new TupleAndConstraint(whole, rc));

      indexedRows.put(key, currentVal);
    }

    public Optional<Set.Transient<TupleAndConstraint>> get(@NonNull Tuple tuple) {
      Set.Transient<TupleAndConstraint> subrows = indexedRows.get(tuple);
      return subrows != null ? Optional.of(subrows) : Optional.empty();
    }

    public int size() {
      return indexedRows.size();
    }

    Map.Immutable<Tuple, Constraint> flatten() {
      Map.Transient<Tuple, Constraint> flattened = PersistentTrieMap.transientOf();

      for (Set<TupleAndConstraint> rows : indexedRows.values()) {
        for (TupleAndConstraint rac : rows) {
          flattened.__put(rac.getTuple(),rac.getConstraint());
        }
      }

      return flattened.freeze();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IndexedRows that = (IndexedRows) o;

      return indexedRows.equals(that.indexedRows);
    }

    @Override
    public int hashCode() {
      return indexedRows.hashCode();
    }

    @NonNull
    @Override
    public Iterator<Tuple> iterator() {
      return indexedRows.keyIterator();
    }
  }

  public static class IndexCacheKey {
    private final Map.Immutable<Tuple, Constraint> rows;
    private final java.util.Set<String> key;

    private IndexCacheKey(Map.Immutable<Tuple, Constraint> rows, java.util.Set<String> key) {
      this.rows = rows;
      this.key = key;
    }

    static IndexCacheKey toKey(Map.Immutable<Tuple, Constraint> rows, java.util.Set<String> key) {
      return new IndexCacheKey(rows,key);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IndexCacheKey that = (IndexCacheKey) o;

      if (!rows.equals(that.rows)) return false;
      return key.equals(that.key);
    }

    @Override
    public int hashCode() {
      int result = rows.hashCode();
      result = 31 * result + key.hashCode();
      return result;
    }
  }

}


