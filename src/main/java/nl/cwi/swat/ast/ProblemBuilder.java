package nl.cwi.swat.ast;

import nl.cwi.swat.ast.relational.*;

import java.util.LinkedList;
import java.util.List;

public class ProblemBuilder {



  public static class FormulaBuilder implements Builder<Formula,Expression,List<Declaration>,Declaration,Literal> {
    private Builder scope;

    public FormulaBuilder(Builder scope) {
      this.scope = scope;
    }

    public Formula build() { return null; }

    public FormulaBuilder var(String name) {

      return this;
    }

    public DeclListBuilder forall() {
      return null;
    }
  }

//
//  public static class ExpressionBuilder implements Builder<Formula,Expression,List<Declaration>,Literal> {
//    @Override
//    public <F, E, D, L> E build() {
//      return null;
//    }
//  }
//
  public static class DeclListBuilder implements Builder<Formula,Expression,List<Declaration>,Declaration,Literal> {
    private Builder parent;
    private List<Builder> children;

    public DeclListBuilder(Builder parent) {
      this.parent = parent;
      this.children = new LinkedList<>();
    }

    public DeclBuilder decl() {
      return null;
    }

    public FormulaBuilder holds() {
      if (children.size() == 0) {
        throw new IllegalArgumentException("Can not construct a quantified formula without declarations");
      }
      return new FormulaBuilder(parent);
    }

    @Override
    public <F, E, DL, D, L> DL build() {
      return null;
    }
  }

  public static class DeclBuilder implements Builder <Formula,Expression,List<Declaration>,Declaration,Literal> {
    @Override
    public <F, E, DL, D, L> D build() {
      return null;
    }
  }
}

interface Builder<F,E,DL,D,L>  {
  <F,E,DL,D,L> Object build();
}