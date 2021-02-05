package nl.cwi.swat.ast.relational;

import nl.cwi.swat.translation.TranslationVisitor;

public abstract class Expression extends Node {
  @Override
  public abstract <F, R, L> R accept(TranslationVisitor<F, R, L> visitor);
}
