package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.TranslationVisitor;

import java.util.Objects;

public class Declaration extends Node {
  private final String variable;
  private final Expression binding;

  public Declaration(String variable, Expression binding) {
    this.variable = variable;
    this.binding = binding;
  }

  public String getVariable() {
    return variable;
  }

  public Expression getBinding() {
    return binding;
  }

  @Override
  public <F,R,L> R accept(TranslationVisitor<F,R,L> visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Declaration that = (Declaration) o;
    return Objects.equals(variable, that.variable) &&
            Objects.equals(binding, that.binding);
  }

  @Override
  public int hashCode() {

    return Objects.hash(variable, binding);
  }
}
