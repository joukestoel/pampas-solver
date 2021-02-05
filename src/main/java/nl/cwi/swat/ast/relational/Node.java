package nl.cwi.swat.ast.relational;

import nl.cwi.swat.translation.TranslationVisitor;

public abstract class Node {
  public abstract <F,R,L> Object accept(TranslationVisitor<F,R,L> visitor);
}
