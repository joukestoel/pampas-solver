package nl.cwi.swat.ast.relational;

import nl.cwi.swat.translation.TranslationVisitor;

import java.util.List;

public class Exist extends QuantifiedFormula {

  public Exist(List<Declaration> decls, Formula formula) {
    super(decls, formula);
  }

  @Override
  public <F,R,L> F accept(TranslationVisitor<F,R,L> visitor) {
    return visitor.visit(this);
  }


}
