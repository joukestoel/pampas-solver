package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.ast.Domain;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single field definition in a
 */
public class FieldDefinition {
  private final String name;
  private final Domain domain;

  /**
   * Constructs an FieldDefinition object. Part of the heading of a relation
   *
   * @param name field name
   * @param domain domain of the field
   */
  FieldDefinition(@NotNull final String name, @NotNull final Domain domain) {
    this.name = name;
    this.domain = domain;
  }

  public String getName() {
    return name;
  }

  public Domain getDomain() {
    return domain;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FieldDefinition that = (FieldDefinition) o;

    if (!name.equals(that.name)) return false;
    return domain.equals(that.domain);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + domain.hashCode();
    return result;
  }
}
