package nl.cwi.swat.ast;

import nl.cwi.swat.ast.relational.Expression;
import nl.cwi.swat.ast.relational.Formula;

public class ProblemBuilder {



  public static class FormulaBuilder implements Builder<Formula,Expression>{
    public Formula build() { return null; }

//    public Builder forall(DeclBuilder decl)
  }

  public static class ExpressionBuilder {

  }
}

interface Builder<F,E>  {
  <F,E> Object build();
}