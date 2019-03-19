package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.ast.Domain;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a single attribute definition.
 */
public class Attribute {
  private final String name;
  private final Domain domain;

  /**
   * Constructs an Attribute object. Part of the heading of a relation
   *
   * @param name field name
   * @param domain domain of the field
   */
  Attribute(@NonNull final String name, @NonNull final Domain domain) {
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

    Attribute that = (Attribute) o;

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
