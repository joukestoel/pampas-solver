package nl.cwi.swat.translation.data;

import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.IdDomain;

import java.util.*;

public class Heading {
  final List<Attribute> attributes;

  private Heading(List<Attribute> attributes) {
    this.attributes = attributes;
  }

  public boolean unionCompatible(Heading other) {
    return this.equals(other);
  }

  public boolean idOnly() {
    for (Attribute att : this.attributes) {
      if (!att.domain.equals(IdDomain.ID)) {
        return false;
      }
    }

    return true;
  }

  public boolean isEmpty() {
    return attributes.isEmpty();
  }

  public List<String> joinedAttributes(Heading other) {
    List<String> joinedAttributes = new ArrayList<>();
    for (Attribute att : attributes) {
      if (other.attributes.contains(att)) {
        joinedAttributes.add(att.name);
      }
    }

    return joinedAttributes;
  }

  int position(String attribute) {
    for (int i = 0; i < attributes.size(); i++) {
      if (attributes.get(i).name.equals(attribute)) {
        return i;
      }
    }

    throw new IllegalArgumentException(String.format("Attribute %s is not part of the relation", attribute));
  }

  public int arity() {
    return attributes.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Heading heading = (Heading) o;
    return Objects.equals(attributes, heading.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributes);
  }

  private static class Attribute {
    private final String name;
    private final Domain domain;

    Attribute(String name, Domain domain) {
      this.name = name;
      this.domain = domain;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Attribute attribute = (Attribute) o;
      return Objects.equals(name, attribute.name) &&
              Objects.equals(domain, attribute.domain);
    }

    @Override
    public int hashCode() {

      return Objects.hash(name, domain);
    }
  }

  public static class Builder {
    private List<Attribute> atts;

    public Builder() {
      this.atts = new ArrayList<>();
    }

    public Heading build() {
      return new Heading(atts);
    }

    public Builder add(String name, Domain domain) {
      atts.add(new Attribute(name, domain));
      return this;
    }
  }

}
