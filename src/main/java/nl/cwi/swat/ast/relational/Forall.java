package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

import java.util.List;

public class Forall extends QuantifiedFormula {
  public Forall(List<Declaration> decls, Formula formula) {
    super(decls, formula);
  }

  @Override
  public <F,R,L> F accept(TranslationVisitor<F,R,L> visitor) {
    return visitor.visit(this);
  }
}
