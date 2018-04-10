package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

public abstract class Formula extends Node {
  @Override
  public abstract <F,R,L> F accept(TranslationVisitor<F,R,L> visitor);
}
