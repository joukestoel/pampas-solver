package nl.cwi.swat.translation.data.relation;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.relational.Literal;
import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.Formula;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.translation.data.relation.idsonly.BinaryIdRelation;
import nl.cwi.swat.translation.data.relation.idsonly.UnaryIdRelation;
import nl.cwi.swat.translation.data.row.Row;
import nl.cwi.swat.translation.data.row.RowConstraint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RelationFactory {
  private final FormulaFactory ff;
  private final Cache<AbstractRelation.IndexCacheKey, AbstractRelation.IndexedRows> indexCache;

  public RelationFactory(final FormulaFactory ff,
                         @NotNull Cache<AbstractRelation.IndexCacheKey, AbstractRelation.IndexedRows> indexCache) {
    this.ff = ff;
    this.indexCache = indexCache;
  }

  public Relation buildRelation(@NotNull Heading heading, @NotNull Map.Immutable<Row,RowConstraint> rows, boolean stable) {
    if (heading.idFieldsOnly()) {
      return buildIdsOnlyRelation(heading, rows);
    } else if (stable) {
      return buildStableRelation(heading, rows);
    } else {
      return buildUnstableRelation(heading, rows);
    }
  }

  private Relation buildIdsOnlyRelation(@NotNull Heading heading, @NotNull Map.Immutable<Row,RowConstraint> rows) {
    switch(heading.arity()) {
      case 1: return new UnaryIdRelation(heading, rows, this, ff, indexCache);
      case 2: return new BinaryIdRelation(heading, rows, this, ff, indexCache);
      default: throw new IllegalArgumentException(String.format("Unable to build an id only relation with arity %d", heading.arity()));
    }
  }

  private Relation buildStableRelation(@NotNull Heading heading, @NotNull Map.Immutable<Row,RowConstraint> rows) {
    switch(heading.arity()) {
//      case 1: return new UnaryStableRelation(heading, rows, this, ff);
      default: throw new IllegalArgumentException(String.format("Unable to build an stable relation with arity %d", heading.arity()));
    }
  }

  private Relation buildUnstableRelation(@NotNull Heading heading, @NotNull Map.Immutable<Row,RowConstraint> rows) {
    switch(heading.arity()) {
      default: throw new IllegalArgumentException(String.format("Unable to build an stable relation with arity %d", heading.arity()));
    }
  }

  class Builder {
    private Builder() {
    }

    public HeaderBuilder create() {
      return new HeaderBuilder();
    }

    private TupleBuilder phase1Done(@NotNull Heading heading) {
      return new TupleBuilder(new RelationUnderConstruction(heading));
    }

    class HeaderBuilder {
      private final List<FieldDefinition> fields;

      public HeaderBuilder() {
        this.fields = new ArrayList<>();
      }

      public HeaderBuilder add(@NotNull String name, @NotNull Domain dom) {
        fields.add(new FieldDefinition(name, dom));
        return this;
      }

      public TupleBuilder done() {
        return Builder.this.phase1Done(new HeadingImpl(fields));
      }

    }

    class TupleBuilder {
      private final RelationUnderConstruction rel;

      private TupleBuilder(@NotNull final RelationUnderConstruction rel) {
        this.rel = rel;
      }

      public TupleBuilder lower(Literal... values) {

        Row r = RowFactory.buildRow(attributes);
        RowConstraint rc = RowFactory.
        rel.
        return this;
      }

      public Relation done() {
        return RelationFactory.this.buildRelation(rel.heading, rel.rows.freeze(), rel.stable);
      }

      private Formula buildRowConstraints(Expression... attributes) {
        return null;
      }
    }
  }

  private static class RelationUnderConstruction  {
    private final Heading heading;
    private final Map.Transient<Row,RowConstraint> rows;

    private boolean stable;

    private RelationUnderConstruction(final Heading heading) {
      this.heading = heading;

      rows = PersistentTrieMap.transientOf();
      stable = heading.idFieldsOnly();
    }

    private void add(Row row, RowConstraint constraint) {

    }
  }
}
