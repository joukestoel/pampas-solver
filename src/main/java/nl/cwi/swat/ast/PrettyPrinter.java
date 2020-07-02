package nl.cwi.swat.ast;

import nl.cwi.swat.ast.relational.*;

import java.util.List;
import java.util.stream.Collectors;

public class PrettyPrinter implements TranslationVisitor<String,String,String> {

  @Override
  public String visit(Subset subset) {
    return visit(subset, "in");
  }

  @Override
  public String visit(Equal equal) {
    return visit(equal, "=");
  }

  private String visit(ComparisonFormula f, String op) {
    String left = f.getLeft().accept(this);
    String right = f.getRight().accept(this);

    return String.format("%s %s %s", left, op, right);
  }

  @Override
  public String visit(And and) {
    return visit(and, "&&");
  }

  @Override
  public String visit(Or or) {
    return visit(or, "||");
  }

  private String visit(BinaryFormula f, String op) {
    String left = f.getLeft().accept(this);
    String right = f.getRight().accept(this);

    return String.format("%s %s %s", left, op, right);
  }

  @Override
  public String visit(Forall forall) {
    return visit(forall, "forall");
  }

  @Override
  public String visit(Exist exist) {
    return visit(exist, "exist");
  }

  private String visit(QuantifiedFormula f, String quant) {
    List<String> decls = f.getDecls().stream().map(d -> d.accept(this)).collect(Collectors.toList());
    String form = f.getFormula().accept(this);

    return String.format("%s %s | %s", quant, String.join(", ", decls), form);
  }

  @Override
  public String visit(Some some) {
    return visit(some, "some");
  }

  @Override
  public String visit(No no) {
    return visit(no, "no");
  }

  @Override
  public String visit(One one) {
    return visit(one, "one");
  }

  @Override
  public String visit(Lone lone) {
    return visit(lone, "lone");
  }

  private String visit(CardinalityFormula f, String comp) {
    String form = f.getExpr().accept(this);
    return String.format("%s %s", comp, form);
  }

  @Override
  public String visit(NaturalJoin naturalJoin) {
    return visit(naturalJoin, "|x|");
  }

  @Override
  public String visit(Product product) {
    return visit(product, "x");
  }

  private String visit(BinaryExpression bin, String op) {
    String left = bin.getLeft().accept(this);
    String right = bin.getRight().accept(this);

    return String.format("%s %s %s", left, op, right);
  }

  @Override
  public String visit(Declaration decl) {
    return String.format("%s: %s", decl.getVariable(), decl.getBinding().accept(this));
  }

  @Override
  public String visit(RelVar relVar) {
    return relVar.getName();
  }

  @Override
  public String visit(Id id) {
    return id.toString();
  }

  @Override
  public String visit(Hole hole) {
    return hole.toString();
  }
}
