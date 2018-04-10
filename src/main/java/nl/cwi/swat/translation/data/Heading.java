package nl.cwi.swat.translation.data;

import nl.cwi.swat.ast.Domain;

import java.util.*;

public class Heading {
  private final List<Attribute> attributes;
  private final List<String> names;

  public Heading(List<Attribute> attributes) {
    this.attributes = attributes;
    this.names = new ArrayList<>(attributes.size());

    for (Attribute att : this.attributes) {
      this.names.add(att.name);
    }
  }

  public boolean unionCompatible(Heading other) {
    return this.equals(other);
  }

  public boolean isEmpty() {
    return attributes.isEmpty();
  }

  public Attribute get(int i) {
    return this.attributes.get(i);
  }

  public List<String> namesOnly() {
    return names;
  }

  public List<String> conjunctNames(Heading other) {
    List<String> joinedAttributes = new ArrayList<>();
    for (Attribute att : attributes) {
      if (other.attributes.contains(att)) {
        joinedAttributes.add(att.name);
      }
    }

    return joinedAttributes;
  }

  public Heading conjunct(Heading other) {
    List<Attribute> joined = new ArrayList<>();
    for (Attribute att : attributes) {
      if (other.attributes.contains(att)) {
        joined.add(att);
      }
    }

    return new Heading(joined);

  }

  public Heading disjunct(Heading other) {
    List<Attribute> joinedAtts = new ArrayList<>(this.attributes);

    for (Attribute att : other.attributes) {
      if (!joinedAtts.contains(att)) {
        joinedAtts.add(att);
      }
    }

    return new Heading(joinedAtts);
  }

  public boolean isDisjunct(Heading other) {
    for (String attName : other.namesOnly()) {
      if (names.contains(attName)) {
        return false;
      }
    }

    return true;
  }

  public void rename(Map<String,String> renamings) {
    for (int i = 0; i < this.attributes.size(); i++) {
      Attribute current = this.attributes.get(i);
      if (renamings.containsKey(current.name)) {
        this.attributes.remove(i);
        this.attributes.add(i, new Attribute(renamings.get(current.name), current.domain));
      }
    }
  }

  public boolean contains(Heading sub) {
    for (Attribute other : sub.attributes) {
      if (!this.attributes.contains(other)) {
        return false;
      }
    }

    return true;
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

  public Heading copy() {
    return new Heading(new ArrayList<>(this.attributes));
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

  @Override
  public String toString() {
    return "Heading{" +
            "attributes=" + attributes +
            '}';
  }

  public static class Attribute {
    private final String name;
    private final Domain domain;

    Attribute(String name, Domain domain) {
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
      Attribute attribute = (Attribute) o;
      return Objects.equals(name, attribute.name) &&
              Objects.equals(domain, attribute.domain);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, domain);
    }

    @Override
    public String toString() {
      return "Attribute{" +
              "name='" + name + '\'' +
              ", domain=" + domain +
              '}';
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
