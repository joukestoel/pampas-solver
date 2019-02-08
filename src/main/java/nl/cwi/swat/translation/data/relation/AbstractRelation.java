package nl.cwi.swat.translation.data.relation;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieMap;
import io.usethesource.capsule.core.PersistentTrieSet;
import nl.cwi.swat.smtlogic.Formula;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.RowAndConstraint;
import nl.cwi.swat.translation.data.row.Constraint;
import nl.cwi.swat.translation.data.row.RowFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRelation implements Relation {
  protected final Map.Immutable<Tuple, Constraint> rows;
  protected final Cache<IndexCacheKey,IndexedRows> indexCache;

  protected final Heading heading;
  protected final FormulaFactory ff;
  protected final RelationFactory rf;

  public AbstractRelation(@NotNull Heading heading, @NotNull Map.Immutable<Tuple, Constraint> rows,
                          @NotNull RelationFactory rf, @NotNull FormulaFactory ff,
                          @NotNull Cache<IndexCacheKey,IndexedRows> indexCache) {
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
  public boolean unionCompatible(@NotNull Relation other) {
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
  public Formula getCombinedConstraints(@NotNull Tuple tuple) {
    if (!rows.containsKey(tuple)) {
      throw new IllegalArgumentException("Tuple is not part of this relation");
    }

    return rows.get(tuple).combined();
  }

  @Override
  public Constraint getRowConstraint(@NotNull Tuple tuple) {
    if (!rows.containsKey(tuple)) {
      throw new IllegalArgumentException("Tuple is not part of this relation");
    }

    return rows.get(tuple);
  }

  @NotNull
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
    List<Integer> attIndices = heading.getAttributeIndices(indexOn);

    for (java.util.Map.Entry<Tuple, Constraint> rac : rows.entrySet()) {
      Tuple key = RowFactory.buildPartialTuple(rac.getKey(), attIndices);
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
  public Relation product(@NotNull Relation other) {
    if (! heading.intersect(other.getHeading()).isEmpty()) {
      throw new IllegalArgumentException("There are overlapping fields. Can not perform cross product");
    }

    Map.Transient<Tuple, Constraint> result = PersistentTrieMap.transientOf();

    for (Tuple lhs : this) {
      Constraint lhsCons = rows.get(lhs);

      for (Tuple rhs : other) {
        Constraint rhsCons = other.getRowConstraint(rhs);

        Tuple joinedTuple = RowFactory.merge(lhs, rhs, Collections.emptyList());
        Formula exists = ff.and(lhsCons.exists(), rhsCons.exists());
        Formula attCons = ff.and(lhsCons.attributeConstraints(), rhsCons.attributeConstraints());

        result.put(joinedTuple, RowFactory.buildRowConstraint(exists, attCons));
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
    private final Map.Transient<Tuple, Set.Transient<RowAndConstraint>> indexedRows;

    IndexedRows() {
      this.indexedRows = PersistentTrieMap.transientOf();
    }

    public void add(@NotNull Tuple key, @NotNull Tuple whole, @NotNull Constraint rc) {
      Set.Transient<RowAndConstraint> currentVal = indexedRows.get(key);

      if (currentVal != null) {
        indexedRows.remove(key);
      } else {
        currentVal = PersistentTrieSet.transientOf();
      }

      currentVal.add(new RowAndConstraint(whole, rc));

      indexedRows.put(key, currentVal);
    }

    public Optional<Set.Transient<RowAndConstraint>> get(@NotNull Tuple tuple) {
      Set.Transient<RowAndConstraint> subrows = indexedRows.get(tuple);
      return subrows != null ? Optional.of(subrows) : Optional.empty();
    }

    public int size() {
      return indexedRows.size();
    }

    protected Map.Immutable<Tuple, Constraint> flatten() {
      Map.Transient<Tuple, Constraint> flattened = PersistentTrieMap.transientOf();

      for (Set<RowAndConstraint> rows : indexedRows.values()) {
        for (RowAndConstraint rac : rows) {
          flattened.put(rac.getTuple(),rac.getConstraint());
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

    @NotNull
    @Override
    public Iterator<Tuple> iterator() {
      return indexedRows.keyIterator();
    }
  }

  public static class IndexCacheKey {
    private final Map.Immutable<Tuple, Constraint> rows;
    private final java.util.Set<String> key;

    private IndexCacheKey(@NotNull Map.Immutable<Tuple, Constraint> rows, @NotNull java.util.Set<String> key) {
      this.rows = rows;
      this.key = key;
    }

    static IndexCacheKey toKey(@NotNull Map.Immutable<Tuple, Constraint> rows, @NotNull java.util.Set<String> key) {
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


