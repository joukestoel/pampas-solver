package nl.cwi.swat.translation;

import nl.cwi.swat.ast.*;
import nl.cwi.swat.ast.relational.*;
import nl.cwi.swat.smtlogic.BooleanConstant;
import nl.cwi.swat.smtlogic.Formula;
import nl.cwi.swat.smtlogic.FormulaAccumulator;
import nl.cwi.swat.smtlogic.FormulaFactory;
import nl.cwi.swat.translation.data.Relation;
import nl.cwi.swat.translation.data.Row;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static nl.cwi.swat.smtlogic.BooleanConstant.FALSE;
import static nl.cwi.swat.smtlogic.BooleanConstant.TRUE;

@Singleton
public class Translator implements TranslationVisitor<Formula, Relation, Literal> {

  private final FormulaFactory ffactory;
  private final TranslationCache cache;

  private Environment environment;

  @Inject
  public Translator(FormulaFactory ffactory, TranslationCache cache) {
    this.ffactory = ffactory;
    this.cache = cache;
  }

  public void setBaseEnvironment(Environment environment) {
    this.environment = environment;
  }

  public Formula translate(Set<nl.cwi.swat.ast.relational.Formula> constraints) {
    FormulaAccumulator accumulator = FormulaAccumulator.AND();
    Iterator<nl.cwi.swat.ast.relational.Formula> it = constraints.iterator();

    while (it.hasNext() && !accumulator.isShortCircuited()) {
      accumulator.add(it.next().accept(this));
    }

    return ffactory.accumulate(accumulator);
  }

  @Override
  public Formula visit(Subset subset) {
    Optional<Formula> cached = cache.fetch(subset, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation left = subset.getLeft().accept(this);
    Relation right = subset.getRight().accept(this);

    return cache.storeAndReturn(subset, environment, left.subset(right));
  }

  @Override
  public Formula visit(Equal equal) {
    return visit(new And(new Subset(equal.getLeft(),equal.getRight()),new Subset(equal.getRight(), equal.getLeft())));
  }

  @Override
  public Formula visit(And and) {
    Optional<Formula> cached = cache.fetch(and, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    Formula left = and.getLeft().accept(this);
    if (left == BooleanConstant.FALSE) {
      return BooleanConstant.FALSE;
    }
    Formula right = and.getRight().accept(this);

    return cache.storeAndReturn(and, environment, ffactory.and(left,right));
  }

  @Override
  public Formula visit(Or or) {
    Optional<Formula> cached = cache.fetch(or, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    Formula left = or.getLeft().accept(this);
    if (left == BooleanConstant.TRUE) {
      return BooleanConstant.TRUE;
    }
    Formula right = or.getRight().accept(this);

    return cache.storeAndReturn(or, environment, ffactory.or(left,right));
  }

  @Override
  public Formula visit(Forall forall) {
    Optional<Formula> cached = cache.fetch(forall, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    final FormulaAccumulator accumulator = FormulaAccumulator.AND();
    translateForall(forall.getDecls(), forall.getFormula(), 0, BooleanConstant.FALSE, accumulator);

    return cache.storeAndReturn(forall, environment, ffactory.accumulate(accumulator));
  }

  private void translateForall(List<Declaration> decls, nl.cwi.swat.ast.relational.Formula formula, int currentDecl, Formula declConstraintValue, FormulaAccumulator accumulator) {
    if (accumulator.isShortCircuited()) {
      return;
    }

    if (decls.size() == currentDecl) {
      accumulator.add(ffactory.or(declConstraintValue, formula.accept(this)));
      return;
    }

    final Declaration decl = decls.get(currentDecl);
    final Relation declRel = decl.getBinding().accept(this);
    final Environment origEnv = environment;

    Iterator<Row> iterator = declRel.iterator();
    while (iterator.hasNext() && !accumulator.isShortCircuited()) {
      Row current = iterator.next();
      environment = origEnv.extend(decl.getVariable(), declRel.singleton(current));
      Formula newConstraintVal = ffactory.or(ffactory.not(declRel.getFormula(current)), declConstraintValue);

      translateForall(decls, formula, currentDecl + 1, newConstraintVal, accumulator);
    }

    environment = origEnv;
  }

  @Override
  public Formula visit(Exist exist) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Formula visit(Some some) {
    Optional<Formula> cached = cache.fetch(some, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation r = some.getExpr().accept(this);
    if (r.isEmpty()) {
      return FALSE;
    }

    Iterator<Row> it = r.iterator();

    FormulaAccumulator accumulator = FormulaAccumulator.OR();

    while (it.hasNext() && !accumulator.isShortCircuited()) {
      accumulator.add(r.getFormula(it.next()));
    }

    return cache.storeAndReturn(some, environment, ffactory.accumulate(accumulator));
  }

  @Override
  public Formula visit(No no) {
    Optional<Formula> cached = cache.fetch(no, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    Formula result = visit(new Some(no.getExpr())).negation();
    return cache.storeAndReturn(no, environment, result);
  }

  @Override
  public Formula visit(One one) {
    Optional<Formula> cached = cache.fetch(one, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation r = one.getExpr().accept(this);
    if (r.isEmpty()) {
      return FALSE;
    }

    FormulaAccumulator accumulator = FormulaAccumulator.AND();
    Formula partial = FALSE;

    Iterator<Row> it = r.iterator();
    while (it.hasNext() && !accumulator.isShortCircuited()) {
      Formula f = r.getFormula(it.next());
      accumulator.add(ffactory.or(f.negation(), partial.negation()));
      partial = ffactory.or(partial, f);
    }

    accumulator.add(partial);

    return cache.storeAndReturn(one, environment, ffactory.accumulate(accumulator));
  }

  @Override
  public Formula visit(Lone lone) {
    Optional<Formula> cached = cache.fetch(lone, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation r = lone.getExpr().accept(this);
    if (r.isEmpty()) {
      return TRUE;
    }

    FormulaAccumulator accumulator = FormulaAccumulator.OR();
    Formula partial = FALSE;

    Iterator<Row> it = r.iterator();

    while (it.hasNext() && !accumulator.isShortCircuited()) {
      Formula f = r.getFormula(it.next());
      accumulator.add(ffactory.or(f.negation(), partial.negation()));
      partial = ffactory.or(partial, f);
    }

    return cache.storeAndReturn(lone, environment, ffactory.accumulate(accumulator));
  }

  @Override
  public Relation visit(NaturalJoin naturalJoin) {
    Optional<Relation> cached = cache.fetch(naturalJoin, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation left = naturalJoin.getLeft().accept(this);
    Relation right = naturalJoin.getRight().accept(this);

    return cache.storeAndReturn(naturalJoin, environment, left.join(right));
  }

  @Override
  public Relation visit(RelVar relVar) {
    Relation rel = environment.get(relVar.getName());
    if (rel == null) {
      throw new IllegalArgumentException(String.format("No relation found with name \'%s\'", relVar.getName()));
    }

    return rel;
  }

  @Override
  public Relation visit(Product product) {
    Optional<Relation> cached = cache.fetch(product, environment);
    if (cached.isPresent()) {
      return cached.get();
    }

    Relation left = product.getLeft().accept(this);
    Relation right = product.getRight().accept(this);

    return cache.storeAndReturn(product, environment, left.product(right));
  }

  @Override
  public Relation visit(Declaration decl) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Literal visit(Id id) {
    throw new UnsupportedOperationException();
  }
}
