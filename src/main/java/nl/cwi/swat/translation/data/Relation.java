package nl.cwi.swat.translation.data;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.ast.*;
import nl.cwi.swat.ast.ints.IntDomain;
import nl.cwi.swat.ast.relational.Hole;
import nl.cwi.swat.ast.relational.Id;
import nl.cwi.swat.ast.relational.IdDomain;
import nl.cwi.swat.smtlogic.BooleanConstant;
import nl.cwi.swat.smtlogic.Formula;
import nl.cwi.swat.smtlogic.FormulaAccumulator;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.smtlogic.ints.IntConstant;
import nl.cwi.swat.smtlogic.ints.IntSort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Relation implements Iterable<Row> {
  private static final Logger log = LogManager.getLogger(Relation.class);

  private final FormulaFactory ffactory;
  private final Cache<IndexCacheKey,IndexedRows> indexCache;

  private final Heading heading;
  private final Map.Immutable<Row,Formula> rows;

  private Heading stableKey;

  private Relation(Heading heading, Map.Immutable<Row,Formula> rows, Heading stableKey, FormulaFactory ffactory, Cache<IndexCacheKey,IndexedRows> indexCache) {
    this.ffactory = ffactory;
    this.heading = heading;
    this.rows = rows;
    this.stableKey = stableKey;
    this.indexCache = indexCache;
  }

  public Relation empty() {
    return new Relation(this.heading, PersistentTrieMap.of(), this.stableKey, this.ffactory, this.indexCache);
  }

  public Heading getHeading() {
    return this.heading;
  }

  public Set<Row> getRows() {
    return this.rows.keySet();
  }

  public Formula getFormula(Row row) {
    return this.rows.get(row);
  }

  public Formula getFormulaOrFalse(Row row) {
    return this.rows.getOrDefault(row, BooleanConstant.FALSE);
  }

  public boolean isStable() {
    return this.stableKey.equals(this.heading);
  }

  public void invalidateCache() {
    indexCache.invalidateAll();
  }

  public boolean isEmpty() { return rows.isEmpty(); }

  public int size() {
    return rows.size();
  }

  public Relation union(Relation other) {
    log.trace("Performing union");
    if (!this.heading.unionCompatible(other.heading)) {
      throw new IllegalArgumentException(String.format("Can not perform UNION on %s and %s", this.heading, other.heading));
    }

    if (this.isEmpty()) {
      return other.copy();
    }
    if (other.isEmpty()) {
      return this.copy();
    }

    if (isStable() && other.isStable()) {
      return stableUnion(other);
    } else {
      throw new UnsupportedOperationException("Not yet implemented");
    }
  }

  private Relation stableUnion(Relation other) {
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
  }

  public Relation intersect(Relation other) { throw new UnsupportedOperationException(); }
  public Relation difference(Relation other) { throw new UnsupportedOperationException(); }

  public Relation join(Relation other) {
    log.trace("Performing join");
    Heading joinOn = heading.conjunct(other.heading);

    if (joinOn.isEmpty()) {
      throw new IllegalArgumentException("No joining attributes");
    }

    if (this.rows.isEmpty() || other.rows.isEmpty()) {
      return empty();
    }

    if (this.stableKey.contains(joinOn) && other.stableKey.contains(joinOn)) {
      return stableJoin(other, joinOn.namesOnly());
    } else {
      return unstableJoin(other, joinOn.namesOnly());
    }
  }

  private Relation unstableJoin(Relation other, List<String> joinOn) {
    throw new UnsupportedOperationException();
  }

  private Relation stableJoin(Relation other, List<String> joinOn) {
    IndexedRows smallest;
    IndexedRows largest;
    if (this.rows.size() > other.rows.size()) {
      smallest = other.index(joinOn);
      largest = index(joinOn);
    } else {
      smallest = index(joinOn);
      largest = other.index(joinOn);
    }

    Map.Transient<Row,Formula> result = PersistentTrieMap.transientOf();
    for (Row key: smallest.indexedRows.keySet()) {
      if (largest.indexedRows.containsKey(key)) {
        for (RowAndConstraint left : smallest.indexedRows.get(key)) {
          for (RowAndConstraint right : largest.indexedRows.get(key)) {
            Row joinedRow = joinRows(left.getRow(), right.getRow(), other.heading, joinOn);
            result.__put(joinedRow, ffactory.and(left.getConstraint(), right.getConstraint()));
          }
        }
      }
    }

    return new Relation(heading.disjunct(other.heading), result.freeze(), heading, ffactory, indexCache);
  }

  private Row joinRows(Row rowLeft, Row rowRight, Heading other, List<String> joinedAtts) {
    int[] rightSideJoinIndices = new int[joinedAtts.size()];
    for (int i = 0; i < rightSideJoinIndices.length; i++) {
      rightSideJoinIndices[i] = other.position(joinedAtts.get(i));
    }

    return rowLeft.append(rowRight, rightSideJoinIndices);
  }

  public Relation product(Relation other) {
    log.trace("Performing product");
    Heading newHeading = heading.disjunct(other.heading);

    if (newHeading.arity() != (heading.arity() + other.heading.arity()))  {
      throw new IllegalArgumentException("PRODUCT only works on relations with distinct attributes");
    }

    Map.Transient<Row, Formula> newRows = PersistentTrieMap.transientOf();
    for (Row left : this.rows.keySet()) {
      for (Row right : other.rows.keySet()) {
        newRows.__put(left.appendAll(right), ffactory.and(this.rows.get(left), other.rows.get(right)));
      }
    }

    return new Relation(newHeading, newRows.freeze(), this.stableKey.disjunct(other.stableKey), ffactory, indexCache);
  }

  public Relation project(List<String> attributes) { throw new UnsupportedOperationException(); }
  public Relation rename(Map<String,String> renamings) { throw new UnsupportedOperationException(); }
  public Relation select() { throw new UnsupportedOperationException(); }

  public Formula subset(Relation other) {
    log.trace("Performing subset check");

    if (!this.heading.unionCompatible(other.heading)) {
      throw new IllegalArgumentException(String.format("Can not perform UNION on %s and %s", this.heading, other.heading));
    }

    if (this.isEmpty()) {
      return BooleanConstant.TRUE;
    }
    else if (other.isEmpty()) {
      return BooleanConstant.FALSE;
    } else if (isStable() && other.isStable()) {
      return stableSubset(other);
    } else {
      throw new UnsupportedOperationException("Unstable subset check not yet implemented");
    }
  }

  private Formula stableSubset(Relation other) {
    FormulaAccumulator accumulator = FormulaAccumulator.AND();

    Iterator<Row> iterator = iterator();

    while (iterator.hasNext() && !accumulator.isShortCircuited()) {
      Row current = iterator.next();
      accumulator.add(ffactory.or(this.rows.get(current).negation(), other.getFormulaOrFalse(current)));
    }

    return ffactory.accumulate(accumulator);
  }

  public Relation singleton(Row row) {
    return new Relation(this.heading, PersistentTrieMap.of(row, BooleanConstant.TRUE), this.stableKey, this.ffactory, this.indexCache);
  }

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
    int[] attPos = getAttributePositions(indexOn);

    Iterator<java.util.Map.Entry<Row, Formula>> iterator = rows.entryIterator();
    while (iterator.hasNext()) {
      java.util.Map.Entry<Row, Formula> entry = iterator.next();
      indexed.add(toKey(entry.getKey(), attPos), entry.getKey(), entry.getValue());
    }

    indexCache.put(IndexCacheKey.toKey(this.rows,indexOn),indexed);

    return indexed;
  }

  private int[] getAttributePositions(List<String> indexOn) {
    int[] attPos = new int[indexOn.size()];
    for (int i = 0; i < indexOn.size(); i++) {
      attPos[i] = heading.position(indexOn.get(i));
    }
    return attPos;
  }

  private Row toKey(Row row, int[] fields) {
    Cell[] cells = new Cell[fields.length];

    for (int i = 0; i < fields.length; i++) {
      cells[i] = row.cellAt(fields[i]);
    }

    return RowFactory.build(cells);
  }

  @Override
  public String toString() {
    return "Relation{" +
            "heading=" + heading +
            ", rows=" + rows +
            ", stableKey=" + stableKey +
            '}';
  }

  @Override
  public Iterator<Row> iterator() {
    return rows.keyIterator();
  }

  public static class IndexedRows {
    private final Map.Transient<Row, List<RowAndConstraint>> indexedRows;

    public IndexedRows() {
      this.indexedRows = PersistentTrieMap.transientOf();
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

    public Map.Immutable<Row,Formula> flatten() {
      Map.Transient<Row,Formula> flattened = PersistentTrieMap.transientOf();
      for (List<RowAndConstraint> rows : indexedRows.values()) {
        for (RowAndConstraint r : rows) {
          flattened.__put(r.getRow(),r.getConstraint());
        }
      }

      return flattened.freeze();
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

  public static class RelationBuilder {
    private IndexedRows rows;
    private final Heading heading;
    private Heading stableKey;
    private final FormulaFactory ffactory;
    private final String relName;

    private final Cache<IndexCacheKey,IndexedRows> indexedRowsCache;

    private RelationBuilder(String relName, Heading heading, FormulaFactory ffactory, Cache<IndexCacheKey,IndexedRows> indexedRowsCache) {
      this.relName = relName;
      this.heading = heading;
      this.ffactory = ffactory;
      this.indexedRowsCache = indexedRowsCache;

      this.rows = new IndexedRows();
      this.stableKey = heading.copy();
    }

    public static RelationBuilder unary(String relName, String att1, Domain dom1, FormulaFactory ffactory, Cache<IndexCacheKey,IndexedRows> indexedRowsCache) {
      return new RelationBuilder(relName, new Heading.Builder().add(att1, dom1).build(), ffactory, indexedRowsCache);
    }

    public static RelationBuilder binary(String relName, String att1, Domain dom1, String att2, Domain dom2, FormulaFactory ffactory, Cache<IndexCacheKey,IndexedRows> indexedRowsCache) {
      return new RelationBuilder(relName, new Heading.Builder().add(att1, dom1).add(att2,dom2).build(), ffactory, indexedRowsCache);
    }

    public static RelationBuilder tenary(String relName, String att1, Domain dom1, String att2, Domain dom2, String att3, Domain dom3, FormulaFactory ffactory, Cache<IndexCacheKey,IndexedRows> indexedRowsCache) {
      return new RelationBuilder(relName, new Heading.Builder().add(att1, dom1).add(att2,dom2).add(att3,dom3).build(), ffactory, indexedRowsCache);
    }

    public static RelationBuilder nary(String relName, List<Heading.Attribute> attributes, FormulaFactory ffactory, Cache<IndexCacheKey,IndexedRows> indexedRowsCache) {
      return new RelationBuilder(relName, new Heading(attributes), ffactory, indexedRowsCache);
    }

    private boolean isStable() {
      return this.heading.equals(this.stableKey);
    }

    private RelationBuilder add(boolean optional, Row row) {
      if (isStable() && row.isStable()) {
        if (!rows.indexedRows.containsKey(row)) {
          this.rows.add(row, row, !optional ? BooleanConstant.TRUE : ffactory.newBoolVar(this.relName));
        } else if (!optional) {
          this.rows.add(row, row, BooleanConstant.TRUE);
        }
      } else {
        throw new UnsupportedOperationException("The addition of non-stable rows (rows with holes) is not yet implemented");
      }

      return this;
    }

    public RelationBuilder add(boolean optional, Object... values) {
      if (values.length != this.heading.arity()) {
        throw new IllegalArgumentException("Number of supplied values does not correspond with the number of attributes in the relation");
      }

      Cell[] cells = new Cell[values.length];
      for (int i = 0; i < values.length; i++) {
        if (values[i] instanceof String && (heading.get(i).getDomain() instanceof IdDomain)) {
          cells[i] = new IdCell(new Id((String) values[i]));
        } else if (values[i] instanceof Integer && heading.get(i).getDomain() instanceof IntDomain) {
          cells[i] = new LiteralCell(new IntConstant((Integer) values[i]));
        } else if (values[i] instanceof Hole && heading.get(i).getDomain() instanceof IntDomain) {
          cells[i] = new HoleCell(ffactory.newVar(IntSort.INT, this.relName));
        }
        else {
          throw new IllegalArgumentException(String.format("Supplied cell value \'%s\' is not of the attribute domain \'%s\'", values[i],  heading.get(i).getDomain()));
        }
      }

      Row row = RowFactory.build(cells);
      return add(optional,row);
    }

    public RelationBuilder lowerBound(java.util.Map<String,Cell> tuple) {
      return add(false, RowFactory.build(tuple, this.heading));
    }

    public RelationBuilder lowerBound(Object... values) {
      return add(false, values);
    }

    public RelationBuilder upperBound(java.util.Map<String,Cell> tuple) {
      return add(true, RowFactory.build(tuple, this.heading));
    }

    public RelationBuilder upperBound(Object... values) {
      return add(true, values);
    }

    public Relation build() {
      Map.Immutable<Row,Formula> flattened = rows.flatten();

      if (!isStable()) {
        // add the indexed rows to the cache as probably every operation on this relation will require this indexed version
        indexedRowsCache.put(IndexCacheKey.toKey(flattened, this.stableKey.namesOnly()), rows);
      }

      return new Relation(this.heading, flattened, this.stableKey, this.ffactory, this.indexedRowsCache);
    }
  }
}


