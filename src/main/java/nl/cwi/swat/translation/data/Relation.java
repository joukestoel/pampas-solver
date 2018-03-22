package nl.cwi.swat.translation.data;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.smtlogic.Formula;
import nl.cwi.swat.smtlogic.FormulaFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Relation {
  private final FormulaFactory ffactory;
  private final Cache<IndexCacheKey,IndexedRows> indexCache;

  private final Heading heading;
  private final Map.Immutable<Row,Formula> rows;

  private Heading stableKey;

  public Relation(Heading heading, FormulaFactory ffactory, Cache<IndexCacheKey,IndexedRows> indexCache) {
    this.ffactory = ffactory;
    this.heading = heading;
    this.rows = PersistentTrieMap.of();
    this.stableKey = heading;
    this.indexCache = indexCache;
  }

  Relation(Heading heading, Map.Immutable<Row,Formula> rows, Heading stableKey, FormulaFactory ffactory, Cache<IndexCacheKey,IndexedRows> indexCache) {
    this.ffactory = ffactory;
    this.heading = heading;
    this.rows = rows;
    this.stableKey = stableKey;
    this.indexCache = indexCache;
  }

  public Heading getHeading() {
    return this.heading;
  }

  public boolean isClosed() {
    return this.stableKey.equals(this.heading);
  }

  public boolean isEmpty() { return rows.isEmpty(); }

  public Relation union(Relation other) {
    if (!this.heading.unionCompatible(other.heading)) {
      throw new IllegalArgumentException(String.format("Can not perform UNION on %s and %s", this.heading, other.heading));
    }

    if (this.isEmpty()) {
      return other.copy();
    }
    if (other.isEmpty()) {
      return this.copy();
    }

    if (!(isClosed() || other.isClosed())) {
      Map.Transient<Row,Formula> biggest;
      Map.Immutable<Row,Formula> smallest;
      if (rows.size() > other.rows.size()) {
        biggest = rows.asTransient();
        smallest = other.rows;
      } else {
        biggest = other.rows.asTransient();
        smallest = rows;
      }

      for (Row row : smallest.keySet()) {
        if (biggest.containsKey(row)) {
          biggest.__put(row, ffactory.or(biggest.get(row), smallest.get(row)));
        } else {
          biggest.__put(row, smallest.get(row));
        }
      }
      return new Relation(heading,biggest.freeze(),stableKey,ffactory,indexCache);
    } else {
      throw new UnsupportedOperationException("Not yet implemented");
    }
  }

  public Relation intersect(Relation other) { throw new UnsupportedOperationException(); }
  public Relation difference(Relation other) { throw new UnsupportedOperationException(); }

  public Relation join(Relation other) {
    List<String> joinOn = heading.joinedAttributes(other.heading);

    IndexedRows thisIndex = index(joinOn);
    IndexedRows otherIndex = other.index(joinOn);

    IndexedRows smallest;
    IndexedRows largest;
    if (thisIndex.size() > otherIndex.size()) {
      smallest = otherIndex;
      largest = thisIndex;
    } else {
      smallest = thisIndex;
      largest = otherIndex;
    }

    Map.Transient<Row,Formula> result = PersistentTrieMap.transientOf();
    for (Row key : smallest.indexedRows.keySet()) {
      if (largest.indexedRows.containsKey(key)) {
        for (RowAndConstraint left : smallest.indexedRows.get(key)) {
          for (RowAndConstraint right : smallest.indexedRows.get(key)) {
            Row joinedRow = RowFactory.
          }
        }
      }
    }
  }

  public Relation product(Relation other) { throw new UnsupportedOperationException(); }

  public Relation project(List<String> attributes) { throw new UnsupportedOperationException(); }
  public Relation rename(Map<String,String> renamings) { throw new UnsupportedOperationException(); }
  public Relation select() { throw new UnsupportedOperationException(); }

  public Relation copy() {
    return new Relation(this.heading,this.rows,this.stableKey,this.ffactory,this.indexCache);
  }

  private IndexedRows index(List<String> indexOn) {
    // check if this index is already in the cache
    IndexedRows indexed = indexCache.getIfPresent(IndexCacheKey.toKey(this.rows, indexOn));
    if (indexed != null) {
      return indexed;
    }

    indexed = new IndexedRows();

    // find positions of fields to index on
    int[] attPos = new int[indexOn.size()];
    for (int i = 0; i < indexOn.size(); i++) {
      attPos[i] = heading.position(indexOn.get(i));
    }

    Iterator<java.util.Map.Entry<Row, Formula>> iterator = rows.entryIterator();
    while (iterator.hasNext()) {
      java.util.Map.Entry<Row, Formula> entry = iterator.next();
      indexed.add(toKey(entry.getKey(), attPos), entry.getKey(), entry.getValue());
    }

    indexCache.put(IndexCacheKey.toKey(this.rows,indexOn),indexed);

    return indexed;
  }

  private Row toKey(Row row, int[] fields) {
    Cell[] cells = new Cell[fields.length];

    for (int i = 0; i < fields.length; i++) {
      cells[i] = row.cellAt(fields[i]);
    }

    return RowFactory.build(cells);
  }

  public static class IndexedRows {
    private final Map.Transient<Row, List<RowAndConstraint>> indexedRows;

    public IndexedRows() {
      this.indexedRows = PersistentTrieMap.Transient.of();
    }

    public void add(Row key, Row whole, Formula constraint) {
      List<RowAndConstraint> currentVal = indexedRows.get(key);
      if (currentVal != null) {
        indexedRows.__remove(key);
      } else {
        currentVal = new ArrayList<>();
      }
      currentVal.add(new RowAndConstraint(whole, constraint));
      indexedRows.__put(key, currentVal);
    }

    public int size() {
      return indexedRows.size();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      IndexedRows that = (IndexedRows) o;
      return Objects.equals(indexedRows, that.indexedRows);
    }

    @Override
    public int hashCode() {
      return Objects.hash(indexedRows);
    }
  }

  public static class IndexCacheKey {
    private final Map.Immutable<Row,Formula> rows;
    private final List<String> key;

    public IndexCacheKey(Map.Immutable<Row, Formula> rows, List<String> key) {
      this.rows = rows;
      this.key = key;
    }

    public static IndexCacheKey toKey(Map.Immutable<Row, Formula> rows, List<String> key) {
      return new IndexCacheKey(rows,key);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      IndexCacheKey that = (IndexCacheKey) o;
      return Objects.equals(rows, that.rows) &&
              Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {

      return Objects.hash(rows, key);
    }
  }
}


