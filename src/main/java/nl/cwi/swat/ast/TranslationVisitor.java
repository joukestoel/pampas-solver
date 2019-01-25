package nl.cwi.swat.ast;

import nl.cwi.swat.ast.relational.*;

public interface TranslationVisitor <F,R,L> {
  F visit(Subset subset);
  F visit(Equal equal);
  F visit(And and);
  F visit(Or or);
  F visit(Forall forall);
  F visit(Exist exist);

  F visit(Some some);
  F visit(No no);
  F visit(One one);
  F visit(Lone lone);

  R visit(NaturalJoin naturalJoin);
  R visit(RelVar relVar);
  R visit(Product product);

  R visit(Declaration decl);

  L visit(Id id);
  L visit(Hole hole);
}
