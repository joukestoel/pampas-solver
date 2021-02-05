package nl.cwi.swat.ast.relational;

import nl.cwi.swat.translation.TranslationVisitor;

import java.util.Objects;

public class RelVar extends Expression {
  private final String name;

  public RelVar(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RelVar relVar = (RelVar) o;
    return Objects.equals(name, relVar.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public <F, R, L> R accept(TranslationVisitor<F, R, L> visitor) {
    return visitor.visit(this);
  }
}
