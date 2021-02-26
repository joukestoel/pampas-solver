package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.ints.IntegerConstant;
import nl.cwi.swat.formulacircuit.ints.IntegerVariable;
import nl.cwi.swat.formulacircuit.rel.IdConstant;
import nl.cwi.swat.translation.data.row.Tuple;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The Heading of a {@link Relation}.
 * A heading describes which attributes (names and associated domains) are part
 * of the relation.
 * More formally (definition taken from 'An Introduction To Database Systems' by C.T.Date; 6th ed. 1994):
 * a Heading is a fixed set of attribute-name domain pairs {<A1:D1>,<A2,D2>...<An,Dn>} such that each attribute
 * Aj corresponds to exactly one domain Dj. The attribute names are arbitrary labels and are all distinct.
 *
 */
public class Heading implements Iterable<Attribute> {
  private final List<Attribute> attributes;
  private final Set<Attribute> attributesAsSet;

  private final boolean idsOnly;

  Heading(@NonNull List<Attribute> attributes) {
    this.attributes = Collections.unmodifiableList(attributes);
    this.attributesAsSet = Collections.unmodifiableSet(new HashSet<>(this.attributes));

    if (this.attributes.size() != attributesAsSet.size()) {
      // Some attribute definitions collapse into each other. This means they are not distinct.
      throw new IllegalArgumentException("Attributes must have distinct names");
    }

    idsOnly = onlyOfIdDomain(attributes);
  }

  private boolean onlyOfIdDomain(@NonNull List<Attribute> attributes) {
    for (Attribute at : attributes) {
      if (Domain.ID != at.getDomain()) {
        return false;
      }
    }

    return true;
  }

  public int arity() {
    return attributes.size();
  }

  boolean isUnionCompatible(@NonNull Heading other) {
    if (other.arity() != this.arity()) {
      return false;
    }

    return attributesAsSet.equals(other.attributesAsSet);
  }

  public Set<Integer> getAttributeIndices(@NonNull Set<String> attributeNames) {
    Set<Integer> indices = new HashSet<>(attributeNames.size());

    for (int i = 0; i < attributes.size(); i++) {
      if (attributeNames.contains(attributes.get(i).getName())) {
        indices.add(i);
      }
    }

    if (indices.size() != attributeNames.size()) {
      throw new IllegalArgumentException("Not all the given attribute names are part of this heading");
    }

    return indices;
  }

  public Set<Integer> getAttributeIndices() {
    return IntStream.range(0, attributes.size()).boxed().collect(Collectors.toUnmodifiableSet());
  }

  @NonNull
  public Iterator<Attribute> iterator() {
    return attributes.iterator();
  }

  Set<String> getNamesOfIdDomainAttributes() {
    return attributes.stream()
            .filter(f -> f.getDomain() == Domain.ID)
            .map(Attribute::getName)
            .collect(Collectors.toSet());
  }

  /**
   * @param index - needs to be in the range of attributes
   * @return
   */
  public String getAttributeNameAt(int index) {
    if (index < 0 || index >= arity()) {
      throw new IllegalArgumentException("Provided index is outside the arity of the relation");
    }

    return attributes.get(index).getName();
  }

  public Domain getDomainAt(int index) {
    if (index < 0 || index >= arity()) {
      throw new IllegalArgumentException("Provided index is outside the arity of the relation");
    }

    return attributes.get(index).getDomain();
  }

  /**
   * Creates a new heading with some attributes renamed.
   *
   * @param renamedAttributes the attributes to rename
   * @requires \forall String a | renamedFields.containsKey(a) | attributes.has(a),
   * @return new {@ref Heading} with the attributes renamed
   * @throws IllegalArgumentException if
   *  not all attributes in the {@code renamedFields} param were part of the original heading
   */
  public Heading rename(@NonNull Map<String, String> renamedAttributes) {
    if (renamedAttributes.size() > arity()) {
      throw new IllegalArgumentException("The number of attributes to rename is larger than the number of attributes in the heading");
    }

    int nrOfRenamedFields = 0;

    List<Attribute> newFields = new ArrayList<>(attributes);

    for (int i = 0; i < newFields.size(); i++) {
      Attribute fd = newFields.get(i);

      if (renamedAttributes.containsKey(fd.getName())) {
        newFields.set(i, new Attribute(renamedAttributes.get(fd.getName()), fd.getDomain()));
        nrOfRenamedFields += 1;
      }
    }

    if (nrOfRenamedFields != renamedAttributes.size()) {
      throw new IllegalArgumentException("Not all renamings could be applied. Do all the renamed attributes exist?");
    }

    return new Heading(newFields);
  }

  /**
   * Creates a new heading with some attributes projected.
   *
   * @param projectedAttributes the attributes to project out of the heading.
   * @requires (\forall String a | projectedFields.contains(a) | attributes.has(a))
   * @return new {@link Heading} containing only the projected attributes.
   * @throws IllegalArgumentException if
   *  not all attributes in the {@code projectedFields} were part of the original heading
   */
  public Heading project(@NonNull Set<String> projectedAttributes) {
    int nrOfProjectedFields = 0;

    List<Attribute> newFields = new ArrayList<>(projectedAttributes.size());

    for (Attribute fd : attributes) {
      if (projectedAttributes.contains(fd.getName())) {
        newFields.add(fd);
        nrOfProjectedFields += 1;
      }
    }

    if (nrOfProjectedFields != projectedAttributes.size()) {
      throw new IllegalArgumentException("Not all attributes could be projected. Do all the projected attributes exist?");
    }

    return new Heading(newFields);
  }

  public Heading join(@NonNull Heading other) {
    ArrayList<Attribute> joinedFields = new ArrayList<>(attributes);
    for (Attribute fd : other) {
      if (!joinedFields.contains(fd)) {
        joinedFields.add(fd);
      }
    }

    return new Heading(joinedFields);
  }

  public Set<String> getIntersectingAttributeNames(@NonNull Heading other) {
    Set<String> fieldNames = getAttributeNamesOnly();
    fieldNames.retainAll(other.getAttributeNamesOnly());
    return fieldNames;
  }

  boolean containsOnlyIdAttributes() {
    return idsOnly;
  }

  boolean isTupleCompatible(@NonNull Tuple tuple) {
    if (tuple.arity() != arity()) {
      return false;
    }

    for (int i = 0; i < arity(); i++) {
      Attribute fd = attributes.get(i);
      Expression att = tuple.getAttributeAt(i);

      if (fd.getDomain() == Domain.ID && !(att instanceof IdConstant)) {
        return false;
      } else if (fd.getDomain() == Domain.INT && (!(att instanceof IntegerConstant || att instanceof IntegerVariable))) {
        return false;
      }
    }

    return true;
  }

  private Set<String> getAttributeNamesOnly() {
    return attributesAsSet.stream()
            .map(Attribute::getName)
            .collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Heading that = (Heading) o;

    return attributes.equals(that.attributes);
  }

  @Override
  public int hashCode() {
    return attributes.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    boolean first = true;
    for (Attribute at : this) {
      if (!first) {
        sb.append(" | ");
      } else {
        first = false;
      }

      sb.append(at.getName()).append(" ").append("(").append(at.getDomain()).append(")");
    }

    return sb.toString();
  }
}
