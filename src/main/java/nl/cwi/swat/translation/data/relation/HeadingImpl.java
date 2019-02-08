package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.ints.IntDomain;
import nl.cwi.swat.ast.relational.IdDomain;
import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.IdAtom;
import nl.cwi.swat.smtlogic.ints.IntConstant;
import nl.cwi.swat.smtlogic.ints.IntVariable;
import nl.cwi.swat.translation.data.row.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class HeadingImpl implements Heading {
  private final List<Attribute> attributes;

  private boolean idsOnly;

  HeadingImpl(@NotNull List<Attribute> attributes) {
    idsOnly = true;

    Set<String> attNames = new HashSet<>(attributes.size());
    for (Attribute fd : attributes) {
      if (attNames.contains(fd.getName())) {
        throw new IllegalArgumentException("Attribute names in heading must be distinct");
      }

      idsOnly = fd.getDomain() == IdDomain.ID;
      attNames.add(fd.getName());
    }

    this.attributes = Collections.unmodifiableList(attributes);
  }

  @Override
  public int arity() {
    return attributes.size();
  }

  public boolean isUnionCompatible(@NotNull Heading other) {
    if (other.arity() != this.arity()) {
      return false;
    }

    return this.equals(other);
  }

  @Override
  public List<Integer> getAttributeIndices(@NotNull Set<String> attributeNames) {
    List<Integer> indices = new ArrayList<>(attributeNames.size());

    for (int i = 0; i < attributes.size(); i++) {
      if (attributeNames.contains(attributes.get(i).getName())) {
        indices.add(i);
      }
    }

    return indices;
  }

  @NotNull
  @Override
  public Iterator<Attribute> iterator() {
    return attributes.iterator();
  }

  @Override
  public Set<String> attributeNamesOnly() {
    return attributes.stream()
            .map(Attribute::getName)
            .collect(Collectors.toSet());
  }

  @Override
  public Set<String> getNamesOfIdDomainAttributes() {
    return attributes.stream()
            .filter(f -> f.getDomain() == IdDomain.ID)
            .map(Attribute::getName)
            .collect(Collectors.toSet());
  }

  /**
   * @param index - needs to be in the range of attributes
   * @return
   */
  @Override
  public String getFieldNameAt(int index) {
    if (index < 0 || index >= arity()) {
      throw new IllegalArgumentException("Provided index is outside the arity of the relation");
    }

    return attributes.get(index).getName();
  }

  @Override
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
   * @return new {@ref HeadingImpl} with the attributes renamed
   * @throws IllegalArgumentException if
   *  not all attributes in the {@code renamedFields} param were part of the original heading
   */
  @Override
  public Heading rename(@NotNull Map<String, String> renamedAttributes) {
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

    return new HeadingImpl(newFields);
  }

  /**
   * Creates a new heading with some attributes projected.
   *
   * @param projectedAttributes the attributes to project out of the heading.
   * @requires \forall String a | projectedFields.contains(a) | attributes.has(a)
   * @return new {@link HeadingImpl} containing only the projected attributes.
   * @throws IllegalArgumentException if
   *  not all attributes in the {@code projectedFields} were part of the original heading
   */
  @Override
  public Heading project(@NotNull Set<String> projectedAttributes) {
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

    return new HeadingImpl(newFields);
  }

  @Override
  public Heading join(@NotNull Heading other) {
    ArrayList<Attribute> joinedFields = new ArrayList<>(attributes);
    for (Attribute fd : other) {
      if (!joinedFields.contains(fd)) {
        joinedFields.add(fd);
      }
    }

    return new HeadingImpl(joinedFields);
  }

  @Override
  public Set<String> intersect(@NotNull Heading other) {
    Set<String> fieldNames = attributeNamesOnly();
    fieldNames.retainAll(other.attributeNamesOnly());
    return fieldNames;
  }

  @Override
  public boolean containsOnlyIdAttributes() {
    return idsOnly;
  }

  @Override
  public boolean isRowCompatible(Tuple tuple) {
    if (tuple.arity() != arity()) {
      return false;
    }

    for (int i = 0; i < arity(); i++) {
      Attribute fd = attributes.get(i);
      Expression att = tuple.getAttributeAt(i);

      if (fd.getDomain() == IdDomain.ID && !(att instanceof IdAtom)) {
        return false;
      } else if (fd.getDomain() == IntDomain.INT && (!(att instanceof IntConstant || att instanceof IntVariable))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HeadingImpl that = (HeadingImpl) o;

    return attributes.equals(that.attributes);
  }

  @Override
  public int hashCode() {
    return attributes.hashCode();
  }
}
