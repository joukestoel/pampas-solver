package nl.cwi.swat.translation.data.relation;

import com.github.benmanes.caffeine.cache.Cache;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.ints.IntLiteral;
import nl.cwi.swat.ast.relational.Hole;
import nl.cwi.swat.ast.relational.Id;
import nl.cwi.swat.ast.relational.Literal;
import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.bool.BooleanAccumulator;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import nl.cwi.swat.translation.data.relation.idsonly.BinaryIdRelation;
import nl.cwi.swat.translation.data.relation.idsonly.UnaryIdRelation;
import nl.cwi.swat.translation.data.row.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RelationFactory {
  private final FormulaFactory ff;
  private final Cache<AbstractRelation.IndexCacheKey, AbstractRelation.IndexedRows> indexCache;

  public RelationFactory(final FormulaFactory ff,
                         @NonNull Cache<AbstractRelation.IndexCacheKey, AbstractRelation.IndexedRows> indexCache) {
    this.ff = ff;
    this.indexCache = indexCache;
  }

  public Relation buildRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows, boolean stable) {
    if (heading.containsOnlyIdAttributes()) {
      return buildIdsOnlyRelation(heading, rows);
    } else if (stable) {
      return buildStableRelation(heading, rows);
    } else {
      return buildUnstableRelation(heading, rows);
    }
  }

  private Relation buildIdsOnlyRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows) {
    switch(heading.arity()) {
      case 1: return new UnaryIdRelation(heading, rows, this, ff, indexCache);
      case 2: return new BinaryIdRelation(heading, rows, this, ff, indexCache);
      default: throw new IllegalArgumentException(String.format("Unable to build an id only relation with arity %d", heading.arity()));
    }
  }

  private Relation buildStableRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows) {
    switch(heading.arity()) {
//      case 1: return new UnaryStableRelation(heading, rows, this, ff);
      default: throw new IllegalArgumentException(String.format("Unable to build an stable relation with arity %d", heading.arity()));
    }
  }

  private Relation buildUnstableRelation(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows) {
    switch(heading.arity()) {
      default: throw new IllegalArgumentException(String.format("Unable to build an stable relation with arity %d", heading.arity()));
    }
  }

  class Builder {
    private String relName;

    private Builder() {
    }

    public HeaderBuilder create(@NonNull  String relName) {
      this.relName = relName;
      return new HeaderBuilder();
    }

    private TupleBuilder phase1Done(@NonNull Heading heading) {
      return new TupleBuilder(new RelationUnderConstruction(heading));
    }

    class HeaderBuilder {
      private final List<Attribute> fields;

      public HeaderBuilder() {
        this.fields = new ArrayList<>();
      }

      public HeaderBuilder add(@NonNull String name, @NonNull Domain dom) {
        fields.add(new Attribute(name, dom));
        return this;
      }

      public TupleBuilder done() {
        return Builder.this.phase1Done(new Heading(fields));
      }

    }

    class TupleBuilder {
      private final RelationUnderConstruction rel;

      private TupleBuilder(@NonNull final RelationUnderConstruction rel) {
        this.rel = rel;
      }

      public TupleBuilder lower(@NonNull Literal... values) {
        Tuple r = TupleFactory.buildTuple(convertToExpressions(values));
        rel.add(r, BooleanConstant.TRUE);

        return this;
      }

      public TupleBuilder upper(@NonNull Literal... values) {
        Tuple r = TupleFactory.buildTuple(convertToExpressions(values));
        rel.add(r, ff.boolVar(Builder.this.relName));

        return this;
      }

      public Relation done() {
        return RelationFactory.this.buildRelation(rel.heading, rel.build(), rel.stable);
      }

      private Expression[] convertToExpressions(@NonNull Literal[] literals) {
        Expression[] exprs = new Expression[literals.length];

        for (int i = 0; i < literals.length; i++) {
          exprs[i] = convertLiteral(literals[i], i);
        }

        return exprs;
      }

      private Expression convertLiteral(Literal literal, int pos) {
        // TODO: Maybe better to have a visitor for this

        if (literal instanceof Id) {
          return ff.idConst(((Id) literal).getValue());
        } else if (literal instanceof IntLiteral) {
          return ff.intConst(((IntLiteral)literal).getValue());
        } else if (Hole.HOLE == literal) {

          if (Domain.INT == rel.heading.getDomainAt(pos)) {
            return RelationFactory.this.ff.intVar(Builder.this.relName);
          } else {
             throw new IllegalStateException("No conversion for literal " + literal);
          }
        } else {
          // Could not be converted, should not happen
          throw new IllegalArgumentException("Could not convert literal. Strange ... should not happen");
        }
      }
    }
  }

  private class RelationUnderConstruction extends AbstractRelation {
    private final IndexedRows indexedRows;

    private boolean stable;
    private final Set<String> partialKey;
    private final Set<Integer> partialKeyIndices;

    RelationUnderConstruction(@NonNull Heading heading) {
      this(heading, PersistentTrieMap.of(), RelationFactory.this, RelationFactory.this.ff, RelationFactory.this.indexCache);
    }

    private RelationUnderConstruction(@NonNull Heading heading, Map.Immutable<Tuple, Constraint> rows,
                                     @NonNull RelationFactory rf, @NonNull FormulaFactory ff,
                                     @NonNull Cache<IndexCacheKey, IndexedRows> indexCache) {
      super(heading, rows, rf, ff, indexCache);

      partialKey = heading.getNamesOfIdDomainAttributes();
      partialKeyIndices = heading.getAttributeIndices(partialKey);

      indexedRows = index(partialKey);

      stable = true;
    }

    @Override
    public int arity() {
      return heading.arity();
    }

    @Override
    public boolean isStable() {
      return stable;
    }

    Map.Immutable<Tuple, Constraint> build() {
      return indexedRows.flatten();
    }

    private void add(Tuple tuple, Formula exists) {
      if (!heading.isTupleCompatible(tuple)) {
        throw new IllegalArgumentException("Tuple to be added is not compatible with relation");
      }

      Tuple key = TupleFactory.buildPartialTuple(tuple, partialKeyIndices);
      Optional<io.usethesource.capsule.Set.Transient<TupleAndConstraint>> existingRows = indexedRows.get(key);

      if (!existingRows.isPresent()) {
        // tuple (or partial tuple) does not yet exists, can be safely added
        indexedRows.add(key, tuple, TupleConstraintFactory.buildConstraint(exists));
      } else {
        // partial tuple does already exist, tuples could potentially collapse into each other, add constraints to prevent this
        indexedRows.add(key, tuple, TupleConstraintFactory.buildConstraint(exists, constraintAttributes(tuple, existingRows.get())));

        // flip the stable property. Since overlap is possible this is not a stable relation anymore
        stable = false;
      }
    }

    private Formula constraintAttributes(Tuple toBeAdded, Set<TupleAndConstraint> overlappingRows) {
      BooleanAccumulator outerAnd = BooleanAccumulator.AND();

      for (TupleAndConstraint rac : overlappingRows) {
        // Build a and gate constraining all the attributes to be equal
        BooleanAccumulator innerAnd = BooleanAccumulator.AND();
        for (int i = 0; i < toBeAdded.arity(); i++) {
          if (!partialKeyIndices.contains(i)) {
            innerAnd.add(ff.equal(toBeAdded.getAttributeAt(i), rac.getTuple().getAttributeAt(i)));
          }
        }
        // build the implication; if the other row exists -> some of the attributes must be different in order for the rows not to collapse into eachother
        outerAnd.add(ff.or(rac.getConstraint().exists().negation(), ff.accumulateBools(innerAnd).negation()));
      }

      return ff.accumulateBools(outerAnd);
    }

    @Override
    public Relation rename(java.util.Map<String, String> renamings) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Relation project(Set<String> projectedAttributes) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Relation restrict() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Relation union(Relation other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Relation intersect(Relation other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Relation difference(Relation other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Formula subset(Relation other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Relation naturalJoin(Relation other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Relation aggregate() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Relation asSingleton(Tuple tuple) {
      throw new UnsupportedOperationException();
    }
  }
}
