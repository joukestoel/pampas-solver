package nl.cwi.swat.translation;

import nl.cwi.swat.ast.relational.*;
import nl.cwi.swat.ast.relational.Literal;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.formulacircuit.bool.BooleanAccumulator;
import nl.cwi.swat.formulacircuit.bool.BooleanConstant;
import nl.cwi.swat.translation.data.relation.Relation;
import nl.cwi.swat.translation.data.row.Tuple;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static nl.cwi.swat.formulacircuit.bool.BooleanConstant.FALSE;
import static nl.cwi.swat.formulacircuit.bool.BooleanConstant.TRUE;

@Singleton
public class Translator implements TranslationVisitor<Formula, Relation, Literal> {

  private final FormulaFactory ffactory;
  private final TranslationCache cache;

  private Environment env;

  @Inject
  public Translator(@NonNull FormulaFactory ffactory, @NonNull TranslationCache cache) {
    this.ffactory = ffactory;
    this.cache = cache;
  }

  public Formula translate(@NonNull Environment env, Set<nl.cwi.swat.ast.relational.Formula> constraints) {
    this.env = env;

    BooleanAccumulator accumulator = BooleanAccumulator.AND();
    Iterator<nl.cwi.swat.ast.relational.Formula> it = constraints.iterator();

    while (it.hasNext() && !accumulator.isShortCircuited()) {
      accumulator.add(it.next().accept(this));
    }

    return ffactory.accumulateBools(accumulator);
  }

  @Override
  public Formula visit(@NonNull Subset subset) {
    Optional<Formula> cached = cache.fetch(subset, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation left = subset.getLeft().accept(this);
    Relation right = subset.getRight().accept(this);

    return cache.storeAndReturn(subset, env, left.subset(right));
  }

  @Override
  public Formula visit(@NonNull Equal equal) {
    Optional<Formula> cached = cache.fetch(equal, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation left = equal.getLeft().accept(this);
    Relation right = equal.getRight().accept(this);

    return cache.storeAndReturn(equal, env, left.equal(right));
  }

  @Override
  public Formula visit(@NonNull And and) {
    Optional<Formula> cached = cache.fetch(and, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Formula left = and.getLeft().accept(this);
    if (left == BooleanConstant.FALSE) {
      return BooleanConstant.FALSE;
    }
    Formula right = and.getRight().accept(this);

    return cache.storeAndReturn(and, env, ffactory.and(left,right));
  }

  @Override
  public Formula visit(@NonNull Or or) {
    Optional<Formula> cached = cache.fetch(or, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Formula left = or.getLeft().accept(this);
    if (left == BooleanConstant.TRUE) {
      return BooleanConstant.TRUE;
    }
    Formula right = or.getRight().accept(this);

    return cache.storeAndReturn(or, env, ffactory.or(left,right));
  }

  @Override
  public Formula visit(@NonNull Forall forall) {
    Optional<Formula> cached = cache.fetch(forall, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    final BooleanAccumulator accumulator = BooleanAccumulator.AND();
    translateForall(forall.getDecls(), forall.getFormula(), 0, BooleanConstant.FALSE, accumulator);

    return cache.storeAndReturn(forall, env, ffactory.accumulateBools(accumulator));
  }

  private void translateForall(@NonNull List<Declaration> decls, nl.cwi.swat.ast.relational.Formula formula,
                               int currentDecl, @NonNull Formula declConstraintValue,
                               @NonNull BooleanAccumulator accumulator) {
    if (accumulator.isShortCircuited()) {
      return;
    }

    if (decls.size() == currentDecl) {
      accumulator.add(ffactory.or(declConstraintValue, formula.accept(this)));
      return;
    }

    final Declaration decl = decls.get(currentDecl);
    final Relation declRel = decl.getBinding().accept(this);
    final Environment origEnv = env;

    Iterator<Tuple> iterator = declRel.iterator();
    while (iterator.hasNext() && !accumulator.isShortCircuited()) {
      Tuple current = iterator.next();
      env = origEnv.extend(decl.getVariable(), declRel.asSingleton(current));
      Formula newConstraintVal = ffactory.or(ffactory.not(declRel.getCombinedConstraints(current)), declConstraintValue);

      translateForall(decls, formula, currentDecl + 1, newConstraintVal, accumulator);
    }

    env = origEnv;
  }

  @Override
  public Formula visit(Exist exist) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Formula visit(@NonNull Some some) {
    Optional<Formula> cached = cache.fetch(some, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation r = some.getExpr().accept(this);
    if (r.isEmpty()) {
      return FALSE;
    }

    Iterator<Tuple> it = r.iterator();

    BooleanAccumulator accumulator = BooleanAccumulator.OR();

    while (it.hasNext() && !accumulator.isShortCircuited()) {
      accumulator.add(r.getCombinedConstraints(it.next()));
    }

    return cache.storeAndReturn(some, env, ffactory.accumulateBools(accumulator));
  }

  @Override
  public Formula visit(@NonNull No no) {
    Optional<Formula> cached = cache.fetch(no, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Formula result = visit(new Some(no.getExpr())).negation();
    return cache.storeAndReturn(no, env, result);
  }

  @Override
  public Formula visit(@NonNull One one) {
    Optional<Formula> cached = cache.fetch(one, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation r = one.getExpr().accept(this);
    if (r.isEmpty()) {
      return FALSE;
    }

    BooleanAccumulator accumulator = BooleanAccumulator.AND();
    Formula partial = FALSE;

    Iterator<Tuple> it = r.iterator();
    while (it.hasNext() && !accumulator.isShortCircuited()) {
      Formula f = r.getCombinedConstraints(it.next());
      accumulator.add(ffactory.or(f.negation(), partial.negation()));
      partial = ffactory.or(partial, f);
    }

    accumulator.add(partial);

    return cache.storeAndReturn(one, env, ffactory.accumulateBools(accumulator));
  }

  @Override
  public Formula visit(@NonNull Lone lone) {
    Optional<Formula> cached = cache.fetch(lone, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation r = lone.getExpr().accept(this);
    if (r.isEmpty()) {
      return TRUE;
    }

    BooleanAccumulator accumulator = BooleanAccumulator.AND();
    Formula partial = FALSE;

    Iterator<Tuple> it = r.iterator();

    while (it.hasNext() && !accumulator.isShortCircuited()) {
      Formula f = r.getCombinedConstraints(it.next());
      accumulator.add(ffactory.or(f.negation(), partial.negation()));
      partial = ffactory.or(partial, f);
    }

    return cache.storeAndReturn(lone, env, ffactory.accumulateBools(accumulator));
  }

  @Override
  public Relation visit(NaturalJoin naturalJoin) {
    Optional<Relation> cached = cache.fetch(naturalJoin, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation left = naturalJoin.getLeft().accept(this);
    Relation right = naturalJoin.getRight().accept(this);

    return cache.storeAndReturn(naturalJoin, env, left.naturalJoin(right));
  }

  @Override
  public Relation visit(RelVar relVar) {
    Relation rel = env.get(relVar.getName());
    if (rel == null) {
      throw new IllegalArgumentException(String.format("No relation found with name \'%s\'", relVar.getName()));
    }

    return rel;
  }

  @Override
  public Relation visit(Product product) {
    Optional<Relation> cached = cache.fetch(product, env);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation left = product.getLeft().accept(this);
    Relation right = product.getRight().accept(this);

    return cache.storeAndReturn(product, env, left.product(right));
  }

  @Override
  public Relation visit(Declaration decl) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Literal visit(Id id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Literal visit(Hole hole) {
    throw new UnsupportedOperationException();
  }
}
